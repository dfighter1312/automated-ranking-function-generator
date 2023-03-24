import time
import traceback
import psutil
import torch
import sympy as sp
import torch.nn as nn
from models.SumOfRelu2 import SumOfRelu2
from checker.javachecker import check_sum_of_relu
from utils.trace_utils import tracing, tracing_cex
from utils.get_loop_heads import get_loop_heads
from utils.training import train_ranking_function_smrl2
from utils.print_result import print_result


processes = psutil.cpu_count(logical=False)


def anticorr_sumofrelu_2(jar_file, class_name, method_name, n_trials = 20, overwrite_sampling='pairanticorr', sample_size=1000, seed=None):
    """Anticorr sumofrelu generator for neural ranking function.

    Args:
        jar_file (str): JAR file
        class_name (str): Class name
        method_name (str): Method name
        overwrite_sampling (str, optional): Sampling technique if do want to use pairanticorr. Defaults to None.
        force_sample_size (int, optional): Limit the sample size. Defaults to None.
        seed (int, optional): Torch random seed. Defaults to None.
    """
    limit = 1000
    learning_rate = 0.001
    n_iterations = 1000
    n_summands = 5
    
    result = {
        'program': jar_file,
        'function': method_name,
        'class': class_name,
        'functions before rounding': [],
        'functions after rounding': [],
        'learning_time': 0,
        'trace_time': 0,
        'checking_time': 0,
        'n_trials': 0,
        'n_iterations': 0,
        'n_pairs': 0
    }
    print("Running with {}".format(overwrite_sampling))
    try:
        # Get loop headers
        loop_heads = get_loop_heads(jar_file, class_name, method_name)
        
        # Dataset only accept methods with less than or equal to 2 headers
        assert len(loop_heads) <= 2 
        
        for trial in range(n_trials):
            
            # if trial % 5 == 0:
            if trial == 0:

                trace, trace_time = tracing(
                    jar_file=jar_file,
                    class_name=class_name,
                    method_name=method_name,
                    samples=sample_size * (trial // 5 + 1),
                    limit=limit,
                    loop_offset=loop_heads,
                    tracing_seed=seed,
                    sampling_strategy=overwrite_sampling,
                    n_processes=processes
                )
                result['trace_time'] += trace_time
                
                input_vars = trace.get_pos_identifiers(frame=5)
                
                input_before = []
                input_after = []
                
                # Create data and model with info from trace
                for head in loop_heads:
                    head_input_before, head_input_after = trace.pair_all_traces_multi_as_tensor(head, loop_heads)
                    input_before.append(head_input_before)
                    input_after.append(head_input_after)
                    
                result['n_pairs'] = list(map(lambda x: x.size()[0], input_before))

            model = SumOfRelu2(
                len(input_vars),
                n_out=len(loop_heads),
                n_summands=5,
                trainable_out=True
            )
            model, training_time = train_ranking_function_smrl2(
                model=model,
                loop_heads=loop_heads,
                trace=trace,
                input_before=input_before,
                input_after=input_after,
                result=result,
                input_vars=input_vars,
                learning_rate=learning_rate,
                n_iterations=n_iterations,
                n_summands=n_summands
            )
            result["learning_time"] += training_time
                
            print('=' * 100)
            print(' - Checking')
            print('=' * 40)

            check_start = time.time()

            lexiW = []
            lexib = []
            lexiout = []
            for i in range(0, len(loop_heads)):
                parameters = next(model.fc1[i].parameters()).detach().numpy()
                result["functions before rounding"] += print_result(parameters, input_vars, f"(Loop header #{i+1}) Hidden layer 1")
                out_parameters = next(model.fc2[i].parameters()).detach().numpy()
                result["functions before rounding"] += print_result(out_parameters, input_vars, f"(Loop header #{i+1}) Output layer", var_dependent=False)
                
            Ses = [0.5, 1.0]

            for S in Ses:
                print('=' * 40)
                print(' - Rounding with S =', S)
                print('=' * 40)

                W_to_check = []
                b_to_check = []
                out_to_check = []
                for i in range(len(loop_heads)):
                    W = model.fc1[i].weight
                    if model.fc1[i].bias is not None:
                        b = model.fc1[i].bias
                    else:
                        b = torch.zeros(model.fc1[i].out_features)
                    out = model.fc2[i].weight

                    W_round = (W.data.numpy() / S).round()
                    b_round = (b.data.numpy() / S).round()
                    out_round = (out.data.numpy() / S).round()

                    symvars = sp.Matrix(sp.symbols(input_vars, real=True))
                    symorig = (W * symvars + b[:, None]).applyfunc(sp.Function('relu'))
                    symround = (W_round * symvars + b_round[:, None]).applyfunc(sp.Function('relu'))
                    # for j, e in enumerate(symorig):
                    #     print('  ', j,
                    #         (round(out[0, j].item(), 2) * e).xreplace({n: round(n, 2) for n in e.atoms(sp.Number)}))
                    print('  After rounding')
                    for j, e in enumerate(symround):
                        print(f"Branch {j + 1}: {out_round[0, j] * e}")
                        result["functions after rounding"] += [f"Branch {j + 1}: {out_round[0, j] * e}"]

                    W_to_check.append(W_round)
                    b_to_check.append(b_round)
                    out_to_check.append(out_round)

                result['decrease'], result['invar'], cex = check_sum_of_relu(jar_file, class_name, method_name,
                                                                            loop_heads, input_vars,
                                                                            out_to_check, W_to_check, b_to_check)

                if (result['decrease']):
                    print('YES')
                    break
                else:
                    result["functions before rounding"] = []
                    result["functions after rounding"] = []
                    print('Not yet.')
                    print(cex)
                    
                    # if len(cex) != 0:
                    #     # Adding counterexamples to the dataset
                    #     trace, trace_time = tracing_cex(
                    #         jar_file=jar_file,
                    #         class_name=class_name,
                    #         method_name=method_name,
                    #         cex=cex
                    #     )
                    #     print(trace)
                    #     for head in loop_heads:
                    #         head_input_before, head_input_after = trace.pair_all_traces_multi_as_tensor(head, loop_heads)
                    #         input_before.append(head_input_before)
                    #         input_after.append(head_input_after)

            checking_time = time.time() - check_start
            result['checking_time'] += checking_time
            
            if (result['decrease']):
                break

        result['n_trials'] = trial + 1
        return result
    except Exception as e:
        result['error'] = e.__class__.__name__ + ': ' + str(e)
        print(e.__class__.__name__, e)
        traceback.print_exc()
        return result