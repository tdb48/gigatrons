package com.example.nexatron.tasks.nex.shadow;


import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Movement;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Attack Shadow Minion",
	priority = 1
)
public class AttackShadowMinion extends StagedTask
{
	@Inject
	public AttackShadowMinion(NexManager nexManager)
	{
		super(nexManager, Stage.MINION_SHADOW);
	}

	@Inject
	GameTickManager gameTickManager;

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null
			|| nexManager.nex.nex == null
			|| nexManager.nex.umbra == null)
		{
			return false;
		}

		ArrayList<Integer> setup = nexManager.nex.setup.rangeNex();
		if (!nexManager.hasGearEquipped(setup))
		{
			nexManager.print("Equipping gear");
			nexManager.swap(setup);
		}

		WorldPoint standTile = decideStandTile();
		if (nexManager.nex.isNexChasingUs())
		{
			nexManager.enableRun(true);
			if (nexManager.nex.distanceToNex() <= 3)
			{
				nexManager.print("Stepping under nex " + nexManager.worldPointString(nexManager.nex.getUnderNex()));
				Movement.walk(nexManager.nex.getUnderNex());
			}
			else if (standTile != null
				&& !client.getLocalPlayer().getWorldLocation().equals(standTile))
			{
				nexManager.print("Moving to stand tile");
				Movement.walk(standTile);
			}
			return true;
		}

		if (standTile != null
			&& !client.getLocalPlayer().getWorldLocation().equals(standTile))
		{
			if (nexManager.getPlayerPoint().distanceTo(standTile) > 1)
			{
				nexManager.enableRun(true);
			}
			nexManager.print("Moving to stand tile v2");
			Movement.walk(standTile);
			return true;
		}

		if (!gameTickManager.isAttackWaiting()
			&& !client.getLocalPlayer().isInteracting())
		{
			nexManager.enableRun(false);
			if ((!nexManager.nex.umbra.isInteracting()
				|| !nexManager.nex.isInteractingWithUs(nexManager.nex.nex))
				&& nexManager.socket.isMaster)
			{
				nexManager.print("Master not ready to attack yet");
				return false;
			}
			nexManager.print("Attacking umbra");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(nexManager.nex.umbra, "Attack");
			return true;
		}
		return false;
	}

	public WorldPoint decideStandTile()
	{
		// Slave
		if (!nexManager.socket.isMaster)
		{
			if (nexManager.nex.outOfNexRange() || nexManager.nex.canStepOut())
			{
				if (client.getLocalPlayer().getWorldLocation().equals(nexManager.nex.slaveStepUnderTile)
					|| client.getLocalPlayer().getWorldLocation().equals(nexManager.nex.slaveDodgeTile))
				{
					return nexManager.nex.getDodgeTile();
				}
				else
				{
					return nexManager.nex.getStepUnderTile();
				}
			}
			else
			{
				return nexManager.nex.getMainTile();
			}
		}

		// Master
		else
		{
			if (nexManager.nex.umbra.isInteracting()
				&& nexManager.nex.isInteractingWithUs(nexManager.nex.nex))
			{
				return nexManager.nex.getMainTile();
			}
			else
			{
				return nexManager.nex.getDodgeTile();
			}
		}
	}
}
