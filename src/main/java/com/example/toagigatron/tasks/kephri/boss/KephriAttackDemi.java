package com.example.toagigatron.tasks.kephri.boss;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Movement;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldArea;
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
		NPC demi = NPCs.getNearest("Spitting Scarab", "Arcane Scarab", "Soldier Scarab");
		NPC resetGhost = NPCs.getNearest(ToaConstants.OSMUMTEN,ToaConstants.SCABARAS);
		if (toaManager.kephri.kephriRoom == null
			|| toaManager.kephri.kephri == null
			|| resetGhost != null
			|| !toaManager.kephri.kephriRoom.contains(playerPoint)
			|| (demi == null && toaManager.kephri.kephri.hasAction("Attack"))
			|| toaManager.kephri.dungGraphicTick > 0
			|| Static.getClient().getLocalPlayer().getGraphic() == ToaConstants.DUNG_GRAPHIC_START)
		{
			return false;
		}
		WorldPoint safeBombTile = safeBombTile(playerPoint);
		NPC arcane = NPCs.getNearest(n ->
			n.getName().equals("Arcane Scarab")
				&& n.getHealthRatio() != 0);
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
					&& Equipment.contains(ItemID.OSMUMTENS_FANG))
				{
					Combat.toggleSpec();
				}
				toaManager.print("Attacking Arcane Scarab");
				arcane.interact("Attack");
				return true;
			}
			return false;
		}

		NPC spitting = NPCs.getNearest(n ->
			n.getName().equals("Spitting Scarab")
				&& n.getHealthRatio() != 0);
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
					&& Equipment.contains(ItemID.OSMUMTENS_FANG))
				{
					Combat.toggleSpec();
				}
				toaManager.print("Attacking Spitting Scarab");
				spitting.interact("Attack");
				return true;
			}
		}

		WorldPoint startTile = getStartTile();
		NPC soldier = NPCs.getNearest(n ->
			n.getName().equals("Soldier Scarab")
				&& n.getHealthRatio() != 0);
		if (soldier != null)
		{
			if (Combat.getSpecEnergy() >= 25
				&& !Combat.isSpecEnabled()
				&& Equipment.contains(ItemID.OSMUMTENS_FANG))
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
				soldier.interact("Attack");
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
		ArrayList<WorldPoint> areaAroundPlayer = (ArrayList<WorldPoint>) new WorldArea(southWest, northEast).toWorldPointList();
		areaAroundPlayer.removeIf(n -> !Reachable.isWalkable(n));
		areaAroundPlayer.removeAll(toaManager.kephri.bombTiles);
		if (areaAroundPlayer.contains(playerPoint))
		{
			// If the playerpoint is a safetile, return that
			return playerPoint;
		}
		TilePath testPath;
		for (WorldPoint worldPoint : areaAroundPlayer)
		{
			testPath = Movement.getPath(List.of(playerPoint), worldPoint, toaManager.baba.toaCollisionMap);
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
		WorldPoint centerOfScarab = scarab.getWorldArea().getCenter();
		WorldPoint southWest = centerOfScarab.dx(-2).dy(-2);
		WorldPoint northEast = centerOfScarab.dx(3).dy(3);
		returnList = (ArrayList<WorldPoint>) new WorldArea(southWest, northEast).toWorldPointList();
		returnList.removeAll(scarab.getWorldArea().toWorldPointList());
		returnList.removeIf(n -> toaManager.isDiagonalOf(n, centerOfScarab));
		returnList.removeAll(toaManager.kephri.bombTiles);
		return returnList;
	}
}
