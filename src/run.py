import argparse
import os
import pathlib
import random
import pprint
import numpy as np

import torch

from strategies.anticorr_sumofrelu import anticorr_sumofrelu
from strategies.anticorr_sumofrelu_2 import anticorr_sumofrelu_2


file_path = str(pathlib.Path(__file__).parent.absolute())

os.environ['CLASSPATH'] = file_path + '/../libs/share/java/com.microsoft.z3.jar' + ':' \
                        + ':'.join(
                            [e.path for e in os.scandir(file_path + '/../libs/')]
                        )
os.environ['LD_LIBRARY_PATH'] = file_path + '/../libs/lib/'

strategy_mapping = {
    "anticorr_sumofrelu": anticorr_sumofrelu,
    "anticorr_sumofrelu_2": anticorr_sumofrelu_2
}

def run_single_check(input, klass, method, strategy, seed, samples, strat_args):
    
    # Set seeds
    if args.seed is not None:
        torch.manual_seed(args.seed)
        random.seed(args.seed)
        np.random.seed(args.seed)
        
        # Output in Torch must be deterministic
        torch.use_deterministic_algorithms(True)
        
    strategy = strategy_mapping[strategy]
    print("Running {} on JAR {}, class {}, method {}".format(
        strategy,
        input,
        klass,
        method
    ))
    
    result = {}
    if args.strat_args is not None:
        result = strategy(input, klass, method, seed=seed, sample_size=samples, strat_args=strat_args)
    else:
        result = strategy(input, klass, method, seed=seed, sample_size=samples)
    result["strategy"] = strategy_mapping[args.strategy].__name__
    result["seed"] = args.seed
    
    if "decrease" in result:
        if result["decrease"]:
            print("Termination was proven.")
        else:
            print("Termination could not be proven.")
    else:
        print("Termination could not be proven or decrease value is not set.")
    
    if "decrease" in result:
        result["decrease"] = str(result["decrease"])     
    if "invar" in result:
        result["invar"] = str(result["invar"])  
    print(result)
    return result


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument('-i', '--input', dest='input', help='Input JAR file', required=True)
    parser.add_argument('-c', '--class', dest='klass', help='Class file to check', required=True)
    parser.add_argument('-m', '--method', dest='method', help='Method to check termination for', required=True)
    parser.add_argument('--strategy', dest='strategy', help='Strategy to use for analysis', required=True)
    parser.add_argument('--seed', dest='seed', help='Seed', type=int, required=False, default=0)
    parser.add_argument('--samples', dest='samples', type=int, help='Number of Samples to use to generate.', required=False, default=1000)
    parser.add_argument('--strat_args', dest='strat_args', type=str, help='Special arguments for strategies. Be careful, this is strategy dependent', required=False)
    
    args = parser.parse_args()
    run_single_check(args.input, args.klass, args.method, args.strategy, args.seed, args.samples, args.strat_args)