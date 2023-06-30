package com.example.toagigatron.tasks.baba.puzzle;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.InventoryUtil;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.GameObject;
import net.runelite.api.Item;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import java.util.ArrayList;

@TaskDescriptor(
		name = "Baba pickup hammer and potion",
		blocking = true
)
public class BabaGetHammerPotion extends StagedTask
{
	@Inject
	public BabaGetHammerPotion(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE);
	}

	public boolean execute()
	{
		TileObject exit = TileObjects.search().withId(ToaConstants.BABA_PUZZLE_EXIT).first().orElse(null);
		if (!toaManager.baba.isPuzzleActive() || exit == null)
		{
			return false;
		}
		if (InventoryUtil.contains("Hammer") && InventoryUtil.contains("Neutralising potion"))
		{
			int offhand = toaManager.meleeSetup.offhand;
			if (InventoryUtil.isFull() && !InventoryUtil.contains(offhand))
			{
				ArrayList<Widget> restores = InventoryUtil.getAll("Super restore(4)");
				if (!restores.isEmpty())
				{
					MousePackets.queueClickPacket();
					WidgetPackets.queueWidgetAction(restores.get(0), "Drop");
					toaManager.print("Dropping restore because inv stuck somehow");
					return true;
				}
			}
			else
			{
				return false;
			}
		}
		TileObject hammers = TileObjects.search().withId(ToaConstants.BABA_CRATE_HAMMERS).first().orElse(null);
		TileObject potions = TileObjects.search().withId(ToaConstants.BABA_CRATE_POTIONS).first().orElse(null);

		if (hammers == null || potions == null)
		{
			return false;
		}
		if (!toaManager.hasGearEquipped(toaManager.mageSetup.getAllItems()))
		{
			toaManager.swap(toaManager.mageSetup.getAllItems());
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		// Drop 2 brews to get a hammer
		if (!InventoryUtil.contains("Hammer"))
		{
			if (playerPoint.distanceTo(hammers.getWorldLocation()) > 6 || Inventory.getEmptySlots() >= 2)
			{
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(hammers, false, "Take");
				toaManager.print("Taking hammers");
				return true;
			}
			ArrayList<Widget> restores = InventoryUtil.getAll("Super restore(4)");
			if (!restores.isEmpty())
			{
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(restores.get(0), "Drop");
				toaManager.print("Dropping restores for hammer");
				return true;
			}
		}
		// Drop 1 restore to get a potion
		else if (!InventoryUtil.contains("Neutralising potion"))
		{
			if (playerPoint.distanceTo(potions.getWorldLocation()) > 6 || Inventory.getEmptySlots() >= 2)
			{
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(hammers, false, "Take");
				toaManager.print("Taking potions");
				return true;
			}
			ArrayList<Widget> restores = InventoryUtil.getAll("Super restore(4)");
			if (!restores.isEmpty())
			{
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(restores.get(0), "Drop");
				toaManager.print("Dropping restores for neutral pot");
				return true;
			}
		}

		return false;
	}
}