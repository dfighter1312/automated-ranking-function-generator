
function samples() {
	for i in 5 10 20 30 50 70 80 100 200 300 500 700 900 1000 2000 5000; do
		${PYTHON3} run_benchmark.py -set complete_best.bm -seed 42 -strategy anticorr_sumofrelu2 -save increase_samples.json -samples $i
	done
}

function nodes() {
	for i in 1 2 3 4 5 6 7 8 9 10; do
		${PYTHON3} run_benchmark.py -set ./problem-sets/three_sets.csv -seed 54 -strategy anticorr_sumofrelu_dynamic_nodes -stratargs nodes=$i -save results/complete_anticorr_sumofrelu2_dyn_nodes_$i.json
		${PYTHON3} run_benchmark.py -set ./problem-sets/three_sets.csv -seed 154 -strategy anticorr_sumofrelu_dynamic_nodes -stratargs nodes=$i -save results/complete_anticorr_sumofrelu2_dyn_nodes_$i.json
		${PYTHON3} run_benchmark.py -set ./problem-sets/three_sets.csv -seed 254 -strategy anticorr_sumofrelu_dynamic_nodes -stratargs nodes=$i -save results/complete_anticorr_sumofrelu2_dyn_nodes_$i.json
		${PYTHON3} run_benchmark.py -set ./problem-sets/three_sets.csv -seed 354 -strategy anticorr_sumofrelu_dynamic_nodes -stratargs nodes=$i -save results/complete_anticorr_sumofrelu2_dyn_nodes_$i.json
		${PYTHON3} run_benchmark.py -set ./problem-sets/three_sets.csv -seed 454 -strategy anticorr_sumofrelu_dynamic_nodes -stratargs nodes=$i -save results/complete_anticorr_sumofrelu2_dyn_nodes_$i.json
	done
}

nodes