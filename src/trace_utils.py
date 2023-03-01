import tempfile

from tracing.trace_handle import Trace
from tracing.utils import run_tracing

from utils.time_decorator import time_it

@time_it
def tracing(
    jar_file,
    class_name,
    method_name,
    samples,
    limit,
    loop_offset,
    tracing_seed,
    sampling_strategy="default",
    n_processes=1
):
    
    # Tracing
    print('=' * 40)
    print('- Tracing')
    print('=' * 40)
    
    f = tempfile.NamedTemporaryFile()
    print("Traces are being written to {}".format(f.name))
    
    run_tracing(jar_file, class_name, method_name, f.name, samples, loop_offset, seed=tracing_seed,
                tracelimit=limit, sampling_strategy=sampling_strategy, num_processes=n_processes)
    
    # Reading trace
    trace = Trace(f.name)
    return trace