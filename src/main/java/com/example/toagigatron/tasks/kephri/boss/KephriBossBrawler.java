//package com.example.toagigatron.tasks.kephri.boss;
//
//import com.example.EthanApiPlugin.Collections.Equipment;
//import com.example.EthanApiPlugin.Collections.TileObjects;
//import com.example.EthanApiPlugin.EthanApiPlugin;
//import com.example.Packets.MousePackets;
//import com.example.Packets.MovementPackets;
//import com.example.Packets.NPCPackets;
//import com.example.Utility.Combat;
//import com.example.Utility.Movement;
//import com.example.Utility.NPCUtil;
//import com.example.Utility.ObjectUtil;
//import com.example.Utility.Reachable;
//import com.example.Utility.Walker;
//import com.example.Utility.WorldAreas;
//import com.example.toagigatron.manager.GameTickManager;
//import com.example.toagigatron.manager.ToaManager;
//import com.example.toagigatron.model.bossmodel.KephriDungRow;
//import com.example.toagigatron.model.constants.Direction;
//import com.example.toagigatron.model.constants.Stage;
//import com.example.toagigatron.model.constants.ToaConstants;
//import com.example.toagigatron.taskformat.StagedTask;
//import com.example.toagigatron.taskformat.TaskDescriptor;
//import java.util.ArrayList;
//import java.util.HashSet;
//import javax.inject.Inject;
//import net.runelite.api.ItemID;
//import net.runelite.api.NPC;
//import net.runelite.api.Varbits;
//import net.runelite.api.coords.WorldPoint;
//
//@TaskDescriptor(
//	name = "Kephri Boss Brawler",
//	priority = 50
//)
//public class KephriBossBrawler extends StagedTask
//{
//
//	@Inject
//	GameTickManager gameTickManager;
//
//
//	@Inject
//	public KephriBossBrawler(ToaManager toaManager)
//	{
//		super(toaManager, Stage.KEPHRI_BOSS);
//	}
//
//	@Override
//	public boolean execute()
//	{
//		if (toaManager.kephri.kephriPhase == 6)
//		{
//			if (toaManager.kephri.kephri != null && client.getVarbitValue(Varbits.BOSS_HEALTH_CURRENT) < 60)
//			{
//				if (client.getLocalPlayer().isInteracting())
//				{
//					MousePackets.queueClickPacket();
//					MovementPackets.queueMovement(client.getLocalPlayer().getWorldLocation());
//				}
//				return false;
//			}
//		}
//
//		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
//
//		if (toaManager.kephri.kephri == null
//			|| !NPCUtil.hasAction(toaManager.kephri.kephri, "Attack")
//			|| toaManager.kephri.kephriRoom == null
//			|| !toaManager.kephri.kephriRoom.contains(playerPoint))
//		{
//			return false;
//		}
//		if(client.getLocalPlayer().getAnimation() == 9799 || toaManager.kephri.dungEscape){
//			return false;
//		}
//
////		KephriRowTest optimalRow = getOptimalRow();
////		if(optimalRow == null){
////			toaManager.print("Optimal row is null ive done something wrong.");
////			return false;
////		}
////		toaManager.kephri.melee = optimalRow.meleeTile;
////		toaManager.kephri.stepBack = optimalRow.stepBack;
//		//toaManager.kephri.dungedPrepathTile = getJustGotDungedTile(playerPoint, optimalRow.direction);
////		HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.kephri.bombTiles);
////		toaManager.kephri.pathToEfficientStartTile = EthanApiPlugin.pathToGoal(toaManager.kephri.melee, dangerTiles);
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
//			&& toaManager.kephri.pathToEfficientStartTile != null)
//		{
//			toaManager.print("Moving back to melee tile to grug smash bug");
//			Walker.stepAlong(toaManager.kephri.pathToEfficientStartTile);
//			return true;
//
//		}
//
//
//		return false;
//	}
//
//
//	private WorldPoint safeBombTile(WorldPoint playerPoint)
//	{
//		WorldPoint southWest = playerPoint.dx(-2).dy(-2);
//		WorldPoint northEast = playerPoint.dx(3).dy(3);
//		// 5x5 around player
//		ArrayList<WorldPoint> areaAroundPlayer = (ArrayList<WorldPoint>) WorldAreas.createArea(southWest, northEast).toWorldPointList();
//		areaAroundPlayer.removeIf(n -> !Reachable.isWalkable(n));
//		areaAroundPlayer.removeAll(toaManager.kephri.bombTiles);
//		if (areaAroundPlayer.contains(playerPoint))
//		{
//			// If the playerpoint is a safetile, return that
//			return playerPoint;
//		}
//		ArrayList<WorldPoint> testPath;
//		HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.kephri.bombTiles);
//		for (WorldPoint worldPoint : areaAroundPlayer)
//		{
//			testPath = EthanApiPlugin.pathToGoal(worldPoint, dangerTiles);
//			if (testPath.size() <= 2)
//			{
//				return worldPoint;
//			}
//		}
//		return null;
//	}
//
//	//using direction as a placeholder for now but we should just split the room into 4 areas
//	// and do a contains check or else calculate direction of tile based on its location relative to the boss
//	private WorldPoint getJustGotDungedTile(WorldPoint playerLoc, Direction direction)
//	{
//		WorldPoint wp;
//		if (direction.equals(Direction.WEST))
//		{
//			wp = playerLoc.dy(-2).dx(1);
//		}
//		else if (direction.equals(Direction.SOUTH))
//		{
//			wp = playerLoc.dy(1).dx(2);
//		}
//		else if (direction.equals(Direction.EAST))
//		{
//			wp = playerLoc.dy(2).dx(-1);
//		}
//		else
//		{
//			wp = playerLoc.dy(-1).dx(-2);
//		}
//		return wp;
//	}
//
//	private KephriRowTest getOptimalRow()
//	{
//		for (KephriRowTest row : toaManager.kephri.kephriRows)
//		{
//			WorldPoint melee = row.meleeTile;
//			WorldPoint stepBack = row.stepBack;
//			//if dung on start or stepback tile skip it
//			if (!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(melee).empty() ||
//				!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(stepBack).empty())
//			{
//				continue;
//			}
//			if (Reachable.isWalkable(melee) && Reachable.isWalkable(stepBack))
//			{
//				return row;
//			}
//		}
//		return null;
//	}
//}
