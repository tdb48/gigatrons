package com.example.toagigatron.tasks.zebak.boss;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.*;
import com.example.Utility.Prayer;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.NPC;
import net.runelite.api.Varbits;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Zebak attack",
	priority = 10
)
public class ZebakAttack extends StagedTask
{

	GameTickManager gameTickManager;

	@Inject
	public ZebakAttack(ToaManager toaManager, GameTickManager gameTickManager)
	{
		super(toaManager, Stage.ZEBAK_BOSS);
		this.gameTickManager = gameTickManager;
	}


	// NOTES

	/**
	 * BGS if no shadow, step back if you miss bgs and you are on attack cooldown
	 * <p>
	 * TIDAL WAVES - Get all wave game objects, iterate them all finding one that has sand after it. Continue iterating until we find sand that has wave after it
	 * Once we know the width of the gap in the wave, we create a world area that is the width of the gap, and the length of gap -> end of room
	 * Once we have the world area, remove all tiles that are poison
	 * Attempt to find a path from player tile to somewhere in the wave world area, then walk it
	 * <p>
	 * <p>
	 * GREAT ROAR
	 * solve for which jug we will need to use while projectiles are in the air
	 * Add all the potentially poison tiles to a list and exclude them list of worldpoints to be used as potential tiles in path to
	 * Find a path toward the jug and start pathing immediately
	 * Once the poison lands, add back all the tiles that did NOT get poisoned to the list of worldpoints to be potentially used as path tiles
	 * Plugin will generate a new path every tick so it might find a better path after this happens
	 * <p>
	 * <p>
	 * BLOOD SPAWN
	 * MOVE 2 TILES IF BLOOD SPAWN IS 1 TILE AWAY FROM PLAYER
	 * Find tile to move by searching all tiles 2 tiles away from the player, and eliminating any tile that you would have to path over a blood to reach.
	 * If blood spawn more than 1 tile away, attack boss
	 */


	public boolean execute()
	{
		if (!toaManager.zebak.isInBossRoom())
		{
			toaManager.print("not in boss room");
			return false;
		}
		if (gameTickManager.isTickWaiting()
			|| toaManager.zebak.zebakBoss == null
			|| toaManager.zebak.zebakBoss.getHealthRatio() == 0)
		{
			return false;
		}
		if (toaManager.zebak.zebakBoss.isDead() || toaManager.zebak.zebakBoss.getHealthRatio() == 0)
		{
			return false;
		}
		//If there are safe rock tiles and we are not on one then dont attack
		if (!toaManager.zebak.safeRockTiles.isEmpty()
			&& !toaManager.zebak.safeRockTiles.contains(LocalPoint.fromWorld(client, client.getLocalPlayer().getWorldLocation())))
		{
			toaManager.print("Rock tiles not empty thing");
			return false;
		}
		//We DO NOT WANT TO ATTACK if there are waves out and the THIRD wave has not passed us yet
		//
		//If we have not moved past the third wave, do not attack
		if ((!toaManager.zebak.wavesOne.isEmpty() || !toaManager.zebak.wavesTwo.isEmpty()) && !toaManager.zebak.isPastThirdWave())
		{
			return false;
		}

		//if blood is ontop of us do not attack
		if (toaManager.zebak.distanceToBlood() <= 1)
		{
			toaManager.print("distance to blood too close");
			return false;
		}
		if (toaManager.zebak.zebakBoss == null)
		{
			return false;
		}
		WorldPoint centerEastZebak = WorldAreas.getCenter(toaManager.zebak.zebakBoss.getWorldArea()).dx(4);
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		NPC osmumten = NPCUtil.findNearest("Osmumten");

		// Walking out of melee range
		if (osmumten == null && playerPoint.distanceTo(toaManager.zebak.zebakBoss.getWorldArea()) <= 1)
		{
			toaManager.print("Stepping out of panic zone");
			WorldArea dodgeArea = WorldAreas.createArea(playerPoint.dx(-2).dy(-2), playerPoint.dx(3).dy(3));
			WorldPoint targetPoint = safeTile(dodgeArea.toWorldPointList());
			if (targetPoint != null)
			{
				toaManager.print("Walking out of melee range of zebak");
				Movement.walk(targetPoint);
				gameTickManager.setTickWait(1);
				return true;
			}
		}

		//BGS
		if (Combat.getSpecEnergy() >= 50 && toaManager.zebak.bgsHit < 15
			&& (client.getVarbitValue(Varbits.BOSS_HEALTH_CURRENT) > (client.getVarbitValue(Varbits.BOSS_HEALTH_MAXIMUM) - 75)))
		{
			toaManager.print("speccing with bgs");
			//Drop item here if we need
			// If you're not wearing bgs gear, equip bgs gear
			if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItemsBgs()))
			{
				if (Inventory.getEmptySlots() == 0 && !InventoryUtil.contains(toaManager.meleeSetup.offhand))
				{
					Widget brewToDrop = Consumables.getBrew();
					if (brewToDrop == null)
					{
						return false;
					}
					toaManager.print("Dropping " + brewToDrop.getName() + " to make space");
					MousePackets.queueClickPacket();
					WidgetPackets.queueWidgetAction(brewToDrop, "Drop");
				}
				toaManager.print("swapping to bgs gear");
				toaManager.swap(toaManager.meleeSetup.getAllItemsBgs());
				return true;
			}

			if (!Combat.isSpecEnabled())
			{
				toaManager.print("enabling spec for bgs");
				Combat.toggleSpec();
			}
			if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(toaManager.zebak.zebakBoss))
			{
				return false;
			}
			if (gameTickManager.isAttackWaiting())
			{
				return false;
			}
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(toaManager.zebak.zebakBoss, "Attack");
			return true;
		}
		// TODO: Make it use the new gear setup
		ArrayList<Integer> gear =
			(toaManager.zebak.distanceToZebak() > ToaConstants.BLOWPIPE_DISTANCE && !toaManager.zebak.rockTiles.isEmpty()) ? toaManager.mageSetup.getAllItems() : toaManager.rangeSetup.getAllItems();
		if (!toaManager.hasGearEquipped(gear
//			toaManager.rangeSetup.getAllItemsBp()
		))
		{
			toaManager.print("swapping to new gear");
			toaManager.swap(gear);
		}
		if (gameTickManager.isAttackWaiting())
		{
			return false;
		}
		else if (!Combat.isSpecEnabled()
			&& Combat.getSpecEnergy() >= 50
			&& Equipment.search().withId(toaManager.rangeSetup.blowpipe).first().orElse(null) != null)
		{
			toaManager.print("enabling blowpipe spec");
			Combat.toggleSpec();
		}
		else if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(toaManager.zebak.zebakBoss))
		{
			return false;
		}
		else if (toaManager.zebak.distanceToZebak() > toaManager.zebak.getAttackDistance())
		{
			ArrayList<WorldPoint> potentialTiles;
			if (toaManager.zebak.wavesThree.isEmpty())
			{
				potentialTiles = toaManager.zebak.blowpipeTiles;
			}
			else
			{
				potentialTiles = toaManager.zebak.getAreaPastThirdWave();
			}
			potentialTiles.removeIf(x -> toaManager.zebak.poisonTiles.contains(LocalPoint.fromWorld(client, x)));
			potentialTiles.removeIf(x -> toaManager.zebak.rockTiles.contains(LocalPoint.fromWorld(client, x)));
			potentialTiles.removeIf(x -> toaManager.zebak.waves.contains(x));
			WorldPoint bestTile = toaManager.findClosestTile(potentialTiles, centerEastZebak);
			if (bestTile == null)
			{
				toaManager.print("Best tile is null in zebak attack method");
				return false;
			}
			//if third wave list is not empty
			//best tile = find best tile using the third wave area
			//WorldPoint bestTile = toaManager.findClosestTile(potentialTiles, centerEastZebak);
			HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.zebak.poisonWorldPoints);
			dangerTiles.addAll(toaManager.zebak.bloods);
			dangerTiles.addAll(toaManager.zebak.getChompZone());
			toaManager.zebak.path = EthanApiPlugin.pathToGoal(bestTile, dangerTiles);
			if (toaManager.zebak.path == null || toaManager.zebak.path.isEmpty())
			{
				toaManager.zebak.getChompZone().forEach(dangerTiles::remove);
				toaManager.zebak.path = EthanApiPlugin.pathToGoal(bestTile, dangerTiles);
			}
//			toaManager.zebak.path = Movement.getAvoidancePath(bestTile, toaManager.zebak.toaCollisionMap, toaManager.zebak.allWalkableRoomTiles, toaManager.zebak.poisonWorldPoints, toaManager.lpToWp(toaManager.zebak.rockTiles), true);
			if (!toaManager.zebak.safeRockTiles.isEmpty()
				&& toaManager.zebak.distanceToZebak() > toaManager.zebak.getAttackDistance()
				&& toaManager.zebak.safeRockTiles.contains(LocalPoint.fromWorld(client, client.getLocalPlayer().getWorldLocation())))
			{
				toaManager.print("Out of range v1");
				return false;
			}
			else if (!playerPoint.equals(bestTile))
			{
				toaManager.print("WALKING BACK INTO ZEBAK ATTACK RANGE");
				System.out.println("WALKING BACK INTO ZEBAK ATTACK RANGE");
				toaManager.print("Walking into range");
				Walker.stepAlong(toaManager.zebak.path);
				return true;
			}
			toaManager.print("Couldnt find a valid tile in range");
			return false;
		}
		else if (!toaManager.zebak.safeRockTiles.isEmpty()
			&& toaManager.zebak.distanceToZebak() > toaManager.zebak.getAttackDistance()
			&& toaManager.zebak.safeRockTiles.contains(LocalPoint.fromWorld(client, client.getLocalPlayer().getWorldLocation())))
		{
			toaManager.print("Out of range v2");
			return false;
		}
		toaManager.print("ATTACKING ZEBAK");
		MousePackets.queueClickPacket();
		NPCPackets.queueNPCAction(toaManager.zebak.zebakBoss, "Attack");
		return true;
	}

	private WorldPoint safeTile(List<WorldPoint> potentialTiles)
	{
		WorldPoint targetPoint = null;
		//player
		for (WorldPoint wp : potentialTiles)
		{
			if (wp.distanceTo(toaManager.zebak.zebakBoss.getWorldArea()) <= 1)
			{
				continue;
			}
			if (toaManager.zebak.poisonTiles.contains(LocalPoint.fromWorld(client, wp)))
			{
				continue;
			}
			if (toaManager.zebak.rockTiles.contains(LocalPoint.fromWorld(client, wp)))
			{
				continue;
			}
			if (!Reachable.isWalkable(wp))
			{
				continue;
			}
			toaManager.print("Found safetile at " + wp);
			targetPoint = wp;
			break;
		}
		return targetPoint;
	}
}
