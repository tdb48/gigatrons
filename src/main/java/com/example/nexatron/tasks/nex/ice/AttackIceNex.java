package com.example.nexatron.tasks.nex.ice;


import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Players;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
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

		if (Equipment.search().nameContains("fang").first().orElse(null) != null
			&& !Combat.getAttackStyle().equals(Combat.AttackStyle.SECOND))
		{
			nexManager.print("Putting fang on stab");
			Combat.toggleStyle(Combat.AttackStyle.SECOND);
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

		// Step under on tick 2 with designated step under tiles OR if we are far out
		if ((nexManager.nex.nexAttackTick == 2)
			&& nexManager.nex.nex.isInteracting()
			&& nexManager.nex.nex.getInteracting().equals(client.getLocalPlayer())
			|| nexManager.getPlayerPoint().distanceTo(nexManager.nex.nex.getWorldArea()) > 3
			&& gameTickManager.isAttackWaiting())
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
				nexManager.print("Step under tile at ice phase is null!");
			}
		}
		Player otherPlayer = Players.search().withName(nexManager.socket.otherName).first().orElse(null);

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
		if (nexManager.nex.sacrificeActive)
		{
			return nexManager.nex.setup.rangeNex();
		}
		return nexManager.nex.reavers.isEmpty()
			&& nexManager.nex.hpUntilProc() >= 70
			&& Combat.getSpecEnergy() >= 75 ?
			nexManager.nex.setup.rangeNex() :
			nexManager.nex.setup.meleeNex();
	}

}
