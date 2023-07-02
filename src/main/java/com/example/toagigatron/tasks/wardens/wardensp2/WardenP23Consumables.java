package com.example.toagigatron.tasks.wardens.wardensp2;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Prayers;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Warden P2/3 Consumables",
	priority = 999
)
public class WardenP23Consumables extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public WardenP23Consumables(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P2, Stage.WARDENS_P3);
	}

	@Override
	public boolean execute()
	{
		if (gameTickManager.isPotionWaiting())
		{
			return false;
		}

		NPC core = NPCUtil.findNearest("Core");

		// Don't run if core is out and you are about to attack
		if (core != null && !gameTickManager.isAttackWaiting())
		{
			return false;
		}


		boolean shouldDrinkAdren = core != null ||
				(toaManager.wardens12.warden != null
						&& toaManager.wardens12.warden.getAnimation() == ToaConstants.WARDEN_P2_DROPPING_CORE_ANIMATION);
		Widget healingPotion = Consumables.getBrew();
		Widget prayerRestore = Consumables.getRestore();
		Widget salt = Consumables.getSalt();
		Widget adrenaline = Consumables.getAdrenaline();
		Widget ambrosia = Consumables.getAmbrosia();
		Widget scarab = Consumables.getScarab();

		NPC playerInteracting = toaManager.playerInteractingWith();

		if (salt != null
			&& !toaManager.isSaltActive())
		{
			toaManager.print("Consuming salt");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(salt,"Crush");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion(); //TODO Do we need this? Idk if salt pot blocks you
			return true;
		}
		if (adrenaline != null && shouldDrinkAdren && !toaManager.isAdrenalineActive())
		{
			toaManager.print("Drinking adrenaline");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(adrenaline,"Drink");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}
		// If you have an ambrosia, regularly drink prayer at 10 prayer points, otherwise at 20

		int prayerThreshold = ambrosia != null ? 3 : 20;
		if (ambrosia != null && Prayers.getPoints() <= 5)
		{
			toaManager.print("Drinking ambrosia");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(ambrosia,"Drink");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}
		// TODO: Check whether scarab is active or not
		if (Prayers.getPoints() <= prayerThreshold)
		{
			if (scarab != null && toaManager.consumableTracker.scarabTicks == 0)
			{
				toaManager.print("Using scarab");
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(scarab,"Crack");
				//toaManager.reAttack(playerInteracting);
				gameTickManager.drinkPotion();
				return true;
			}
			else if (prayerRestore != null && toaManager.consumableTracker.scarabTicks < 20)
			{
				toaManager.print("Drinking restore");
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(prayerRestore,"Drink");
				//toaManager.reAttack(playerInteracting);
				gameTickManager.drinkPotion();
				return true;
			}

		}

		if (healingPotion != null
			&& toaManager.isSaltActive()
			&& toaManager.isSaltBrewTick()
			&& Combat.getMissingHealth() > 5)
		{
			toaManager.print("Drinking brew on GOD tick");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(healingPotion,"Drink");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if (healingPotion != null
			&& Combat.getCurrentHealth() < 60)
		{
			toaManager.print("Panic brew");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(healingPotion,"Drink");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}
		return false;
	}
}
