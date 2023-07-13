package com.example.toagigatron.tasks.zebak.boss;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.TileItemPackets;
import com.example.Utility.InventoryUtil;
import com.example.Utility.ObjectUtil;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import java.util.ArrayList;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Zebak dying",
	priority = 9999,
	blocking = true
)
public class ZebakDying extends StagedTask
{
	@Inject
	public ZebakDying(ToaManager toaManager)
	{
		super(toaManager, Stage.ZEBAK_BOSS);
	}

	public boolean execute()
	{
		if (toaManager.zebak.zebakBoss == null
			|| toaManager.zebak.zebakBoss.getHealthRatio() != 0)
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
		GameObject entry = ObjectUtil.getNearestGameObject(ToaConstants.ZEBAK_BOSS_ENTRY);

		if (entry == null)
		{
			return null;
		}
		return entry.getWorldLocation().dx(-25);
	}

}