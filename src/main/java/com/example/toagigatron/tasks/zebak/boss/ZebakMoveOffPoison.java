package com.example.toagigatron.tasks.zebak.boss;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.*;
import com.example.Utility.Prayer;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Zebak move off poison",
	priority = 1,
	blocking = true
)
public class ZebakMoveOffPoison extends StagedTask
{
	@Inject
	public ZebakMoveOffPoison(ToaManager toaManager)
	{
		super(toaManager, Stage.ZEBAK_BOSS);
	}

	public boolean execute()
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		if (toaManager.zebak.poisonWorldPoints.contains(playerPoint))
		{
			WorldPoint safeTile = getSafeTile(playerPoint);
			if (safeTile == null)
			{
				toaManager.print("unlucky");
				return false;
			}
			toaManager.print("Moving off poison to " + toaManager.worldPointString(safeTile));
			Movement.walk(safeTile);
			return true;
		}
		return false;
	}


	private WorldPoint getSafeTile(WorldPoint player)
	{
		WorldArea area = WorldAreas.createArea(player.dx(-2).dy(-2), player.dx(3).dy(3));
		for (WorldPoint wp : area.toWorldPointList())
		{
			if (wp.distanceTo(player) > 3)
			{
				continue;
			}
			Tile currentTile = Tiles.getAt(wp);
			if (currentTile.getGameObjects() != null
				&& !toaManager.zebak.poisonWorldPoints.contains(wp)
				&& !toaManager.containsObjectZebak(currentTile.getGameObjects())
				&& Reachable.isWalkable(wp))
			{
				return wp;
			}
		}
		return null;
	}
}
