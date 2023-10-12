package com.example.nexatron.tasks.nex.smoke;


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
	name = "Attack Smoke Minion",
	priority = 1
)
public class AttackSmokeMinion extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public AttackSmokeMinion(NexManager nexManager)
	{
		super(nexManager, Stage.MINION_SMOKE);
	}

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null
			|| nexManager.nex.nex == null
			|| nexManager.nex.fumus == null)
		{
			return false;
		}

		ArrayList<Integer> setup = nexManager.nex.setup.rangeNex();
		if (!nexManager.hasGearEquipped(setup))
		{
			nexManager.print("Equipping gear");
			setActionCount(getActionCount() + nexManager.swap(setup));
		}

		WorldPoint mainTile;
		if (!nexManager.socket.isMaster
			&& nexManager.nex.nex.isInteracting()
			&& (nexManager.nex.outOfNexRange() || (nexManager.nex.getDodgeTile().distanceTo(nexManager.nex.nex.getWorldArea()) >= 11 && nexManager.nex.canStepOut() && nexManager.nex.dashTick == 0)))
		{
			mainTile = nexManager.nex.getDodgeTile();
		}
		else
		{
			mainTile = nexManager.nex.getMainTile();
		}

		if (nexManager.nex.isNexChasingUs()
			&& nexManager.nex.nex.getWorldArea().distanceTo(nexManager.nex.centerPoint) >= 4)
		{
			setActionCount(getActionCount() + nexManager.enableRun(true));
//			nexManager.enableRun(true);
			if (nexManager.nex.distanceToNex() <= 3)
			{
				nexManager.print("Stepping under nex " + nexManager.worldPointString(nexManager.nex.getUnderNex()));
				Movement.walk(nexManager.nex.getUnderNex());
				incrementActionCount();
			}
			else if (mainTile != null
				&& !client.getLocalPlayer().getWorldLocation().equals(mainTile))
			{
				nexManager.print("Moving to main tile");
				Movement.walk(mainTile);
				incrementActionCount();
			}
			return true;
		}

		if (mainTile != null
			&& !client.getLocalPlayer().getWorldLocation().equals(mainTile))
		{
			if (nexManager.getPlayerPoint().distanceTo(mainTile) > 1)
			{
				setActionCount(getActionCount() + nexManager.enableRun(true));
//				nexManager.enableRun(true);
			}
			nexManager.print("Moving to main tile");
			Movement.walk(mainTile);
			incrementActionCount();
			return true;
		}

		if (!gameTickManager.isAttackWaiting()
			&& !client.getLocalPlayer().isInteracting())
		{
			setActionCount(getActionCount() + nexManager.enableRun(!nexManager.socket.isMaster));
//			nexManager.enableRun(!nexManager.socket.isMaster);
			nexManager.print("Attacking fumus");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(nexManager.nex.fumus, "Attack");
			incrementActionCount();
			return true;
		}
		return false;
	}
}
