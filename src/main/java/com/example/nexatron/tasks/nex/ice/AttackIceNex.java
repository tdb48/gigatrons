package com.example.nexatron.tasks.nex.ice;


import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.Utility.Reachable;
import com.example.Utility.WorldAreas;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.TileObject;
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

		// If there's a prison and it's not on us, free the other person
		if (nexManager.nex.pisonActive
			&& nexManager.nex.stuckInPrisonTick == 0)
		{
			GameObject nearestSpike = findPrisonSpike();
			if (nearestSpike != null)
			{
				nexManager.print("Freeing ice prison");
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(nearestSpike, false, "Attack");
				return true;
			}
		}

		// Step under on tick 2 with designated step under tiles OR if we are far out
		if (nexManager.nex.shadowTick == 0 && (nexManager.nex.nexAttackTick == 2)
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
		}

		// Dodge contain this special
		if (nexManager.nex.shadowTick != 0
			&& nexManager.nex.shadowTick <= 12)
		{
			WorldPoint containTile = nearestContainWp();
			if (containTile != null
				&& !nexManager.getPlayerPoint().equals(containTile))
			{
				nexManager.print("Moving out of contain this to " + nexManager.worldPointString(containTile));
				Movement.walk(containTile);
				return true;
			}
		}

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

	public GameObject findPrisonSpike()
	{
		WorldPoint playerPoint = nexManager.getPlayerPoint();
		ArrayList<WorldPoint> adjecantTiles = (ArrayList<WorldPoint>) List.of(playerPoint.dx(-1), playerPoint.dx(1), playerPoint.dy(-1), playerPoint.dy(1));
		List<TileObject> allSpikes = TileObjects.search().withId(NexConst.ICE_PRISON).result();
		for (TileObject spike : allSpikes)
		{
			if (adjecantTiles.contains(spike.getWorldLocation()))
			{
				return (GameObject) spike;
			}
		}
		return (GameObject) TileObjects.search().withId(NexConst.ICE_PRISON).nearestToPlayer().orElse(null);
	}

	public ArrayList<Integer> decideSetup()
	{
		if (nexManager.nex.pisonActive)
		{
			return nexManager.nex.setup.meleeNex();
		}
		if (nexManager.nex.shadowTick != 0
			&& nexManager.nex.shadowTick <= 12)
		{
			return nexManager.nex.setup.rangeNex();
		}
		return nexManager.nex.hpUntilProc() >= 120
			&& Combat.getSpecEnergy() >= 75 ?
			nexManager.nex.setup.rangeNex() :
			nexManager.nex.setup.meleeNex();
	}

	public WorldPoint nearestContainWp()
	{
		WorldPoint playerPoint = nexManager.getPlayerPoint();
		ArrayList<WorldPoint> possibleTiles = (ArrayList<WorldPoint>) WorldAreas.createArea(playerPoint.dx(-3).dy(-3), playerPoint.dx(4).dy(4)).toWorldPointList();
		possibleTiles.removeIf(n -> !Reachable.isWalkable(n));
		possibleTiles.removeIf(n -> n.distanceTo(nexManager.nex.nex.getWorldArea()) <= 1);
		return nexManager.findClosestTileToPlayer(possibleTiles);
	}

}
