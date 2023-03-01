import torch
import torch.nn.functional as F

from torch import optim
from models.SumOfRelu import SumOfRelu
from utils.loss import ranking_loss_function
from utils.time_decorator import time_it


@time_it
def train_ranking_function(
    loop_heads,
    trace,
    input_before,
    input_after,
    result,
    input_vars,
    learning_rate,
    n_iterations=1000,
    n_summands=5
):

    model = SumOfRelu(
        n_in=len(trace.get_pos_identifiers()),
        n_out=len(loop_heads),
        n_summands=n_summands,
        input_vars=input_vars
    )
    optimizer = optim.AdamW(model.parameters(), lr=learning_rate)
    
    print('=' * 40)
    print('- Learning')
    print('=' * 40)
    
    loss = None
    for iteration in range(n_iterations):
        optimizer.zero_grad()
        
        output_before = []
        output_after = []
        
        for i in range(len(loop_heads)):
            output_before.append(model(input_before[i]))
            output_after.append(model(input_after[i]))
        
        loss = ranking_loss_function(
            n_loop_heads=len(loop_heads),
            out_before=output_before,
            out_after=output_after
        )
        
        # Stop if loss = 0, TODO: should change this into EarlyStopping instead
        if loss == 0.:
            result["iteration"] = iteration
        
        loss.backward()
        optimizer.step()
            
    return model
            
                
    