package com.example.nexatron.tasks.nex.zaros;


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
	name = "Attack Zaros Nex",
	priority = 1
)
public class AttackZarosNex extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public AttackZarosNex(NexManager nexManager)
	{
		super(nexManager, Stage.NEX_ZAROS);
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
			&& nexManager.getBossHp() >= 160)
		{
			nexManager.print("Enabling spec");
			Combat.toggleSpec();
		}

		// If there's a prison, and it's not on us, free the other person
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

		// Step under on tick 2 with designated step under tiles OR if we are far out
		if (nexManager.nex.containTick == 0
			&& !nexManager.nex.prisonActive
			&& (nexManager.nex.nexAttackTick == 2
			&& nexManager.nex.nex.isInteracting()
			&& nexManager.nex.nex.getInteracting().equals(client.getLocalPlayer())
			|| nexManager.nex.distanceToNex() > 3
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

		// If we are not interacting, attack target
		if (!client.getLocalPlayer().isInteracting()
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
		// We have to melee the ice prison
		if (nexManager.nex.prisonActive
			&& nexManager.nex.stuckInPrisonTick == 0)
		{
			return nexManager.nex.setup.meleeNex();
		}

		// Range if we somehow still end up with a contain
		if (nexManager.nex.containTick != 0
			&& nexManager.nex.containTick <= 12)
		{
			return nexManager.nex.setup.rangeNex();
		}

//		if (gameTickManager.getAttackWait() > 1)
//		{
//			return nexManager.nex.setup.defensiveNex();
//		}

		// If its deflecting melee, use range, otherwise use melee
		if (isDeflectMeleeActive())
		{
			return nexManager.setup.rangeNex();
		}
		if (nexManager.nex.distanceToNex() > 3)
		{
			return nexManager.nex.setup.rangeNex();
		}
		return nexManager.setup.meleeNex();
	}

	public boolean isDeflectMeleeActive()
	{
		int zarosCounter = nexManager.nex.nexZarosAttacks;
		if (nexManager.nex.nexAttackTick == gameTickManager.getAttackWait())
		{
			zarosCounter++;
		}
		return zarosCounter >= 9 || zarosCounter == 1;
	}
}
