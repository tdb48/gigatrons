package com.example.nexatron.tasks.nex.zaros;


import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
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
import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
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

		boolean usingFang = false;
		if (nexManager.gearSetup.contains(ItemID.OSMUMTENS_FANG))
		{
			usingFang = true;
		}

		if (Equipment.search().nameContains("crossbow").first().orElse(null) != null
			&& !Combat.isSpecEnabled()
			&& nexManager.nex.isDeflectMeleeActive()
			&& Combat.getSpecEnergy() >= 75
			&& !Consumable.isDrained(Skill.RANGED)
			&& Equipment.search().withId(ItemID.RUBY_DRAGON_BOLTS_E).first().orElse(null) != null
			&& nexManager.getBossHp() >= 250)
		{
			nexManager.print("Enabling spec");
			setActionCount(getActionCount() + Combat.toggleSpec());
//			Combat.toggleSpec();
		}
		else if (usingFang
			&& !Combat.isSpecEnabled()
			&& !nexManager.nex.isDeflectMeleeActive()
			&& !Consumable.isDrained(Skill.STRENGTH)
			&& Combat.getSpecEnergy() >= 25
			&& nexManager.getBossHp() < 220)
		{
			nexManager.print("Enabling spec");
			setActionCount(getActionCount() + Combat.toggleSpec());
//			Combat.toggleSpec();
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

		// Dodge contain this special
		if (nexManager.nex.containTick != 0
			&& nexManager.nex.containTick <= 14)
		{
			WorldPoint containTile = nexManager.nex.nearestContainWp(1);
			if (containTile != null
				&& !nexManager.getPlayerPoint().equals(containTile))
			{
				nexManager.print("Moving out of contain this to " + nexManager.worldPointString(containTile));
				Movement.walk(containTile);
				incrementActionCount();
				return true;
			}
		}

		WorldPoint stepUnderTile = nexManager.nex.getBloodIceStepUnderNEW();
		if (stepUnderTile != null
			&& nexManager.nex.invincibleTick > 0
			&& nexManager.nex.containTick == 0
			&& !nexManager.nex.prisonActive
			&& !nexManager.getPlayerPoint().equals(stepUnderTile))
		{
			nexManager.print("Prepathing to " + nexManager.worldPointString(stepUnderTile));
			Movement.move(stepUnderTile);
			incrementActionCount();
			return true;
		}

		int distance = nexManager.nex.distanceToTile(stepUnderTile);
		boolean isFar = distance > 2;
		// Step under on tick 2 with designated step under tiles OR if we are far out
		if (nexManager.nex.containTick == 0
			&& !nexManager.nex.prisonActive
			&& (nexManager.nex.nexAttackTick == 2 || isFar && nexManager.nex.nexAttackTick == 3)
			&& nexManager.nex.nex.isInteracting()
			&& nexManager.nex.nex.getInteracting() != null
			&& nexManager.nex.nex.getInteracting().equals(client.getLocalPlayer())
			|| nexManager.nex.distanceToNex() > 3
			&& gameTickManager.isAttackWaiting())
		{
			if (stepUnderTile != null)
			{
				nexManager.print("Stepping under at " + nexManager.worldPointString(stepUnderTile));
				Movement.move(stepUnderTile);
				incrementActionCount();
				return true;
			}
		}

		// If we are not interacting, attack target
		if (!gameTickManager.isAttackWaiting()
			&& nexManager.nex.invincibleTick == 0)
		{
			nexManager.print("Attacking " + nexManager.nex.nex.getName());
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(nexManager.nex.nex, "Attack");
			incrementActionCount();
			return true;
		}
		return false;
	}
}
