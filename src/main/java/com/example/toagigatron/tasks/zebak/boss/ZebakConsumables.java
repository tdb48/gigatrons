package com.example.toagigatron.tasks.zebak.boss;

import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Combat;
import com.example.Utility.Prayers;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Zebak consumables",
	priority = 999
)
public class ZebakConsumables extends StagedTask
{
	@Inject
	public ZebakConsumables(ToaManager toaManager)
	{
		super(toaManager, Stage.ZEBAK_BOSS);
	}

	@Inject
	GameTickManager gameTickManager;

	public boolean execute()
	{
		if (gameTickManager.isPotionWaiting()
			|| toaManager.zebak.zebakBoss == null
			|| toaManager.zebak.zebakBoss.getHealthRatio() == 0)
		{
			return false;
		}

		Widget healingPotion = Consumables.getBrew();
		Widget prayerRestore = Consumables.getRestore();
		Widget salt = Consumables.getSalt();

		NPC playerInteracting = toaManager.playerInteractingWith();

		if (salt != null
			&& !toaManager.isSaltActive())
		{
			toaManager.print("Consuming salt");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(salt, "Crush");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		// panic brew
		if (healingPotion != null
			&& Combat.getMissingHealth() >= 70)
		{
			toaManager.print("Panic brew");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(healingPotion, "Drink");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if (healingPotion != null
			&& toaManager.isSaltBrewTick()
			&& toaManager.isSaltActive()
			&& Combat.getMissingHealth() >= 50)
		{
			toaManager.print("Drinking brew on brew tick");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(healingPotion, "Drink");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if (prayerRestore != null && Prayers.getPoints() <= 5)
		{
			toaManager.print("Drinking restore");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(prayerRestore, "Drink");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}
		return false;
	}
}

