package com.example.nexatron.tasks.bank;

import com.example.Utility.BankUtil;
import com.example.Utility.InventoryUtil;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.client.game.ItemManager;

@TaskDescriptor(
	name = "Get required items",
	priority = Integer.MAX_VALUE,
	blocking = true
)
public class GetRequiredItems extends StagedTask
{
	@Inject
	ItemManager itemManager;

	@Inject
	public GetRequiredItems(NexManager nexManager)
	{
		super(nexManager, Stage.BANK);
	}

	public boolean execute()
	{
		ArrayList<Integer> requiredItems = nexManager.nexBank.requiredItems();
		if (nexManager.hasAllItems(requiredItems))
		{
			nexManager.print("We have all required items");
			return false;
		}
		if (!BankUtil.isOpen())
		{
			return nexManager.nexBank.openBank();
		}
		ArrayList<Integer> setup = decideSetup();
		requiredItems.removeAll(InventoryUtil.getAllPlayerItems());
		if (!nexManager.hasGearEquipped(setup)
			&& InventoryUtil.hasItem(setup))
		{
			nexManager.print("Equipping gear");
			nexManager.swap(setup);
		}

		if (!nexManager.hasAllItems(requiredItems))
		{
			nexManager.print("Withdrawing necessary items");
			nexManager.withdraw(requiredItems);
			return true;
		}
		return false;
	}

	public ArrayList<Integer> decideSetup()
	{
		return nexManager.socket.isMaster ? nexManager.setup.rangeNex() : nexManager.setup.meleeNex();
	}
}
