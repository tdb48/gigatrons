package com.example.toagigatron.tasks.baba.puzzle;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Game;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Reachable;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;

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
			System.out.println("here");
			return false;
		}
		//Combat
		int[] potentialCombat = Consumables.COMBAT.stream().mapToInt(i -> i).toArray();

//		Widget combatPotion = InventoryUtil.getFirst(potentialCombat);
//		if (client.getBoostedSkillLevel(Skill.STRENGTH) < (client.getRealSkillLevel(Skill.STRENGTH) + 13) && combatPotion != null && combatPotion.getId() != ItemID.SUPER_COMBAT_POTION1)
//		{
//			toaManager.print("Drinking scb");
//			combatPotion.interact("Drink");
//			return true;
//		}

		TileObject exit = TileObjects.search().withId(ToaConstants.BABA_PUZZLE_EXIT).first().orElse(null);
		if (exit != null && Reachable.isWalkable(exit.getWorldLocation().dx(-1)))
		{
			System.out.println("Returning false in exit");
			return false;
		}
		System.out.println("here");
		TileObject entry = TileObjects.search().withId(ToaConstants.BARRIER).first().orElse(null);
		if (entry != null)
		{
			toaManager.print("Entering puzzle ");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(entry, false, "Quick-Pass");
			return true;
		}

		return false;
	}
}
