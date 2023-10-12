package com.example.nexatron.tasks.bank;

import com.example.Utility.BankUtil;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Bank junk",
	priority = Integer.MAX_VALUE - 2,
	blocking = true
)
public class BankJunk extends StagedTask
{
	@Inject
	public BankJunk(NexManager nexManager)
	{
		super(nexManager, Stage.BANK);
	}

	public boolean execute()
	{
		ArrayList<Widget> junk = nexManager.getJunk();
		if (nexManager.hasGearEquipped(nexManager.setup.rangeKc())
			&& !nexManager.shouldKc())
		{
			nexManager.print("Banking all junk");
			BankUtil.depositInventory();
			BankUtil.depositInventory();
			incrementActionCount();
			incrementActionCount();
			return true;
		}
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
