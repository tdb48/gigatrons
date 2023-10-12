package com.example.nexatron.tasks.bank;

import com.example.Utility.BankUtil;
import com.example.Utility.Movement;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;

@TaskDescriptor(
	name = "Pre Pot",
	priority = Integer.MAX_VALUE - 30,
	blocking = true
)
public class PrePot extends StagedTask
{
	@Inject
	Consumable consumable;

	@Inject
	public PrePot(NexManager nexManager)
	{
		super(nexManager, Stage.BANK);
	}

	public boolean execute()
	{
		if (!nexManager.socket.readyToStart
			|| !nexManager.socket.otherReadyToStart
			|| nexManager.shouldKc()
			|| nexManager.isPrePotted())
		{
			return false;
		}
		if (!BankUtil.isOpen())
		{
			if (nexManager.nexBank.openBank() == 0)
			{
				return false;
			}
			incrementActionCount();
			return true;
			//return nexManager.nexBank.openBank();
		}
		if (!nexManager.isBoosted(Skill.HITPOINTS))
		{
			nexManager.print("Angler prepot");
			if(consumable.prePot(Consumable.PREPOT_ANGLER) == 0)
			{
				return false;
			}
			incrementActionCount();
			return true;
		}
		if (!nexManager.isAntiPoisoned())
		{
			nexManager.print("anti prepot");
			if(consumable.prePot(Consumable.PREPOT_ANTI) == 0)
			{
				return false;
			}
			incrementActionCount();
			return true;
		}
		if (!nexManager.isBoosted(Skill.STRENGTH))
		{
			nexManager.print("Scb prepot");
			if(consumable.prePot(Consumable.PREPOT_SCB) == 0)
			{
				return false;
			}
			incrementActionCount();
			return true;
		}
		if (!nexManager.isBoosted(Skill.RANGED))
		{
			nexManager.print("Range prepot");
			if(consumable.prePot(Consumable.PREPOT_RANGE) == 0)
			{
				return false;
			}
			incrementActionCount();
			return true;
		}
		if (!Movement.isStaminaBoosted())
		{
			nexManager.print("Stamina prepot");
			if(consumable.prePot(Consumable.PREPOT_STAM) == 0)
			{
				return false;
			}
			incrementActionCount();
			return true;
		}
		return false;
	}
}