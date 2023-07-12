package com.example.toagigatron.tasks.outside;


import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.BankInventory;
import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.ChargesTracker;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.setup.mage.MageBody;
import com.example.toagigatron.model.setup.mage.MageLegs;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Find chargeable items",
	priority = 500,
	blocking = true
)
public class FindChargeableItems extends StagedTask
{

	@Inject
	public FindChargeableItems(ToaManager toaManager)
	{
		super(toaManager, Stage.OUTSIDE);
	}

	public boolean execute()
	{
		if (toaManager.overall.died)
		{
			return false;
		}
		// Skirt, find a new ID when its 25% durability or if its not set
		if (toaManager.config.mageLegs() == MageLegs.AHRIMS
			&& (toaManager.chargesTracker.ahrimsSkirt == -1
			|| toaManager.chargesTracker.ahrimsSkirt == ItemID.AHRIMS_ROBESKIRT_25))
		{
			Widget skirt = findLowestCharge(ChargesTracker.ASKIRT);
			if (skirt != null)
			{
				toaManager.print("Ahrims skirt found with id " + skirt.getItemId());
				toaManager.chargesTracker.ahrimsSkirt = skirt.getItemId();
			}
			else
			{
				if (!Bank.isOpen())
				{
					return openBank();
				}
				skirt = findLowestChargeBank(ChargesTracker.ASKIRT);
				if (skirt != null)
				{
					toaManager.print("Ahrims skirt found with id " + skirt.getItemId());
					toaManager.chargesTracker.ahrimsSkirt = skirt.getItemId();
				}
				else
				{
					toaManager.print("Can't find ahrims skirt");
				}
			}
			return true;
		}

		if (toaManager.config.mageBody() == MageBody.AHRIMS
			&& (toaManager.chargesTracker.ahrimsTop == -1
			|| toaManager.chargesTracker.ahrimsTop == ItemID.AHRIMS_ROBETOP_25))
		{
			Widget top = findLowestCharge(ChargesTracker.ATOP);
			if (top != null)
			{
				toaManager.print("Ahrims top found with id " + top.getItemId());
				toaManager.chargesTracker.ahrimsTop = top.getItemId();
			}
			else
			{
				if (!Bank.isOpen())
				{
					return openBank();
				}
				top = findLowestChargeBank(ChargesTracker.ATOP);
				if (top != null)
				{
					toaManager.print("Ahrims top found with id " + top.getItemId());
					toaManager.chargesTracker.ahrimsTop = top.getItemId();
				}
				else
				{
					toaManager.print("Can't find ahrims top");
				}
			}
			return true;
		}

		return false;
	}

	public Widget findLowestCharge(ArrayList<Integer> list)
	{
		for (int i : list)
		{
			Widget item = Inventory.search().withId(i).first().orElse(null);
			if (item != null)
			{
				return item;
			}
			item = Equipment.search().withId(i).first().orElse(null);
			if (item != null)
			{
				return item;
			}
		}
		return null;
	}

	public Widget findLowestChargeBank(ArrayList<Integer> list)
	{
		for (int i : list)
		{
			Widget item = BankInventory.search().withId(i).first().orElse(null);
			if (item != null)
			{
				return item;
			}
			item = Bank.search().withId(i).first().orElse(null);
			if (item != null)
			{
				return item;
			}
		}
		return null;
	}

	public boolean openBank()
	{
		TileObject camel = TileObjects.search().withId(ToaConstants.BANK_CAMEL).first().orElse(null);
		if (camel != null)
		{
			toaManager.print("Clicking banker in finding charges");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(camel, false, "Bank");
			return true;
		}
		return false;
	}

}