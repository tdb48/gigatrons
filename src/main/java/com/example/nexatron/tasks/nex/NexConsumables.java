package com.example.nexatron.tasks.nex;

import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Prayers;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Nex consumables",
	priority = Integer.MAX_VALUE,
	register = true
)
public class NexConsumables extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public NexConsumables(NexManager nexManager)
	{
		super(nexManager,
			Stage.NEX_DEAD,
			Stage.MINION_SMOKE,
			Stage.NEX_SMOKE,
			Stage.MINION_SHADOW,
			Stage.NEX_SHADOW,
			Stage.MINION_BLOOD,
			Stage.NEX_BLOOD,
			Stage.MINION_ICE,
			Stage.NEX_ICE,
			// TODO: come back to zaros consumables
			Stage.NEX_ZAROS);
	}

	public boolean execute()
	{
		if (gameTickManager.isPotionWaiting())
		{
			return false;
		}
		if (nexManager.nex.nex == null
			|| nexManager.nex.nex.getHealthRatio() == 0)
		{
			return false;
		}

		Widget combatPotion = Consumable.getSCB();
		Widget rangePotion = Consumable.getRange();
		Widget healingPotion = Consumable.getBrew();
		Widget prayerRestore = Consumable.getRestore();

		if (Prayers.getPoints() <= 5 && prayerRestore != null)
		{
			nexManager.print("Drinking restore (panic)");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(prayerRestore, "Drink");
			gameTickManager.drinkPotion();
			return true;
		}

		if (nexManager.nex.shouldTripleBrew && healingPotion != null)
		{
			nexManager.print("Drinking brew");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(healingPotion, "Drink");
			gameTickManager.drinkPotion();
			return true;
		}
		if (prayerRestore != null &&
			(Prayers.getPoints() <= 20))
		{
			nexManager.print("Drinking restore for prayer");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(prayerRestore, "Drink");
			gameTickManager.drinkPotion();
			return true;
		}

		if (client.getLocalPlayer().getOverheadText() != null
			&& client.getLocalPlayer().getOverheadText().toLowerCase().contains("cough"))
		{
			return false;
		}

		if (prayerRestore != null &&
			((nexManager.nex.onMeleePhase() && client.getBoostedSkillLevel(Skill.STRENGTH) < client.getRealSkillLevel(Skill.STRENGTH))
				|| (nexManager.nex.onRangedPhase() && client.getBoostedSkillLevel(Skill.RANGED) < client.getRealSkillLevel(Skill.RANGED))))
		{
			nexManager.print("Drinking restore for drain");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(prayerRestore, "Drink");
			gameTickManager.drinkPotion();
			return true;
		}

		if (combatPotion != null
			&& nexManager.nex.onMeleePhase()
			&& client.getBoostedSkillLevel(Skill.STRENGTH) < client.getRealSkillLevel(Skill.STRENGTH) + 10)
		{
			nexManager.print("Drinking scb");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(combatPotion, "Drink");
			gameTickManager.drinkPotion();
			return true;
		}

		if (rangePotion != null
			&& nexManager.nex.onRangedPhase()
			&& client.getBoostedSkillLevel(Skill.RANGED) < client.getRealSkillLevel(Skill.RANGED) + 6)
		{
			nexManager.print("Drinking ranged");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(rangePotion, "Drink");
			gameTickManager.drinkPotion();
			return true;
		}

		return false;
	}
}

