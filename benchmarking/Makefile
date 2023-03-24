SHELL := /bin/bash

mk_folder:
	mkdir -p results

run_mvgaussian:

	${PYTHON3} run_benchmark.py -set ./problem-sets/three_sets.csv -seed 151 -strategy anticorr_sumofrelu_2 -save results/complete_anticorr_sumofrelu2.json
	${PYTHON3} run_benchmark.py -set ./problem-sets/three_sets.csv -seed 251 -strategy anticorr_sumofrelu_2 -save results/complete_anticorr_sumofrelu2.json
	${PYTHON3} run_benchmark.py -set ./problem-sets/three_sets.csv -seed 351 -strategy anticorr_sumofrelu_2 -save results/complete_anticorr_sumofrelu2.json
	${PYTHON3} run_benchmark.py -set ./problem-sets/three_sets.csv -seed 451 -strategy anticorr_sumofrelu_2 -save results/complete_anticorr_sumofrelu2.json
	${PYTHON3} run_benchmark.py -set ./problem-sets/three_sets.csv -seed 551 -strategy anticorr_sumofrelu_2 -save results/complete_anticorr_sumofrelu2.json

run_nodes_1_10: mk_folder
	./benchmark.sh

run_experim: mk_folder run_mvgaussian run_nodes_1_10
	