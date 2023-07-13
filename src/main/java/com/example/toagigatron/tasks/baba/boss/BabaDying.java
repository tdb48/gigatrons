package com.example.toagigatron.tasks.baba.boss;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.TileItemPackets;
import com.example.Utility.InventoryUtil;
import com.example.Utility.TileItemUtil;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import java.util.ArrayList;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Baba dying",
	priority = 9999,
	blocking = true
)
public class BabaDying extends StagedTask
{
	@Inject
	public BabaDying(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_BOSS);
	}

	public boolean execute()
	{
		if (toaManager.baba.babaBoss == null || toaManager.baba.babaBoss.getHealthRatio() != 0)
		{
			return false;
		}
		ArrayList<ETileItem> tileItems = toaManager.getTileItemSupplies();
		if (!tileItems.isEmpty() && !InventoryUtil.isFull())
		{
			toaManager.print("Picking up " + tileItems.get(0).getTileItem().getId());
			MousePackets.queueClickPacket();
			TileItemPackets.queueTileItemAction(tileItems.get(0), false);
		}
		else
		{
			WorldPoint prepathTile = getPrePathTile();
			if(prepathTile != null)
			{
				if (client.getLocalPlayer().getWorldLocation().equals(prepathTile))
				{
					return false;
				}
				toaManager.print("Prepathing to Osmumten");
				MousePackets.queueClickPacket();
				MovementPackets.queueMovement(prepathTile);
			}
		}
		return true;
	}

	public WorldPoint getPrePathTile()
	{
		if (toaManager.baba.babaEntry == null)
		{
			return null;
		}
		WorldPoint reference = toaManager.baba.babaEntry.getWorldLocation();
		return reference.dx(22);
	}

}