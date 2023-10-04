package com.example.nexatron.tasks.nex;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.Packets.MousePackets;
import com.example.Packets.TileItemPackets;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Movement;
import com.example.Utility.Prayers;
import com.example.Utility.TileItemUtil;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.Comparator;
import javax.inject.Inject;
import net.runelite.client.game.ItemManager;

@TaskDescriptor(
	name = "Nex finish up",
	priority = 1
)
public class NexFinishUp extends StagedTask
{
	@Inject
	ItemManager itemManager;

	@Inject
	public NexFinishUp(NexManager nexManager)
	{
		super(nexManager, Stage.NEX_DEAD);
	}

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null)
		{
			return false;
		}
		// Dodge mushroom, tick gets set in ProgressStage task
		if (nexManager.nex.invincibleTick > 0)
		{
			if (!nexManager.getPlayerPoint().equals(nexManager.nex.masterMainTile))
			{
				nexManager.print("Walking to safety (wrath)");
				Movement.walk(nexManager.nex.masterMainTile);
			}
			nexManager.print("Waiting for wrath to disappear");
			return true;
		}

		ETileItem loot = findLoot();
		if (loot != null
			&& !InventoryUtil.isFull())
		{
			nexManager.print("Picking up " + itemManager.getItemComposition(loot.tileItem.getId()).getName());
			MousePackets.queueClickPacket();
			TileItemPackets.queueTileItemAction(loot, false);
		}
		else
		{
			nexManager.print("Setting teleport out to true");
			nexManager.nex.teleportOut = true;
		}
		return true;
	}

	public ETileItem findLoot()
	{
		ArrayList<ETileItem> potentialLoot = TileItemUtil.getAllETileItems(NexConst.HIGH_PRIO_LOOT);
		if (!potentialLoot.isEmpty())
		{
			return potentialLoot.stream().min(Comparator.comparingInt(o -> nexManager.getPlayerPoint().distanceTo(o.getLocation()))).orElse(null);
		}
		potentialLoot = TileItemUtil.getAllETileItems(NexConst.LOW_PRIO_LOOT);
		return potentialLoot.stream().min(Comparator.comparingInt(o -> nexManager.getPlayerPoint().distanceTo(o.getLocation()))).orElse(null);
	}

}

