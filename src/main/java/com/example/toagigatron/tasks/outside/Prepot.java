package com.example.toagigatron.tasks.outside;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.BankUtil;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Movement;
import com.example.toagigatron.ToaGigatronPlugin;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import javax.inject.Inject;

@TaskDescriptor(
	name = "Prepotting",
	priority = 20
)
public class Prepot extends StagedTask
{

	@Inject
	ToaGigatronPlugin plugin;

	GameTickManager gameTickManager;
	@Inject
	public Prepot(ToaManager toaManager, GameTickManager gameTickManager)
	{
		super(toaManager, Stage.OUTSIDE);
		this.gameTickManager = gameTickManager;
	}

	public boolean execute()
	{
		if (toaManager.overall.died || !toaManager.getAllUnnecessaryItems().isEmpty() || toaManager.isPrePotted())
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
		if (!BankUtil.isOpen())
		{
			TileObject camel = TileObjects.search().withId(ToaConstants.BANK_CAMEL).first().orElse(null);
			if (camel != null)
			{
				toaManager.print("Clicking banker in prepot task");
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(camel, false, "Bank");
				gameTickManager.setTickWait(2);
				return true;
			}
			toaManager.print("Cant find banker somehow");
			return false;
		}
		if (!BankUtil.isMainTabOpen())
		{
			BankUtil.openMainTab();
			return true;
		}
		if (!toaManager.isAntiVenomed())
		{
			toaManager.print("anti prepot");
			return executePrePot(Consumables.PREPOT_ANTI);
		}
		if (!toaManager.isBoosted(Skill.HITPOINTS))
		{
			toaManager.print("Angler prepot");
			return executePrePot(Consumables.PREPOT_ANGLER);
		}
		if (!toaManager.isBoosted(Skill.STRENGTH))
		{
			toaManager.print("Scb prepot");
			return executePrePot(Consumables.PREPOT_SCB);
		}
		if (!toaManager.isBoosted(Skill.MAGIC) && client.getVarbitValue(Varbits.IMBUED_HEART_COOLDOWN) == 0)
		{
			toaManager.print("Saturated heart prepot");
			return executePrePot(Consumables.PREPOT_SATURATED_HEART);
		}
		else if (InventoryUtil.contains(Consumables.PREPOT_SATURATED_HEART))
		{
			toaManager.print("Banking heart");
			BankUtil.depositAll(Consumables.PREPOT_SATURATED_HEART);
		}
		if (!toaManager.isBoosted(Skill.RANGED))
		{
			toaManager.print("Range prepot");
			return executePrePot(Consumables.PREPOT_RANGE);
		}
		if (!Movement.isStaminaBoosted())
		{
			toaManager.print("Stamina prepot");
			return executePrePot(Consumables.PREPOT_STAM);
		}
		toaManager.print("Returning in prepot");
		return false;
	}

	public boolean executePrePot(int item)
	{
		if (!InventoryUtil.contains(item))
		{
			if (!BankUtil.contains(item))
			{
				plugin.stopPlugin = true;
				toaManager.print("Missing item id: " + item);
				return false;
			}
			BankUtil.withdrawOne(item);
			//BankUtil.withdraw(item, 1, Bank.WithdrawMode.DEFAULT);
		}
		else
		{
			Widget boost = InventoryUtil.getFirst(item);
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetActionPacket(9, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getId(), boost.getItemId(), boost.getIndex());

			//boost.interact(9, MenuAction.CC_OP_LOW_PRIORITY.getId(), boost.getSlot(), 983043);
			toaManager.print("Drinking/eating " + boost.getName());
			return true;
		}
		return true;
	}

}

