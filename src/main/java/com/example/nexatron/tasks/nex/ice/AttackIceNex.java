package com.example.nexatron.tasks.nex.ice;


import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Attack Ice Nex",
	priority = 1
)
public class AttackIceNex extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public AttackIceNex(NexManager nexManager)
	{
		super(nexManager, Stage.NEX_ICE);
	}

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null
			|| nexManager.nex.nex == null)
		{
			return false;
		}

		ArrayList<Integer> setup = decideSetup();
		if (!nexManager.hasGearEquipped(setup))
		{
			nexManager.print("Equipping gear");
			nexManager.swap(setup);
		}

		if (Equipment.search().nameContains("crossbow").first().orElse(null) != null
			&& !Combat.isSpecEnabled()
			&& Combat.getSpecEnergy() >= 75
			&& nexManager.nex.hpUntilProc() >= 120)
		{
			nexManager.print("Enabling spec");
			Combat.toggleSpec();
		}

		// If there's a prison and it's not on us, free the other person
		if (nexManager.nex.prisonActive
			&& nexManager.nex.stuckInPrisonTick == 0)
		{
			GameObject nearestSpike = nexManager.nex.findNearestPrisonSpike();
			if (nearestSpike != null)
			{
				nexManager.print("Freeing ice prison");
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(nearestSpike, false, "Attack");
				return true;
			}
		}

		// Dodge contain this special
		if (nexManager.nex.containTick != 0
			&& nexManager.nex.containTick <= 12)
		{
			WorldPoint containTile = nexManager.nex.nearestContainWp(1);
			if (containTile != null
				&& !nexManager.getPlayerPoint().equals(containTile))
			{
				nexManager.print("Moving out of contain this to " + nexManager.worldPointString(containTile));
				Movement.walk(containTile);
				return true;
			}
		}

		if (nexManager.nex.shouldPrayAltar())
		{
			nexManager.print("Praying at altar");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(nexManager.nex.altar, false, "Pray");
			return true;
		}

		// Step under on tick 2 with designated step under tiles OR if we are far out
		if (nexManager.nex.containTick == 0
			&& !nexManager.nex.prisonActive
			&& (nexManager.nex.nexAttackTick == 2
			&& nexManager.nex.nex.isInteracting()
			&& nexManager.nex.nex.getInteracting().equals(client.getLocalPlayer())
			|| nexManager.getPlayerPoint().distanceTo(nexManager.nex.nex.getWorldArea()) > 3
			&& gameTickManager.isAttackWaiting()))
		{
			WorldPoint stepUnderTile = nexManager.nex.getBloodIceStepUnderNEW();
			if (stepUnderTile != null)
			{
				nexManager.print("Stepping under at " + nexManager.worldPointString(stepUnderTile));
				Movement.move(stepUnderTile);
				return true;
			}
		}

		// Step out if we are DD'd and theres no specials going on
		if (nexManager.isDDd()
			&& nexManager.socket.isMaster
			&& !nexManager.nex.prisonActive
			&& nexManager.nex.containTick == 0)
		{
			WorldPoint containTile = nexManager.nex.nearestContainWp(1);
			if (containTile != null
				&& !nexManager.getPlayerPoint().equals(containTile))
			{
				nexManager.print("Moving out of DD to " + nexManager.worldPointString(containTile));
				Movement.walk(containTile);
				return true;
			}
		}

		// If we are not interacting, attack target
		if ((!client.getLocalPlayer().isInteracting()
			|| !client.getLocalPlayer().getInteracting().equals(nexManager.nex.nex))
			&& nexManager.nex.invincibleTick == 0)
		{
			nexManager.print("Attacking " + nexManager.nex.nex.getName());
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(nexManager.nex.nex, "Attack");
			return true;
		}
		return false;
	}

	public ArrayList<Integer> decideSetup()
	{
		if (nexManager.nex.prisonActive
			&& nexManager.nex.stuckInPrisonTick == 0)
		{
			return nexManager.nex.setup.meleeNex();
		}

		if (nexManager.nex.shouldPrayAltar())
		{
			return nexManager.nex.setup.rangeNex();
		}

		if (nexManager.nex.containTick != 0
			&& nexManager.nex.containTick <= 12)
		{
			return nexManager.nex.setup.rangeNex();
		}

		return nexManager.nex.hpUntilProc() >= 120
			&& Combat.getSpecEnergy() >= 75 ?
			nexManager.nex.setup.rangeNex() :
			nexManager.nex.setup.meleeNex();
	}
}
