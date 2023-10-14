package com.example.nexatron.tasks.nex.smoke;


import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Attack Smoke Nex",
	priority = 1
)
public class AttackSmokeNex extends StagedTask
{
	@Inject
	public AttackSmokeNex(NexManager nexManager)
	{
		super(nexManager, Stage.NEX_SMOKE);
	}

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null
			|| nexManager.nex.nex == null)
		{
			return false;
		}


		if (!nexManager.hasGearEquipped(nexManager.gearSetup))
		{
			nexManager.print("Equipping gear");
			setActionCount(getActionCount() + nexManager.swap(nexManager.gearSetup));
		}

		// Step under on tick 1 if boss is interacting with us
		if (nexManager.nex.nexAttackTick == 2
			&& nexManager.nex.nex.isInteracting()
			&& nexManager.nex.nex.getInteracting().equals(client.getLocalPlayer()))
		{
			WorldPoint stepUnderTile = nexManager.nex.getStepUnderTile();
			if (stepUnderTile != null)
			{
				nexManager.print("Stepping under");
				Movement.move(stepUnderTile);
				incrementActionCount();
				return true;
			}
		}

		// If we have a crossbow equipped, we have 75 spec and spec is not enabled, enable spec
		if (Equipment.search().nameContains("crossbow").first().orElse(null) != null
			&& !Combat.isSpecEnabled()
			&& Combat.getSpecEnergy() >= 75
			&& nexManager.nex.hpUntilProc() >= 120)
		{
			nexManager.print("Enabling spec");
			setActionCount(getActionCount() + Combat.toggleSpec());
//			Combat.toggleSpec();
		}

		// If we are not interacting, attack nex
		if (!client.getLocalPlayer().isInteracting()
			&& nexManager.nex.invincibleTick == 0
			&& nexManager.nex.dashTick == 0)
		{
			nexManager.print("Attacking Nex");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(nexManager.nex.nex, "Attack");
			incrementActionCount();
			return true;
		}

		return false;
	}
}
