package com.example.nexatron.tasks.bank;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.InteractionApi.BankInteraction;
import com.example.Utility.BankUtil;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;

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

	@Inject
	Consumable consumable;
	@Inject
	ItemManager itemManager;

	@Inject
	public WithdrawSupplies(NexManager nexManager)
	{
		super(nexManager, Stage.BANK);
	}

	public boolean execute()
	{
		if (!nexManager.socket.readyToStart
			|| !nexManager.socket.otherReadyToStart
			|| !nexManager.isPrePotted())
		{
			return false;
		}
		int requiredRestore = nexManager.config.restoreCount() - Inventory.getItemAmount(RESTORE);
		int requiredBrew = requiredBrews();
		int requiredRpot = nexManager.config.rangeCount() - Inventory.getItemAmount(RPOT);
		int requiredScb = nexManager.config.scbCount() - Inventory.getItemAmount(SCB);
		int missingId = getMissingSupplyId(nexManager.config.restoreCount(), nexManager.config.scbCount(), nexManager.config.rangeCount());
		if (requiredRpot == 0 && requiredBrew == 0 && requiredScb == 0 && requiredRestore == 0 && missingId == -1)
		{
			return false;
		}
		if (!BankUtil.isOpen())
		{
			nexManager.print("Opening bank in supplies");
			return nexManager.nexBank.openBank();
		}
		// If we have too many of a resource, this will return the item id, so we can bank it all and start over
		if (missingId != -1)
		{
			nexManager.print("Wrong supply count, depositing all of " + itemManager.getItemComposition(missingId).getName());
			BankUtil.depositAll(missingId);
			return true;
		}
		return withdrawMissingSupplies(requiredRpot, requiredScb, requiredRestore, requiredBrew);
	}

	public boolean withdrawMissingSupplies(int requiredRpot, int requiredScb, int requiredRestore, int requiredBrew)
	{
		Widget bankRestores = Bank.search().withId(RESTORE).first().orElse(null);
		Widget bankBrews = Bank.search().withId(BREW).first().orElse(null);
		Widget bankRpots = Bank.search().withId(RPOT).first().orElse(null);
		Widget bankScbs = Bank.search().withId(SCB).first().orElse(null);
		if (bankRestores == null || bankBrews == null || bankRpots == null || bankScbs == null)
		{
			nexManager.print("We are somehow missing bank widgets?");
			return true;
		}
		boolean returnValue = false;
		if (requiredRpot > 0)
		{
			nexManager.print("Withdrawing " + requiredRpot + " Ranging potions");
			BankInteraction.withdrawX(bankRpots, requiredRpot);
			returnValue = true;
		}
		if (requiredScb > 0)
		{
			nexManager.print("Withdrawing " + requiredScb + " Super Combat Potions");
			BankInteraction.withdrawX(bankScbs, requiredScb);
			returnValue = true;
		}
		if (requiredRestore > 0)
		{
			nexManager.print("Withdrawing " + requiredRestore + " Super Restores");
			BankInteraction.withdrawX(bankRestores, requiredRestore);
			returnValue = true;
		}
		if (requiredBrew > 0)
		{
			nexManager.print("Withdrawing " + requiredBrew + " Saradomin brews");
			BankInteraction.withdrawX(bankBrews, requiredBrew);
			returnValue = true;
		}
		return returnValue;
	}

	public int getBrewCount()
	{
		// We use 4 slots for gear and 1 for bolts
		int brewCount = 23;
		if (nexManager.config.rangeCape().itemId == nexManager.config.meleeCape().itemId)
		{
			brewCount = 24;
		}

		// Remove 2 slots if we are using thralls
		if (nexManager.config.useThralls())
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