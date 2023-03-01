from operator import itemgetter
import matplotlib.pylab as pylab
import matplotlib.pyplot as plt 
import numpy as np 
import matplotlib.ticker as mtick 
from matplotlib.ticker import PercentFormatter 
import decimal
import pandas as pd
import json

plt.rc('xtick', labelsize=12)    # fontsize of the tick labels
plt.rc('ytick', labelsize=12)    # fontsize of the tick labels

json_files = ["results1.json", "results2.json", ""]

def calc_accumulation(x_axis, y_axis):
    accum = list(map(lambda x: len(list(filter(lambda y: y <= x, y_axis))), x_axis))
    return accum

def problems_over_time():
    names = []
    lits = []
    for json_file in json_files:
        with open(json_file, "r") as f:
            data = json.load(f)
            tmp = {}
            for _, v in data.items():
                if v['strategy'] in tmp:
                    tmp[v['strategy']].append(v['times_correct'])
                else:
                    tmp[v['strategy']] = [v['times_correct']]

            for k, v in tmp.items():
                names.append(k)
                lits.append(v)


    for name, lit in zip(names,lits):
        print("80 percent of problems in less than {} seconds {}".format(sum([sorted(x)[42] if len(x)>42 else 0 for x in lit])/len(lit), name))


    MAX = max(sum(list(map(lambda x: sum(x,[]),lits)), []))
    MIN = min(sum(list(map(lambda x: sum(x,[]),lits)), []))

    MAX_solved = max(list(map(lambda x: len(x), sum(lits,[]))))


    x_axis_vals = list(np.arange(MIN, MAX, 0.1).round(2)) + [np.round(MAX + 1.0, 2)]


    # for every list

    ys = []
    for method, name in zip(lits,names):
        y_axis_vals = []
        for current_l in method:
            y_axis_vals.append(calc_accumulation(x_axis_vals, current_l))

        pd_y_axis = pd.DataFrame(y_axis_vals)

        min_ys = list(pd_y_axis.min())
        max_ys = list(pd_y_axis.max())
        avg_ys = list(pd_y_axis.mean())
        ys.append((min_ys,max_ys, avg_ys,name))


    fig, ax = plt.subplots()

    for mini, maxi, avg, name in ys:
        p = ax.plot(x_axis_vals, avg, '-', label=name)

        ax.fill_between(x_axis_vals, avg, mini, alpha=0.15,color=p[0].get_color())
        ax.fill_between(x_axis_vals, avg, maxi, alpha=0.15,color=p[0].get_color())
        


    yticks = mtick.PercentFormatter(113, decimals=None)
    ax.yaxis.set_major_formatter(yticks)

    ax.set_xticks(list(np.arange(0, MAX+1, 1).round(2)))

    ax.set_xlabel('Time(s)', fontsize=10)
    ax.set_ylabel('# problems solved (accumulative)', fontsize=10)

    plt.legend()
    #plt.show()
    fig.savefig("timings.png")


def gen_stat():
    names = []
    lits = []
    for json_file in json_files:

        with open(json_file, "r") as f:
            data = json.load(f)
            tmp = {}
            for _, v in data.items():
                if v['strategy'] in tmp:
                    tmp[v['strategy']].append(v['iterations'])
                else:
                    tmp[v['strategy']] = [v['iterations']]

            for k, v in tmp.items():
                names.append(k)
                lits.append(v)


    for name, its in zip(names, lits):
        print(name)
        print("\tSolved best: {}/113".format(max(list( map(lambda x: len(x), its)))))
        print("Percent best: {}".format(max(list( map(lambda x: len(x), its)))/1.13))



def problems_over_iteration():

    names = []
    lits = []
    for json_file in json_files:
        with open(json_file, "r") as f:
            data = json.load(f)
            tmp = {}
            for _, v in data.items():
                if v['strategy'] in tmp:
                    tmp[v['strategy']].append(v['iterations'])
                else:
                    tmp[v['strategy']] = [v['iterations']]

            for k, v in tmp.items():
                names.append(k)
                lits.append(v)


    for name, lit in zip(names,lits):
        print("problems with fewer than 50 iterations {} {}".format(name, sum([len(list(filter(lambda e: e <= 50, x))) for x in lit])/len(lit)))


    MAX = max(sum(list(map(lambda x: sum(x,[]),lits)), []))
    MIN = min(sum(list(map(lambda x: sum(x,[]),lits)), []))

    MAX_solved = max(list(map(lambda x: len(x), sum(lits,[]))))


    x_axis_vals = list(np.arange(MIN, MAX, 0.1).round(2)) + [np.round(MAX + 1.0, 2)]


    ys = []
    for method, name in zip(lits,names):
        y_axis_vals = []
        for current_l in method:
            y_axis_vals.append(calc_accumulation(x_axis_vals, current_l))

        pd_y_axis = pd.DataFrame(y_axis_vals)

        min_ys = list(pd_y_axis.min())
        max_ys = list(pd_y_axis.max())
        avg_ys = list(pd_y_axis.mean())
        ys.append((min_ys,max_ys, avg_ys,name))



    fig, ax = plt.subplots()

    for mini, maxi, avg, name in ys:
        p = ax.plot(x_axis_vals, avg, '-', label=name)

        ax.fill_between(x_axis_vals, avg, mini, alpha=0.15,color=p[0].get_color())
        ax.fill_between(x_axis_vals, avg, maxi, alpha=0.15,color=p[0].get_color())
        


    yticks = mtick.PercentFormatter(113, decimals=None)
    ax.yaxis.set_major_formatter(yticks)

    #ax.set_xticks(list(np.arange(MIN, MAX+1, 2).round(2)))
    ax.set_xticks( list(range(0, MAX, 100)))

    ax.set_xlabel('# Iterations', fontsize=10)
    ax.set_ylabel('# problems solved (accumulative)', fontsize=10)

    plt.legend()
    #plt.show()
    fig.savefig("iterations.png")




def problems_over_samples():
    names = []
    res = []

    for json_file in json_files:
        with open(json_file, "r") as f:
            data = json.load(f)
            tmp = {}
            for _, v in data.items():
                if v['strategy'] in tmp:
                    tmp[v['strategy']].append((v['sample_size'],v['solved']))
                else:
                    tmp[v['strategy']] = [(v['sample_size'],v['solved'])]

            for k, v in tmp.items():
                names.append(k)
                res.append(v)

    
    x_axis = sorted(list(set(sum(list(map(lambda x: list(map(lambda y: y[0], x)), res)),[]))))

    ys = []
    for pairs, name in zip(res,names):
        y_axis_vals = []

        # pairs.sort(key=itemgetter(0))
        vals = {}
        for x in x_axis:
            vals[x] = list(map(lambda z: z[1], filter(lambda y: y[0] == x, pairs)))
        
        pd_y_axis = pd.DataFrame(dict([ (k,pd.Series(v)) for k,v in vals.items() ]))

        print(pd_y_axis)

        min_ys = list(pd_y_axis.min())
        max_ys = list(pd_y_axis.max())
        avg_ys = list(pd_y_axis.mean())
        ys.append((min_ys,max_ys, avg_ys,name))




    #MAX = max(sum(list(map(lambda x: sum(x,[]),lits)), []))

    #MAX_solved = max(list(map(lambda x: len(x), sum(lits,[]))))






    fig, ax = plt.subplots()

    for mini, maxi, avg, name in ys:
        p = ax.plot(x_axis, avg, '-', label=name)

        ax.fill_between(x_axis, avg, mini, alpha=0.15,color=p[0].get_color())
        ax.fill_between(x_axis, avg, maxi, alpha=0.15,color=p[0].get_color())
        


    yticks = mtick.PercentFormatter(113, decimals=None)
    ax.yaxis.set_major_formatter(yticks)

    #ax.set_xticks(list(np.arange(MIN, MAX+1, 2).round(2)))
    # ax.set_xticks( list(range(0, MAX, 100)))

    ax.set_xlabel('# of samples', fontsize=10)
    ax.set_ylabel('% problems solved', fontsize=10)

    plt.legend()
    #plt.show()
    fig.savefig("samples.png")


problems_over_samples()
gen_stat()
problems_over_iteration()
problems_over_time()
