package com.example.toagigatron.tasks.baba.boss;


import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Utility.NPCUtil;
import com.example.Utility.Reachable;
import com.example.Utility.Walker;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Baba dodge special",
	priority = 10
)
public class BabaDodgeSpecial extends StagedTask
{
	@Inject
	public BabaDodgeSpecial(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_BOSS);
	}

	//todo we can update these to be WAY more efficient now that our pathing works well and we can path through or around specials like shockwave tiles/under boss
	public boolean execute()
	{
		toaManager.baba.attackPath = null;
		NPC weakBoulder = NPCUtil.findNearest(ToaConstants.WEAK_BOULDER);
		if (weakBoulder != null)
		{
			return false;
		}
		if (!toaManager.baba.blockTiles.isEmpty()
			&& toaManager.baba.bouldersKilled != 7
			&& toaManager.baba.bouldersKilled != 14)
		{
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		// Run out of gap on proc
		// If standing in gap, boss has procced and haven't killed any boulders of current set
		if (toaManager.baba.hasProcced()
			&& (toaManager.baba.bouldersKilled == 7 || toaManager.baba.bouldersKilled == 0))
		{
			//toaManager.print("Boss is proccing - running out of gap");
			ArrayList<WorldPoint> safeTiles = new ArrayList<>(toaManager.baba.babaBossRoom);
			safeTiles.removeAll(toaManager.baba.babaBossRowGap.toWorldPointList());
			safeTiles.removeIf(n -> !Reachable.isWalkable(n));
			safeTiles.removeAll(toaManager.baba.badTiles);
			WorldPoint safeTile = toaManager.findClosestTile(safeTiles, playerPoint);
			if (playerPoint.equals(safeTile))
			{
				//toaManager.print("We are already standing on a safe tile.");
				return false;
			}
			HashSet<WorldPoint> dangerTiles = new HashSet<>();
			toaManager.baba.attackPath = EthanApiPlugin.pathToGoal(safeTile, dangerTiles);
			Walker.stepAlong(toaManager.baba.attackPath);
			//toaManager.print("22222Boss is proccing - running out of gap");
			return true;
		}

		if (toaManager.baba.babaBoss == null
			|| !toaManager.baba.badTiles.contains(playerPoint))
		{
			return false;
		}
		WorldPoint refPoint = WorldAreas.getCenter(toaManager.baba.babaBoss.getWorldArea());
		WorldPoint southWest = refPoint.dx(-3).dy(-3);
		WorldPoint northEast = refPoint.dx(4).dy(4);
		ArrayList<WorldPoint> babaMeleeTiles = (ArrayList<WorldPoint>) WorldAreas.createArea(southWest, northEast).toWorldPointList();
		WorldPoint reference = WorldAreas.getCenter(toaManager.baba.babaBoss.getWorldArea());
		babaMeleeTiles.removeIf(n -> toaManager.isDiagonalOf(n, reference));
		babaMeleeTiles.removeAll(toaManager.baba.badTiles);
		babaMeleeTiles.removeIf(n -> !Reachable.isWalkable(n));
		babaMeleeTiles.removeAll(toaManager.baba.tilesUnderBoss());
		if (!babaMeleeTiles.isEmpty()
//			&& !toaManager.baba.closeToProccing()
			&& toaManager.baba.rockfallTick == 0)
		{
			WorldPoint safeTile = toaManager.findClosestTile(babaMeleeTiles, playerPoint);
			if (safeTile == null)
			{
				return false;
			}
			toaManager.print("In danger, moving to melee tile " + toaManager.worldPointString(safeTile));
			HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.baba.badTiles);
			dangerTiles.removeIf(x -> toaManager.baba.shockwaveTiles.contains(x));
			toaManager.baba.attackPath = EthanApiPlugin.pathToGoal(safeTile, dangerTiles);
			Walker.stepAlong(toaManager.baba.attackPath);
		}
		else
		{
			ArrayList<WorldPoint> safeTiles = new ArrayList<>(toaManager.baba.babaBossRoom);
			safeTiles.removeAll(toaManager.baba.badTiles);
			safeTiles.removeIf(n -> !Reachable.isWalkable(n));
			safeTiles.removeAll(toaManager.baba.tilesUnderBoss());
			WorldPoint safeTile = toaManager.findClosestTile(safeTiles, playerPoint);
			if (safeTile == null)
			{
				return false;
			}
			toaManager.print("In danger, moving to " + toaManager.worldPointString(safeTile));
			HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.baba.bananaTiles);
			toaManager.baba.attackPath = EthanApiPlugin.pathToGoal(safeTile, dangerTiles);
			Walker.stepAlong(toaManager.baba.attackPath);
		}
		return true;
	}
}
