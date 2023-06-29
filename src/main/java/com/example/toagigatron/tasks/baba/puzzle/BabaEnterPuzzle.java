package com.example.toagigatron.tasks.baba.puzzle;

import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.GameObject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.queries.GameObjectQuery;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Reachable;

import javax.inject.Inject;

@TaskDescriptor(
	name = "Baba enter puzzle",
	priority = 1
)
public class BabaEnterPuzzle extends StagedTask
{

	@Inject
	public BabaEnterPuzzle(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE);
	}

	public boolean execute()
	{
		if (toaManager.baba.isPuzzleActive())
		{
			return false;
		}
		//Combat
		int[] potentialCombat = Consumables.COMBAT.stream().mapToInt(i -> i).toArray();
		Item combatPotion = Inventory.getFirst(potentialCombat);
		if (client.getBoostedSkillLevel(Skill.STRENGTH) < (client.getRealSkillLevel(Skill.STRENGTH) + 13) && combatPotion != null && combatPotion.getId() != ItemID.SUPER_COMBAT_POTION1)
		{
			toaManager.print("Drinking scb");
			combatPotion.interact("Drink");
			return true;
		}

		GameObject exit = new GameObjectQuery().idEquals(ToaConstants.BABA_PUZZLE_EXIT).result(client).first();
		if (exit != null && Reachable.isInteractable(exit))
		{
			return false;
		}
		GameObject entry = new GameObjectQuery().idEquals(ToaConstants.BARRIER).result(client).first();
		if (entry != null)
		{
			toaManager.print("Entering puzzle ");
			entry.interact("Quick-Pass");
			return true;
		}

		return false;
	}
}
