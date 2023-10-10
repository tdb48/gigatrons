package com.example.nexatron.tasks.kcArea;


import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Combat;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "KC Consumables",
	priority = Integer.MAX_VALUE
)
public class KcConsumables extends StagedTask
{
	@Inject
	public KcConsumables(NexManager nexManager)
	{
		super(nexManager, Stage.KC_AREA);
	}

	public boolean execute()
	{
		if (!nexManager.shouldKc())
		{

			return false;
		}
		Widget restore = Consumable.getRestore();
		if (Consumable.isDrainedMore(Skill.PRAYER, 32)
			&& restore != null)
		{
			nexManager.print("Drinking restore pot");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(restore, "Drink");
			return true;
		}

		Widget rangePot = Consumable.getRange();
		if (client.getBoostedSkillLevel(Skill.RANGED) < (client.getRealSkillLevel(Skill.RANGED) + 6)
			&& rangePot != null)
		{
			nexManager.print("Drinking range pot");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(rangePot, "Drink");
			return true;
		}
		Widget anti = Consumable.getAnti();
		if (Combat.isPoisoned()
			&& anti != null)
		{
			nexManager.print("Drinking anti pot");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(anti, "Drink");
			return true;
		}

		return false;
	}
}
