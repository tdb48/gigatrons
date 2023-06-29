package com.example.toagigatron.tasks.baba.boss;

import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.items.Inventory;

import javax.inject.Inject;

@TaskDescriptor(
	name = "Baba attack boss",
	priority = 10
)
public class BabaAttackBoss extends StagedTask
{
	@Inject
	public BabaAttackBoss(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_BOSS);
	}

	private boolean somethingHappening(){
		return toaManager.baba.rockfallTick > 0 ||
				toaManager.baba.ceilingTick > 0 ||
				toaManager.baba.shockwaveTick > 0 ||
				toaManager.baba.closeToProccing();
	}
	public boolean execute()
	{
		NPC weakBoulder = NPCs.getNearest(ToaConstants.WEAK_BOULDER);
		if (toaManager.baba.babaBoss == null
			|| (!toaManager.isNextToNpc(toaManager.baba.babaBoss) &&
				somethingHappening() &&
				client.getVarbitValue(Varbits.BOSS_HEALTH_CURRENT) < client.getVarbitValue(Varbits.BOSS_HEALTH_MAXIMUM))
			|| weakBoulder != null)
		{
			return false;
		}
		// ||
		//				toaManager.baba.rockfallTick > 0 ||
		//				toaManager.baba.shockwaveTick > 0  ||
		//				toaManager.baba.ceilingTick > 0) ||
		//				toaManager.baba.closeToProccing()


		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
//		if (toaManager.baba.closeToProccing() && toaManager.baba.babaBossRowGap.contains(playerPoint))
//		{
//			if (toaManager.baba.rockfallTick > 0)
//			{
//				return false;
//			}
//			ArrayList<WorldPoint> safeTiles = new ArrayList<>(toaManager.baba.babaBossRoom);
//			safeTiles.removeAll(toaManager.baba.babaBossRowGap.toWorldPointList());
//			safeTiles.removeAll(toaManager.baba.shockwaveTiles);
//			safeTiles.removeAll(toaManager.baba.babaBoss.getWorldArea().toWorldPointList());
//			safeTiles.removeAll(toaManager.baba.bananaTiles);
//			safeTiles.removeIf(n -> !Reachable.isWalkable(n));
//			WorldPoint targetPoint = toaManager.findClosestTile(safeTiles);
//			if(targetPoint == null){
//				toaManager.print("Targetpoint is null in baba attack boss class");
//				return false;
//			}
//			Movement.walk(targetPoint);
//			toaManager.print("Moving to " + toaManager.worldPointString(targetPoint));
//			return true;
//		}

		// If you have enough spec AND bgs so far on baba is lower than 15
		if (Combat.getSpecEnergy() >= 50 && toaManager.baba.bgsHit < 15)
		{
			int offhand = toaManager.meleeSetup.offhand;
			Item brewToDrop = Consumables.getBrew();
			if (Inventory.isFull() && !Inventory.contains(offhand))
			{
				if (brewToDrop != null)
				{
					brewToDrop.interact("Drop");
					toaManager.print("Dropping low dose brew to make space");
					return true;
				}
			}
			// If you're not wearing bgs gear, equip bgs gear
			if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItemsBgs()))
			{
				toaManager.swap(toaManager.meleeSetup.getAllItemsBgs());
				return true;
			}

			if (!Combat.isSpecEnabled())
			{
				Combat.toggleSpec();
			}
			if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(toaManager.baba.babaBoss))
			{
				return false;
			}
			toaManager.baba.babaBoss.interact("Attack");
			return true;
		}

		if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItems()))
		{
			toaManager.swap(toaManager.meleeSetup.getAllItems());
		}
		else if (Combat.getSpecEnergy() >= 25)
		{
			Combat.toggleSpec();
		}
		else if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(toaManager.baba.babaBoss))
		{
			return false;
		}
		if (toaManager.baba.badTiles.contains(playerPoint))
		{
			return false;
		}
		toaManager.baba.babaBoss.interact("Attack");
		return true;
	}
}