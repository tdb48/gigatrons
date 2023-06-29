package com.example.toagigatron.tasks.baba;

import com.example.InteractionApi.InventoryInteraction;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Combat;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Movement;
import com.example.Utility.Prayers;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Baba puzzle consumables",
	priority = 999
)
public class BabaConsumables extends StagedTask
{
	@Inject
	public BabaConsumables(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE, Stage.BABA_BOSS);
	}

	@Inject
	GameTickManager gameTickManager;

	public boolean execute()
	{
		if (gameTickManager.isPotionWaiting())
		{
			return false;
		}
		if ((toaManager.baba.babaBossRoom.contains(client.getLocalPlayer().getWorldLocation()) && toaManager.baba.babaBoss == null)
			|| (toaManager.baba.babaBoss != null && toaManager.baba.babaBoss.getHealthRatio() == 0))
		{
			return false;
		}
		//Prayer
//		int[] potentialRestore = Consumables.RESTORE.stream().mapToInt(i -> i).toArray();
//		Item prayerRestore = Inventory.getFirst(potentialRestore);

//		//Combat
		int[] potentialCombat = Consumables.COMBAT.stream().mapToInt(i -> i).toArray();
		Widget combatPotion = InventoryUtil.getFirst(potentialCombat);

		// Poison
		int[] potentialAnti = Consumables.ANTI.stream().mapToInt(i -> i).toArray();
		Widget poisonPotion = InventoryUtil.getFirst(potentialAnti);

		//Healing
//		int[] potentialBrew = Consumables.BREW.stream().mapToInt(i -> i).toArray();
//		Item healingPotion = Inventory.getFirst(potentialBrew);

		//Stamina
		int[] potentialStamina = Consumables.STAM.stream().mapToInt(i -> i).toArray();
		Widget staminaPotion = InventoryUtil.getFirst(potentialStamina);

		Widget healingPotion = Consumables.getBrew();
		Widget prayerRestore = Consumables.getRestore();

		NPC playerInteracting = toaManager.playerInteractingWith();


		if (Prayers.getPoints() == 0 && prayerRestore != null)
		{
			toaManager.print("Drinking restore");
			InventoryInteraction.useItem(prayerRestore, "Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if (toaManager.baba.shouldTripleBrew && healingPotion != null)
		{
			toaManager.print("Drinking brew");
			InventoryInteraction.useItem(healingPotion, "Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if ((Prayers.getPoints() <= 5 || client.getBoostedSkillLevel(Skill.STRENGTH) < client.getRealSkillLevel(Skill.STRENGTH)) && prayerRestore != null)
		{
			toaManager.print("Drinking restore");
			InventoryInteraction.useItem(prayerRestore, "Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if (Combat.isVenomed() && poisonPotion != null)
		{
			toaManager.print("Drinking anti");
			InventoryInteraction.useItem(poisonPotion, "Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}
		if (Movement.getRunEnergy() <= 5 && staminaPotion != null)
		{
			toaManager.print("Drinking stamina");
			InventoryInteraction.useItem(staminaPotion, "Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}
//		// Don't drink scb 1 dose - save for kephri
		if (toaManager.getStage().equals(Stage.BABA_BOSS) && client.getBoostedSkillLevel(Skill.STRENGTH) < (client.getRealSkillLevel(Skill.STRENGTH) + 13) && combatPotion != null && combatPotion.getId() != ItemID.SUPER_COMBAT_POTION1)
		{
			toaManager.print("Drinking scb");
			InventoryInteraction.useItem(combatPotion, "Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		return false;
	}
}

