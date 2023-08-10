package com.example.nexatron.tasks.kcArea;


import com.example.Utility.Prayer;
import com.example.Utility.Prayers;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.NPC;

@TaskDescriptor(
	name = "Enter bank",
	priority = 1
)
public class EnterBank extends StagedTask
{
	@Inject
	public EnterBank(NexManager nexManager)
	{
		super(nexManager, Stage.KC_AREA);
	}

	public boolean execute()
	{

		return false;
	}

}
