package com.example.toagigatron.tasks.akkha.boss;

import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Combat;
import com.example.Utility.Prayers;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Akkha consumables",
	priority = 999
)
public class AkkhaConsumables extends StagedTask
{
	@Inject
	public AkkhaConsumables(ToaManager toaManager)
	{
		super(toaManager,
			Stage.AKKHA_BOSS,
			Stage.AKKHA_PUZZLE);
	}

	@Inject
	GameTickManager gameTickManager;

	public boolean execute()
	{
		if (gameTickManager.isPotionWaiting()
			|| (toaManager.akkha.akkhaBoss != null
			&& toaManager.akkha.akkhaBoss.getHealthRatio() == 0))
		{
			return false;
		}

		Widget healingPotion = Consumables.getBrew();
		Widget prayerRestore = Consumables.getRestore();
		Widget salt = Consumables.getSalt();

		NPC playerInteracting = toaManager.playerInteractingWith();

		if (toaManager.getStage() == Stage.AKKHA_BOSS
			&& salt != null
			&& !toaManager.isSaltActive())
		{
			toaManager.print("Consuming salt");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(salt, "Crush");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		// panic restore
		if (prayerRestore != null && Prayers.getPoints() == 0)
		{
			toaManager.print("Drinking restore");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(prayerRestore, "Drink");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}
		// panic brew
		if (healingPotion != null
			&& Combat.getMissingHealth() >= 60)
		{
			toaManager.print("Panic brew");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(healingPotion, "Drink");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		if (healingPotion != null
			&& toaManager.isSaltActive()
			&& toaManager.isSaltBrewTick()
			&& Combat.getMissingHealth() >= 40)
		{
			toaManager.print("Drinking brew on brew tick");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(healingPotion, "Drink");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}

		int restorePrayerAt = 15;
		if (toaManager.akkha.akkhaBoss != null && toaManager.akkha.akkhaBoss.getId() == ToaConstants.FINAL_AKKHA)
		{
			restorePrayerAt = 3;
		}
		if (prayerRestore != null && Prayers.getPoints() <= restorePrayerAt)
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