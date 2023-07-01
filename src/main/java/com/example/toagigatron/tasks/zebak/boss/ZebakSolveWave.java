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
import java.util.HashSet;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Zebak solve wave",
	priority = 1,
	blocking = true
)
public class ZebakSolveWave extends StagedTask
{
	@Inject
	public ZebakSolveWave(ToaManager toaManager)
	{
		super(toaManager, Stage.ZEBAK_BOSS);
	}

	/**
	 * TIDAL WAVES - Get all wave game objects, iterate them all finding one that has sand after it. Continue iterating until we find sand that has wave after it
	 * Once we know the width of the gap in the wave, we create a world area that is the width of the gap, and the length of gap -> end of room
	 * Once we have the world area, remove all tiles that are poison
	 * Attempt to find a path from player tile to somewhere in the wave world area, then walk it
	 */
	public boolean execute()
	{
		NPC wave = NPCUtil.findNearest(ToaConstants.ZEBAK_WAVE);
		if (wave == null || toaManager.zebak.zebakBoss == null)
		{
			return false;
		}
		if (!toaManager.zebak.bloods.isEmpty())
		{
			return false;
		}
		boolean south = wave.getOrientation() == 0;
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		ArrayList<NPC> currentWaves = null;
		WorldArea currentArea = null;
		//toaManager.print("Wave to center tile delta -> " + Math.abs(toaManager.zebak.wavesOne.get(0).getWorldLocation().getY() - centerEastZebak.getY()));
		if (south)
		{
			if (!toaManager.zebak.wavesOne.isEmpty()
				&& playerPoint.getY() < (toaManager.zebak.wavesOne.get(0).getWorldLocation().getY()))
			{
				//&& Math.abs(toaManager.zebak.wavesOne.get(0).getWorldLocation().getY() - centerEastZebak.getY()) < 5
				currentWaves = toaManager.zebak.wavesOne;
				currentArea = toaManager.zebak.wavesOneSafe;
			}
			else if (!toaManager.zebak.wavesTwo.isEmpty() && playerPoint.getY() < (toaManager.zebak.wavesTwo.get(0).getWorldLocation().getY()))
			{
				currentWaves = toaManager.zebak.wavesTwo;
				currentArea = toaManager.zebak.wavesTwoSafe;
			}
			else if (!toaManager.zebak.wavesThree.isEmpty() && playerPoint.getY() < (toaManager.zebak.wavesThree.get(0).getWorldLocation().getY()))
			{
				currentWaves = toaManager.zebak.wavesThree;
				currentArea = toaManager.zebak.wavesThreeSafe;
			}
		}
		else
		{
			if (!toaManager.zebak.wavesOne.isEmpty()
				&& playerPoint.getY() > (toaManager.zebak.wavesOne.get(0).getWorldLocation().getY()))
			//&& Math.abs(toaManager.zebak.wavesOne.get(0).getWorldLocation().getY() - centerEastZebak.getY()) < 5
			{
				currentWaves = toaManager.zebak.wavesOne;
				currentArea = toaManager.zebak.wavesOneSafe;
			}
			else if (!toaManager.zebak.wavesTwo.isEmpty() && playerPoint.getY() > (toaManager.zebak.wavesTwo.get(0).getWorldLocation().getY()))
			{
				currentWaves = toaManager.zebak.wavesTwo;
				currentArea = toaManager.zebak.wavesTwoSafe;
			}
			else if (!toaManager.zebak.wavesThree.isEmpty() && playerPoint.getY() > (toaManager.zebak.wavesThree.get(0).getWorldLocation().getY()))
			{
				currentWaves = toaManager.zebak.wavesThree;
				currentArea = toaManager.zebak.wavesThreeSafe;
			}

		}
		if (currentWaves == null || currentArea == null)
		{
			return false;
		}

		ArrayList<WorldPoint> waveAreaWithoutPoison = new ArrayList<>(currentArea.toWorldPointList());
		waveAreaWithoutPoison.removeIf(n -> !Reachable.isWalkable(n));
		waveAreaWithoutPoison.removeIf(n -> toaManager.zebak.poisonWorldPoints.contains(n));
		waveAreaWithoutPoison.removeIf(n -> toaManager.zebak.bloods.contains(n));
		waveAreaWithoutPoison.removeIf(n -> toaManager.zebak.waves.contains(n));
		waveAreaWithoutPoison.removeIf(n -> n.distanceTo(toaManager.zebak.zebakBoss.getWorldArea()) <= 1);
		waveAreaWithoutPoison.removeIf(n -> toaManager.zebak.distanceToBlood(n) <= 1);
		WorldPoint bestTile = toaManager.findClosestTile(waveAreaWithoutPoison, toaManager.zebak.zebakBoss.getWorldArea().toWorldPoint());
		if (bestTile == null)
		{
			toaManager.print("Best tile is null in zebak solve wave method");
			return false;
		}
		HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.zebak.poisonWorldPoints);
		dangerTiles.addAll(toaManager.zebak.bloods);
		dangerTiles.addAll(toaManager.zebak.getChompZone());
		toaManager.zebak.path = EthanApiPlugin.pathToGoal(bestTile, dangerTiles);
		if (toaManager.zebak.path == null || toaManager.zebak.path.isEmpty())
		{
			toaManager.zebak.getChompZone().forEach(dangerTiles::remove);
			toaManager.zebak.path = EthanApiPlugin.pathToGoal(bestTile, dangerTiles);
		}
//		toaManager.zebak.path = com.example.toagigatron.model.pathing.Movement.getAvoidancePath(bestTile, toaManager.zebak.toaCollisionMap, toaManager.zebak.allWalkableRoomTiles, toaManager.zebak.poisonWorldPoints, toaManager.lpToWp(toaManager.zebak.rockTiles), true);
//		if(toaManager.zebak.path.size() == 0){
//			toaManager.zebak.path = com.example.toagigatron.model.pathing.Movement.getAvoidancePath(bestTile, toaManager.zebak.toaCollisionMap, toaManager.zebak.allWalkableRoomTilesIncludingChompZone, toaManager.zebak.poisonWorldPoints, toaManager.lpToWp(toaManager.zebak.rockTiles), true);
//		}
		int distance = toaManager.zebak.getAttackDistance();
		if (toaManager.zebak.distanceToBlood() <= 1 || canWeAttackZebak(bestTile, currentArea)
//			!currentArea.toWorldPointList().contains(playerPoint)
		)
		{
			toaManager.print("Moving to " + toaManager.worldPointString(bestTile));
			Walker.stepAlong(toaManager.zebak.path);
			return true;
		}
		else
		{
			if (toaManager.zebak.distanceToZebak() <= distance)
			{
				if (client.getLocalPlayer().getInteracting() == null)
				{
					MousePackets.queueClickPacket();
					NPCPackets.queueNPCAction(toaManager.zebak.zebakBoss, "Attack");
					return true;
				}
			}
		}

		return false;
	}

	public boolean canWeAttackZebak(WorldPoint bestTile, WorldArea currentArea)
	{
		if (!currentArea.toWorldPointList().contains(client.getLocalPlayer().getWorldLocation()))
		{
			return true;
		}
		// If we can already attack, dont care about moving
		if (toaManager.zebak.distanceToZebak() <= toaManager.zebak.getAttackDistance())
		{
			return false;
		}
		// If we can attack from the best tile, we should move
		return bestTile.distanceTo(toaManager.zebak.zebakBoss.getWorldArea()) <= toaManager.zebak.getAttackDistance();
	}


}