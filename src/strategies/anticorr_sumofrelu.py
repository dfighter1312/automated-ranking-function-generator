import time
import psutil
import torch
import sympy as sp
import torch.nn as nn
from javachecker import check_sum_of_relu
from trace_utils import tracing
from get_loop_heads import get_loop_heads
from utils.training import train_ranking_function


processes = psutil.cpu_count(logical=False)


def anticorr_sumofrelu(jar_file, class_name, method_name, overwrite_sampling='pairanticorr', sample_size=1000, seed=None):
    """Anticorr sumofrelu generator for neural ranking function.

    Args:
        jar_file (str): JAR file
        class_name (str): Class name
        method_name (str): Method name
        overwrite_sampling (str, optional): Sampling technique if do want to use pairanticorr. Defaults to None.
        force_sample_size (int, optional): Limit the sample size. Defaults to None.
        seed (int, optional): Torch random seed. Defaults to None.
    """
    limit = 100
    learning_rate = 0.001
    n_iterations = 1000
    n_summands = 5
    n_trials = 10
    
    result = {
        'program': jar_file,
        'function': method_name,
        'class': class_name,
        'learning': None,
        'trace_time': None,
        'checking': None,
        'error': None
    }
    print("Running with {}".format(overwrite_sampling))
    try:
        # Get loop headers
        loop_heads = get_loop_heads(jar_file, class_name, method_name)
        
        # Dataset only accept methods with less than or equal to 2 headers
        assert len(loop_heads) <= 2
        
        trace, result['trace_time'] = tracing(
            jar_file=jar_file,
            class_name=class_name,
            method_name=method_name,
            samples=sample_size,
            limit=limit,
            loop_offset=loop_heads,
            tracing_seed=seed,
            sampling_strategy=overwrite_sampling,
            n_processes=processes
        )
        
        input_vars = trace.get_pos_indetifiers(frame=5)
        
        input_before = []
        input_after = []
        
        # Create data and model with info from trace
        for head in loop_heads:
            head_input_before, head_input_after = trace.pair_all_traces_multi_as_tensor(head, loop_heads)
            input_before.append(head_input_before)
            input_after.append(head_input_after)
        
        for _ in range(n_trials):
            model, result["learning"] = train_ranking_function(
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
            
            # TODO: Refactor this rounding depends on strategies
            print('=' * 40)
            print(' - Rounding')
            print('=' * 40)
            S = 1
            W = model.fc1.weight
            for row in W:
                for x in row:
                    while (2 * S < abs(x)):
                        S = 2 * S

            if model.fc1.bias is not None:
                b = model.fc1.bias
                for x in b:
                    while (2 * S < abs(x)):
                        S = 2 * S
            else:
                b = torch.zeros(model.fc1.out_features)


            W_round = (model.fc1.weight.data.numpy() / S).round()
            b_round = (b.data.numpy() / S).round()

            symvars = sp.Matrix(sp.symbols(input_vars, real=True))
            symorig = (W * symvars + b).applyfunc(sp.Function('relu'))
            symround = (W_round * symvars + b_round).applyfunc(sp.Function('relu'))
            for i, e in enumerate(symorig):
                print(i, e.xreplace({n: round(n, 2) for n in e.atoms(sp.Number)}))
            print('After rounding')
            for i, e in enumerate(symround):
                print(i, e)
                
            print('=' * 40)
            print(' - Checking')
            print('=' * 40)

            check_start = time.time()

            n_summands = model.n_summands
            lexiW = []
            lexib = []
            lexiout = []
            for i in range(0, len(loop_heads)):
                lexiW.append(W_round[n_summands*i:n_summands*(i+1)])
                lexib.append(b_round[n_summands*i:n_summands*(i+1)])
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

            result['checking'] += time.time() - check_start

            if (result['decrease']):
                break
                
            print('=' * 40)
            print(' - Randomising')
            print('=' * 40)
            seen = set()
            for i, row in enumerate(W_round):
                row = tuple(row.tolist())
                if row in seen:
                    nn.init.normal_(model.fc1.weight[i])
                    e = symvars.dot(model.fc1.weight.data.numpy()[i])
                    print("reinitialising {} to {}".format(i, e.xreplace(
                        {n: round(n, 2) for n in e.atoms(sp.Number)})))
                else:
                    seen.add(row)
        return result
    except Exception as e:
        result['error'] = (e.__class__.__name__, str(e))
        print(e.__class__.__name__, e)
        return result
    
    
    
    
    
    
    