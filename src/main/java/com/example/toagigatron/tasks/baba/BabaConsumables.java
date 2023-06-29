package com.example.toagigatron.tasks.baba;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.Utility.Prayers;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import net.runelite.api.Item;
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
		Item combatPotion = Inventory.getFirst(potentialCombat);

		// Poison
		int[] potentialAnti = Consumables.ANTI.stream().mapToInt(i -> i).toArray();
		Item poisonPotion = Inventory.getFirst(potentialAnti);

		//Healing
//		int[] potentialBrew = Consumables.BREW.stream().mapToInt(i -> i).toArray();
//		Item healingPotion = Inventory.getFirst(potentialBrew);

		//Stamina
		int[] potentialStamina = Consumables.STAM.stream().mapToInt(i -> i).toArray();
		Item staminaPotion = Inventory.getFirst(potentialStamina);

		Widget healingPotion = Consumables.getBrew();
		Widget prayerRestore = Consumables.getRestore();

		NPC playerInteracting = toaManager.playerInteractingWith();


		if (Prayers.getPoints() == 0 && prayerRestore != null)
		{
			toaManager.print("Drinking restore");
			prayerRestore.interact("Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if (toaManager.baba.shouldTripleBrew && healingPotion != null)
		{
			toaManager.print("Drinking brew");
			healingPotion.interact("Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if ((Prayers.getPoints() <= 5 || client.getBoostedSkillLevel(Skill.STRENGTH) < client.getRealSkillLevel(Skill.STRENGTH)) && prayerRestore != null)
		{
			toaManager.print("Drinking restore");
			prayerRestore.interact("Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if (Combat.isVenomed() && poisonPotion != null)
		{
			toaManager.print("Drinking anti");
			poisonPotion.interact("Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if (Movement.getRunEnergy() <= 5 && staminaPotion != null)
		{
			toaManager.print("Drinking stamina");
			staminaPotion.interact("Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}
//		// Don't drink scb 1 dose - save for kephri
		if (toaManager.getStage().equals(Stage.BABA_BOSS) && client.getBoostedSkillLevel(Skill.STRENGTH) < (client.getRealSkillLevel(Skill.STRENGTH) + 13) && combatPotion != null && combatPotion.getId() != ItemID.SUPER_COMBAT_POTION1)
		{
			toaManager.print("Drinking scb");
			combatPotion.interact("Drink");
			toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		return false;
	}
}

