package com.example.nexatron.tasks.bank;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Inventory;
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
	priority = Integer.MAX_VALUE - 10,
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
		ArrayList<Integer> setup = decideSetup();
		if (nexManager.hasAllItems(requiredItems)
			&& nexManager.hasGearEquipped(setup))
		{
			nexManager.socket.readyToStart = true;
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

		for (int i : requiredItems)
		{
			if (Inventory.getItemAmount(i) > 1
				|| Inventory.getItemAmount(i) >= 1 && Equipment.search().withId(i).result().size() >= 1)
			{
				nexManager.print("Depositing too many of " + itemManager.getItemComposition(i).getName());
				BankUtil.depositAll(i);
				incrementActionCount();
				return true;
			}
		}

		requiredItems.removeAll(InventoryUtil.getAllPlayerItems());
		if (!nexManager.hasGearEquipped(setup)
			&& InventoryUtil.hasItem(setup))
		{
			nexManager.print("Equipping gear");
			setActionCount(getActionCount() + nexManager.swap(setup));
		}

		if (!nexManager.hasAllItems(requiredItems))
		{
			nexManager.print("Withdrawing necessary items");
			setActionCount(getActionCount() + nexManager.withdraw(requiredItems));
			return true;
		}
		return false;
	}

	public ArrayList<Integer> decideSetup()
	{
		return nexManager.socket.isMaster ? nexManager.setup.rangeNex() : nexManager.setup.meleeNex();
	}
}
