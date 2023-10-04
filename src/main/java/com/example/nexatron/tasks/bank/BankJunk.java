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
		ArrayList<Widget> junk = getJunk();
		if (junk.isEmpty())
		{
			return false;
		}
		if (!BankUtil.isOpen())
		{
			return nexManager.nexBank.openBank();
		}
		nexManager.bank(junk);
		return false;

	}

	public ArrayList<Widget> getJunk()
	{
		ArrayList<Integer> requiredItems = nexManager.nexBank.requiredItems();
		ArrayList<Widget> unNecessaryItems = (ArrayList<Widget>) Inventory.search().result();
		if (!nexManager.isPrePotted())
		{
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumable.PREPOT_SCB);
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumable.PREPOT_ANGLER);
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumable.PREPOT_RANGE);
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumable.PREPOT_STAM);
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumable.PREPOT_ANTI);
		}
		else
		{
			unNecessaryItems.removeIf(n -> Consumable.getNecessaryPotions.contains(n.getItemId()));
		}
		unNecessaryItems.removeIf(n -> requiredItems.contains(n.getItemId()));
		return unNecessaryItems;
	}
}
