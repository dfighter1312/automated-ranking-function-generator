
import os
import subprocess
import argparse
import tqdm
import json
import time
from termcolor import colored
from datetime import datetime
import os.path
import pandas as pd

def main(args):

    # create problem list
    problems = None

    if args.fileline is not None:
        file, line = args.fileline
        line = int(line)

        with open(file) as f:
            lines = f.readlines()

            problems = pd.DataFrame(index=["File", "Class", "Function", "Strategy"])
            problems.loc[len(problems)] = lines[line-1].split(",")
            problems["File"] = problems["File"].map(lambda x: os.path.abspath(os.path.join(os.path.basename(file), x)))

    else:
        problems = pd.read_csv(args.set, sep=',', comment="#")
        problems["File"] = problems["File"].map(lambda x: os.path.abspath(os.path.join(os.path.basename(args.set), x)))

    succ = []
    err = []
    timeout = []
    for problem in tqdm.tqdm(problems.to_dict(orient="records"), unit="problems"):
        cmd = ["python3", "../src/run.py", "-i", problem["File"], "-c", problem["Class"], "-m",
                   problem["Function"]]

        if args.strategy is not None:
            cmd = cmd + ["--strategy", args.strategy]
        else:
            cmd = cmd + ["--strategy", problem["Strategy"]]

        if args.seed is not None:
            cmd = cmd + ["--seed", args.seed]

        if args.samples is not None:
            cmd = cmd + ["--samples", str(args.samples)]

        if args.strat_args is not None:
            cmd = cmd + ["--stratargs", str(args.strat_args)]

        print()
        print(" ".join(cmd))
        start = time.time()
        try:
            process = subprocess.run(cmd, stdout=subprocess.PIPE, universal_newlines=True, timeout=args.timeout)
        except subprocess.TimeoutExpired as e:
            wc_time = time.time() - start
            s = "UKN" if e.stdout is None else str(e.stdout)  # This is not working
            r = {"program": problem["File"], "class": problem["Class"], "function": problem["Function"],
                 "strategy": problem["Strategy"], "output": s, "wallclock_time": wc_time}
            timeout.append(r)
            if args.verbose:
                print(s)
            continue
        wc_time = time.time() - start
        try:
            if args.verbose:
                print(process.stdout)
            l = process.stdout.strip().split("{")[-1].split("}")[0]
            json_res = "{" + l.replace("\'", "\"") + "}"
            res_dict = json.loads(json_res)
            res_dict["wallclock_time"] = wc_time
            succ.append(res_dict)
        except Exception as e:
            print(e)
            err.append((problem["File"], problem["Class"], problem["Function"], problem["Strategy"]))

    print("\n\n")
    print("Successful Runs")
    print("{:4s} {:50s}\t{:15s}\t{:11s}\t{:11s}\t{:11s}\t{:11s}\t{:11s}".format("#", "Class", "Function", " Tracing", "Learning", "Time", "Decreasing", "Invariant"))
    print("-" * 140)
    for i, r in enumerate(succ, 1):
        if "error" in r:
            print(colored("{:4} {:50s}\t{:15s}\t{}".format(i, r["class"], r["function"], r["error"]), "yellow"))
            continue
        if "decrease" not in r:
            print(colored("{:4} {:50s}\t{:15s}".format(i, r["class"], r["function"]), "yellow"))
            continue
        if r["decrease"]:
            print(colored("{:4} {:50s}\t{:15s}\t{:8.2f}\t{:8.2f}\t{:8.2f}\t      {}\t    {}".format(i, r["class"], r["function"], r["trace_time"], r["learning_time"], r["wallclock_time"],r["decrease"], r["invar"]), "green"))
        else:
            print(colored("{:4} {:50s}\t{:15s}\t{:8.2f}\t{:8.2f}\t{:8.2f}\t      {}\t    {}".format(i, r["class"], r["function"], r["trace_time"], r["learning_time"], r["wallclock_time"],r["decrease"], r["invar"]), "red"))

    print("\n\n\n")
    print("Unsuccessful Runs with Exceptions or other causes")
    print("-" * 140)
    print("{:4} {:50s}\t{:25}\t{:20s}\t{:30s}".format("#", "Program", "Class", "Function", "Strategy"))
    for i, r in enumerate(err, 1):
        print(colored("{:4} {:50s}\t{:25}\t{:20s}\t{:30s}".format(i, r[0][-47:], r[1], r[2], r[3]), "red"))

    # TIMEOUT
    print("\n\n\n")
    print("Timed out")
    print("-" * 140)
    print("{:4} {:50s}\t{:25}\t{:20s}\t{:30s}\t{:6}".format("#", "Program", "Class", "Function", "Strategy", "Time"))
    for i, r in enumerate(timeout, 1):
        print(colored("{:4} {:50s}\t{:25}\t{:20s}\t{:30s}\t{:4.2f}".format(i, "..."+r["program"][-47:], r["class"],
                                                                   r["function"], r["strategy"], r["wallclock_time"]),
                      "red"))


    print("\n\n\n")
    print("Total: {}".format(len(succ) + len(err) + len(timeout)))

    size = len(list(filter(lambda x: x["decrease"],
                           filter(lambda x: "decrease" in x, succ))))

    print("Solved: {}".format(size))
    print("Failed: {}".format(len(succ) + len(err) + len(timeout) - size))

    print("Timed out (included in fails): {}".format(len(timeout)))

    if args.save_file is not None:
        if args.seed:
            dt = {"successful": succ, "error": err, "timed_out_problems": timeout, "timeout": args.timeout, "seed": args.seed}
        else:
            dt = {"successful": succ, "error": err, "timed_out_problems": timeout, "timeout": args.timeout}

        data = None
        now = datetime.now()

        if os.path.isfile(args.save_file):
            with open(args.save_file, "r") as read_file:
                data = json.load(read_file)
                data[now.strftime("%d/%m/%Y %H:%M:%S")] = dt
        else:
            data = {now.strftime("%d/%m/%Y %H:%M:%S"): dt}
            
        

        with open(args.save_file, "w") as write_file:
            json.dump(data, write_file, indent=4)


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument('-set', dest='set', help='Benchmarking set file', required=False)
    parser.add_argument('-save', dest='save_file', help='File the results are saved to.', required=False)
    parser.add_argument('-seed', dest='seed', help='File the results are saved to.', required=False)
    parser.add_argument('-strategy', dest='strategy', help='File the results are saved to.', required=False)
    parser.add_argument('-verbose', action='store_true', help='Verbose output', required=False)
    parser.add_argument('-samples', dest='samples', type=int, help='Number of samples.', required=False)
    parser.add_argument('-timeout', dest='timeout', type=int, help='Seconds after which we time out.', required=False, default=60)
    parser.add_argument('-stratargs', dest='strat_args', type=str, help='Strategy arguments.', required=False)
    parser.add_argument('-fileline', metavar=('file', 'line'), help='File and line in file (starting at 1).', nargs=2, required=False)

    args = parser.parse_args()

    main(args)
