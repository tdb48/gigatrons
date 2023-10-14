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
	GameTickManager gameTickManager;

	@Inject
	public AttackBloodMinion(NexManager nexManager)
	{
		super(nexManager, Stage.MINION_BLOOD);
	}

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null
			|| nexManager.nex.nex == null
			|| nexManager.nex.cruor == null)
		{
			return false;
		}

		if (nexManager.nex.sacrificeActive
			&& nexManager.nex.distanceToNex() <= 8)
		{
			WorldPoint sacrificeTile = nexManager.nex.getBloodMinionSacrificeTile();
			if (!nexManager.nex.sacrificeTiles.contains(nexManager.getPlayerPoint())
				&& sacrificeTile != null)
			{
				nexManager.print("Moving to " + nexManager.worldPointString(sacrificeTile));
				Movement.walk(sacrificeTile);
				incrementActionCount();
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
			incrementActionCount();
			return true;
		}

		if (nexManager.nex.shouldStepUnderNexBlood())
		{
			WorldPoint tileUnderNex = nexManager.nex.getUnderNex();
			nexManager.print("Stepping under nex " + nexManager.worldPointString(tileUnderNex));
			Movement.walk(tileUnderNex);
			incrementActionCount();
			return true;
		}

		WorldPoint standTile = decideStandTile();
		if (standTile != null
			&& !client.getLocalPlayer().getWorldLocation().equals(standTile))
		{
			nexManager.print("Moving to prepath tile");
			Movement.walk(standTile);
			incrementActionCount();
			return true;
		}
		return false;
	}

	public WorldPoint decideStandTile()
	{
		if (nexManager.nex.masterMainTile.distanceTo(nexManager.nex.nex.getWorldArea()) >= 11)
		{
			return nexManager.nex.masterMainTile;
		}
		return nexManager.nex.getMainTile();
	}

}
