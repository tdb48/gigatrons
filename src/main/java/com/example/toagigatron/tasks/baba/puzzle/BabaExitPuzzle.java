package com.example.toagigatron.tasks.baba.puzzle;

import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.GameObject;
import net.runelite.api.TileItem;
import net.runelite.api.queries.GameObjectQuery;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Reachable;

import javax.inject.Inject;
import java.util.ArrayList;

@TaskDescriptor(
		name = "Baba exit puzzle",
		priority = 1
)
public class BabaExitPuzzle extends StagedTask
{
	@Inject
	public BabaExitPuzzle(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE);
	}

	public boolean execute()
	{
		if (toaManager.baba.isPuzzleActive())
		{
			return false;
		}
		if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItems()))
		{
			toaManager.print("Switching to melee gear");
			toaManager.swap(toaManager.meleeSetup.getAllItems());
		}
		GameObject exit = new GameObjectQuery().idEquals(ToaConstants.BABA_PUZZLE_EXIT).result(client).first();
		ArrayList<TileItem> tileItems = (ArrayList<TileItem>) TileItems.getAll("Saradomin brew(4)", "Super restore(4)");
		if (!tileItems.isEmpty() && !Inventory.isFull())
		{
			tileItems.get(0).interact("Take");
			return true;
		}
		else if (exit != null && Reachable.isInteractable(exit))
		{
			toaManager.print("Exiting baba puzzle");
			exit.interact("Quick-Enter");
			return true;
		}

		return false;
	}
}