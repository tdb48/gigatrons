package com.example.nexatron.tasks.bank;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.InteractionApi.BankInteraction;
import com.example.Utility.BankUtil;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Withdraw supplies",
	priority = Integer.MAX_VALUE - 40,
	blocking = true
)
public class WithdrawSupplies extends StagedTask
{
	public static final int RESTORE = ItemID.SUPER_RESTORE4;
	public static final int BREW = ItemID.SARADOMIN_BREW4;
	public static final int SCB = ItemID.SUPER_COMBAT_POTION4;
	public static final int RPOT = ItemID.RANGING_POTION4;
	public static final int ANTI = ItemID.ANTIDOTE4_5952;

	@Inject
	public WithdrawSupplies(NexManager nexManager)
	{
		super(nexManager, Stage.BANK);
	}

	public boolean execute()
	{
		if (!nexManager.socket.readyToStart
			|| (!nexManager.socket.otherReadyToStart && !nexManager.shouldKc())
			|| (!nexManager.isPrePotted()) && !nexManager.shouldKc())
		{
			return false;
		}
		int requiredRestore = nexManager.config.restoreCount() - Inventory.getItemAmount(RESTORE);
		int requiredBrew = requiredBrews();
		int requiredRpot = nexManager.config.rangeCount() - Inventory.getItemAmount(RPOT);
		int requiredScb = nexManager.config.scbCount() - Inventory.getItemAmount(SCB);
		int requiredAnti = 0;
		if (nexManager.shouldKc())
		{
			if (nexManager.kcArea.canKillMage())
			{
				requiredAnti = 2 - Inventory.getItemAmount(ANTI);
			}
			requiredScb = 0;
			requiredBrew = 0;
			requiredRpot = 3 - Inventory.getItemAmount(RPOT);
			requiredRestore = (28 - 3 - 2) - Inventory.getItemAmount(RESTORE);
		}
//			int missingId = getMissingSupplyId(nexManager.config.restoreCount(), nexManager.config.scbCount(), nexManager.config.rangeCount());
		if (requiredRpot == 0
			&& requiredBrew == 0
			&& requiredScb == 0
			&& requiredRestore == 0
			&& requiredAnti == 0)
//				&& missingId == -1
		{
			return false;
		}
		if (!BankUtil.isOpen())
		{
			nexManager.print("Opening bank in supplies");
			if (nexManager.nexBank.openBank() == 0)
			{
				return false;
			}
			nexManager.print("Opening bank @ supplies");
			incrementActionCount();
			return true;
		}
		// If we have too many of a resource, this will return the item id, so we can bank it all and start over
//			if (missingId != -1)
//			{
//				nexManager.print("Wrong supply count, depositing all of " + itemManager.getItemComposition(missingId).getName());
//				BankUtil.depositAll(missingId);
//				incrementActionCount();
//				return true;
//			}
		return withdrawMissingSupplies(requiredRpot, requiredScb, requiredRestore, requiredBrew, requiredAnti);
	}

	//Incrementing the action count by 1 per withdrawX call (im not sure if its 2 actions for
	// clicking the X button and then running the script to set value but i've left it as 1 for now
	public boolean withdrawMissingSupplies(int requiredRpot, int requiredScb, int requiredRestore, int requiredBrew, int requiredAnti)
	{
		Widget bankRestores = Bank.search().withId(RESTORE).first().orElse(null);
		Widget bankBrews = Bank.search().withId(BREW).first().orElse(null);
		Widget bankRpots = Bank.search().withId(RPOT).first().orElse(null);
		Widget bankScbs = Bank.search().withId(SCB).first().orElse(null);
		Widget bankAntis = Bank.search().withId(ANTI).first().orElse(null);
		if (bankRestores == null || bankBrews == null || bankRpots == null || bankScbs == null || bankAntis == null)
		{
			nexManager.print("We are somehow missing bank widgets?");
			return true;
		}
		if (requiredRpot > 0)
		{
			nexManager.print("Withdrawing " + requiredRpot + " Ranging potions");
			BankInteraction.withdrawX(bankRpots, requiredRpot);
			incrementActionCount();
			return true;
		}
		if (requiredScb > 0)
		{
			nexManager.print("Withdrawing " + requiredScb + " Super Combat Potions");
			BankInteraction.withdrawX(bankScbs, requiredScb);
			incrementActionCount();
			return true;
		}
		if (requiredRestore > 0)
		{
			nexManager.print("Withdrawing " + requiredRestore + " Super Restores");
			BankInteraction.withdrawX(bankRestores, requiredRestore);
			incrementActionCount();
			return true;
		}
		if (requiredBrew > 0)
		{
			nexManager.print("Withdrawing " + requiredBrew + " Saradomin brews");
			BankInteraction.withdrawX(bankBrews, requiredBrew);
			incrementActionCount();
			return true;
		}
		if (requiredAnti > 0)
		{
			nexManager.print("Withdrawing " + requiredAnti + " Antidote++");
			BankInteraction.withdrawX(bankAntis, requiredAnti);
			incrementActionCount();
			return true;
		}
		return false;
	}

	public int getBrewCount()
	{
		// We use 4 slots for gear and 1 for bolts
		int brewCount = 23;
//		if (nexManager.config.rangeCape().itemId == nexManager.config.meleeCape().itemId)
//		{
//			brewCount = 24;
//		}

		// Remove 2 slots if we are using thralls
		if (nexManager.useThralls())
		{
			brewCount -= 2;
		}
		// Remove the other supplies
		brewCount -= nexManager.config.restoreCount();
		brewCount -= nexManager.config.scbCount();
		brewCount -= nexManager.config.rangeCount();

		return brewCount;
	}

	public int requiredBrews()
	{
		return getBrewCount() - Inventory.getItemAmount(BREW);
	}

	public int getMissingSupplyId(int restores, int scbs, int rpots)
	{
		if (Inventory.search().withId(RESTORE).result().size() > restores)
		{
			return RESTORE;
		}
		if (Inventory.search().withId(SCB).result().size() > scbs)
		{
			return SCB;
		}
		if (Inventory.search().withId(RPOT).result().size() > rpots)
		{
			return RPOT;
		}
		if (Inventory.search().withId(BREW).result().size() > getBrewCount())
		{
			return BREW;
		}
		return -1;
	}
}