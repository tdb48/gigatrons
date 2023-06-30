package com.example.toagigatron.tasks;



import com.example.Utility.Prayers;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;

import javax.inject.Inject;

@TaskDescriptor(
	name = "Disable prayers",
	priority = 80
)
public class DisablePrayers extends StagedTask
{
	@Inject
	public DisablePrayers(ToaManager toaManager)
	{
		super(toaManager, Stage.INSIDE, Stage.OUTSIDE);
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
