package com.example.toagigatron.tasks.outside;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.BankUtil;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.GameObject;
import net.runelite.api.Item;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import java.util.ArrayList;

@TaskDescriptor(
	name = "Banking junk",
	priority = 5
)
public class Bank extends StagedTask
{
	GameTickManager gameTickManager;
	@Inject
	public Bank(ToaManager toaManager, GameTickManager gameTickManager)
	{
		super(toaManager, Stage.OUTSIDE);
		this.gameTickManager = gameTickManager;
	}

	public boolean execute()
	{
		if (toaManager.overall.died)
		{
			return false;
		}
		if (toaManager.onBreak())
		{
			return false;
		}
		if (toaManager.needsBreak() && !toaManager.allowedToBreak)
		{
			toaManager.allowedToBreak = true;
			return false;
		}
		if (toaManager.chargesTracker.shouldRechargeAnything())
		{
			return false;
		}
		if(gameTickManager.isTickWaiting()){
			return false;
		}
		if (BankUtil.isOpen())
		{
			if (!BankUtil.isMainTabOpen())
			{
				BankUtil.openMainTab();
				return true;
			}
			for (int i : toaManager.getAllNecessaryItems())
			{
				if (Inventory.getItemAmount(i) > 1 || Inventory.getItemAmount(i) >= 1 && Equipment.search().withId(i).result().size() >= 1)
				{
					toaManager.print("Depositing too many of id: " + i);
					BankUtil.depositAll(i);
					return true;
				}
			}
		}
		ArrayList<Widget> junk = toaManager.getAllUnnecessaryItems();
		if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItems())
			&& toaManager.hasItem(toaManager.meleeSetup.getAllItems()))
		{
			toaManager.print("Equipping melee gear");
			toaManager.swap(toaManager.meleeSetup.getAllItems());
		}
		if (!junk.isEmpty())
		{
			if (!BankUtil.isOpen())
			{
				TileObject camel = TileObjects.search().withId(ToaConstants.BANK_CAMEL).first().orElse(null);
				if (camel != null)
				{
					toaManager.print("Clicking banker");
					MousePackets.queueClickPacket();
					ObjectPackets.queueObjectAction(camel, false, "Bank");
					gameTickManager.setTickWait(2);
					return true;
				}
				toaManager.print("Cant find banker somehow");
				return false;
			}
			else
			{
				toaManager.bank(junk);
				return true;
			}
		}
		if (!toaManager.hasAllItems(toaManager.getAllNecessaryItems()))
		{
			if (!BankUtil.isOpen())
			{
				TileObject camel = TileObjects.search().withId(ToaConstants.BANK_CAMEL).first().orElse(null);
				if (camel != null)
				{
					toaManager.print("Clicking banker down here");
					MousePackets.queueClickPacket();
					ObjectPackets.queueObjectAction(camel, false, "Bank");
					gameTickManager.setTickWait(2);
					return true;
				}
				toaManager.print("Cant find banker somehow");
				return false;
			}
			else
			{
				toaManager.print("Withdrawing necessary items");
				toaManager.withdraw(toaManager.withdrawNecessaryItems());
				return true;
			}
		}
		return false;
	}
}

