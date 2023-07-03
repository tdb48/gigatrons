package com.example.toagigatron.tasks.wardens.wardensp1;

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
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Warden P1 Consumables",
	priority = 999
)
public class WardenP1Consumables extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public WardenP1Consumables(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P1);
	}

	@Override
	public boolean execute()
	{
		//these change on raid level, this was at level 315 we need to confirm it works at 345
		int ballOneMax = 40; //42 at 345
		int ballTwoMax = 27; //28 at 345

		//we need some logic for when we should brew to maximize dps but keep us alive in p1
		//need to know max hit of orb 1 and orb 2
		//if max hp or less and our stats are boosted and we on salt tick, drink brew
		//if max hp or less, but more hp than needed to survive the next ball, and our stats are brewed down, and on salt tick, sip restore
		//if we dont have enough hp to survive incoming ball, sip brew
		//something like this.
		//we could potentially use redemption to redemp the second ball but probably not worth it

		if (gameTickManager.isPotionWaiting()
			|| toaManager.wardens12.obelisk == null
			|| toaManager.wardens12.obelisk.getHealthRatio() == 0
			|| !toaManager.wardens12.bagOpened)
		{
			return false;
		}
		Widget healingPotion = Consumables.getBrew();
		Widget prayerRestore = Consumables.getRestore();
		Widget salt = Consumables.getSalt();
		Widget ambrosia = Consumables.getAmbrosia();
		Widget adrenaline = Consumables.getAdrenaline();

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
		if (adrenaline != null && Combat.getSpecEnergy() == 100 && !toaManager.isAdrenalineActive())
		{
			toaManager.print("Drinking adrenaline");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(adrenaline, "Drink");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}
		if (prayerRestore != null && Prayers.getPoints() <= 15)
		{
			toaManager.print("Drinking restore");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(prayerRestore, "Drink");
			//toaManager.reAttack(playerInteracting);
			gameTickManager.drinkPotion();
			return true;
		}
		if (ambrosia != null)
		{
			if ((Combat.getMissingHealth() >= 50 && toaManager.wardens12.ballTick > 0 && toaManager.wardens12.ballTick < 5)
				|| Combat.getCurrentHealth() < 30)
			{
				toaManager.print("Drinking ambrosia");
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(ambrosia, "Drink");
				//toaManager.reAttack(playerInteracting);
				return true;
			}

		}
//		else if (healingPotion != null && toaManager.wardens12.ballTick <= 0 && Combat.getCurrentHealth() <= 30)
//		{
//			toaManager.print("Drinking brew on GOD tick");
//			healingPotion.interact("Drink");
//			gameTickManager.drinkPotion();
//			return true;
//		}
		else
		{
			if (healingPotion != null
				&& toaManager.isSaltActive()
				&& toaManager.isSaltBrewTick()
				&& Combat.getMissingHealth() > 5)
			{
				toaManager.print("Drinking brew on GOD tick");
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(healingPotion, "Drink");
				//toaManager.reAttack(playerInteracting);
				gameTickManager.drinkPotion();
				return true;
			}
			if (healingPotion != null
				&& Combat.getCurrentHealth() < 55)
			{
				toaManager.print("Panic brew");
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(healingPotion, "Drink");
				//toaManager.reAttack(playerInteracting);
				gameTickManager.drinkPotion();
				return true;
			}
		}
		return false;
	}
}
