package com.example.nexatron.tasks.nex;

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
	Consumable consumable;

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
			setActionCount(getActionCount() + consumable.consume(prayerRestore));
			return true;
		}

		if (nexManager.nex.shouldTripleBrew && healingPotion != null)
		{
			if ((prayerRestore != null
				&& nexManager.nex.onMeleePhase()
				&& Consumable.isDrainedMore(Skill.STRENGTH, 29))
				|| prayerRestore != null
				&& nexManager.nex.onRangedPhase()
				&& Consumable.isDrainedMore(Skill.RANGED, 29))
			{
				nexManager.print("Drinking restore for drain in triple brew");
				setActionCount(getActionCount() + consumable.consume(prayerRestore));
			}
			else
			{
				nexManager.print("Drinking brew");
				setActionCount(getActionCount() + consumable.consume(healingPotion));
			}
			return true;
		}

		if (prayerRestore != null &&
			(Prayers.getPoints() <= 20))
		{
			nexManager.print("Drinking restore for prayer");
			setActionCount(getActionCount() + consumable.consume(prayerRestore));
			return true;
		}

		if (client.getLocalPlayer().getOverheadText() != null
			&& client.getLocalPlayer().getOverheadText().toLowerCase().contains("cough"))
		{
			return false;
		}

		if (prayerRestore != null &&
			((nexManager.nex.onMeleePhase() && Consumable.isDrainedMore(Skill.STRENGTH, 3))
				|| (nexManager.nex.onRangedPhase() && Consumable.isDrainedMore(Skill.RANGED, 3))))
		{
			nexManager.print("Drinking restore for drain");
			setActionCount(getActionCount() + consumable.consume(prayerRestore));
			return true;
		}

		if (combatPotion != null
			&& nexManager.nex.onMeleePhase()
			&& client.getBoostedSkillLevel(Skill.STRENGTH) < client.getRealSkillLevel(Skill.STRENGTH) + 10)
		{
			nexManager.print("Drinking scb");
			setActionCount(getActionCount() + consumable.consume(combatPotion));
			return true;
		}

		if (rangePotion != null
			&& nexManager.nex.onRangedPhase()
			&& client.getBoostedSkillLevel(Skill.RANGED) < client.getRealSkillLevel(Skill.RANGED) + 6)
		{
			nexManager.print("Drinking ranged");
			setActionCount(getActionCount() + consumable.consume(rangePotion));
			return true;
		}
		return false;
	}
}

