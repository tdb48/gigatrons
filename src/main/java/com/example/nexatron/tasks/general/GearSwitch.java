package com.example.nexatron.tasks.general;

import com.example.Utility.Combat;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;

@TaskDescriptor(
	name = "Gear",
	priority = Integer.MAX_VALUE,
	register = true
)
public class GearSwitch extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;
	@Inject
	ItemManager itemManager;

	@Inject
	public GearSwitch(NexManager nexManager)
	{
		super(nexManager,
			Stage.MINION_SMOKE,
			Stage.NEX_SMOKE,
			Stage.MINION_SHADOW,
			Stage.NEX_SHADOW,
			Stage.MINION_BLOOD,
			Stage.NEX_BLOOD,
			Stage.MINION_ICE,
			Stage.NEX_ICE,
			Stage.NEX_ZAROS,
			Stage.KC_AREA,
			Stage.BANK);
	}

	// Returning true means we have prayers left to do
	public boolean execute()
	{
		if (nexManager.switchesLeft.isEmpty())
		{
			return false;
		}
		ArrayList<Integer> item = new ArrayList<>();
		item.add(nexManager.switchesLeft.get(0));
		nexManager.print("Switching item: " + itemManager.getItemComposition(item.get(0)).getName() + ", on tick " + nexManager.totalClientTicks);
		nexManager.swap(item);
		nexManager.switchesLeft.remove(0);
		return !nexManager.switchesLeft.isEmpty();
	}

	@Subscribe(priority = 10)
	public void onGameTick(GameTick gameTick)
	{
		nexManager.gearSetup = findSetup();
		nexManager.switchesLeft = getRemainingSwitches(nexManager.gearSetup);
		System.out.println("");
	}

	public ArrayList<Integer> getRemainingSwitches(ArrayList<Integer> gearSetup)
	{
		int swaps = (int) (3 + (Math.abs(nexManager.random.nextGaussian() * 1.5)));
		int counter = 0;
		ArrayList<Integer> returnList = new ArrayList<>();
		for (int item : gearSetup)
		{
			if (!nexManager.hasEquipped(item))
			{
				returnList.add(item);
			}
			if (swaps == counter)
			{
				return returnList;
			}
		}
		return returnList;
	}

	public ArrayList<Integer> findSetup()
	{
		if (nexManager.nex.shouldTeleport())
		{
			return nexManager.nex.setup.rangeNex();
		}
		switch (nexManager.getStage())
		{
			case BANK:
				if (nexManager.shouldKc())
				{
					return nexManager.setup.rangeKc();
				}
				return nexManager.socket.isMaster ? nexManager.setup.rangeNex() : nexManager.setup.meleeNex();

			case NEX_SMOKE:
				return nexManager.getBossHp() >= 3200 && Combat.getSpecEnergy() >= 75 ?
					nexManager.nex.setup.rangeNex() :
					nexManager.nex.setup.meleeNex();

			case MINION_SMOKE:
			case NEX_SHADOW:
			case MINION_SHADOW:
				return nexManager.nex.setup.rangeNex();

			case NEX_BLOOD:
				NPC target = nexManager.bloodNexDecideTarget();
				if (nexManager.nex.sacrificeActive)
				{
					return nexManager.nex.setup.rangeNex();
				}
				if (nexManager.nex.shouldPrayAltar())
				{
					return nexManager.nex.setup.rangeNex();
				}
				if (nexManager.nex.distanceToNex() > 5
					&& nexManager.targetIsNex(target))
				{
					return nexManager.nex.setup.rangeNex();
				}
				return nexManager.targetIsNex(target)
					&& nexManager.nex.hpUntilProc() >= 80
					&& !Consumable.isDrained(Skill.RANGED)
					&& nexManager.nex.attacksUntilSpecial > 1
					&& Combat.getSpecEnergy() >= 75 ?
					nexManager.nex.setup.rangeNex() :
					nexManager.nex.setup.meleeNex();

			case MINION_BLOOD:
				int distance;
				if (nexManager.nex.sacrificeActive)
				{
					WorldPoint sacrificeTile = nexManager.nex.getBloodMinionSacrificeTile();
					distance = nexManager.nex.wpDistanceToMinion(sacrificeTile);
				}
				else
				{
					distance = nexManager.nex.distanceToActiveMinion();
				}

				if (nexManager.nex.shouldStepUnderNexBlood())
				{
					return nexManager.setup.rangeNex();
				}
				if (nexManager.socket.isSlave()
					&& nexManager.nex.cruor != null
					&& nexManager.nex.cruor.getHealthRatio() != -1
					&& nexManager.nex.getNPCHP(nexManager.nex.cruor) >= 80)
				{
					return nexManager.nex.setup.rangeNex();
				}
				return distance >= 3 ? nexManager.nex.setup.rangeNex() : nexManager.nex.setup.meleeNex();

			case NEX_ICE:
				if (nexManager.nex.prisonActive
					&& nexManager.nex.stuckInPrisonTick == 0)
				{
					return nexManager.nex.setup.meleeNex();
				}

				if (nexManager.nex.shouldPrayAltar())
				{
					return nexManager.nex.setup.rangeNex();
				}

				if (nexManager.nex.containTick != 0
					&& nexManager.nex.containTick <= 14)
				{
					return nexManager.nex.setup.rangeNex();
				}

				return nexManager.nex.hpUntilProc() >= 120
					&& Combat.getSpecEnergy() >= 75 ?
					nexManager.nex.setup.rangeNex() :
					nexManager.nex.setup.meleeNex();

			case MINION_ICE:
				if (nexManager.nex.prisonActive
					&& nexManager.nex.stuckInPrisonTick == 0)
				{
					return nexManager.nex.setup.meleeNex();
				}
				if (nexManager.nex.shouldStepUnderNexIce())
				{
					return nexManager.setup.rangeNex();
				}
				if (nexManager.socket.isMaster
					&& nexManager.nex.glacies != null
					&& nexManager.nex.glacies.getHealthRatio() != -1
					&& nexManager.nex.getNPCHP(nexManager.nex.glacies) <= 60)
				{
					return nexManager.setup.meleeNex();
				}
				return nexManager.setup.rangeNex();

			case NEX_ZAROS:
				// We have to melee the ice prison
				if (nexManager.nex.prisonActive
					&& nexManager.nex.stuckInPrisonTick == 0)
				{
					return nexManager.nex.setup.meleeNex();
				}

				if (nexManager.nex.containTick != 0
					&& nexManager.nex.containTick <= 14)
				{
					return nexManager.nex.setup.rangeNex();
				}

				if (nexManager.nex.distanceToNex() > 3)
				{
					return nexManager.nex.setup.rangeNex();
				}
				if (gameTickManager.attackWait > 2)
				{
					return nexManager.nex.setup.defensiveNex();
				}
				if (nexManager.nex.isDeflectMeleeActive())
				{
					return nexManager.setup.rangeNex();
				}
				return nexManager.setup.meleeNex();
		}
		return new ArrayList<>();
	}


}
