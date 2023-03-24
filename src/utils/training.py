import torch
import torch.nn.functional as F

from torch import optim
from models.SumOfRelu import SumOfRelu
from utils.loss import ranking_loss_function
from utils.time_decorator import time_it


@time_it
def train_ranking_function(
    model,
    loop_heads,
    trace,
    input_before,
    input_after,
    result,
    input_vars,
    learning_rate,
    loss_function: callable,
    n_iterations=1000,
    n_summands=5
):

    optimizer = optim.AdamW(model.parameters(), lr=learning_rate)
    
    print('=' * 100)
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
        
        loss = loss_function(
            n_loop_heads=len(loop_heads),
            out_before=output_before,
            out_after=output_after
        )
    
        # Stop if loss = 0
        if loss == 0.:
            break
            
        loss.backward()
        optimizer.step()
    
    result["n_iterations"] += iteration + 1        
    return model

@time_it
def train_ranking_function_smrl2(
    model,
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

    optimizer = optim.AdamW(model.parameters(), lr=learning_rate)
    
    print('=' * 100)
    print('- Learning')
    print('=' * 40)
    
    loss = None
    for iteration in range(n_iterations):
        optimizer.zero_grad()
        
        loss = torch.tensor([])
        for i in range(len(loop_heads)):
            output_before = model(input_before[i])
            output_after = model(input_after[i])
            loss = torch.cat((loss, F.relu(output_after[i] - output_before[i] + 1)), 0)
            for j in range(0, i):
                loss = torch.cat((loss, F.relu(output_after[j] - output_before[j])), 0)

        loss = loss.mean()

        # Stop if loss = 0
        if loss == 0.:
            break
            
        loss.backward()
        optimizer.step()
    
    result["n_iterations"] += iteration + 1        
    return model