package com.example.toagigatron.tasks.kephri.boss;

import com.example.Packets.*;
import com.example.Utility.*;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Kephri consumables",
	priority = 999
)
public class KephriConsumables extends StagedTask
{
	@Inject
	public KephriConsumables(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_BOSS);
	}

	@Inject
	GameTickManager gameTickManager;

	private int getDoseCount(int itemID)
	{
		if (itemID == ItemID.SUPER_COMBAT_POTION1)
		{
			return 1;
		}
		if (itemID == ItemID.SUPER_COMBAT_POTION2)
		{
			return 2;
		}
		if (itemID == ItemID.SUPER_COMBAT_POTION3)
		{
			return 3;
		}
		if (itemID == ItemID.SUPER_COMBAT_POTION4)
		{
			return 4;
		}
		return -1;
	}

	public boolean execute()
	{
		if (gameTickManager.isPotionWaiting()
			|| toaManager.kephri.kephri == null
			|| toaManager.kephri.kephri.getHealthRatio() == 0)
		{
			return false;
		}

		NPC playerInteracting = toaManager.playerInteractingWith();

		//Combat
		int[] potentialCombat = Consumables.COMBAT.stream().mapToInt(i -> i).toArray();
		Widget combatPotion = InventoryUtil.getFirst(potentialCombat);
		int scbDoseCount = combatPotion != null ? getDoseCount(combatPotion.getId()) : -1;

		// Poison
		int[] potentialAnti = Consumables.ANTI.stream().mapToInt(i -> i).toArray();
		Widget poisonPotion = InventoryUtil.getFirst(potentialAnti);

		//Stamina
		int[] potentialStamina = Consumables.STAM.stream().mapToInt(i -> i).toArray();
		Widget staminaPotion = InventoryUtil.getFirst(potentialStamina);

		Widget healingPotion = Consumables.getBrew();
		Widget sanfewPotion = Consumables.getSanfew();
		Widget restorePotion = Consumables.getRestore();

		if (Prayers.getPoints() <= 50 && sanfewPotion != null)
		{
			toaManager.print("Drinking sanfew");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(sanfewPotion, "Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}
		if ((Prayers.getPoints() <= 5 || client.getBoostedSkillLevel(Skill.STRENGTH) < client.getRealSkillLevel(Skill.STRENGTH)) && restorePotion != null)
		{
			toaManager.print("Drinking restore");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(restorePotion, "Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if (Combat.isPoisoned() && poisonPotion != null)
		{
			toaManager.print("Drinking anti");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(poisonPotion, "Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if (client.getBoostedSkillLevel(Skill.HITPOINTS) <= 40 && healingPotion != null)
		{
			toaManager.print("Drinking brew");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(healingPotion, "Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if (Movement.getRunEnergy() <= 10 && staminaPotion != null)
		{
			toaManager.print("Drinking brew");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(staminaPotion, "Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if (client.getBoostedSkillLevel(Skill.STRENGTH) <= (client.getRealSkillLevel(Skill.STRENGTH) + determineDrinkAtLevel(scbDoseCount)) && combatPotion != null)
		{
			toaManager.print("Drinking scb");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(combatPotion, "Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		return false;
	}

	private int determineDrinkAtLevel(int doseCount)
	{
		int returnVal = 14;
		if (doseCount == 4)
		{
			returnVal = 17;
		}
		else if (doseCount == 3)
		{
			returnVal = 16;
		}
		else if (doseCount == 2)
		{
			returnVal = 15;
		}
		return returnVal;
	}
}
