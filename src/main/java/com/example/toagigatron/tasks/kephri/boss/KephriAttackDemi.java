package com.example.toagigatron.tasks.kephri.boss;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.Utility.NPCUtil;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.bossmodel.KephriDungRow;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Kephri attack demi",
	priority = 100
)
public class KephriAttackDemi extends StagedTask
{
	@Inject
	public KephriAttackDemi(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_BOSS);
	}

	@Inject
	GameTickManager gameTickManager;

	public boolean execute()
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		NPC demi = NPCUtil.findNearest("Spitting Scarab", "Arcane Scarab", "Soldier Scarab");
		NPC resetGhost = NPCUtil.findNearest(ToaConstants.OSMUMTEN, ToaConstants.SCABARAS);
		if (toaManager.kephri.kephriRoom == null
			|| toaManager.kephri.kephri == null
			|| resetGhost != null
			|| !toaManager.kephri.kephriRoom.contains(playerPoint)
			|| (demi == null && Arrays.asList(client.getNpcDefinition(toaManager.kephri.kephri.getId()).getActions()).contains("Attack"))
			|| toaManager.kephri.dungGraphicTick > 0
			|| Static.getClient().getLocalPlayer().getGraphic() == ToaConstants.DUNG_GRAPHIC_START)
		{
			return false;
		}
		WorldPoint safeBombTile = safeBombTile(playerPoint);
		NPC arcane = NPCUtil.findNearest("Arcane Scarab");
		if (arcane != null)
		{
			WorldPoint closestSafeTile = getClosestSafeTile(getTilesAroundScarab(arcane));
			if (!playerPoint.equals(closestSafeTile)
				&& (toaManager.kephri.kephriBombTick <= 2 || gameTickManager.isAttackWaiting()) || playerPoint.distanceTo(closestSafeTile) > 5)
			{
				if (toaManager.kephri.kephriBombTick == 2 && safeBombTile != null)
				{
					toaManager.print("Dodging bomb to " + toaManager.worldPointString(safeBombTile));
					Movement.walk(safeBombTile);
					return true;
				}

				toaManager.print("Arcane scarab moving to " + toaManager.worldPointString(closestSafeTile));
				Movement.walk(closestSafeTile);
				return true;
			}
			if (client.getLocalPlayer().getInteracting() == null)
			{
				if (Combat.getSpecEnergy() >= 25
					&& !Combat.isSpecEnabled()
					&& !Equipment.search().withId(ItemID.OSMUMTENS_FANG).empty())
				{
					Combat.toggleSpec();
				}
				toaManager.print("Attacking Arcane Scarab");
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(arcane, "Attack");
				return true;
			}
			return false;
		}

		NPC spitting = NPCUtil.findNearest("Spitting Scarab");
		if (spitting != null)
		{
			WorldPoint closestSafeTile = getClosestSafeTile(getTilesAroundScarab(spitting));
			if (!playerPoint.equals(closestSafeTile)
				&& (toaManager.kephri.kephriBombTick <= 2 || gameTickManager.isAttackWaiting()) || playerPoint.distanceTo(closestSafeTile) > 5)
			{
				if (toaManager.kephri.kephriBombTick == 2 && safeBombTile != null)
				{
					toaManager.print("Dodging bomb to " + toaManager.worldPointString(safeBombTile));
					Movement.walk(safeBombTile);
					return true;
				}
				toaManager.print("Spitting scarab moving to " + toaManager.worldPointString(closestSafeTile));
				Movement.walk(closestSafeTile);
				return true;
			}
			if (client.getLocalPlayer().getInteracting() == null)
			{
				if (Combat.getSpecEnergy() >= 95
					&& !Combat.isSpecEnabled()
					&& !Equipment.search().withId(ItemID.OSMUMTENS_FANG).empty())
				{
					Combat.toggleSpec();
				}
				toaManager.print("Attacking Spitting Scarab");
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(spitting, "Attack");
				return true;
			}
		}

		WorldPoint startTile = toaManager.kephri.currentRow.startPoint;
		NPC soldier = NPCUtil.findNearest("Soldier Scarab");
		if (soldier != null)
		{
			if (Combat.getSpecEnergy() >= 25
				&& !Combat.isSpecEnabled()
				&& !Equipment.search().withId(ItemID.OSMUMTENS_FANG).empty())
			{
				Combat.toggleSpec();
			}

			if (toaManager.kephri.kephriBombTick == 2)
			{
				if (safeBombTile != null)
				{
					toaManager.print("Dodging bomb to " + toaManager.worldPointString(safeBombTile));
					Movement.walk(safeBombTile);
					return true;
				}
			}

			if (gameTickManager.isAttackWaiting() && startTile != null)
			{
				toaManager.print("Moving to start");
				Movement.walk(startTile);
				return true;
			}

			if (!gameTickManager.isAttackWaiting())
			{
				toaManager.print("Attacking soldier scarab");
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(soldier, "Attack");
				return true;
			}
		}

		if (toaManager.kephri.kephriBombTick == 2)
		{
			if (safeBombTile != null)
			{
				toaManager.print("Else - Dodging bomb to " + toaManager.worldPointString(safeBombTile));
				Movement.walk(safeBombTile);
				return true;
			}
		}
		else if (startTile != null && !playerPoint.equals(startTile) && demi == null)
		{
			toaManager.print("Else - Moving to start");
			Movement.walk(startTile);
			return true;
		}
		return false;
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

	private WorldPoint getStartTile()
	{
		for (KephriDungRow row : toaManager.kephri.kephriDungRows)
		{
			if (Reachable.isWalkable(row.startPoint) && !toaManager.kephri.bombTiles.contains(row.startPoint))
			{
				return row.startPoint;
			}
		}
		return null;
	}

	private WorldPoint getClosestSafeTile(ArrayList<WorldPoint> potentialTiles)
	{
		return toaManager.findClosestTile(potentialTiles);
	}

	private ArrayList<WorldPoint> getTilesAroundScarab(NPC scarab)
	{
		ArrayList<WorldPoint> returnList = new ArrayList<>();
		if (scarab == null)
		{
			return returnList;
		}
		WorldPoint centerOfScarab = WorldAreas.getCenter(scarab.getWorldArea());
		WorldPoint southWest = centerOfScarab.dx(-2).dy(-2);
		WorldPoint northEast = centerOfScarab.dx(3).dy(3);
		returnList = (ArrayList<WorldPoint>) WorldAreas.createArea(southWest, northEast).toWorldPointList();
		returnList.removeAll(scarab.getWorldArea().toWorldPointList());
		returnList.removeIf(n -> toaManager.isDiagonalOf(n, centerOfScarab));
		returnList.removeAll(toaManager.kephri.bombTiles);
		return returnList;
	}
}
