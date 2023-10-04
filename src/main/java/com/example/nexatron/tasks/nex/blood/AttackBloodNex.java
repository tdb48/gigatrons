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
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.Objects;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
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
		nexManager.enableRun(true);
		NPC target = decideTarget();
		ArrayList<Integer> setup = decideSetup(target);
		if (!nexManager.hasGearEquipped(setup))
		{
			nexManager.print("Equipping gear");
			nexManager.swap(setup);
		}

		if (target == null)
		{
			nexManager.print("Target somehow null ins attack blood nex");
			return true;
		}

		if (Equipment.search().nameContains("crossbow").first().orElse(null) != null
			&& !Combat.isSpecEnabled()
			&& targetIsNex(target)
			&& nexManager.nex.attacksUntilSpecial > 1
			&& Combat.getSpecEnergy() >= 75
			&& nexManager.nex.hpUntilProc() >= 80)
		{
			nexManager.print("Enabling spec");
			Combat.toggleSpec();
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
			}

			// Attack our target if we are out of sacrifice range
			else if (!gameTickManager.isAttackWaiting()
				&& !client.getLocalPlayer().isInteracting()
				&& nexManager.nex.invincibleTick == 0)
			{
				nexManager.print("Attacking " + target.getName());
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(target, "Attack");
			}
			return true;
		}

		if (nexManager.nex.shouldPrayAltar())
		{
			nexManager.print("Praying at altar");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(nexManager.nex.altar, false, "Pray");
			return true;
		}

		// Step under on tick 2 with designated step under tiles OR if we are far out
		if ((nexManager.nex.nexAttackTick == 2)
			&& nexManager.nex.nex.isInteracting()
			&& targetIsNex(target)
			&& nexManager.nex.nex.getInteracting().equals(client.getLocalPlayer())
			|| nexManager.getPlayerPoint().distanceTo(nexManager.nex.nex.getWorldArea()) > 3
			&& gameTickManager.isAttackWaiting()
			&& targetIsNex(target))
		{
			WorldPoint stepUnderTile = nexManager.nex.getBloodIceStepUnderNEW();
			if (stepUnderTile != null)
			{
				nexManager.print("Stepping under at " + nexManager.worldPointString(stepUnderTile));
				Movement.move(stepUnderTile);
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
			return true;
		}

		Player otherPlayer = nexManager.socket.getOtherPlayer();
		// If slave standing next to master attacking reaver, move out
		if (!nexManager.socket.isMaster
			&& otherPlayer != null
			&& !targetIsNex(target)
			&& nexManager.getPlayerPoint().distanceTo(otherPlayer.getWorldLocation()) <= 1)
		{
			WorldPoint reaverTile = findReaverTile(target, otherPlayer.getWorldLocation());
			if (reaverTile != null)
			{
				nexManager.print("Moving away from other player while attacking reavers");
				Movement.walk(reaverTile);
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

	public NPC decideTarget()
	{
		if (nexManager.nex.nex == null)
		{
			return null;
		}
		if (!nexManager.nex.reavers.isEmpty())
		{
			// If we are the master, we only want to hit reavers until they are about half hp,
			// slave hits anything above 10%
			int threshHold = nexManager.socket.isMaster ? 50 : 10;
			ArrayList<NPC> targets = new ArrayList<>();
			for (NPC reaver : nexManager.nex.reavers.keySet())
			{
				if (nexManager.nex.reavers.get(reaver) >= threshHold)
				{
					targets.add(reaver);
				}
			}
			if (!targets.isEmpty())
			{
				return nexManager.findClosestNPC(targets);
			}
		}
		return nexManager.nex.nex;
	}

	public boolean targetIsNex(NPC target)
	{
		return Objects.requireNonNull(target.getName()).toLowerCase().contains("nex");
	}

	public ArrayList<Integer> decideSetup(NPC target)
	{
		if (nexManager.nex.sacrificeActive)
		{
			return nexManager.nex.setup.rangeNex();
		}

		if (nexManager.nex.shouldPrayAltar())
		{
			return nexManager.nex.setup.rangeNex();
		}

//		if (gameTickManager.getAttackWait() > 1)
//		{
//			return nexManager.nex.setup.defensiveNex();
//		}

		if (nexManager.nex.distanceToNex() > 3
			&& targetIsNex(target))
		{
			return nexManager.nex.setup.rangeNex();
		}

		return targetIsNex(target)
			&& nexManager.nex.hpUntilProc() >= 80
			&& nexManager.nex.attacksUntilSpecial > 1
			&& Combat.getSpecEnergy() >= 75 ?
			nexManager.nex.setup.rangeNex() :
			nexManager.nex.setup.meleeNex();
	}

}
