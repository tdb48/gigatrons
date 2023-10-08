package com.example.toagigatron.tasks;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.BankUtil;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Static;
import com.example.Utility.WidgetUtil;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Dart;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.setup.mage.MageWeapon;
import com.example.toagigatron.taskformat.Task;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	priority = 999,
	name = "Check charges",
	register = true,
	blocking = true
)
public class CheckCharges extends Task
{
	private final ToaManager toaManager;

	private boolean widgetOpened;

	@Inject
	public CheckCharges(ToaManager toaManager)
	{
		widgetOpened = false;
		this.toaManager = toaManager;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Widget itemWidget = Static.getClient().getWidget(10616869);
		if (WidgetUtil.isVisible(itemWidget))
		{
			widgetOpened = true;
		}
	}
//	@Subscribe
//	public void widgetDespawn(WidgetHiddenChanged widgetHiddenChanged)
//	{
//		if (!widgetHiddenChanged.isHidden())
//		{
//			return;
//		}
//		int rechargeWidget = 10616869;
//		if (widgetHiddenChanged.getWidget().getId() == rechargeWidget)
//		{
//			for (MageWeapon mageWeapon : MageWeapon.values())
//			{
//				int i = mageWeapon.itemId;
//				if (Inventory.contains(i))
//				{
//					Inventory.getFirst(i).interact("Check");
//				}
//				else if (Equipment.contains(i))
//				{
//					Equipment.getFirst(i).interact("Check");
//				}
//			}
//			toaManager.print("Recharge widget closed");
//		}
//	}

	public boolean run()
	{
		if (toaManager.getStage() != Stage.OUTSIDE)
		{
			return false;
		}
		if (!InventoryUtil.contains(ToaConstants.BLOWPIPE_CHARGED) && !InventoryUtil.contains(ToaConstants.BLOWPIPE_EMPTY))
		{
			return false;
		}
		if (!InventoryUtil.contains(ItemID.SANGUINESTI_STAFF))
		{
			return false;
		}
		if (InventoryUtil.contains(ItemID.TOXIC_BLOWPIPE_EMPTY))
		{
			toaManager.chargesTracker.blowpipeScales = 0;
			toaManager.chargesTracker.blowpipeDarts = 0;
			toaManager.chargesTracker.dartType = Dart.DRAGON;
		}
		boolean returnValue = false;
		if (toaManager.chargesTracker.blowpipeDarts == -1
			|| toaManager.chargesTracker.blowpipeScales == -1)
		{
			if (Bank.isOpen())
			{
				BankUtil.close();
			}
			if (InventoryUtil.contains(ItemID.TOXIC_BLOWPIPE))
			{
				Widget bp = InventoryUtil.getFirst(ItemID.TOXIC_BLOWPIPE);
				if (bp != null && bp.getActions() != null)
				{
					MousePackets.queueClickPacket();
					WidgetPackets.queueWidgetAction(bp, "Check");
				}
			}

			else if (Equipment.search().withId(ItemID.TOXIC_BLOWPIPE).first().orElse(null) != null)
			{
				Widget bp = Equipment.search().withId(ItemID.TOXIC_BLOWPIPE).first().orElse(null);
				if (bp != null && bp.getActions() != null)
				{
					MousePackets.queueClickPacket();
					WidgetPackets.queueWidgetAction(bp, "Check");
				}
			}
			// dont change this print message, can't have 'charges' in it
			toaManager.print("Checking blowpipe charge");
			returnValue = true;
		}
		if (toaManager.chargesTracker.mageCharges == -1 || widgetOpened)
		{
			if (Bank.isOpen())
			{
				BankUtil.close();
			}
			for (MageWeapon mageWeapon : MageWeapon.values())
			{
				int i = mageWeapon.itemId;
				if (InventoryUtil.contains(i))
				{
					Widget sang = InventoryUtil.getFirst(i);
					if (sang != null && sang.getActions() != null)
					{
						MousePackets.queueClickPacket();
						WidgetPackets.queueWidgetAction(sang, "Check");
						widgetOpened = false;
					}
				}
				else if (Equipment.search().withId(i).first().orElse(null) != null)
				{
					Widget sang = Equipment.search().withId(i).first().orElse(null);
					if (sang != null && sang.getActions() != null)
					{
						MousePackets.queueClickPacket();
						WidgetPackets.queueWidgetAction(sang, "Check");
						widgetOpened = false;
					}
				}
			}
			// dont change this print message, can't have 'charges' in it
			toaManager.print("Checking mage weapon charge");
			returnValue = true;
		}
		return returnValue;
	}
}
