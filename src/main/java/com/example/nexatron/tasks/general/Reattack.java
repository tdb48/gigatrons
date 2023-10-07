package com.example.nexatron.tasks.general;


import com.example.Utility.Prayers;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.Task;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Reattack",
	priority = -Integer.MAX_VALUE
)
public class Reattack extends Task
{
	@Inject
	NexManager nexManager;

	public boolean run()
	{
		if (nexManager.shouldReattack)
		{
//			nexManager.reattackInteracting();
		}
		nexManager.shouldReattack = false;
		return false;
	}
}
