package com.example.nexatron.tasks.nex.shadow;


import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Attack Shadow Nex",
	priority = 1
)
public class AttackShadowNex extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public AttackShadowNex(NexManager nexManager)
	{
		super(nexManager, Stage.NEX_SHADOW);
	}

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null
			|| nexManager.nex.nex == null
			|| nexManager.nex.umbra == null)
		{
			return false;
		}
		setActionCount(getActionCount() + nexManager.enableRun(true));
//		nexManager.enableRun(true);

		ArrayList<Integer> setup = nexManager.nex.setup.rangeNex();
		if (!nexManager.hasGearEquipped(setup))
		{
			nexManager.print("Equipping gear");
			setActionCount(getActionCount() + nexManager.swap(setup));
		}

		if (Equipment.search().nameContains("crossbow").first().orElse(null) != null
			&& !Combat.isSpecEnabled()
			&& Combat.getSpecEnergy() >= 75
			&& !Consumable.isDrained(Skill.RANGED)
			&& nexManager.nex.hpUntilProc() >= 120)
		{
			nexManager.print("Enabling spec");
			setActionCount(getActionCount() + Combat.toggleSpec());
//			Combat.toggleSpec();
		}

		WorldPoint standTile = nexManager.nex.shadowTick > 0 ?
			nexManager.nex.getDodgeTile() :
			nexManager.nex.getMainTile();

		if (nexManager.nex.isNexChasingUs())
		{
			if (nexManager.nex.distanceToNex() <= 3)
			{
				nexManager.print("Stepping under nex " + nexManager.worldPointString(nexManager.nex.getUnderNex()));
				Movement.walk(nexManager.nex.getUnderNex());
				incrementActionCount();
				return true;
			}
			else if (standTile != null
				&& !client.getLocalPlayer().getWorldLocation().equals(standTile))
			{
				nexManager.print("Moving to stand tile");
				Movement.walk(standTile);
				incrementActionCount();
				return true;
			}
		}

		if (standTile != null
			&& !client.getLocalPlayer().getWorldLocation().equals(standTile))
		{
			if (nexManager.nex.shadowTick > 0
				&& nexManager.nex.outOfNexRange()
				&& !nexManager.socket.isMaster)
			{
				nexManager.print("Idling while shadow is out on slave");
				return true;
			}
			nexManager.print("Moving to stand tile v2");
			Movement.walk(standTile);
			incrementActionCount();
			return true;
		}

		if (!gameTickManager.isAttackWaiting()
			&& !client.getLocalPlayer().isInteracting()
			&& nexManager.nex.invincibleTick == 0)
		{
			nexManager.print("Attacking nex");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(nexManager.nex.nex, "Attack");
			incrementActionCount();
			return true;
		}
		return false;
	}
}
