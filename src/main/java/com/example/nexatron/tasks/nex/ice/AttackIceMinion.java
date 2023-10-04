package com.example.nexatron.tasks.nex.ice;


import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Movement;
import com.example.Utility.Reachable;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexSpecial;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.Objects;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Attack Ice Minion",
	priority = 1
)
public class AttackIceMinion extends StagedTask
{
	@Inject
	public AttackIceMinion(NexManager nexManager)
	{
		super(nexManager, Stage.MINION_ICE);
	}

	@Inject
	GameTickManager gameTickManager;

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null
			|| nexManager.nex.nex == null
			|| nexManager.nex.glacies == null)
		{
			return false;
		}

		ArrayList<Integer> setup = decideSetup();
		if (!nexManager.hasGearEquipped(setup))
		{
			nexManager.print("Equipping gear");
			nexManager.swap(setup);
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


		WorldPoint standTile = nexManager.nex.getMainTile();
		WorldPoint containTile = nexManager.nex.nearestContainWp(2);
//		int runFromContainTick = nexManager.nex.distanceToTile(containTile) > 2 ? 12 : 14;
		// Deal with contain this if somehow out of it
		if (nexManager.nex.containTick != 0
			&& containTile != null
			&& nexManager.nex.wpDistanceToMinion(nexManager.nex.nex.getWorldLocation()) < 10
			&& nexManager.nex.containTick <= 14
			&& nexManager.nex.distanceToNex() <= 2)
		{
			// This check is incase nex does contain when we just start moving to the minion
			// So the bot doesnt stand still near nex and wait for contain to disapper when it still has to run 20 tiles
			if (!(nexManager.nex.containTick <= 10
				&& Reachable.isWalkable(standTile)))
			{
				if (!nexManager.getPlayerPoint().equals(containTile))
				{
					nexManager.print("Moving out of contain this to " + nexManager.worldPointString(containTile));
					Movement.walk(containTile);
					return true;
				}
			}
		}

//		// Step out if we are DD'd and theres no specials going on
//		if (nexManager.isDDd()
//			&& nexManager.socket.isMaster
//			&& !nexManager.nex.prisonActive
//			&& nexManager.nex.containTick == 0)
//		{
//			WorldPoint containTile = nexManager.nex.nearestContainWp(1);
//			if (containTile != null
//				&& !nexManager.getPlayerPoint().equals(containTile))
//			{
//				nexManager.print("Moving out of DD to " + nexManager.worldPointString(containTile));
//				Movement.walk(containTile);
//				return true;
//			}
//		}

		// Attack first, then second prio is moving to our main tile
		if (!gameTickManager.isAttackWaiting()
			&& nexManager.nex.distanceToActiveMinion() <= 10)
		{
			if (client.getLocalPlayer().isInteracting()
				&& Objects.requireNonNull(client.getLocalPlayer().getInteracting().getName()).contains("lacies"))
			{
				return true;
			}
			nexManager.print("Attacking glacies");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(nexManager.nex.glacies, "Attack");
			return true;
		}

		// Send nex back to middle when possible
		if (nexManager.nex.glacies.getWorldLocation().distanceTo(nexManager.nex.nex.getWorldArea()) <= 6
			&& nexManager.nex.isNexChasingUs()
			&& !(nexManager.nex.nextSpecial.equals(NexSpecial.PRISON) && nexManager.nex.attacksUntilSpecial == 1))
		{
			WorldPoint tileUnderNex = nexManager.nex.getUnderNex();
			nexManager.print("Stepping under nex " + nexManager.worldPointString(tileUnderNex));
			Movement.walk(tileUnderNex);
			return true;
		}

		// Stand on our correct tiles
		if (standTile != null
			&& nexManager.nex.containTick == 0
			&& !client.getLocalPlayer().getWorldLocation().equals(standTile))
		{
			nexManager.print("Moving to set tile");
			Movement.walk(standTile);
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
		if (nexManager.socket.isSlave()
			&& nexManager.nex.glacies != null
			&& nexManager.nex.glacies.getHealthRatio() != -1
			&& nexManager.nex.getNPCHP(nexManager.nex.glacies) <= 60)
		{
			return nexManager.setup.meleeNex();
		}
		return nexManager.setup.rangeNex();
	}
}
