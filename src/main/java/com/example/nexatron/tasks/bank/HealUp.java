package com.example.nexatron.tasks.bank;

import com.example.Utility.BankUtil;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.client.game.ItemManager;

@TaskDescriptor(
	name = "Heal Up",
	priority = Integer.MAX_VALUE - 20,
	blocking = true
)
public class HealUp extends StagedTask
{
	@Inject
	ItemManager itemManager;

	@Inject
	Consumable consumable;

	@Inject
	public HealUp(NexManager nexManager)
	{
		super(nexManager, Stage.BANK);
	}

	public boolean execute()
	{
		if (nexManager.shouldKc())
		{
			return false;
		}
		if (Consumable.isDrained(Skill.HITPOINTS) || Consumable.isDrainedMore(Skill.PRAYER, 5))
		{
			if (!BankUtil.isOpen())
			{
				if (nexManager.nexBank.openBank() == 0)
				{
					return false;
				}
				incrementActionCount();
				return true;
			}
			if (Consumable.isDrained(Skill.HITPOINTS))
			{
				if (consumable.prePot(ItemID.ANGLERFISH) == 0)
				{
					return false;
				}
				incrementActionCount();
				return true;
				//return consumable.prePot(ItemID.ANGLERFISH);
			}
			else if (Consumable.isDrainedMore(Skill.PRAYER, 5))
			{
				if (consumable.prePot(ItemID.SUPER_RESTORE1) == 0)
				{
					return false;
				}
				incrementActionCount();
				return true;
			}
			return true;
		}
		return false;

	}
}