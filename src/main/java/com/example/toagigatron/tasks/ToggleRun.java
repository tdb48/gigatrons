package com.example.toagigatron.tasks;

import com.example.Utility.Movement;
import com.example.toagigatron.taskformat.Task;
import com.example.toagigatron.taskformat.TaskDescriptor;

@TaskDescriptor(
	priority = 19,
	name = "Updating stage"
)
public class ToggleRun extends Task
{
	public boolean run()
	{
		if (!Movement.isRunEnabled() && Movement.getRunEnergy() >= 1)
		{
			Movement.toggleRun();
			return true;
		}
		return false;
	}
}
