package com.example.nexatron.tasks.bank;

import com.example.Utility.BankUtil;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;

@TaskDescriptor(
	name = "Bank junk",
	priority = Integer.MAX_VALUE - 2,
	blocking = true
)
public class BankJunk extends StagedTask
{
	@Inject
	ItemManager itemManager;

	@Inject
	public BankJunk(NexManager nexManager)
	{
		super(nexManager, Stage.BANK);
	}

	public boolean execute()
	{
		ArrayList<Widget> junk = nexManager.getJunk();
		if (junk.isEmpty())
		{
			return false;
		}
		if (!BankUtil.isOpen())
		{
			if (nexManager.nexBank.openBank() == 0)
			{
				return false;
			}
			incrementActionCount();
			return true;
		}
		setActionCount(nexManager.bank(junk));
		return true;

	}
}
