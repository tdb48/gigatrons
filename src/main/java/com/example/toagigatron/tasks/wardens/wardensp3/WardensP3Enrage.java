package com.example.toagigatron.tasks.wardens.wardensp3;

import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "P3 enrage",
	priority = 1
)
public class WardensP3Enrage extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public WardensP3Enrage(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P3);
	}

	public boolean execute()
	{
		if (!toaManager.wardens3.enrage
			|| toaManager.wardens3.warden == null
			|| toaManager.wardens3.warden.getHealthRatio() == 0)
		{
			return false;
		}
		ArrayList<Integer> gearSet = gearSet();
		if (!toaManager.hasGearEquipped(gearSet))
		{
			toaManager.print("Equipping gear");
			toaManager.swap(gearSet);
		}
		if (!Combat.isSpecEnabled() && Combat.getSpecEnergy() >= bgsSpecRequired() && toaManager.hasGearEquipped(gearSet))
		{
			Combat.toggleSpec();
		}

		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		// || (isInMinorDanger() && gameTickManager.isAttackWaiting())
		if (isInDanger())
		{
//			toaManager.print("We are in danger");
			WorldPoint safeTile = optimalTile();
			if (safeTile != null && !playerPoint.equals(safeTile))
			{
				toaManager.print("Found optimal tile at " + toaManager.worldPointString(safeTile));
//				toaManager.print("Walking enrage");
				Movement.walk(safeTile);
				return true;
			}
			else if (safeTile == null)
			{
//				toaManager.print("Safetile is null somehow in wardens p3 enrage");
			}
		}
		if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(toaManager.wardens3.warden))
		{
			return false;
		}

		if (!Combat.isSpecEnabled()
			&& Combat.getSpecEnergy() >= 50
			&& toaManager.wardens3.bgsHit >= 20
			&& toaManager.hasEquipped(ItemID.TOXIC_BLOWPIPE))
//		Equipment.contains(ItemID.TOXIC_BLOWPIPE))
		{
			toaManager.print("Enabling bp spec");
			Combat.toggleSpec();
		}
		if (!gameTickManager.isAttackWaiting()) //&& client.getLocalDestinationLocation() == null
		{
			if (client.getLocalDestinationLocation() != null)
			{
//				toaManager.print("Destination tile is not null but we are attacking anyway");
			}
//			toaManager.print("Attacking Warden Enrage");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(toaManager.wardens3.warden, "Attack");
			return true;
		}

		return false;
	}

	public WorldPoint optimalTile()
	{
		if (toaManager.wardens3.warden == null)
		{
			return null;
		}
		ArrayList<WorldPoint> potentialTiles = new ArrayList<>();
		WorldPoint reference = WorldAreas.getCenter(toaManager.wardens3.warden.getWorldArea()).dy(3);
		ArrayList<WorldPoint> tilesInRange = toaManager.wardens3.tilesInRunRange(toaManager.wardens3.enrageArea);
		for (WorldPoint wp : tilesInRange)
		{
			if (wp.equals(client.getLocalPlayer().getWorldLocation()))
			{
				continue;
			}
			if (wp.distanceTo(client.getLocalPlayer().getWorldLocation()) > 2)
			{
				continue;
			}
			if (!toaManager.wardens3.babaBombs.containsKey(wp) && !toaManager.wardens3.lightning.containsKey(wp))
			{
				// Found empty tile
				potentialTiles.add(wp);
			}
		}
		// Found empty tile, return the tile thats closest to the middle
		if (!potentialTiles.isEmpty())
		{
//			toaManager.print("Found potential tiles with no danger");
			return toaManager.findClosestTile(potentialTiles, reference);
		}
		for (WorldPoint wp : tilesInRange)
		{
			if (wp.equals(client.getLocalPlayer().getWorldLocation()))
			{
				continue;
			}
			if (wp.distanceTo(client.getLocalPlayer().getWorldLocation()) > 2)
			{
				continue;
			}
			if (toaManager.wardens3.lightning.size() > 0 && toaManager.wardens3.lightning.containsKey(wp) && (toaManager.wardens3.lightning.get(wp) == 1)) // || toaManager.wardens3.lightning.get(wp) == 0
			{
				//There is lightning with 1 tick remaining on it we want to check if there is also a boulder that might smoke us
				if ((toaManager.wardens3.babaBombs.size() > 0 && toaManager.wardens3.babaBombs.containsKey(wp) && toaManager.wardens3.babaBombs.get(wp) >= 4) //there is a boulder on this tile but its ticks are 3 or higher so its fine
					|| (toaManager.wardens3.babaBombs.size() > 0 && !toaManager.wardens3.babaBombs.containsKey(wp)) //there is no boulder on this tile
					|| (toaManager.wardens3.babaBombs.size() == 0))
				{ //there are no boulders at all
					if (!potentialTiles.contains(wp))
					{
						potentialTiles.add(wp);
						continue;
					}
				}
				//toaManager.print("Found potential dangerous tile with lightning on it");
			}
			if (toaManager.wardens3.babaBombs.size() > 0 && toaManager.wardens3.babaBombs.containsKey(wp) && (toaManager.wardens3.babaBombs.get(wp) >= 4 || toaManager.wardens3.babaBombs.get(wp) == 0))
			{
				//toaManager.print("Found potential dangerous tile with baba bomb on it");
				if (!potentialTiles.contains(wp))
				{
					potentialTiles.add(wp);
				}
			}
		}
		if (!potentialTiles.isEmpty())
		{
//			toaManager.print("Found tiles with 1 or 0 lightning ticks remaining on them, or 0/>3 boulder ticks, these should be safe.");
			return toaManager.findClosestTile(potentialTiles, reference);
		}

		//Finding the highest tick count available on tiles in range
		int highestTick = -1;
		for (WorldPoint wp : tilesInRange)
		{
			if (wp.equals(client.getLocalPlayer().getWorldLocation()))
			{
				continue;
			}
			if (wp.distanceTo(client.getLocalPlayer().getWorldLocation()) > 2)
			{
				continue;
			}
			//There is a baba bomb on this tile
			if (toaManager.wardens3.babaBombs.size() > 0 && toaManager.wardens3.babaBombs.containsKey(wp))
			{
				//dont want to stand on a low tick boulder tile no matter what, would rather tank a lightning hit
				if (toaManager.wardens3.babaBombs.get(wp) < 3)
				{
					continue;
				}
				//The baba bomb tick is higher than our current highest tick
				if (toaManager.wardens3.babaBombs.get(wp) > highestTick)
				{
					//There is lightning counting down on this tile
					if (toaManager.wardens3.lightning.containsKey(wp))
					{
						//Set highest tick to the lowest value of lightning or boulder as this is the attack thats going to hit first
						highestTick = Math.min(toaManager.wardens3.babaBombs.get(wp), toaManager.wardens3.lightning.get(wp));
					}
					else
					{
						highestTick = toaManager.wardens3.babaBombs.get(wp);
					}
				}
			}
			//There is lightning on this tile and no baba bomb as that is handled in the above statement
			else if (toaManager.wardens3.lightning.size() > 0 && toaManager.wardens3.lightning.containsKey(wp))
			{
				//Check if lightning tick is higher than our current highest tick
				if (toaManager.wardens3.lightning.get(wp) > highestTick)
				{
					highestTick = toaManager.wardens3.lightning.get(wp);
				}
			}
		}
//		toaManager.print("Highest tick -> " + highestTick);
		//Finding all tiles in range with the highest tick count
		for (WorldPoint wp : tilesInRange)
		{
			if (wp.equals(client.getLocalPlayer().getWorldLocation()))
			{
				continue;
			}
			if (wp.distanceTo(client.getLocalPlayer().getWorldLocation()) > 2)
			{
				continue;
			}
			if (toaManager.wardens3.lightning.size() > 0 && toaManager.wardens3.lightning.containsKey(wp) && toaManager.wardens3.lightning.get(wp) == highestTick)
			{
				//toaManager.print("Found potential dangerous tile with lightning on it");
				if (!potentialTiles.contains(wp))
				{
					potentialTiles.add(wp);
				}
			}
		}
		if (!potentialTiles.isEmpty())
		{
//			toaManager.print("Found potential tiles with tick count more than 0 and LIGHTNING, tick: " + highestTick);
			return toaManager.findClosestTile(potentialTiles, reference);
		}
		for (WorldPoint wp : tilesInRange)
		{
			if (wp.equals(client.getLocalPlayer().getWorldLocation()))
			{
				continue;
			}
			if (wp.distanceTo(client.getLocalPlayer().getWorldLocation()) > 2)
			{
				continue;
			}
			if (toaManager.wardens3.babaBombs.size() > 0 && toaManager.wardens3.babaBombs.containsKey(wp) && toaManager.wardens3.babaBombs.get(wp) == highestTick)
			{
				//toaManager.print("Found potential dangerous tile with baba bomb on it");
				if (!potentialTiles.contains(wp))
				{
					potentialTiles.add(wp);
				}
			}
		}
		if (!potentialTiles.isEmpty())
		{
//			toaManager.print("Found potential tiles with tick count more than 0 AND BOULDER, tick:  " + highestTick);
			return toaManager.findClosestTile(potentialTiles, reference);
		}
//		toaManager.print("Unexpected state");
		return null;
	}

	public boolean isInMinorDanger()
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		if (toaManager.wardens3.babaBombs.containsKey(playerPoint))
		{
			if (toaManager.wardens3.babaBombs.get(playerPoint) <= 3)
			{
				return true;
			}
		}
		if (toaManager.wardens3.lightning.containsKey(playerPoint))
		{
			if (toaManager.wardens3.lightning.get(playerPoint) <= 3)
			{
				return true;
			}
		}
		return false;
	}

	public boolean isInDanger()
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		if (toaManager.wardens3.babaBombs.containsKey(playerPoint))
		{
			if (toaManager.wardens3.babaBombs.get(playerPoint) == 2 || toaManager.wardens3.babaBombs.get(playerPoint) == 1)
			{
				return true;
			}
		}
		if (toaManager.wardens3.lightning.containsKey(playerPoint))
		{
			if (toaManager.wardens3.lightning.get(playerPoint) == 2 || toaManager.wardens3.lightning.get(playerPoint) == 1)
			{
				return true;
			}
		}
		return false;
	}


	public int bgsSpecRequired()
	{
		return toaManager.isAdrenalineActive() ? 25 : 50;
	}

	public ArrayList<Integer> gearSet()
	{
		if (toaManager.wardens3.bgsHit < 20 && Combat.getSpecEnergy() >= bgsSpecRequired() && client.getVarbitValue(Varbits.BOSS_HEALTH_CURRENT) >= 150)
		{
			return toaManager.meleeSetup.getAllItemsBgs();
		}
		else
		{
			return toaManager.rangeSetup.getAllItemsBp();
		}
	}
}
