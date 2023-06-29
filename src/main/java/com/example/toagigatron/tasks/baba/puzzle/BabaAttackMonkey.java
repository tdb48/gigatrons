package com.example.toagigatron.tasks.baba.puzzle;

import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ProjectileSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.scene.Tiles;
import net.unethicalite.api.widgets.Prayers;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@TaskDescriptor(
	name = "Baba attack monkey",
	priority = 10,
	register = true
)
public class BabaAttackMonkey extends StagedTask
{
	private int lastAttackedIndex = 0;
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public BabaAttackMonkey(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE);
	}

	@Subscribe
	public void onProjectileSpawned(ProjectileSpawned projectileSpawned)
	{
		Projectile projectile = projectileSpawned.getProjectile();
		if (ToaConstants.DARTS.contains(projectile.getId()))
		{
			if (client.getLocalPlayer().getInteracting() != null)
			{
				if (client.getLocalPlayer().getInteracting() instanceof NPC)
				{
					lastAttackedIndex = ((NPC) client.getLocalPlayer().getInteracting()).getIndex();
				}
			}
		}
	}

	public boolean execute()
	{
		toaManager.baba.attackPath = null;
		if (!Inventory.contains("Hammer") || !Inventory.contains("Neutralising potion") || gameTickManager.isTickWaiting())
		{
			return false;
		}

		if (toaManager.baba.targetVent != null || toaManager.baba.targetPillar != null)
		{
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();

		NPC shaman = NPCs.getNearest(n -> (
				n.getHealthRatio() != 0
						&& n.distanceTo(playerPoint) > 0
						&& n.distanceTo(playerPoint) <= 10
						&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
						//&& n.getWorldArea().hasLineOfSightTo(client, playerPoint)
						&& (n.getName().equals("Baboon Shaman"))));

		NPC nearestOther = NPCs.getNearest(n -> (
			n.getHealthRatio() != 0
				&& n.distanceTo(playerPoint) > 0
					&& n.distanceTo(playerPoint) <= 10
					&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				//&& n.getWorldArea().hasLineOfSightTo(client, playerPoint)
				&& (n.getName().equals("Baboon Thrower")
				|| n.getName().equals("Baboon Mage")
				|| n.getName().equals("Baboon Brawler"))));

		NPC thrall = NPCs.getNearest(n -> (
			n.getHealthRatio() != 0
				&& n.getIndex() != lastAttackedIndex
				&& n.distanceTo(playerPoint) > 0
					&& n.distanceTo(playerPoint) <= 10
					&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				//&& n.getWorldArea().hasLineOfSightTo(client, playerPoint)
				&& (n.getName().equals("Baboon Thrall"))));

		ArrayList<NPC> allThralls = (ArrayList<NPC>) NPCs.getAll(n -> (
			n.getHealthRatio() != 0
					&& n.distanceTo(playerPoint) > 0
					&& n.distanceTo(playerPoint) <= 10
				//&& n.getWorldArea().hasLineOfSightTo(client, playerPoint)
					&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				&& (n.getName().equals("Baboon Thrall"))));

		NPC stinker = NPCs.getNearest(n -> (
			n.getHealthRatio() != 0
				&& n.distanceTo(playerPoint) > 0
					&& n.distanceTo(playerPoint) <= 10
				//&& n.getWorldArea().hasLineOfSightTo(client, playerPoint)
					&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				&& (n.getName().equals("Cursed Baboon"))));

		NPC ranger = NPCs.getNearest(n -> (
			n.getHealthRatio() != 0
				&& n.distanceTo(playerPoint) > 0
					&& n.distanceTo(playerPoint) <= 10
				//&& n.getWorldArea().hasLineOfSightTo(client, playerPoint)
					&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				&& (n.getName().equals("Baboon Thrower"))));

		NPC brawler = NPCs.getNearest(n -> (
			n.getHealthRatio() != 0
				&& n.distanceTo(playerPoint) > 0
					&& n.distanceTo(playerPoint) <= 10
				//&& n.getWorldArea().hasLineOfSightTo(client, playerPoint)
					&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				&& (n.getName().equals("Baboon Brawler"))));

		NPC magician = NPCs.getNearest(n -> (
				n.getHealthRatio() != 0
						&& n.distanceTo(playerPoint) > 0
						&& n.distanceTo(playerPoint) <= 10
						//&& n.getWorldArea().hasLineOfSightTo(client, playerPoint)
						&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
						&& (n.getName().equals("Baboon Mage"))));

		// Fail safe in the case that we end up with only 1 thrall left and it happens to be the index integer
		if (allThralls.size() <= 1)
		{
			lastAttackedIndex = 0;
		}

		if (shaman != null)
		{
			//Add more logic here to factor in room time into using spec (if room is nearly over i think its better to keep spec vs HP to potentially 2x bgs baba)
			if (!Combat.isSpecEnabled() && Combat.getSpecEnergy() == 100 && Combat.getMissingHealth() >= 20)
			{
				Combat.toggleSpec();
			}
			return attackWithRange(playerPoint, shaman, false);
		}

		else if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MELEE)
			&& allThralls.size() >= 2
			&& toaManager.hasGearEquipped(toaManager.rangeSetup.getAllItemsBp()))
		{
			return attackWithRange(playerPoint, thrall, false);
		}

		else if (stinker != null && ranger == null)
		{
			return attackWithMage(playerPoint, stinker, true);
		}
		else if (ranger != null && brawler != null)
		{
			return attackWithMelee(playerPoint, ranger);
		}
		else if(Prayers.isEnabled(Prayer.PROTECT_FROM_MAGIC)
				&& magician != null){
			return attackWithRange(playerPoint,magician,false);
		}
		else if (nearestOther != null)
		{
			if (nearestOther.getName().equals("Baboon Thrower"))
			{
				return attackWithMelee(playerPoint, nearestOther);
			}
			else if (nearestOther.getName().equals("Baboon Brawler"))
			{
				return attackWithMage(playerPoint, nearestOther, false);
			}
			else if (nearestOther.getName().equals("Baboon Mage"))
			{
				return attackWithRange(playerPoint, nearestOther, false);
			}
		}

		else if (thrall != null)
		{
			return attackWithRange(playerPoint, thrall, false);
		}

		else if (client.getLocalPlayer().isIdle())
		{
			WorldPoint breakTile = toaManager.findClosestTile(breakLoSPoints());
			if (breakTile != null && !playerPoint.equals(breakTile))
			{
				toaManager.print("Breaking LoS");
				toaManager.baba.attackPath = com.example.toagigatron.model.pathing.Movement.getAvoidancePath(breakTile, toaManager.baba.toaCollisionMap, toaManager.baba.getTrueBabaRoom(), toaManager.baba.poisonTiles, new ArrayList<>(), false);
				toaManager.stepAlong(toaManager.baba.attackPath);
				return true;
			}
		}
		return false;
	}


	public ArrayList<WorldPoint> breakLoSPoints()
	{
		ArrayList<WorldPoint> returnList = new ArrayList<>();
		if (toaManager.baba.babaPuzzleStatue == null)
		{
			return returnList;
		}
		WorldPoint nw = toaManager.baba.babaPuzzleStatue.getWorldArea().getCenter().dx(-3).dy(3);
		WorldPoint ne = toaManager.baba.babaPuzzleStatue.getWorldArea().getCenter().dx(3).dy(3);
		WorldPoint sw = toaManager.baba.babaPuzzleStatue.getWorldArea().getCenter().dx(-3).dy(-3);
		WorldPoint se = toaManager.baba.babaPuzzleStatue.getWorldArea().getCenter().dx(3).dy(-3);
		returnList.add(nw);
		returnList.add(ne);
		returnList.add(sw);
		returnList.add(se);
		returnList.removeIf(n -> toaManager.findTileGameObject(client, List.of(ToaConstants.BABA_PUZZLE_POISON), n));
		return returnList;
	}

	public boolean attackWithMage(WorldPoint playerPoint, NPC targetNPC, boolean isStinker)
	{
		WorldPoint safeTile = getSafeAttackTile(targetNPC.getWorldLocation(), isStinker ? 4 : 8, false);
		if (!toaManager.hasGearEquipped(toaManager.mageSetup.getAllItems()))
		{
			toaManager.swap(toaManager.mageSetup.getAllItems());
		}
		if (isStinker && client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(targetNPC))
		{
			toaManager.print("Clicking on current tile to prevent drag");
			clickOnPlayerTile();
			return true;
		}
		if (playerPoint.distanceTo(targetNPC.getWorldLocation()) > (isStinker ? 6 : 8) && safeTile != null)
		{
			toaManager.baba.attackPath = com.example.toagigatron.model.pathing.Movement.getAvoidancePath(safeTile, toaManager.baba.toaCollisionMap, toaManager.baba.getTrueBabaRoom(), toaManager.baba.poisonTiles, new ArrayList<>(), false);
			toaManager.stepAlong(toaManager.baba.attackPath);
			return true;
		}
		if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(targetNPC))
		{
			return false;
		}
		else if (playerPoint.distanceTo(targetNPC.getWorldLocation()) < (isStinker ? 7 : 9))
		{
			toaManager.print("Attacking " + targetNPC.getName());
			targetNPC.interact("Attack");
			return true;
		}
		return false;
	}

	public boolean attackWithMelee(WorldPoint playerPoint, NPC targetNPC)
	{
		WorldPoint safeTile = getSafeAttackTile(targetNPC.getWorldLocation(), 1, true);
		if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItems()))
		{
			toaManager.swap(toaManager.meleeSetup.getAllItems());
		}
		if (playerPoint.distanceTo(targetNPC.getWorldLocation()) > 1 && safeTile != null)
		{
			toaManager.baba.attackPath = com.example.toagigatron.model.pathing.Movement.getAvoidancePath(safeTile, toaManager.baba.toaCollisionMap, toaManager.baba.getTrueBabaRoom(), toaManager.baba.poisonTiles, new ArrayList<>(), false);
			toaManager.stepAlong(toaManager.baba.attackPath);
			return true;
		}
		else if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(targetNPC))
		{
			return false;
		}
		// TODO: change if we are next to it, not if distance is 1
		else if (playerPoint.distanceTo(targetNPC.getWorldLocation()) == 1)
		{
			toaManager.print("Attacking " + targetNPC.getName());
			targetNPC.interact("Attack");
			return true;
		}
		return false;
	}

	public boolean attackWithRange(WorldPoint playerPoint, NPC targetNPC, boolean isStinker)
	{
		WorldPoint safeTile = getSafeAttackTile(targetNPC.getWorldLocation(), isStinker ? 3 : 5, false);
		// If no range gear equipped, equip that first
		if (!toaManager.hasGearEquipped(toaManager.rangeSetup.getAllItemsBp()))
		{
			toaManager.swap(toaManager.rangeSetup.getAllItemsBp());
		}
		if (isStinker && client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(targetNPC))
		{
			toaManager.print("Clicking on current tile to prevent drag");
			clickOnPlayerTile();
			return true;
		}
		else if (playerPoint.distanceTo(targetNPC.getWorldLocation()) > (isStinker ? 4 : 5) && safeTile != null)
		{
			toaManager.baba.attackPath = com.example.toagigatron.model.pathing.Movement.getAvoidancePath(safeTile, toaManager.baba.toaCollisionMap, toaManager.baba.getTrueBabaRoom(), toaManager.baba.poisonTiles, new ArrayList<>(), false);
			toaManager.stepAlong(toaManager.baba.attackPath);
			return true;
		}
		else if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(targetNPC))
		{
			return false;
		}
		else if (playerPoint.distanceTo(targetNPC.getWorldLocation()) < (isStinker ? 4 : 6))
		{
			toaManager.print("Attacking " + targetNPC.getName());
			targetNPC.interact("Attack");
			return true;
		}
		return false;
	}

	private WorldPoint getSafeAttackTile(WorldPoint target, int maxDistance, boolean melee)
	{
		ArrayList<WorldPoint> potentialTiles = new ArrayList<>();
		WorldArea area = new WorldArea(target.dx(-maxDistance).dy(-maxDistance), target.dx(maxDistance + 1).dy(maxDistance + 1));
		for (WorldPoint wp : area.toWorldPointList())
		{
			if (wp.distanceTo(target) > maxDistance || (melee && isCornerTile(wp, target)))
			{
				continue;
			}
			Tile currentTile = Tiles.getAt(wp);
			if (currentTile.getGameObjects() != null
				&& !toaManager.containsObjectBaba(currentTile.getGameObjects())
				&& Reachable.isWalkable(wp))
			{
				potentialTiles.add(wp);
			}
		}
		return getNearest(potentialTiles, client.getLocalPlayer().getWorldLocation(), toaManager.baba.babaPuzzleStatue.getWorldArea().getCenter());
	}

	public void clickOnPlayerTile()
	{
		WorldPoint walkPoint = client.getLocalPlayer().getWorldLocation();
		int sceneX = walkPoint.getX() - client.getBaseX();
		int sceneY = walkPoint.getY() - client.getBaseY();
		Point canv = Perspective.localToCanvas(client, LocalPoint.fromScene(sceneX, sceneY), client.getPlane());
		int x = canv != null ? canv.getX() : -1;
		int y = canv != null ? canv.getY() : -1;
		client.interact(0, MenuAction.WALK.getId(), sceneX, sceneY, x, y);
	}

	private boolean isCornerTile(WorldPoint wp, WorldPoint ref)
	{
		return Math.abs(ref.getX() - wp.getX()) == 1 && Math.abs(ref.getY() - wp.getY()) == 1;
	}

	private WorldPoint getNearest(ArrayList<WorldPoint> worldPoints, WorldPoint player, WorldPoint center)
	{
		int distance = Integer.MAX_VALUE;
		int distanceTwo = Integer.MAX_VALUE;
		WorldPoint returnPoint = null;
		for (WorldPoint wp : worldPoints)
		{
			if (wp.distanceTo(center) < distance)
			{
				distance = wp.distanceTo(center);
				//returnPoint = wp;
			}
		}

		for (WorldPoint wp : worldPoints)
		{
			if (wp.distanceTo(center) > distance)
			{
				continue;
			}
			if (wp.distanceTo(player) < distanceTwo)
			{
				distanceTwo = wp.distanceTo(player);
				returnPoint = wp;
			}
		}
		return returnPoint;
	}


}
