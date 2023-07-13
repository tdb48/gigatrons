package com.example.toagigatron.tasks.kephri.boss;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.TileItemPackets;
import com.example.Utility.InventoryUtil;
import com.example.Utility.TileItemUtil;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import java.util.ArrayList;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Kephri dying",
	priority = 9999,
	blocking = true
)
public class KephriDying extends StagedTask
{
	@Inject
	public KephriDying(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_BOSS);
	}

	public boolean execute()
	{
		if (toaManager.kephri.kephri == null
			|| toaManager.kephri.kephriPhase != 6
			|| toaManager.kephri.kephri.getHealthRatio() != 0)
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
			if (prepathTile != null)
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
		WorldArea kephriArea = toaManager.kephri.kephri.getWorldArea();
		if (kephriArea == null)
		{
			return null;
		}
		WorldPoint reference = WorldAreas.getCenter(kephriArea);
		return reference.dx(6);
	}

}