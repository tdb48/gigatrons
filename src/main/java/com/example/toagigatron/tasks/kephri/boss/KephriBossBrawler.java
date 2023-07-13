package com.example.toagigatron.tasks.kephri.boss;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.Utility.NPCUtil;
import com.example.Utility.Reachable;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.bossmodel.KephriRowTest;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Kephri Boss Brawler",
	priority = 50
)
public class KephriBossBrawler extends StagedTask
{

	@Inject
	GameTickManager gameTickManager;

	private final List<Integer> nonMeleeTileIndexs = new ArrayList<>(Arrays.asList(5, 11, 17, 23));

	@Inject
	public KephriBossBrawler(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_BOSS);
	}

	@Override
	public boolean execute()
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		NPC resetGhost = NPCUtil.findNearestNpcAliveOrDead(ToaConstants.OSMUMTEN);
		NPC demi = NPCUtil.findNearest("Spitting Scarab", "Arcane Scarab", "Soldier Scarab");

		if (toaManager.kephri.kephri == null
			|| !NPCUtil.hasAction(toaManager.kephri.kephri, "Attack")
			|| toaManager.kephri.kephriRoom == null
			|| !toaManager.kephri.kephriRoom.contains(playerPoint)
			|| resetGhost != null
			|| demi != null)
		{
			return false;
		}
		if (client.getLocalPlayer().getAnimation() == 9799 || toaManager.kephri.dungEscape)
		{
			return false;
		}
		if (shouldWeSpec())
		{
			toaManager.print("Toggling spec");
			Combat.toggleSpec();
		}
		KephriRowTest row = toaManager.kephri.optimalRow;
		if (row == null)
		{
			toaManager.print("Optimal row is null in kephri boss brawler, returning false.");
			return false;
		}
		WorldPoint stepBackTile = row.stepBack;
		WorldPoint meleeTile = row.meleeTile;
		WorldPoint dodgeTile = getSafeTile();
		WorldPoint p5Tile = toaManager.kephri.bombTiles.contains(meleeTile) ? row.stepBack : row.meleeTile;
		if (client.getVarbitValue(Varbits.BOSS_HEALTH_CURRENT) < 60
			&& toaManager.kephri.kephriPhase == 5
			&& (toaManager.kephri.dungGraphicTick > 0 || !onStartRow(playerPoint))) //getting double dunged or not on start row, dont attack or stop attacking
		{
			if (client.getLocalPlayer().getInteracting() != null)
			{
				toaManager.print("50HP - Stop interacting with Kephri");
				Movement.walk(playerPoint);
				return false;
			}
			if (toaManager.kephri.kephriBombTick == 2
				&& toaManager.kephri.bombTiles.contains(playerPoint))
			{
				if (playerPoint.equals(p5Tile))
				{
					toaManager.print("50HP - Returning in step back up top");
					return false;
				}
				Movement.walk(p5Tile);
				toaManager.print("50HP - Stepping back up top");
				return true;
			}
//			KephriRowTest currentRow = getRow(dodgeTile);
			KephriRowTest currentRow = toaManager.kephri.optimalRow;
			WorldPoint startTile = null;
			if (currentRow != null)
			{
				startTile = currentRow.meleeTile;
				toaManager.print("Setting startTile var to currentRow meleetile - " + toaManager.worldPointString(currentRow.meleeTile));
			}
			if (toaManager.kephri.bombTiles.isEmpty() && startTile != null)
			{
				toaManager.print("50HP - Moving to start tile");
				Movement.walk(startTile);
				return true;
			}
			toaManager.print("50HP - Idle because below 50 with dung on us");
			return false;
		}

		if (gameTickManager.isAttackWaiting() && toaManager.kephri.dungGraphicTick == 0)
		{
			if (dodgeTile == null)
			{
				toaManager.print("We are lost in the sauce in kephri attack boss");
				return false;
			}
			HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.kephri.bombTiles);
			toaManager.kephri.kephriPath = EthanApiPlugin.pathToGoal(dodgeTile, dangerTiles);

			if (toaManager.kephri.kephriPath == null || toaManager.kephri.kephriPath.isEmpty())
			{
				toaManager.print("Empty path?");
				return false;
			}


			if (toaManager.kephri.kephriPhase == 5 && client.getVarbitValue(Varbits.BOSS_HEALTH_CURRENT) < 60)
			{
				toaManager.print("Phase is 5 and boss health is under 55");
				// Step back
				if (toaManager.kephri.kephriBombTick == 2
					&& toaManager.kephri.bombTiles.contains(playerPoint))
				{
					toaManager.print("Stepback in phase 5 check");
					if (playerPoint.equals(stepBackTile))
					{
						toaManager.print("Returning in step back - we are already here this seems wrong?????");
						return false;
					}
					Movement.walk(stepBackTile);
					toaManager.print("Stepping back to stepback tile");
					return true;
				}
				else
				{
					toaManager.print("Phase is 5 but the stepback logic is failing");
				}
			}
			else
			{

				// Sidestep
				if (toaManager.kephri.kephriPath.size() <= 2
					&& toaManager.kephri.kephriTick <= 3
					&& !toaManager.kephri.bombTiles.isEmpty())
				{
					if (playerPoint.equals(dodgeTile))
					{
						toaManager.print("Returning in sidestep - playerpoint equals dodgetile - " + toaManager.worldPointString(dodgeTile));
						return false;
					}
					Movement.walk(dodgeTile);
					toaManager.print("Stepping sideways to " + toaManager.worldPointString(dodgeTile));
					return true;
				}
				// Corner
				if (toaManager.kephri.kephriPath.size() > 2
					&& toaManager.kephri.kephriTick >= 2
					&& toaManager.kephri.kephriTick <= 4
					&& gameTickManager.attackWait >= 2
					&& !toaManager.kephri.bombTiles.isEmpty())
				{
					//toaManager.print("path: " + toaManager.kephri.kephriPath.size() + ", tick: " + toaManager.kephri.kephriTick + ", att wait: " + gameTickManager.attackWait);
					if (playerPoint.equals(dodgeTile))
					{
						toaManager.print("Returning in corner");
						return false;
					}
					Movement.walk(dodgeTile);
					toaManager.print("Corner tile dodge");
					return true;
				}
				// Step back
				else if (toaManager.kephri.kephriBombTick == 2
					&& toaManager.kephri.bombTiles.contains(playerPoint)
					&& toaManager.kephri.kephriPath.size() >= 3)
				{
					if (playerPoint.equals(stepBackTile))
					{
						toaManager.print("Returning in step back");
						return false;
					}
					Movement.walk(stepBackTile);
					toaManager.print("Stepping back");
					return true;
				}
			}
		}
		if (toaManager.kephri.kephriBombTick == 2 && toaManager.kephri.bombTiles.contains(playerPoint))
		{
			WorldPoint safeBombTile = safeBombTile(playerPoint);
			Movement.walk(safeBombTile);
			toaManager.print("Edge case - last resort dodging");
			return true;
		}
		if (client.getLocalPlayer().getInteracting() == null)
		{
			toaManager.print("Attacking kephri");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(toaManager.kephri.kephri, "Attack");
			return true;
		}
		return false;


//
//		if (toaManager.kephri.melee != null && !playerPoint.equals(toaManager.kephri.melee) && toaManager.kephri.pathToEfficientStartTile == null)
//		{
//			toaManager.print("Path to start tile is null");
//		}
//		else
//		{
//			toaManager.print("Found a path how good.");
//		}
//		if (toaManager.kephri.kephriBombTick == 2 && toaManager.kephri.bombTiles.contains(playerPoint))
//		{
//			WorldPoint safeBombTile = safeBombTile(playerPoint);
//			Movement.walk(safeBombTile);
//			toaManager.print("Edge case - last resort dodging");
//			return true;
//		}
//		if (!playerPoint.equals(toaManager.kephri.melee)
//			&& !toaManager.kephri.bombTiles.contains(toaManager.kephri.melee)
//			&& toaManager.kephri.pathToEfficientStartTile != null
//		&& toaManager.kephri.dungGraphicTick <= 0)
//		{
//			toaManager.print("Moving back to melee tile to grug smash bug");
//			Walker.stepAlong(toaManager.kephri.pathToEfficientStartTile);
//			return true;
//
//		}
//
//
//		return false;
	}


	//todo rewrite the logic for creating dodge tiles
	// instead of starting at melee tile index and iterating forward until a valid tile found,
	// we should start at player index and iterate outwards in all directions until a valid tile found,
	// this will give us the best tile rather than the first tile
	private WorldPoint getSafeTile()
	{
		if (toaManager.kephri.optimalRow == null || toaManager.kephri.kephriRows == null || toaManager.kephri.kephriRows.size() == 0)
		{
			return null;
		}
		int startIndex = Math.max(toaManager.kephri.optimalRow.index, 0);
		for (int i = startIndex; i < toaManager.kephri.kephriRows.size(); i++)
		{
			KephriRowTest row = toaManager.kephri.kephriRows.get(i);
			if (nonMeleeTileIndexs.contains(row.index))
			{
				continue;
			}

			if (Reachable.isWalkable(row.meleeTile) && !toaManager.kephri.bombTiles.contains(row.meleeTile)
				&& !toaManager.kephri.dungGraphicPoints.containsKey(row.meleeTile) && TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(row.meleeTile).empty())
			{
				return row.meleeTile;
			}
		}
		return null;
	}

	private WorldPoint safeBombTile(WorldPoint playerPoint)
	{
		WorldPoint southWest = playerPoint.dx(-2).dy(-2);
		WorldPoint northEast = playerPoint.dx(3).dy(3);
		// 5x5 around player
		ArrayList<WorldPoint> areaAroundPlayer = (ArrayList<WorldPoint>) WorldAreas.createArea(southWest, northEast).toWorldPointList();
		areaAroundPlayer.removeIf(n -> !Reachable.isWalkable(n));
		areaAroundPlayer.removeAll(toaManager.kephri.bombTiles);
		if (areaAroundPlayer.contains(playerPoint))
		{
			// If the playerpoint is a safetile, return that
			return playerPoint;
		}
		ArrayList<WorldPoint> testPath;
		HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.kephri.bombTiles);
		if (toaManager.kephri.kephriPhase == 5)
		{
			if (areaAroundPlayer.contains(toaManager.kephri.melee))
			{
				testPath = EthanApiPlugin.pathToGoal(toaManager.kephri.melee, dangerTiles);
				if (testPath != null && testPath.size() <= 2)
				{
					return toaManager.kephri.melee;
				}
				if (testPath == null)
				{
					toaManager.print("Path is null in safe bomb tile");
				}
			}
			if (areaAroundPlayer.contains(toaManager.kephri.stepBack))
			{
				testPath = EthanApiPlugin.pathToGoal(toaManager.kephri.stepBack, dangerTiles);
				if (testPath != null && testPath.size() <= 2)
				{
					return toaManager.kephri.stepBack;
				}
				if (testPath == null)
				{
					toaManager.print("Path is null in safe bomb tile");
				}
			}
		}
		for (WorldPoint worldPoint : areaAroundPlayer)
		{
			testPath = EthanApiPlugin.pathToGoal(worldPoint, dangerTiles);
			if (testPath != null && testPath.size() <= 2)
			{
				return worldPoint;
			}
			if (testPath == null)
			{
				toaManager.print("Path is null in safe bomb tile");
			}
		}
		return null;
	}


	private boolean shouldWeSpec()
	{

		if (Equipment.search().withId(ItemID.OSMUMTENS_FANG).empty() || Combat.isSpecEnabled())
		{
			return false;
		}
		if (toaManager.kephri.kephriPhase == 5)
		{
			return toaManager.getBossHp() > 50 && Combat.getSpecEnergy() >= 25;
		}

		if (toaManager.kephri.kephriPhase > 5)
		{
			return Combat.getSpecEnergy() >= 25;
		}
		if (toaManager.kephri.kephriPhase == 3 && toaManager.getBossHp() < 100)
		{
			return false;
		}
		return toaManager.getBossHp() > 50 && Combat.getSpecEnergy() >= 95;
	}

	private boolean onStartRow(WorldPoint playerPoint)
	{
		KephriRowTest row = toaManager.kephri.optimalRow;
		if (row == null)
		{
			return false;
		}
		return playerPoint.equals(row.meleeTile) ||
			playerPoint.equals(row.stepBack);
	}

	private KephriRowTest getRow(WorldPoint worldPoint)
	{
		for (KephriRowTest row : toaManager.kephri.kephriRows)
		{
			if (worldPoint.equals(row.meleeTile))
			{
				return row;
			}
		}
		return null;
	}
}
