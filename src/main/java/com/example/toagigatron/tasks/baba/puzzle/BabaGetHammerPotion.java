package com.example.toagigatron.tasks.baba.puzzle;

import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.GameObject;
import net.runelite.api.Item;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.queries.GameObjectQuery;
import net.unethicalite.api.items.Inventory;

import javax.inject.Inject;
import java.util.ArrayList;

@TaskDescriptor(
		name = "Baba pickup hammer and potion",
		blocking = true
)
public class BabaGetHammerPotion extends StagedTask
{
	@Inject
	public BabaGetHammerPotion(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE);
	}

	public boolean execute()
	{
		GameObject exit = new GameObjectQuery().idEquals(ToaConstants.BABA_PUZZLE_EXIT).result(client).first();
		if (!toaManager.baba.isPuzzleActive() || exit == null)
		{
			return false;
		}
		if (Inventory.contains("Hammer") && Inventory.contains("Neutralising potion"))
		{
			int offhand = toaManager.meleeSetup.offhand;
			if (Inventory.isFull() && !Inventory.contains(offhand))
			{
				ArrayList<Item> restores = (ArrayList<Item>) Inventory.getAll("Super restore(4)");
				if (!restores.isEmpty())
				{
					restores.get(0).interact("Drop");
					toaManager.print("Dropping restore because inv stuck somehow");
					return true;
				}
			}
			else
			{
				return false;
			}
		}
		GameObject hammers = new GameObjectQuery().idEquals(ToaConstants.BABA_CRATE_HAMMERS).result(client).nearestTo(exit);
		GameObject potions = new GameObjectQuery().idEquals(ToaConstants.BABA_CRATE_POTIONS).result(client).nearestTo(exit);

		if (hammers == null || potions == null)
		{
			return false;
		}
		if (!toaManager.hasGearEquipped(toaManager.mageSetup.getAllItems()))
		{
			toaManager.swap(toaManager.mageSetup.getAllItems());
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		// Drop 2 brews to get a hammer
		if (!Inventory.contains("Hammer"))
		{
			if (playerPoint.distanceTo(hammers) > 6 || Inventory.getFreeSlots() >= 2)
			{
				hammers.interact("Take");
				toaManager.print("Taking hammers");
				return true;
			}
			ArrayList<Item> restores = (ArrayList<Item>) Inventory.getAll("Super restore(4)");
			if (!restores.isEmpty())
			{
				restores.get(0).interact("Drop");
				toaManager.print("Dropping restores for hammer");
				return true;
			}
		}
		// Drop 1 restore to get a potion
		else if (!Inventory.contains("Neutralising potion"))
		{
			if (playerPoint.distanceTo(potions) > 6 || Inventory.getFreeSlots() >= 2)
			{
				potions.interact("Take");
				toaManager.print("Taking potions");
				return true;
			}
			ArrayList<Item> restores = (ArrayList<Item>) Inventory.getAll("Super restore(4)");
			if (!restores.isEmpty())
			{
				restores.get(0).interact("Drop");
				toaManager.print("Dropping restores for neutral pot");
				return true;
			}
		}

		return false;
	}
}