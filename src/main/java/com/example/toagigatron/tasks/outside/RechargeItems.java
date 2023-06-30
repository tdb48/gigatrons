package com.example.toagigatron.tasks.outside;


import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.BankUtil;
import com.example.Utility.Dialog;
import com.example.Utility.InventoryUtil;
import com.example.toagigatron.ToaGigatronPlugin;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.setup.mage.MageWeapon;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Recharge items",
	priority = 50,
	blocking = true
)
public class RechargeItems extends StagedTask
{
	@Inject
	ToaGigatronPlugin plugin;

	@Inject
	public RechargeItems(ToaManager toaManager)
	{
		super(toaManager, Stage.OUTSIDE);
	}

	public static final int scales = ItemID.ZULRAHS_SCALES;
	public static final int brune = ItemID.BLOOD_RUNE;

	public boolean execute()
	{
		if (!InventoryUtil.contains(ToaConstants.BLOWPIPE_EMPTY) && !InventoryUtil.contains(ToaConstants.BLOWPIPE_CHARGED))
		{
			return false;
		}
		if (!InventoryUtil.contains(toaManager.mageSetup.weapon))
		{
			return false;
		}
		if (!toaManager.chargesTracker.shouldRechargeAnything())
		{
//			toaManager.print("Dont need to recharge anything");
			return false;
		}
		if (toaManager.chargesTracker.blowpipeScales == -1
			|| toaManager.chargesTracker.mageCharges == -1
			|| toaManager.chargesTracker.blowpipeDarts == -1
			|| toaManager.chargesTracker.dartType == null)
		{
			toaManager.print("Not done checking charge");
			return false;
		}
		// Only support sang for now
		Widget rechargeWidget = client.getWidget(162, 41);
		if (rechargeWidget != null && !rechargeWidget.isHidden())
		{
			Dialog.type(String.valueOf(99999), true);
			toaManager.print("Entering sang charge");
			return true;
		}
		// Don't run if you don't need to recharge anything

		// Use scales on pipe
		if (toaManager.chargesTracker.shouldRechargeBlowpipe()
			&& InventoryUtil.contains(scales)
			&& InventoryUtil.contains(ToaConstants.blowpipe))
		{
			toaManager.print("refiill bp");
			Widget blowieP = InventoryUtil.getFirst(ToaConstants.blowpipe);
			if(blowieP != null){
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetOnWidget(InventoryUtil.getFirst(scales), blowieP);
			}
			//Inventory.getFirst(scales).useOn(Inventory.getFirst(ToaConstants.blowpipe));
			return true;
		}

		// Use darts on pipe
		if (toaManager.chargesTracker.shouldRefillBlowpipe()
			&& InventoryUtil.contains(toaManager.chargesTracker.dartType.itemId)
			&& InventoryUtil.contains(ToaConstants.blowpipe))
		{
			toaManager.print("recharg bp");
			Widget blowieP = InventoryUtil.getFirst(ToaConstants.blowpipe);
			if(blowieP != null){
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetOnWidget(InventoryUtil.getFirst(toaManager.chargesTracker.dartType.itemId), blowieP);
			}
			//toaManager.itemOnItemPacket(Inventory.getFirst(toaManager.chargesTracker.dartType.itemId),Inventory.getFirst(ToaConstants.blowpipe));
			//Inventory.getFirst(toaManager.chargesTracker.dartType.itemId).useOn(Inventory.getFirst(ToaConstants.blowpipe));
			return true;
		}
		// Use blood runes on sang
		if (toaManager.chargesTracker.shouldRechargeMageWeapon()
			&& InventoryUtil.contains(brune)
			&& InventoryUtil.getFirst(brune).getItemQuantity() >= 3
			&& InventoryUtil.contains(MageWeapon.SANG.itemId))
		{
			toaManager.print("charge mage");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetOnWidget(InventoryUtil.getFirst(brune), InventoryUtil.getFirst(MageWeapon.SANG.itemId));
//			toaManager.itemOnItemPacket(Inventory.getFirst(brune),Inventory.getFirst(MageWeapon.SANG.itemId));
			//Inventory.getFirst(brune).useOn(Inventory.getFirst(MageWeapon.SANG.itemId));
			return true;
		}

		if (!BankUtil.isOpen())
		{
			TileObject camel = TileObjects.search().withId(ToaConstants.BANK_CAMEL).first().orElse(null);
			if (camel != null)
			{
				toaManager.print("Clicking banker in recharge task");
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(camel, false, "Bank");
				return true;
			}
			toaManager.print("Cant find banker somehow");
			return false;
		}
		else
		{
			if (toaManager.chargesTracker.shouldRechargeBlowpipe())
			{

				if (BankUtil.getQuantity(scales) == 0 && Inventory.getItemAmount(ItemID.ZULRAHS_SCALES) == 0)
				{
					toaManager.print("NOT ENOUGH BP CHARGE - LOGGING OUT");
					System.out.println("NOT ENOUGH BP CHARGE - LOGGING OUT");
					plugin.stopPlugin = true;
					return false;
				}
				BankUtil.withdrawAll(scales);
				return true;
			}
			if (toaManager.chargesTracker.shouldRefillBlowpipe())
			{
				if (BankUtil.getQuantity(toaManager.chargesTracker.dartType.itemId) == 0 && Inventory.getItemAmount(toaManager.chargesTracker.dartType.itemId) == 0)
				{
					toaManager.print("NOT ENOUGH AMMO - LOGGING OUT");
					System.out.println("NOT ENOUGH AMMO - LOGGING OUT");
					plugin.stopPlugin = true;
					return false;
				}
				BankUtil.withdrawAll(toaManager.chargesTracker.dartType.itemId);
				return true;
			}

			if (toaManager.chargesTracker.shouldRechargeMageWeapon())
			{
				if (BankUtil.getFirst(brune).getItemQuantity() <= 2 && InventoryUtil.getFirst(brune).getItemQuantity() <= 2)
				{
					toaManager.print("NOT ENOUGH MAGE CHARG - LOGGING OUT");
					System.out.println("NOT ENOUGH MAGE CHARG - LOGGING OUT");
					plugin.stopPlugin = true;
					return false;
				}
				BankUtil.withdrawAll(brune);
				return true;
			}
		}
		return false;
	}
}
