package com.example.toagigatron.tasks.outside;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.BankUtil;
import com.example.Utility.InventoryUtil;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.GameObject;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Claim death",
	priority = 999,
	blocking = true
)
public class ClaimDeath extends StagedTask
{

	@Inject
	public ClaimDeath(ToaManager toaManager)
	{
		super(toaManager, Stage.OUTSIDE);
	}

	public boolean execute()
	{
		if (!toaManager.overall.died)
		{
			return false;
		}
		if (InventoryUtil.isFull())
		{
			if (BankUtil.isOpen())
			{
				toaManager.print("Depositing all");
				BankUtil.depositInventory();
				BankUtil.depositEquipment();
				return true;
			}
			else
			{
				TileObject camel = TileObjects.search().withId(ToaConstants.BANK_CAMEL).first().orElse(null);
				if (camel != null)
				{
					toaManager.print("Clicking banker claim death");
					MousePackets.queueClickPacket();
					ObjectPackets.queueObjectAction(camel, false, "Bank");
					return true;
				}
				toaManager.print("Cant find banker somehow");
				return false;
			}
		}
		if (!isChestInterfaceOpen())
		{
			TileObject chest = TileObjects.search().withId(ToaConstants.DEATH_CHEST).first().orElse(null);
			if (chest == null)
			{
				return false;
			}
			toaManager.print("Clicking chest");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(chest, false, "Claim");
			return true;
		}
		Widget claimWidget = client.getWidget(602,6);
		if (claimWidget != null && !claimWidget.isHidden() && claimWidget.getActions() != null)
		{
			String action = claimWidget.getActions()[0];
			toaManager.print("Unlocking chest");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(claimWidget, action);
			return true;
		}
		/*
		 * if inventory is full, bank
		 * if inventory is empty and widget of no more items is visible, died is false
		 * if chest interface is not open, open it
		 * if chest is locked, unlock it
		 * */
		return false;
	}

	public boolean isChestInterfaceOpen()
	{
		Widget chestWidget = client.getWidget(602,2);
		return (chestWidget != null && !chestWidget.isHidden());
	}
}