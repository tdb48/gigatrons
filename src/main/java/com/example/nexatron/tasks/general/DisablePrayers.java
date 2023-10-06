package com.example.nexatron.tasks.general;


import com.example.Utility.Prayers;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Disable prayers",
	priority = Integer.MAX_VALUE
)
public class DisablePrayers extends StagedTask
{
	@Inject
	public DisablePrayers(NexManager nexManager)
	{
		super(nexManager,
			Stage.LOBBY,
			Stage.BANK,
			Stage.NEX_DEAD,
			Stage.NONE);
	}

	public boolean execute()
	{
		if (Prayers.anyActive())
		{
			Prayers.disableAll();
			return true;
		}
		return false;
	}
}
