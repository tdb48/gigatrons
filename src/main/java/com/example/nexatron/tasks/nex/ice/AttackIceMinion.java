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
	GameTickManager gameTickManager;

	@Inject
	public AttackIceMinion(NexManager nexManager)
	{
		super(nexManager, Stage.MINION_ICE);
	}

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null
			|| nexManager.nex.nex == null
			|| nexManager.nex.glacies == null)
		{
			return false;
		}

		if (!nexManager.hasGearEquipped(nexManager.gearSetup))
		{
			nexManager.print("Equipping gear");
			setActionCount(getActionCount() + nexManager.swap(nexManager.gearSetup));
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
				incrementActionCount();
				return true;
			}
		}

		WorldPoint standTile = nexManager.nex.getMainTile();
		WorldPoint containTile = nexManager.nex.nearestContainWp(2);
		// Deal with contain this if somehow out of it
		if (nexManager.nex.containTick > 0
			&& containTile != null
			&& nexManager.nex.distanceToActiveMinion() < 10
			&& nexManager.nex.distanceToNex() <= 2)
		{
			// This check is in case nex does contain when we just start moving to the minion
			// So the bot doesn't stand still near nex and wait for contain to disappear when it still has to run 20 tiles
			if (!(nexManager.nex.containTick <= 10
				&& Reachable.isWalkable(standTile)))
			{
				if (!nexManager.getPlayerPoint().equals(containTile))
				{
					nexManager.print("Moving out of contain this to " + nexManager.worldPointString(containTile));
					Movement.walk(containTile);
					incrementActionCount();
					return true;
				}
			}
		}

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
			incrementActionCount();
			return true;
		}

		// Send nex back to middle when possible
		if (nexManager.nex.shouldStepUnderNexIce())
		{
			WorldPoint tileUnderNex = nexManager.nex.getUnderNex();
			nexManager.print("Stepping under nex " + nexManager.worldPointString(tileUnderNex));
			Movement.walk(tileUnderNex);
			incrementActionCount();
			return true;
		}

		// Stand on our correct tiles
		if (standTile != null
//			&& nexManager.nex.containTick == 0
			&& !client.getLocalPlayer().getWorldLocation().equals(standTile))
		{
			nexManager.print("Moving to set tile");
			Movement.walk(standTile);
			incrementActionCount();
			return true;
		}
		return false;
	}
}
