import torch

import torch.nn.functional as F


def ranking_loss_function(n_loop_heads, out_before, out_after):
    """Ranking loss function

    Args:
        n_loop_heads (int): Number of loop headers
        out_before (torch.Tensor): Ranking value from previous iterations
        out_after (torch.Tensor): Ranking value from next iterations (elemet-wise)

    Returns:
        loss: Loss value
    """
    loss = torch.tensor([])
    for i in range(n_loop_heads):
        for j in range(0, i):
            loss = torch.cat((loss, F.relu(out_after[j] - out_before[j])[:, j]), 0)
        loss = torch.cat((loss, F.relu(out_after[i] - out_before[i] + 1)[:, i]), 0)
    loss = loss.mean()
    return loss
    