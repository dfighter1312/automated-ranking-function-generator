import argparse
import json

import pandas as pd
from typing import List
import matplotlib.pyplot as plt
import numpy as np
import matplotlib.ticker as mtick
from matplotlib.ticker import PercentFormatter

ignore_problems = ["CookSeeZulegerTACAS2013Fig8amodified"] # ignore as we use the unmodified version



def comp_general_data(data_nms: List):

    all_problems_names = []
    name_to_n_solved = {}
    for name, json_data in data_nms:

        name_to_n_solved[name] = []

        for _, data in json_data.items():  # run
            n_solved = 0
            for probs in data["successful"]:
                all_problems_names.append(probs["program"])

                if "decrease" in probs:
                    if probs["decrease"]:
                        n_solved += 1

            name_to_n_solved[name].append(n_solved)

            for probs in data["error"]:
                all_problems_names.append(probs[0])
            for probs in data["timed_out_problems"]:
                all_problems_names.append(probs["program"])

    print(name_to_n_solved)

    df = pd.DataFrame(name_to_n_solved)

    all_problems_names_unique = list(set(all_problems_names))

    print("Best run")
    for k, v in df.max().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))

    print()
    print("Worst run")
    for k, v in df.min().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))

    print()
    print("Average run")
    for k, v in df.mean().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))


def comp_general_data_aprove09_ds(data_nms: List):
    print()
    print()
    print("##############")
    print("APROVE 09")

    all_problems_names = []
    name_to_n_solved = {}
    for name, json_data in data_nms:

        name_to_n_solved[name] = []

        for _, data in json_data.items():  # run
            n_solved = 0
            for probs in data["successful"]:
                if not "termination-suite/term-comp/Aprove_09" in probs["program"]:
                    continue

                all_problems_names.append(probs["program"])

                if "decrease" in probs:
                    if probs["decrease"]:
                        n_solved += 1

            name_to_n_solved[name].append(n_solved)

            for probs in data["error"]:
                if not "termination-suite/term-comp/Aprove_09" in probs[0]:
                    continue
                all_problems_names.append(probs[0])
            for probs in data["timed_out_problems"]:
                if not "termination-suite/term-comp/Aprove_09" in probs["program"]:
                    continue
                all_problems_names.append(probs["program"])

    df = pd.DataFrame(name_to_n_solved)

    all_problems_names_unique = list(set(all_problems_names))

    print("Best run")
    for k, v in df.max().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))

    print()
    print("Worst run")
    for k, v in df.min().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))

    print()
    print("Average run")
    for k, v in df.mean().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))


def comp_general_data_sv_comp_ds(data_nms: List):
    print()
    print()
    print("##############")
    print("SVCOMP")

    all_problems_names = []
    name_to_n_solved = {}
    for name, json_data in data_nms:

        name_to_n_solved[name] = []

        for _, data in json_data.items():  # run
            n_solved = 0
            for probs in data["successful"]:
                if not "termination-suite/sv-comp/termination-crafted-lit" in probs["program"]:
                    continue

                all_problems_names.append(probs["program"])

                if "decrease" in probs:
                    if probs["decrease"]:
                        n_solved += 1

            name_to_n_solved[name].append(n_solved)

            for probs in data["error"]:
                if not "termination-suite/sv-comp/termination-crafted-lit" in probs[0]:
                    continue
                all_problems_names.append(probs[0])
            for probs in data["timed_out_problems"]:
                if not "termination-suite/sv-comp/termination-crafted-lit" in probs["program"]:
                    continue
                all_problems_names.append(probs["program"])

    df = pd.DataFrame(name_to_n_solved)

    all_problems_names_unique = list(set(all_problems_names))

    print("Best run")
    for k, v in df.max().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))

    print()
    print("Worst run")
    for k, v in df.min().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))

    print()
    print("Average run")
    for k, v in df.mean().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))


def comp_general_data_aprove09_sv_comp_ds(data_nms: List):
    print()
    print()

    print("##############")
    print("APROVE 09 + SVCOMP")

    all_problems_names = []
    name_to_n_solved = {}
    for name, json_data in data_nms:

        name_to_n_solved[name] = []

        for _, data in json_data.items():  # run
            n_solved = 0
            for probs in data["successful"]:
                if "termination-suite/term-comp/Aprove_09" not in probs["program"] and "termination-suite/sv-comp/termination-crafted-lit" not in probs["program"] :
                    continue

                all_problems_names.append(probs["program"])

                if "decrease" in probs:
                    if probs["decrease"]:
                        n_solved += 1

            name_to_n_solved[name].append(n_solved)

            for probs in data["error"]:
                if "termination-suite/term-comp/Aprove_09" not in probs[0] and "termination-suite/sv-comp/termination-crafted-lit" not in probs[0] :
                    continue
                all_problems_names.append(probs[0])
            for probs in data["timed_out_problems"]:
                if "termination-suite/term-comp/Aprove_09" not in probs["program"] and "termination-suite/sv-comp/termination-crafted-lit" not in probs["program"]:
                    continue
                all_problems_names.append(probs["program"])

    df = pd.DataFrame(name_to_n_solved)

    all_problems_names_unique = list(set(all_problems_names))

    print("Best run")
    for k, v in df.max().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))

    print()
    print("Worst run")
    for k, v in df.min().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))

    print()
    print("Average run")
    for k, v in df.mean().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))


def comp_general_data_nuterm_ds(data_nms: List):

    print("##############")
    print("NUTERM ADVANTAGE")


    all_problems_names = []
    name_to_n_solved = {}
    for name, json_data in data_nms:

        name_to_n_solved[name] = []

        for _, data in json_data.items():  # run
            n_solved = 0
            for probs in data["successful"]:
                if not "termination-suite/nuTerm-advantage" in probs["program"]:
                    continue

                all_problems_names.append(probs["program"])

                if "decrease" in probs:
                    if probs["decrease"]:
                        n_solved += 1

            name_to_n_solved[name].append(n_solved)

            for probs in data["error"]:
                if not "termination-suite/nuTerm-advantage" in probs[0]:
                    continue
                all_problems_names.append(probs[0])
            for probs in data["timed_out_problems"]:
                if not "termination-suite/nuTerm-advantage" in probs["program"]:
                    continue
                all_problems_names.append(probs["program"])

    df = pd.DataFrame(name_to_n_solved)


    all_problems_names_unique = list(set(all_problems_names))

    print("Best run")
    for k, v in df.max().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))

    print()
    print("Worst run")
    for k, v in df.min().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))

    print()
    print("Average run")
    for k, v in df.mean().items():
        print("{} {}/{}\t=\t {}".format(k, v, len(all_problems_names_unique), round(float(v) / (float(len(all_problems_names_unique)) / 100.0), 2)))


def plot_solved_over_WC_time(data_nms: List):
    all_problems_names = []

    name_to_data_solved = {}
    for name, json_data in data_nms:

        name_to_data_solved[name] = []

        for _, data in json_data.items():  # run
            for prob in data["successful"]:
                all_problems_names.append(prob["program"])
                if "decrease" in prob:
                    if prob["decrease"]:
                        name_to_data_solved[name].append(prob)

            for probs in data["error"]:

                all_problems_names.append(probs[0])
            for probs in data["timed_out_problems"]:

                all_problems_names.append(probs["program"])

    all_problems_names_unique = list(set(all_problems_names))

    # Wallclock time

    MAX_TIME = 25
    seconds = list(np.arange(2, MAX_TIME+1, 0.1))

    wc_times = {}


    for name, json_data in data_nms:

        df = pd.DataFrame(columns=seconds)

        ### General DATA
        runs = json_data.values()

        solved_lst = []
        for run in runs:
            solved = []
            for problem in run["successful"]:
                if problem["class"] in ignore_problems:
                    continue
                if "decrease" not in problem:
                    continue
                elif problem["decrease"] :
                    solved.append(problem)
            times = list(map(lambda x: x["wallclock_time"],solved))

            l = list(map(lambda x: len([t for t in times if t <= x]), seconds))
            df = df.append(pd.Series(l, index=seconds), ignore_index=True)

        wc_times[name] = df


    fig, ax = plt.subplots()

    linestyles = ['-', ':', '--', ':']
    for k, v in wc_times.items():

        xs = [x[0] for x in list(v.mean().items())]
        ys = [x[1] for x in list(v.mean().items())]

        p = ax.plot(xs, ys, linestyles.pop(0), label=k)

        ax.fill_between(xs, ys, [x[1] for x in list(v.min().items())], alpha=0.15, color=p[0].get_color())
        ax.fill_between(xs, ys, [x[1] for x in list(v.max().items())], alpha=0.15, color=p[0].get_color())

    yticks = mtick.PercentFormatter(len(all_problems_names_unique), decimals=None)
    ax.yaxis.set_major_formatter(yticks)
    ax.set_xticks(list(np.arange(2, MAX_TIME+1, 2)))
    #ax.set_xticks(list(np.arange(0, 2, .1).round(2)))

    ax.set_xlabel('Time(s)', fontsize=10)
    # ax.set_ylabel('# problems solved (accumulative)', fontsize=10)

    plt.legend()
    plt.show()
    fig.savefig("timings.png", bbox_inches='tight')



def plot_solved_over_time(data_nms):
    all_problems_names = []

    name_to_data_solved = {}
    name, json_data = data_nms

    name_to_data_solved[name] = []

    for _, data in json_data.items():  # run
        for prob in data["successful"]:
            all_problems_names.append(prob["program"])
            if "decrease" in prob:
                if prob["decrease"]:
                    name_to_data_solved[name].append(prob)

        for probs in data["error"]:

            all_problems_names.append(probs[0])
        for probs in data["timed_out_problems"]:

            all_problems_names.append(probs["program"])

    all_problems_names_unique = list(set(all_problems_names))

    # Wallclock time

    MAX_TIME = 4
    seconds = list(np.arange(0, MAX_TIME+1, 0.01))

    wc_times = {}


    name, json_data = data_nms

    df = pd.DataFrame(columns=seconds)

    runs = json_data.values()

    solved_lst = []
    for run in runs:
        solved = []
        for problem in run["successful"]:
            if problem["class"] in ignore_problems:
                continue
            if "decrease" not in problem:
                continue
            elif problem["decrease"] :
                solved.append(problem)
        times = list(map(lambda x: x["checking"],solved))

        l = list(map(lambda x: len([t for t in times if t <= x]), seconds))
        df = df.append(pd.Series(l, index=seconds), ignore_index=True)

    wc_times["Verification"] = df

    runs = json_data.values()

    solved_lst = []
    for run in runs:
        solved = []
        for problem in run["successful"]:
            if problem["class"] in ignore_problems:
                continue
            if "decrease" not in problem:
                continue
            elif problem["decrease"]:
                solved.append(problem)
        times = list(map(lambda x: x["learning"], solved))

        l = list(map(lambda x: len([t for t in times if t <= x]), seconds))
        df = df.append(pd.Series(l, index=seconds), ignore_index=True)

    wc_times["Learning"] = df

    solved_lst = []
    for run in runs:
        solved = []
        for problem in run["successful"]:
            if problem["class"] in ignore_problems:
                continue
            if "decrease" not in problem:
                continue
            elif problem["decrease"]:
                solved.append(problem)
        times = list(map(lambda x: x["tracing"], solved))

        l = list(map(lambda x: len([t for t in times if t <= x]), seconds))
        df = df.append(pd.Series(l, index=seconds), ignore_index=True)

    wc_times["Tracing"] = df



    fig, ax = plt.subplots()

    linestyles = ['-', ':', '--', ':']
    for k, v in wc_times.items():

        xs = [x[0] for x in list(v.mean().items())]
        ys = [x[1] for x in list(v.mean().items())]

        p = ax.plot(xs, ys, linestyles.pop(0), label=k)

        ax.fill_between(xs, ys, [x[1] for x in list(v.min().items())], alpha=0.15, color=p[0].get_color())
        ax.fill_between(xs, ys, [x[1] for x in list(v.max().items())], alpha=0.15, color=p[0].get_color())

    yticks = mtick.PercentFormatter(len(all_problems_names_unique), decimals=None)
    ax.yaxis.set_major_formatter(yticks)
    ax.set_xticks(list(np.arange(2, MAX_TIME+1, 2)))
    #ax.set_xticks(list(np.arange(0, 2, .1).round(2)))

    ax.set_xlabel('Time(s)', fontsize=10)
    # ax.set_ylabel('# problems solved (accumulative)', fontsize=10)

    plt.legend()
    plt.show()
    fig.savefig("learning.png", bbox_inches='tight')


def plot_solved_over_neurons(data_nms: List):
    data_nms = data_nms[0:-1]
    all_problems_names = []
    name_to_n_solved = {}
    for name, json_data in data_nms:

        name_to_n_solved[name] = []

        for _, data in json_data.items():  # run
            n_solved = 0
            for probs in data["successful"]:
                all_problems_names.append(probs["program"])

                if "decrease" in probs:
                    if probs["decrease"]:
                        n_solved += 1

            name_to_n_solved[name].append(n_solved)

            for probs in data["error"]:
                all_problems_names.append(probs[0])
            for probs in data["timed_out_problems"]:
                all_problems_names.append(probs["program"])

    df = pd.DataFrame(name_to_n_solved)

    all_problems_names_unique = list(set(all_problems_names))

    fig, ax = plt.subplots()

    x = [values/len(all_problems_names_unique)*100 for _, values in df.mean().items()]

    bars = ax.bar(range(1, len(data_nms)+1), x)

    ax.set_xticks(list(range(1, 11)))
    ax.set_yticks(list(range(0, 101,20)))

    ax.bar_label(bars, fmt="%.f")

    ax.set_xlabel('# of neurons', fontsize=10)
    ax.set_ylabel('% of problems solved', fontsize=10)

    plt.show()

    fig.savefig("perc_over_neurons.svg", format = 'svg' ,bbox_inches='tight')



def main(args):

    df = pd.DataFrame({})
    all_probs = []

    #data_nms = [("anticorrSOR2", anticorrSOR2), ("biastest", biastest), ("portfolio", portfolio), ("baseline", baseline)]
    #data_nms = [("SOR 5 hidden neurons", anticorrSOR2),  ("SOR 1 hidden neuron", baseline), ("sampling with normal distribution", gaussian)]

    #data_nms = [("SOR 5 hidden neurons", anticorrSOR2),  ("SOR 1 hidden neuron", baseline), ("SOR 10 hidden neurons", baseline)]


    l = [("1 neuron", "../results/complete_anticorr_sumofrelu2_dyn_nodes_1.json"),
         ("2 neurons", "../results/complete_anticorr_sumofrelu2_dyn_nodes_2.json"),
         ("3 neurons", "../results/complete_anticorr_sumofrelu2_dyn_nodes_3.json"),
         ("4 neurons", "../results/complete_anticorr_sumofrelu2_dyn_nodes_4.json"),
         ("5 neurons", "../results/complete_anticorr_sumofrelu2_dyn_nodes_5.json"),
         ("6 neurons", "../results/complete_anticorr_sumofrelu2_dyn_nodes_6.json"),
         ("7 neurons", "../results/complete_anticorr_sumofrelu2_dyn_nodes_7.json"),
         ("8 neurons", "../results/complete_anticorr_sumofrelu2_dyn_nodes_8.json"),
         ("9 neurons", "../results/complete_anticorr_sumofrelu2_dyn_nodes_9.json"),
         ("10 neurons", "../results/complete_anticorr_sumofrelu2_dyn_nodes_10.json"),
         ("anticorr_sumofrelu2", "../results/complete_anticorr_sumofrelu2.json")
         ]

    data_nms = []
    for x, y in l:
        with open(y, "r") as read_file:
            json_data = json.load(read_file)
            data_nms.append((x, json_data))


    comp_general_data(data_nms)
    comp_general_data_aprove09_ds(data_nms)
    comp_general_data_sv_comp_ds(data_nms)
    comp_general_data_nuterm_ds(data_nms)
    comp_general_data_aprove09_sv_comp_ds(data_nms)

    #plot_solved_over_WC_time([data_nms[0], data_nms[-1], data_nms[-5]])
    #plot_solved_over_time(data_nms[-5])
    plot_solved_over_neurons(data_nms)


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    # parser.add_argument('--seed', dest='seed', type=int, default=199)
    # parser.add_argument('-save', dest='save_file', help='File the results are saved to.', required=True)
    parser.add_argument('-input', dest='strategy', help='File the results are saved to.', required=False)

    args = parser.parse_args()

    main(args)
