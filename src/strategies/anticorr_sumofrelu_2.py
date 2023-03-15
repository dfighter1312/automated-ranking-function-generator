import time
import traceback
import psutil
import torch
import sympy as sp
import torch.nn as nn
from checker.javachecker import check_sum_of_relu
from utils.trace_utils import tracing
from utils.get_loop_heads import get_loop_heads
from utils.training import train_ranking_function
from utils.print_result import print_result


processes = psutil.cpu_count(logical=False)


def anticorr_sumofrelu(jar_file, class_name, method_name, n_trials = 20, overwrite_sampling='pairanticorr', sample_size=1000, seed=None):
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
        'learning_time': 0,
        'trace_time': 0,
        'checking_time': 0,
        'n_trials': 0,
        'n_iterations': 0
    }
    print("Running with {}".format(overwrite_sampling))
    try:
        # Get loop headers
        loop_heads = get_loop_heads(jar_file, class_name, method_name)
        
        # Dataset only accept methods with less than or equal to 2 headers
        assert len(loop_heads) <= 2 
        
        for trial in range(n_trials):
            
            if trial % 5 == 0:

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

            model = SumOfRelu2(
                len(input_vars),
                n_out=len(loop_heads),
                n_summands=5
            )
            model, training_time = train_ranking_function(
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
            
            parameters = next(model.fc1.parameters()).detach().numpy()
            result["functions"] = print_result(parameters, input_vars, "Hidden layer 1")
            print(f"Final result for each loop header is the sum of every {n_summands} neurons.")
            
            W = model.fc1.weight

            if model.fc1.bias is not None:
                b = model.fc1.bias
            else:
                b = torch.zeros(model.fc1.out_features, 1)
                
            print('=' * 100)
            print(' - Checking')
            print('=' * 40)

            check_start = time.time()

            lexiW = []
            lexib = []
            lexiout = []
            for i in range(0, len(loop_heads)):
                lexiW.append(W[n_summands*i:n_summands*(i+1)])
                lexib.append(b[n_summands*i:n_summands*(i+1)])
                lexiout.append(torch.ones(1, n_summands))

            result['decrease'], result['invar'], cex = check_sum_of_relu(jar_file, class_name, method_name,
                                                                         loop_heads, input_vars,
                                                                         lexiout, lexiW, lexib)

            if (result['decrease']):
                print("Termination was proven.")
                print('YES')
            else:
                print('Not yet.')
                print(cex)

            checking_time = time.time() - check_start
            result['checking_time'] += checking_time
            
            if (result['decrease']):
                break
                
            print('=' * 100)
            print(' - Randomising')
            print('=' * 40)
            seen = set()
            for i, row in enumerate(W):
                row = tuple(row.tolist())
                if row in seen:
                    nn.init.normal_(model.fc1.weight[i])
                    e = symvars.dot(model.fc1.weight.data.numpy()[i])
                    print("reinitialising {} to {}".format(i, e.xreplace(
                        {n: round(n, 2) for n in e.atoms(sp.Number)})))
                else:
                    seen.add(row)
        result['n_trials'] = trial + 1
        return result
    except Exception as e:
        result['error'] = e.__class__.__name__ + ': ' + str(e)
        print(e.__class__.__name__, e)
        traceback.print_exc()
        return result