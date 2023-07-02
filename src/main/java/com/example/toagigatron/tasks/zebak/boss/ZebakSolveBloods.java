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
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Zebak solve bloods",
	priority = 1
)
public class ZebakSolveBloods extends StagedTask
{
	@Inject
	public ZebakSolveBloods(ToaManager toaManager)
	{
		super(toaManager, Stage.ZEBAK_BOSS);
	}

	public boolean execute()
	{
		// Dont deal with bloods during waves or rocks in this task
		//if (toaManager.zebak.bloods.isEmpty() || !toaManager.zebak.waves.isEmpty() || !toaManager.zebak.safeRockTiles.isEmpty())
		if (toaManager.zebak.bloods.isEmpty() || !toaManager.zebak.safeRockTiles.isEmpty())
		{
			toaManager.print("Return bloods 1");
			return false;
		}
		if (!toaManager.zebak.waves2.isEmpty())
		{
			//Need to add the waves check to ensure player isnt afking in wave tiles while waiting for bloods to get close
			if (toaManager.zebak.distanceToBlood() <= 1 || toaManager.zebak.waves2.contains(client.getLocalPlayer().getWorldLocation()))
			{
				WorldPoint safeTile = getSafeWaveTile(client.getLocalPlayer().getWorldLocation());
				if (safeTile == null)
				{
					return false;
				}
				toaManager.print("Moving away from blood to " + toaManager.worldPointString(safeTile));
				Movement.walk(safeTile);
				return true;
			}
		}
		else
		{
			if (toaManager.zebak.distanceToBlood() <= 1)
			{
				WorldPoint safeTile = getSafeTile(client.getLocalPlayer().getWorldLocation());
				if (safeTile == null)
				{
					return false;
				}
				toaManager.print("Moving away from blood to " + toaManager.worldPointString(safeTile));
				Movement.walk(safeTile);
				return true;
			}

			if (toaManager.gameTickManager.isAttackWaiting() && toaManager.zebak.distanceToBlood() <= 3)
			{
				WorldPoint safeTile = getSafeTileInRange(client.getLocalPlayer().getWorldLocation());
				if (safeTile == null)
				{
					return false;
				}
				toaManager.print("Moving away from blood to " + toaManager.worldPointString(safeTile));
				Movement.walk(safeTile);
				return true;
			}
		}
		return false;
	}

	private WorldPoint getSafeTileInRange(WorldPoint player)
	{
		WorldArea area = WorldAreas.createArea(player.dx(-2).dy(-2), player.dx(3).dy(3));
		ArrayList<WorldPoint> safeTiles = new ArrayList<>();
		for (WorldPoint wp : area.toWorldPointList())
		{
			if (wp.distanceTo(player) > 3)
			{
				continue;
			}
			if (!toaManager.zebak.bloods.contains(wp)
				&& !toaManager.zebak.poisonWorldPoints.contains(wp)
				&& wp.distanceTo(toaManager.zebak.zebakBoss.getWorldArea()) <= toaManager.zebak.getAttackDistance()
				//&& wp.distanceTo(toaManager.zebak.zebakBoss.getWorldArea()) > 1
				&& !toaManager.zebak.meleeRange(wp)
				&& Reachable.isWalkable(wp))
			{
				safeTiles.add(wp);
			}
		}
		WorldPoint closestBlood = toaManager.zebak.closestBlood();
		if (closestBlood == null)
		{
			toaManager.print("Closest blood is null in zebak solve bloods task getSafeTileInRange");
			return null;
		}
		int furthestTileDistance = toaManager.findFurthestTile(safeTiles, closestBlood).distanceTo(closestBlood);
		ArrayList<WorldPoint> bestTiles = new ArrayList<>();
		for (WorldPoint wp : safeTiles)
		{
			if (wp.distanceTo(closestBlood) >= furthestTileDistance)
			{
				bestTiles.add(wp);
			}
		}
		if (bestTiles.isEmpty())
		{
			return toaManager.findFurthestTile(safeTiles, closestBlood);
		}
		return toaManager.findClosestTile(bestTiles, toaManager.zebak.centerOfZebakRoom());
	}

	private WorldPoint getSafeTile(WorldPoint player)
	{
		WorldArea area = WorldAreas.createArea(player.dx(-2).dy(-2), player.dx(3).dy(3));
		ArrayList<WorldPoint> safeTiles = new ArrayList<>();
		for (WorldPoint wp : area.toWorldPointList())
		{
			if (wp.distanceTo(player) > 3)
			{
				continue;
			}
			if (!toaManager.zebak.bloods.contains(wp)
				&& !toaManager.zebak.poisonWorldPoints.contains(wp)
				&& !toaManager.zebak.meleeRange(wp)
//				&& wp.distanceTo(toaManager.zebak.zebakBoss.getWorldArea()) > 1
				&& Reachable.isWalkable(wp))
			{
				safeTiles.add(wp);
			}
		}
		WorldPoint closestBlood = toaManager.zebak.closestBlood();
		if (closestBlood == null)
		{
			toaManager.print("Closest blood is null in zebak solve bloods task getSafeTile");
			return null;
		}
		return toaManager.findFurthestTile(safeTiles, closestBlood);
	}

	private WorldPoint getSafeWaveTile(WorldPoint player)
	{
		WorldArea area = WorldAreas.createArea(player.dx(-2).dy(-2), player.dx(3).dy(3));
		ArrayList<WorldPoint> safeTiles = new ArrayList<>();
		for (WorldPoint wp : area.toWorldPointList())
		{
			if (wp.distanceTo(player) > 2)
			{
				continue;
			}
			if (!toaManager.zebak.bloods.contains(wp)
				&& !toaManager.zebak.poisonWorldPoints.contains(wp)
				&& !toaManager.zebak.meleeRange(wp)
				&& !toaManager.zebak.waves2.contains(wp)
				&& Reachable.isWalkable(wp)
				&& isVerticalHorizontal(player, wp))
			{
				safeTiles.add(wp);
			}
		}
		WorldPoint closestBlood = toaManager.zebak.closestBlood();
		if (closestBlood == null)
		{
			toaManager.print("Closest blood is null in zebak solve bloods task getSafeTile");
			return null;
		}
		return toaManager.findFurthestTile(safeTiles, closestBlood);
	}

	public boolean isVerticalHorizontal(WorldPoint playerPoint, WorldPoint destinationPoint)
	{
		if (playerPoint.getY() == destinationPoint.getY())
		{
			return true;
		}
		if (playerPoint.getX() == destinationPoint.getX())
		{
			return true;
		}
		return Math.abs(playerPoint.getX() - destinationPoint.getX()) == Math.abs(playerPoint.getY() - destinationPoint.getY());
	}
}