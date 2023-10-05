package com.example.nexatron.tasks.bank;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.Utility.BankUtil;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import com.example.toagigatron.model.constants.Consumables;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;

@TaskDescriptor(
	name = "Bank junk",
	priority = Integer.MAX_VALUE,
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
			return nexManager.nexBank.openBank();
		}
		nexManager.bank(junk);
		return true;

	}
}
