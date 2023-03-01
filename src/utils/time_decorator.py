import time

def time_it(func):
    """
    A decorator that calculate the running time of a function.
    Decorator can be used as below:
    
    ```
    @time_it
    def example(a, b, c):
        // Some stuff running here
        return result
    ```  
    Note that when time_it is used, the function return 1 more argument.
    
    ```
    result = example(a, b, c) // WARNING
    result, elapsed_time = example(a, b, c) // CORRECT
    ```
    """
    def wrapper(*args, **kwargs):
        start_time = time.time()
        result = func(*args, **kwargs)
        end_time = time.time()
        elapsed_time = end_time - start_time
        print(f"Function '{func.__name__}' took {elapsed_time:.4f} seconds to execute.")
        return result, elapsed_time
    return wrapper
