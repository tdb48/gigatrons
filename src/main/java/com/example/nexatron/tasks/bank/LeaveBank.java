package com.example.nexatron.tasks.bank;


import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Leave bank",
	priority = 1
)
public class LeaveBank extends StagedTask
{
	public static final WorldPoint BANK_TILE = new WorldPoint(2898, 5203, 0);

	@Inject
	public LeaveBank(NexManager nexManager)
	{
		super(nexManager, Stage.BANK);
	}

	public boolean execute()
	{
		// TODO
		return false;
	}

}
