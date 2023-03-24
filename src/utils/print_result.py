import numpy as np


def print_result(parameters, input_vars, layer_name, var_dependent=True):
    string_res = []
    for i, x in enumerate(parameters):
        out_string = f"{layer_name} - Neuron {i+1}: ReLU( "
        if var_dependent:
            for var, num in zip(input_vars, x):
                out_string += (str(np.round(num, 4)) if num < 0 else "+" + str(np.round(num, 4))) + "*" + str(var) + " "
            out_string += ")"
            print(out_string)
            string_res.append(out_string)
        else:
            for k, num in enumerate(x):
                out_string += (str(np.round(num, 4)) if num < 0 else "+" + str(np.round(num, 4))) + "*" + f"n{k+1}"
            out_string += ")"
            print(out_string)
            string_res.append(out_string)
    return string_res