package com.example.nexatron.tasks.nex.blood;


import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Movement;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.Objects;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Attack Blood Minion",
	priority = 1
)
public class AttackBloodMinion extends StagedTask
{
	@Inject
	public AttackBloodMinion(NexManager nexManager)
	{
		super(nexManager, Stage.MINION_BLOOD);
	}

	@Inject
	GameTickManager gameTickManager;

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null
			|| nexManager.nex.nex == null
			|| nexManager.nex.cruor == null)
		{
			return false;
		}

		ArrayList<Integer> setup = decideSetup();
		if (!nexManager.hasGearEquipped(setup))
		{
			nexManager.print("Equipping gear");
			nexManager.swap(setup);
		}

		if (nexManager.nex.sacrificeActive
			&& nexManager.nex.distanceToNex() <= 8)
		{
			WorldPoint sacrificeTile = nexManager.nex.getSacrificeTile();
			if (!nexManager.nex.sacrificeTiles.contains(nexManager.getPlayerPoint())
				&& sacrificeTile != null)
			{
				nexManager.print("Moving to " + nexManager.worldPointString(sacrificeTile));
				Movement.walk(sacrificeTile);
				return true;
			}
		}

		// Attack first, then second prio is moving to our main tile
		if (!gameTickManager.isAttackWaiting())
		{
			if (client.getLocalPlayer().isInteracting()
				&& Objects.requireNonNull(client.getLocalPlayer().getInteracting().getName()).contains("ruor"))
			{
				return true;
			}
			nexManager.print("Attacking cruor");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(nexManager.nex.cruor, "Attack");
			return true;
		}

		if (shouldStepUnderNex())
		{
			WorldPoint tileUnderNex = nexManager.nex.getUnderNex();
			nexManager.print("Stepping under nex " + nexManager.worldPointString(tileUnderNex));
			Movement.walk(tileUnderNex);
			return true;
		}

		WorldPoint standTile = decideStandTile();
		if (standTile != null
			&& !client.getLocalPlayer().getWorldLocation().equals(standTile))
		{
			nexManager.print("Moving to prepath tile");
			Movement.walk(standTile);
			return true;
		}
		return false;
	}

	public WorldPoint decideStandTile()
	{
		if (nexManager.nex.slaveMainTile.distanceTo(nexManager.nex.nex.getWorldArea()) > 11)
		{
			return nexManager.nex.slaveMainTile;
		}
		return nexManager.nex.getMainTile();
	}

	public boolean shouldStepUnderNex()
	{
		return nexManager.nex.cruor.getWorldLocation().distanceTo(nexManager.nex.nex.getWorldArea()) <= 6
			&& nexManager.nex.isNexChasingUs();
	}

	public ArrayList<Integer> decideSetup()
	{
		int distance;
		if (nexManager.nex.sacrificeActive)
		{
			WorldPoint sacrificeTile = nexManager.nex.getSacrificeTile();
			distance = nexManager.nex.wpDistanceToMinion(sacrificeTile);
		}
		else
		{
			distance = nexManager.nex.distanceToActiveMinion();
		}

		if (shouldStepUnderNex())
		{
			return nexManager.setup.rangeNex();
		}
//		if (gameTickManager.getAttackWait() > 1)
//		{
//			return nexManager.nex.setup.defensiveNex();
//		}

		if (nexManager.socket.isMaster
			&& nexManager.nex.cruor != null
			&& nexManager.nex.cruor.getHealthRatio() != -1
			&& nexManager.nex.getNPCHP(nexManager.nex.cruor) >= 80)
		{
			return nexManager.nex.setup.rangeNex();
		}
		return distance >= 3 ? nexManager.nex.setup.rangeNex() : nexManager.nex.setup.meleeNex();
	}
}
