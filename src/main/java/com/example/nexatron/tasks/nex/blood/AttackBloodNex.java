package com.example.nexatron.tasks.nex.blood;


import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.Utility.Reachable;
import com.example.Utility.WorldAreas;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.Objects;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Attack Blood Nex",
	priority = 1
)
public class AttackBloodNex extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public AttackBloodNex(NexManager nexManager)
	{
		super(nexManager, Stage.NEX_BLOOD);
	}

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null
			|| nexManager.nex.nex == null)
		{
			return false;
		}
		setActionCount(getActionCount() + nexManager.enableRun(true));
//		nexManager.enableRun(true);
		NPC target = nexManager.bloodNexDecideTarget();

		if (target == null)
		{
			nexManager.print("Target somehow null ins attack blood nex");
			return true;
		}

		if (Equipment.search().nameContains("crossbow").first().orElse(null) != null
			&& !Combat.isSpecEnabled()
			&& nexManager.targetIsNex(target)
			&& !Consumable.isDrained(Skill.RANGED)
			&& nexManager.nex.attacksUntilSpecial > 1
			&& Combat.getSpecEnergy() >= 75
			&& nexManager.nex.hpUntilProc() >= 80)
		{
			nexManager.print("Enabling spec");
			setActionCount(getActionCount() + Combat.toggleSpec());
//			Combat.toggleSpec();
		}

		if (nexManager.nex.sacrificeActive)
		{
			WorldPoint sacrificeTile = nexManager.nex.getSacrificeTile();
			// Run out of sacrifice range
			if (!nexManager.nex.sacrificeTiles.contains(nexManager.getPlayerPoint())
				&& sacrificeTile != null)
			{
				nexManager.print("Moving to " + nexManager.worldPointString(sacrificeTile));
				Movement.walk(sacrificeTile);
				incrementActionCount();
			}

			// Attack our target if we are out of sacrifice range
			else if (!gameTickManager.isAttackWaiting()
				&& !client.getLocalPlayer().isInteracting()
				&& nexManager.nex.invincibleTick == 0)
			{
				nexManager.print("Attacking " + target.getName());
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(target, "Attack");
				incrementActionCount();
			}
			return true;
		}

		if (nexManager.nex.shouldPrayAltar())
		{
			nexManager.print("Praying at altar");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(nexManager.nex.altar, false, "Pray");
			incrementActionCount();
			return true;
		}

		WorldPoint stepUnderTile = nexManager.nex.getBloodIceStepUnderNEW();
		if (stepUnderTile != null
			&& nexManager.nex.invincibleTick > 0
			&& nexManager.targetIsNex(target)
			&& !nexManager.nex.sacrificeActive
			&& !nexManager.getPlayerPoint().equals(stepUnderTile))
		{
			nexManager.print("Prepathing to " + nexManager.worldPointString(stepUnderTile));
			Movement.move(stepUnderTile);
			incrementActionCount();
			return true;
		}

		int distance = nexManager.nex.distanceToTile(stepUnderTile);
		boolean isFar = distance > 2;
		// Step under on tick 2 with designated step under tiles OR if we are far out
		if ((nexManager.nex.nexAttackTick == 2 || isFar && nexManager.nex.nexAttackTick == 3)
			&& nexManager.nex.nex.isInteracting()
			&& nexManager.targetIsNex(target)
			&& nexManager.nex.nex.getInteracting().equals(client.getLocalPlayer())
			|| nexManager.getPlayerPoint().distanceTo(nexManager.nex.nex.getWorldArea()) > 3
			&& gameTickManager.isAttackWaiting()
			&& nexManager.targetIsNex(target))
		{
			if (stepUnderTile != null)
			{
				nexManager.print("Stepping under at " + nexManager.worldPointString(stepUnderTile));
				Movement.move(stepUnderTile);
				incrementActionCount();
				return true;
			}
			else
			{
				nexManager.print("Step under tile at blood phase is null!");
			}
		}

		// If we are not interacting, attack target
		if ((!client.getLocalPlayer().isInteracting() || !client.getLocalPlayer().getInteracting().equals(target))
			&& nexManager.nex.invincibleTick == 0)
		{
			nexManager.print("Attacking " + target.getName());
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(target, "Attack");
			incrementActionCount();
			return true;
		}

		Player otherPlayer = nexManager.socket.getOtherPlayer();
		// If slave standing next to master attacking reaver, move out
		if (!nexManager.socket.isMaster
			&& otherPlayer != null
			&& !nexManager.targetIsNex(target)
			&& nexManager.getPlayerPoint().distanceTo(otherPlayer.getWorldLocation()) <= 1)
		{
			WorldPoint reaverTile = findReaverTile(target, otherPlayer.getWorldLocation());
			if (reaverTile != null)
			{
				nexManager.print("Moving away from other player while attacking reavers");
				Movement.walk(reaverTile);
				incrementActionCount();
				return true;
			}
		}

		return false;
	}

	public WorldPoint findReaverTile(NPC reaver, WorldPoint otherPlayer)
	{
		WorldPoint southWest = reaver.getWorldLocation().dx(-1).dy(-1);
		WorldPoint northEast = reaver.getWorldLocation().dx(3).dy(3);
		ArrayList<WorldPoint> possibleTiles = (ArrayList<WorldPoint>) WorldAreas.createArea(southWest, northEast).toWorldPointList();
		possibleTiles.removeIf(n -> reaver.getWorldArea().toWorldPointList().contains(n));
		possibleTiles.removeIf(n -> n.distanceTo(otherPlayer) <= 1);
		possibleTiles.removeIf(n -> !Reachable.isWalkable(n));
		// Remove corner tiles
		possibleTiles.remove(southWest);
		possibleTiles.remove(reaver.getWorldLocation().dx(-1).dy(2));
		possibleTiles.remove(reaver.getWorldLocation().dx(2).dy(2));
		possibleTiles.remove(reaver.getWorldLocation().dx(2).dy(-1));
		return nexManager.findClosestTileToPlayer(possibleTiles);
	}
}
