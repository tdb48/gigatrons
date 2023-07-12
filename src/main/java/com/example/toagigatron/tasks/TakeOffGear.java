package com.example.toagigatron.tasks;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Static;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.setup.mage.MageBoots;
import com.example.toagigatron.model.setup.mage.MageOffhand;
import com.example.toagigatron.model.setup.range.RangeBoots;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Take off gear",
	priority = 1
)
public class TakeOffGear extends StagedTask
{
	@Inject
	public TakeOffGear(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P2, Stage.WARDENS_P3, Stage.AKKHA_BOSS, Stage.ZEBAK_BOSS);
	}

	public boolean execute()
	{
		if (client.getLocalPlayer().getHealthRatio() == -1)
		{
			return false;
		}
		//int invSlotsNeeded = invSpaceRequired();
		int invSlotsNeeded = 1;
		// If we don't have less slots than we need, return
		if (Inventory.getEmptySlots() < invSlotsNeeded)
		{
//			if (Static.getClient().getTickCount() % 5 == 0)
//			{
//				toaManager.print("Want to unequip, but don't have enough space");
//			}
			return false;
		}

		NPC interactingPlayer = toaManager.playerInteractingWith();

		int offHand = toaManager.meleeSetup.offhand;

		if (toaManager.mageSetup.offhand == MageOffhand.NONE.itemId
			&& Equipment.search().withId(offHand).first().orElse(null) != null
			&& Equipment.search().withId(toaManager.mageSetup.weapon).first().orElse(null) != null)
		{
			toaManager.print("Unequipping mage offhand");
			Widget offHandd = Equipment.search().withId(offHand).first().orElse(null);
			if (offHandd != null)
			{
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(offHandd, "Remove");
			}
			toaManager.reAttack(interactingPlayer);
			return true;
		}

		int boots = toaManager.meleeSetup.boots;
		// If we aren't wearing prims, unequip them

		if (Equipment.search().withId(boots).first().orElse(null) == null)
		{
			return false;
		}

		if (toaManager.mageSetup.boots == MageBoots.NONE.itemId
			&& Equipment.search().withId(toaManager.mageSetup.weapon).first().orElse(null) != null)
		{
			toaManager.print("Unequipping mage boots");
			Widget booties = Equipment.search().withId(boots).first().orElse(null);
			if (booties != null)
			{
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(booties, "Remove");
			}
			toaManager.reAttack(interactingPlayer);
			return true;
		}

		if (toaManager.rangeSetup.boots == RangeBoots.NONE.itemId
			&& Equipment.search().withId(toaManager.rangeSetup.weapon).first().orElse(null) != null)
		{
			toaManager.print("Unequipping range boots");
			Widget booties = Equipment.search().withId(boots).first().orElse(null);
			if (booties != null)
			{
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(booties, "Remove");
			}
			toaManager.reAttack(interactingPlayer);
			return true;
		}
		return false;
	}

	public int invSpaceRequired()
	{
		// If offhand is equipped, we need two inv spaces
		if (Equipment.search().withId(toaManager.meleeSetup.offhand).first().orElse(null) != null)
		{
			return 2;
		}
		return 1;
	}
}
