package com.example.nexatron.tasks.general;

import com.example.Utility.Movement;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.Task;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;

@TaskDescriptor(
	priority = 19,
	name = "Updating stage"
)
public class ToggleRun extends Task
{
	@Inject
	NexManager nexManager;

	public boolean run()
	{
		if (nexManager.getStage().equals(Stage.MINION_SMOKE)
			|| nexManager.getStage().equals(Stage.MINION_SHADOW))
		{
			return false;
		}
		if (!Movement.isRunEnabled() && Movement.getRunEnergy() >= 1)
		{
			Movement.toggleRun();
			incrementActionCount();
			return true;
		}
		return false;
	}
}
