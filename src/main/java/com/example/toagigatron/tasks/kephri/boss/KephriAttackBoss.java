package com.example.toagigatron.tasks.kephri.boss;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.Utility.NPCUtil;
import com.example.Utility.Reachable;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.bossmodel.KephriDungRow;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Kephri attack boss",
	priority = 10
)
public class KephriAttackBoss extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public KephriAttackBoss(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_BOSS);
	}

	//todo optimise the under 50hp handling so it doesnt start 6ticking
	// also check the sidestep/step back logic, i think its wrong
	public boolean execute()
	{
		if(toaManager.kephri.kephriPhase == 6){
			if(toaManager.kephri.kephri != null && client.getVarbitValue(Varbits.BOSS_HEALTH_CURRENT) < 60){
				if(client.getLocalPlayer().isInteracting()){
					MousePackets.queueClickPacket();
					MovementPackets.queueMovement(client.getLocalPlayer().getWorldLocation());
				}
				return false;
			}
		} else{
			return false;
		}
		return false;

//		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
//		NPC resetGhost = NPCUtil.findNearest(ToaConstants.OSMUMTEN);
//		NPC demi = NPCUtil.findNearest("Spitting Scarab", "Arcane Scarab", "Soldier Scarab");
//
//		if (toaManager.kephri.kephri == null
//			|| !NPCUtil.hasAction(toaManager.kephri.kephri, "Attack")
//			|| toaManager.kephri.kephriRoom == null
//			|| !toaManager.kephri.kephriRoom.contains(playerPoint)
//			|| resetGhost != null
//			|| demi != null)
//		{
//			return false;
//		}
//		if (shouldWeSpec())
//		{
//			toaManager.print("Toggling spec");
//			Combat.toggleSpecVoid();
//		}
//		WorldPoint stepBackTile = getStepBackTile();
//		WorldPoint stepBackStartTile = getStepBackStartTile();
//		WorldPoint dodgeTile = getSafeTile();
//
//		// If below 50 hp
//		if (client.getVarbitValue(Varbits.BOSS_HEALTH_CURRENT) < 55
//			&& toaManager.kephri.kephriPhase == 5
//			&& (toaManager.kephri.dungGraphicTick > 0 || !onStartRow(playerPoint))) //getting double dunged or not on start row, dont attack or stop attacking
//		{
//			if (client.getLocalPlayer().getInteracting() != null)
//			{
//				toaManager.print("50HP - Stop interacting with Kephri");
//				Movement.walk(playerPoint);
//			}
//			if (toaManager.kephri.kephriBombTick == 2
//				&& toaManager.kephri.bombTiles.contains(playerPoint))
//			{
//				if (playerPoint.equals(stepBackStartTile))
//				{
//					toaManager.print("50HP - Returning in step back");
//					return false;
//				}
//				Movement.walk(stepBackStartTile);
//				toaManager.print("50HP - Stepping back");
//				return true;
//			}
//			KephriDungRow currentRow = getRow(dodgeTile);
//			WorldPoint startTile = null;
//			WorldPoint middleStartRow = null;
//			if (currentRow != null)
//			{
//				startTile = currentRow.startPoint;
//				middleStartRow = currentRow.middlePoint;
//			}
//			if (toaManager.kephri.bombTiles.isEmpty() && startTile != null)
//			{
//				toaManager.print("50HP - Moving to start tile");
//				Movement.walk(startTile);
//				return true;
//			}
////			if (!onStartRow(playerPoint)
////				&& middleStartRow != null
////				&& startTile != null)
////			{
////				if (!toaManager.kephri.bombTiles.contains(startTile))
////				{
////					toaManager.print("50HP - setting up to start tile");
////					Movement.walk(startTile);
////					return true;
////				}
////				toaManager.print("50HP - setting up to middle start tile");
////				Movement.walk(middleStartRow);
////				return true;
////			}
//			toaManager.print("50HP - Idle because below 50 with dung on us");
//			return false;
//		}
//
//		if (gameTickManager.isAttackWaiting() && toaManager.kephri.dungGraphicTick == 0)
//		{
//			if (dodgeTile == null)
//			{
//				toaManager.print("We are lost in the sauce in kephri attack boss");
//				return false;
//			}
//			HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.kephri.bombTiles);
//			toaManager.kephri.kephriPath = EthanApiPlugin.pathToGoal(dodgeTile, dangerTiles);
//
//			if (toaManager.kephri.kephriPath == null || toaManager.kephri.kephriPath.isEmpty())
//			{
//				toaManager.print("Empty path?");
//				return false;
//			}
//
//			// Sidestep
//			if (toaManager.kephri.kephriPath.size() <= 3
//				&& toaManager.kephri.kephriTick <= 3
//				&& !toaManager.kephri.bombTiles.isEmpty())
//			{
//				if (playerPoint.equals(dodgeTile))
//				{
//					toaManager.print("Returning in sidestep");
//					return false;
//				}
//				Movement.walk(dodgeTile);
//				toaManager.print("Stepping sideways to " + toaManager.worldPointString(dodgeTile));
//				return true;
//			}
//
//			// Corner
//			if (toaManager.kephri.kephriPath.size() > 3
//				&& toaManager.kephri.kephriTick >= 2
//				&& toaManager.kephri.kephriTick <= 4
//				&& gameTickManager.attackWait >= 2
//				&& !toaManager.kephri.bombTiles.isEmpty())
//			{
//				toaManager.print("path: " + toaManager.kephri.kephriPath.size() + ", tick: " + toaManager.kephri.kephriTick + ", att wait: " + gameTickManager.attackWait);
//				if (playerPoint.equals(dodgeTile))
//				{
//					toaManager.print("Returning in corner");
//					return false;
//				}
//				Movement.walk(dodgeTile);
//				toaManager.print("Corner tile dodge");
//				return true;
//			}
//			// Step back
//			else if (toaManager.kephri.kephriBombTick == 2
//				&& toaManager.kephri.bombTiles.contains(playerPoint)
//				&& toaManager.kephri.kephriPath.size() >= 4)
//			{
//				if (playerPoint.equals(stepBackTile))
//				{
//					toaManager.print("Returning in step back");
//					return false;
//				}
//				Movement.walk(stepBackTile);
//				toaManager.print("Stepping back");
//				return true;
//			}
//		}
//		if (toaManager.kephri.kephriBombTick == 2 && toaManager.kephri.bombTiles.contains(playerPoint))
//		{
//			WorldPoint safeBombTile = safeBombTile(playerPoint);
//			Movement.walk(safeBombTile);
//			toaManager.print("Edge case - last resort dodging");
//			return true;
//		}
//
//		if (client.getLocalPlayer().getInteracting() == null)
//		{
//			toaManager.print("Attacking kephri");
//			//MousePackets.queueClickPacket();
//			//NPCPackets.queueNPCAction(toaManager.kephri.kephri, "Attack");
//			return true;
//		}
//		return false;
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
		for (WorldPoint worldPoint : areaAroundPlayer)
		{
			testPath = EthanApiPlugin.pathToGoal(worldPoint, dangerTiles);
			if (testPath.size() <= 3)
			{
				return worldPoint;
			}
		}
		return null;
	}

	private boolean onStartRow(WorldPoint playerPoint)
	{
		KephriDungRow row = toaManager.kephri.currentRow;
		if (row == null)
		{
			return false;
		}
		return playerPoint.equals(row.middlePoint) ||
			playerPoint.equals(row.startPoint) ||
			playerPoint.equals(row.prePathPoint) ||
			playerPoint.equals(row.endPoint);
	}

//	private boolean onStartRow(WorldPoint playerPoint)
//	{
//		WorldPoint dodgeTile = getSafeTile();
//		KephriDungRow row = getRow(dodgeTile);
//		if (row == null)
//		{
//			return false;
//		}
//		return playerPoint.equals(row.middlePoint) ||
//			playerPoint.equals(row.startPoint) ||
//			playerPoint.equals(row.prePathPoint) ||
//			playerPoint.equals(row.endPoint);
//	}

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

	private WorldPoint getSafeTile()
	{
		int startIndex = toaManager.kephri.currentRow.index;
		for (int i = startIndex; i < toaManager.kephri.kephriDungRows.size(); i++)
		{
			KephriDungRow row = toaManager.kephri.kephriDungRows.get(i);
			if (Reachable.isWalkable(row.startPoint) && !toaManager.kephri.bombTiles.contains(row.startPoint))
			{
				return row.startPoint;
			}
		}
//		for (KephriDungRow row : toaManager.kephri.kephriDungRows)
//		{
//			if (Reachable.isWalkable(row.startPoint) && !toaManager.kephri.bombTiles.contains(row.startPoint))
//			{
//				return row.startPoint;
//			}
//		}
		return null;
	}

	private WorldPoint getStepBackTile()
	{
		KephriDungRow currentRow = getRowStandingOn();
		if (currentRow != null)
		{
			return currentRow.middlePoint;
		}
		return null;
	}

	private WorldPoint getStepBackStartTile()
	{
		int startIndex = toaManager.kephri.currentRow.index;
		for (int i = startIndex; i < toaManager.kephri.kephriDungRows.size(); i++)
		{
			KephriDungRow row = toaManager.kephri.kephriDungRows.get(i);
			if (Reachable.isWalkable(row.middlePoint))
			{
				if (!toaManager.kephri.bombTiles.contains(row.middlePoint))
				{
					return row.middlePoint;
				}
				else
				{
					return row.startPoint;
				}
			}
		}
//		for (KephriDungRow row : toaManager.kephri.kephriDungRows)
//		{
//			if (Reachable.isWalkable(row.middlePoint))
//			{
//				if (!toaManager.kephri.bombTiles.contains(row.middlePoint))
//				{
//					return row.middlePoint;
//				}
//				else
//				{
//					return row.startPoint;
//				}
//			}
//		}
		return null;
	}

	private KephriDungRow getRowStandingOn()
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		return getRow(playerPoint);
	}

	private KephriDungRow getRow(WorldPoint worldPoint)
	{
		for (KephriDungRow row : toaManager.kephri.kephriDungRows)
		{
			if (worldPoint.equals(row.startPoint))
			{
				return row;
			}
		}
		return null;
	}
}
