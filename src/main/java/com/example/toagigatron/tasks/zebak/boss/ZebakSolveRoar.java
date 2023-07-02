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
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Zebak solve roar"
)
public class ZebakSolveRoar extends StagedTask
{
	@Inject
	public ZebakSolveRoar(ToaManager toaManager)
	{
		super(toaManager, Stage.ZEBAK_BOSS);
	}

	/**
	 * *GREAT ROAR
	 * * solve for which jug we will need to use while projectiles are in the air
	 * * Add all the potentially poison tiles to a list and exclude them list of worldpoints to be used as potential tiles in path to
	 * * Find a path toward the jug and start pathing immediately
	 * * Once the poison lands, add back all the tiles that did NOT get poisoned to the list of worldpoints to be potentially used as path tiles
	 * * Plugin will generate a new path every tick so it might find a better path after this happens
	 */

	public boolean execute()
	{
		/* 	1. Run to safe tile
		 *  2. Shooting a jug that can solve it?
		 *  3. push or pulling if on correct tile
		 *  4. running to push or pull tile
		 * 	*/
		if (toaManager.zebak.rockTiles.isEmpty())
		{
			return false;
		}
		// Reset it so overlay looks so good
		toaManager.zebak.path = null;

		LocalPoint playerPoint = client.getLocalPlayer().getLocalLocation();
		WorldPoint playerWorldPoint = client.getLocalPlayer().getWorldLocation();
		ArrayList<WorldPoint> safeTiles = toaManager.lpToWp(toaManager.zebak.safeRockTiles);
		safeTiles.removeAll(toaManager.lpToWp(toaManager.zebak.poisonTiles));
		WorldPoint closestSafeTile = null;
		if (toaManager.zebak.getSafeRoarTile() != null)
		{
			closestSafeTile = WorldPoint.fromLocal(client, toaManager.zebak.getSafeRoarTile());
		}
		// Return if on correct tile and theres nothing in the air
		if (toaManager.zebak.isRoarSolved()
			&& !isProjectileInTheAir()
			&& toaManager.zebak.bloods.isEmpty()
			&& playerWorldPoint.equals(closestSafeTile))
		{
			return false;
		}
		NPC wave = NPCUtil.findNearest(ToaConstants.ZEBAK_WAVE);
		if (wave != null)
		{
			return false;
		}

		// Step 1. Walk to safe tile
		if (!isProjectileInTheAir() && toaManager.zebak.isRoarSolved() && closestSafeTile != null && !playerWorldPoint.equals(closestSafeTile))
		{
			if (!toaManager.zebak.bloods.isEmpty() && toaManager.zebak.distanceToBlood() <= 3)
			{
				WorldPoint safeTile = getSafeTile(client.getLocalPlayer().getWorldLocation());
				if (safeTile == null)
				{
					return false;
				}
				toaManager.print("ROAR Moving away from blood to " + toaManager.worldPointString(safeTile));
				Movement.walk(safeTile);
				return true;
			}
			else
			{
				HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.zebak.poisonWorldPoints);
				dangerTiles.addAll(toaManager.zebak.bloods);
				dangerTiles.addAll(toaManager.zebak.getChompZone());
				toaManager.zebak.path = EthanApiPlugin.pathToGoal(closestSafeTile, dangerTiles);
				if (toaManager.zebak.path == null || toaManager.zebak.path.isEmpty())
				{
					toaManager.zebak.getChompZone().forEach(dangerTiles::remove);
					toaManager.zebak.path = EthanApiPlugin.pathToGoal(closestSafeTile, dangerTiles);
					if (toaManager.zebak.path == null || toaManager.zebak.path.isEmpty())
					{
						toaManager.zebak.poisonWorldPoints.forEach(dangerTiles::remove);
						toaManager.zebak.path = EthanApiPlugin.pathToGoal(closestSafeTile, dangerTiles);
					}
				}

//				toaManager.zebak.path = com.example.toagigatron.model.pathing.Movement.getAvoidancePath(closestSafeTile, toaManager.zebak.toaCollisionMap, toaManager.zebak.allWalkableRoomTiles, toaManager.zebak.poisonWorldPoints, toaManager.lpToWp(toaManager.zebak.rockTiles), true);
//				if (toaManager.zebak.path.size() == 0)
//				{
//					toaManager.zebak.path = com.example.toagigatron.model.pathing.Movement.getAvoidancePath(closestSafeTile, toaManager.zebak.toaCollisionMap, toaManager.zebak.allWalkableRoomTilesIncludingChompZone, toaManager.zebak.poisonWorldPoints, toaManager.lpToWp(toaManager.zebak.rockTiles), true);
//				}
				Walker.stepAlong(toaManager.zebak.path);
				return true;
			}
		}

		if (!toaManager.zebak.rollingJugs.isEmpty() && !toaManager.zebak.bloods.isEmpty() && toaManager.zebak.distanceToBlood() <= 3)
		{
			WorldPoint safeTile = getSafeTile(client.getLocalPlayer().getWorldLocation());
			if (safeTile == null)
			{
				return false;
			}
			toaManager.print("ROAR Moving away from blood to " + toaManager.worldPointString(safeTile));
			Movement.walk(safeTile);
			return true;
		}


		// Step 2. Shoot the jug or walk into range of a rolling jug so you don't get dragged when blowpiping it
		if (!toaManager.zebak.rollingJugs.isEmpty())
		{
			for (LocalPoint lp : toaManager.zebak.rollingJugs)
			{
				if (shouldHitJug(lp))
				{
					ArrayList<NPC> jug = (ArrayList<NPC>) NPCs.search().withId(ToaConstants.ZEBAK_ROLLING_JUG).filter(n -> n.getLocalLocation().equals(lp)).result();
//					ArrayList<NPC> jug = (ArrayList<NPC>) NPCs.getAll(n ->
//						n.getId() == ToaConstants.ZEBAK_ROLLING_JUG
//							&& n.getLocalLocation().equals(lp));
					if (!jug.isEmpty() && jug.get(0) != null)
					{
						toaManager.print("Hitting the jug");
						MousePackets.queueClickPacket();
						NPCPackets.queueNPCAction(jug.get(0), "Attack");
						return true;
					}
					else
					{
						toaManager.print("is invalid?");
					}
				}
				if (playerPoint.distanceTo(lp) >= 256)
				{
					toaManager.print("Walking into range");
					WorldPoint southWest = client.getLocalPlayer().getWorldLocation().dx(-2).dy(-2);
					WorldPoint northEast = client.getLocalPlayer().getWorldLocation().dx(3).dy(3);
					ArrayList<WorldPoint> worldAreaList = (ArrayList<WorldPoint>) WorldAreas.createArea(southWest, northEast).toWorldPointList();
					ArrayList<LocalPoint> tilesAroundPlayer = new ArrayList<>();
					for (WorldPoint wp : worldAreaList)
					{
						tilesAroundPlayer.add(LocalPoint.fromWorld(client, wp));
					}
					tilesAroundPlayer.removeAll(toaManager.zebak.poisonTiles);
					tilesAroundPlayer.removeAll(toaManager.zebak.rockTiles);
					tilesAroundPlayer.removeAll(toaManager.zebak.staticJugs);
					LocalPoint targetPoint = toaManager.findClosestTile(tilesAroundPlayer, lp);

					if (targetPoint == null)
					{
						toaManager.print("Failing to find zebak path in solve roar");
						return false;
					}
					HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.zebak.poisonWorldPoints);
					dangerTiles.addAll(toaManager.zebak.bloods);
					dangerTiles.addAll(toaManager.zebak.getChompZone());
					toaManager.zebak.path = EthanApiPlugin.pathToGoal(WorldPoint.fromLocal(client, targetPoint), dangerTiles);
					if (toaManager.zebak.path == null || toaManager.zebak.path.isEmpty())
					{
						toaManager.zebak.getChompZone().forEach(dangerTiles::remove);
						toaManager.zebak.path = EthanApiPlugin.pathToGoal(WorldPoint.fromLocal(client, targetPoint), dangerTiles);
						if (toaManager.zebak.path == null || toaManager.zebak.path.isEmpty())
						{
							toaManager.zebak.poisonWorldPoints.forEach(dangerTiles::remove);
							toaManager.zebak.path = EthanApiPlugin.pathToGoal(WorldPoint.fromLocal(client, targetPoint), dangerTiles);
						}
					}
//					toaManager.zebak.path = com.example.toagigatron.model.pathing.Movement.getAvoidancePath(WorldPoint.fromLocal(client, targetPoint), toaManager.zebak.toaCollisionMap, toaManager.zebak.allWalkableRoomTiles, toaManager.zebak.poisonWorldPoints, toaManager.lpToWp(toaManager.zebak.rockTiles), true);
//					if(toaManager.zebak.path.size() == 0){
//						toaManager.zebak.path = com.example.toagigatron.model.pathing.Movement.getAvoidancePath(WorldPoint.fromLocal(client, targetPoint), toaManager.zebak.toaCollisionMap, toaManager.zebak.allWalkableRoomTilesIncludingChompZone, toaManager.zebak.poisonWorldPoints, toaManager.lpToWp(toaManager.zebak.rockTiles), true);
//					}
					Walker.stepAlong(toaManager.zebak.path);

					//Movement.walk(WorldPoint.fromLocal(client, targetPoint));
					return true;
				}
			}
			toaManager.print("fail step 1");
		}
		if (toaManager.zebak.rollingJugs.isEmpty() && isProjectileInTheAir() && !toaManager.zebak.bloods.isEmpty())
		{
			toaManager.print("Projectiles in the air and bloods present, dodging bloods");
			if (toaManager.zebak.distanceToBlood() <= 3)
			{
				WorldPoint safeTile = getSafeTile(client.getLocalPlayer().getWorldLocation());
				if (safeTile == null)
				{
					return false;
				}
				toaManager.print("ROAR Moving away from blood to " + toaManager.worldPointString(safeTile));
				Movement.walk(safeTile);
				return true;
			}
			return false;
		}
		if (toaManager.zebak.rollingJugs.isEmpty() && !isThereASplash() && toaManager.zebak.hittableJug != null)
		{
			LocalPoint pushOrPullTile = toaManager.zebak.hittableJug.getSolveTile();
			WorldPoint pushOrPullWorldPoint = WorldPoint.fromLocal(client, pushOrPullTile);
			String interact = toaManager.zebak.hittableJug.push ? "Push" : "Pull";

			// Step 3
			if (toaManager.zebak.hittableJug.jug != null && playerWorldPoint.equals(pushOrPullWorldPoint))
			{
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(toaManager.zebak.hittableJug.jug, interact);
				toaManager.print("Standing on correct tile, interacting: " + interact);
				return true;
			}
			else
			{
				if (toaManager.zebak.hittableJug.jug == null)
				{
					toaManager.print("Jug is null");
				}
				else
				{
					toaManager.print("playerpoint is wrong");
				}
			}
			// Step 4
			if (!playerPoint.equals(pushOrPullTile))
			{
				toaManager.print("Moving to the push or pull tile");
				WorldPoint tile = WorldPoint.fromLocal(client, pushOrPullTile);

				HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.zebak.poisonWorldPoints);
				dangerTiles.addAll(toaManager.zebak.bloods);
				dangerTiles.addAll(toaManager.zebak.getChompZone());
				toaManager.zebak.path = EthanApiPlugin.pathToGoal(tile, dangerTiles);
				if (toaManager.zebak.path == null || toaManager.zebak.path.isEmpty())
				{
					toaManager.zebak.getChompZone().forEach(dangerTiles::remove);
					toaManager.zebak.path = EthanApiPlugin.pathToGoal(tile, dangerTiles);
					if (toaManager.zebak.path == null || toaManager.zebak.path.isEmpty())
					{
						toaManager.zebak.poisonWorldPoints.forEach(dangerTiles::remove);
						toaManager.zebak.path = EthanApiPlugin.pathToGoal(tile, dangerTiles);
					}
				}
//				toaManager.zebak.path = com.example.toagigatron.model.pathing.Movement.getAvoidancePath(tile, toaManager.zebak.toaCollisionMap, toaManager.zebak.allWalkableRoomTiles, toaManager.zebak.poisonWorldPoints, toaManager.lpToWp(toaManager.zebak.rockTiles), true);
//				if(toaManager.zebak.path.size() == 0){
//					toaManager.zebak.path = com.example.toagigatron.model.pathing.Movement.getAvoidancePath(tile, toaManager.zebak.toaCollisionMap, toaManager.zebak.allWalkableRoomTilesIncludingChompZone, toaManager.zebak.poisonWorldPoints, toaManager.lpToWp(toaManager.zebak.rockTiles), true);
//				}
				Walker.stepAlong(toaManager.zebak.path);
				toaManager.print("Moving to " + toaManager.worldPointString(tile));
				return true;
			}

		}
		else
		{
			toaManager.print("There is rolling jugs or a splash");
		}
		return false;
	}

	public boolean isThereASplash()
	{
		for (Projectile p : client.getProjectiles())
		{
			if (p.getId() == ToaConstants.ZEBAK_JUG_SPLASH)
			{
				return true;
			}
		}
		return false;
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
				//&& wp.distanceTo(toaManager.zebak.zebakBoss) > 1
				&& !toaManager.zebak.meleeRange(wp)
				&& Reachable.isWalkable(wp))
			{
				safeTiles.add(wp);
			}
		}
		WorldPoint closestBlood = toaManager.zebak.closestBlood();
		if (closestBlood == null)
		{
			toaManager.print("Closest blood is null in zebak solve roar task getSafeTile");
			return null;
		}
		return toaManager.findFurthestTile(safeTiles, closestBlood);
	}

	public boolean isProjectileInTheAir()
	{
		for (Projectile p : client.getProjectiles())
		{
			if (p.getId() == ToaConstants.ZEBAK_JUG_PROJECTILE
				|| p.getId() == ToaConstants.ZEBAK_ROCK_PROJECTILE ||
				ToaConstants.ZEBAK_POISON_PROJECTILE.contains(p.getId()))
			{
				return true;
			}
		}
		return false;
	}

	public boolean shouldHitJug(LocalPoint lp)
	{
		ArrayList<LocalPoint> rockTiles = new ArrayList<>();
		rockTiles.addAll(toaManager.zebak.rockTiles);
		rockTiles.addAll(toaManager.zebak.safeRockTiles);
		for (LocalPoint rockTile : rockTiles)
		{
			if (rockTile.distanceTo(lp) <= 384)
			{
				return true;
			}
		}
		return false;
	}
}