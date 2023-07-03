package com.example.toagigatron.tasks.baba.puzzle;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Combat;
import com.example.Utility.Game;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Movement;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Prayer;
import com.example.Utility.Prayers;
import com.example.Utility.Reachable;
import com.example.Utility.Tiles;
import com.example.Utility.Walker;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Baba attack monkey",
	priority = 10,
	register = true
)
public class BabaAttackMonkey extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;
	private int lastAttackedIndex = 0;

	@Inject
	public BabaAttackMonkey(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE);
	}


	@Subscribe
	public void onProjectileMoved(ProjectileMoved projectileSpawned)
	{
		if (!toaManager.baba.babaPuzzleRoom.contains(client.getLocalPlayer().getWorldLocation()))
		{
			return;
		}
		Projectile projectile = projectileSpawned.getProjectile();
		if (ToaConstants.DARTS.contains(projectile.getId()))
		{
			if (client.getLocalPlayer().getInteracting() != null)
			{
				if (client.getLocalPlayer().getInteracting() instanceof NPC)
				{
					int remainingCycles = projectile.getRemainingCycles();
					int distance = client.getLocalPlayer().getWorldLocation().distanceTo(client.getLocalPlayer().getInteracting().getWorldLocation());
//					System.out.println("Distance -> " + distance + "  Cycles remaining -> " + projectile.getRemainingCycles());
//					System.out.println("Setting lastattackedindex to -> " + ((NPC) client.getLocalPlayer().getInteracting()).getIndex());
//					System.out.println("Remaining cycles -> " + projectile.getRemainingCycles());
					if (distance == 1 && remainingCycles == 37)
					{
						//System.out.println("Distance 1 remaining cycles 37");
						lastAttackedIndex = ((NPC) client.getLocalPlayer().getInteracting()).getIndex();
					}
					else if (distance == 2 && remainingCycles == 42)
					{
						//System.out.println("Distance 1 remaining cycles 42");
						lastAttackedIndex = ((NPC) client.getLocalPlayer().getInteracting()).getIndex();
					}
					else if (distance == 3 && remainingCycles == 47)
					{
						//System.out.println("Distance 1 remaining cycles 47");
						lastAttackedIndex = ((NPC) client.getLocalPlayer().getInteracting()).getIndex();
					}
					else if (distance == 4 && remainingCycles == 52)
					{
						//System.out.println("Distance 1 remaining cycles 52");
						lastAttackedIndex = ((NPC) client.getLocalPlayer().getInteracting()).getIndex();
					}
					else if (distance == 5 && remainingCycles == 57)
					{
						//System.out.println("Distance 1 remaining cycles 57");
						lastAttackedIndex = ((NPC) client.getLocalPlayer().getInteracting()).getIndex();
					}

				}
			}
		}
	}

	public boolean execute()
	{
		toaManager.baba.attackPath = null;
		if (!InventoryUtil.contains("Hammer") || !InventoryUtil.contains("Neutralising potion") || gameTickManager.isTickWaiting())
		{
			return false;
		}

		if (toaManager.baba.targetVent != null || toaManager.baba.targetPillar != null)
		{
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();

		NPC shaman = NPCs.search().alive().filter(n ->
			n.getWorldLocation().distanceTo(playerPoint) > 0
				&& n.getWorldLocation().distanceTo(playerPoint) <= 10
				&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				&& n.getName() != null && n.getName().equals("Baboon Shaman")).nearestToPlayer().orElse(null);

		NPC nearestOther = NPCs.search().alive().filter(n ->
			n.getWorldLocation().distanceTo(playerPoint) > 0
				&& n.getWorldLocation().distanceTo(playerPoint) <= 10
				&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				&& n.getName() != null
				&& (n.getName().equals("Baboon Thrower")
				|| n.getName().equals("Baboon Mage")
				|| n.getName().equals("Baboon Brawler"))).nearestToPlayer().orElse(null);

		NPC thrall = NPCs.search().alive().filter(n ->
			n.getIndex() != lastAttackedIndex
				&& n.getWorldLocation().distanceTo(playerPoint) > 0
				&& n.getWorldLocation().distanceTo(playerPoint) <= 10
				&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				&& n.getName() != null
				&& n.getName().equals("Baboon Thrall")).nearestToPlayer().orElse(null);

		ArrayList<NPC> allThralls = (ArrayList<NPC>) NPCs.search().alive().filter(n ->
			n.getWorldLocation().distanceTo(playerPoint) > 0
				&& n.getWorldLocation().distanceTo(playerPoint) <= 10
				&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				&& n.getName() != null
				&& n.getName().equals("Baboon Thrall")).result();

		System.out.println("All thralls size -> " + allThralls.size());
		NPC stinker = NPCs.search().alive().filter(n ->
			n.getWorldLocation().distanceTo(playerPoint) > 0
				&& n.getWorldLocation().distanceTo(playerPoint) <= 10
				&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				&& n.getName() != null
				&& n.getName().equals("Cursed Baboon")).nearestToPlayer().orElse(null);

		NPC ranger = NPCs.search().alive().filter(n ->
			n.getWorldLocation().distanceTo(playerPoint) > 0
				&& n.getWorldLocation().distanceTo(playerPoint) <= 10
				&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				&& n.getName() != null
				&& n.getName().equals("Baboon Thrower")).nearestToPlayer().orElse(null);

		NPC brawler = NPCs.search().alive().filter(n ->
			n.getWorldLocation().distanceTo(playerPoint) > 0
				&& n.getWorldLocation().distanceTo(playerPoint) <= 10
				&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				&& n.getName() != null
				&& n.getName().equals("Baboon Brawler")).nearestToPlayer().orElse(null);

		NPC magician = NPCs.search().alive().filter(n ->
			n.getWorldLocation().distanceTo(playerPoint) > 0
				&& n.getWorldLocation().distanceTo(playerPoint) <= 10
				&& client.getLocalPlayer().getWorldArea().hasLineOfSightTo(client, n.getWorldLocation())
				&& n.getName() != null
				&& n.getName().equals("Baboon Mage")).nearestToPlayer().orElse(null);


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
			System.out.println("Returning attack shaman at top");
			return attackWithRange(playerPoint, shaman, false);
		}

		else if (!Prayers.isEnabled(Prayer.PROTECT_FROM_MELEE)
			&& allThralls.size() >= 2
			&& thrall != null
			&& toaManager.hasGearEquipped(toaManager.rangeSetup.getAllItemsBp()))
		{
			System.out.println("Returning attack with range thralls at top, thrall index: " + thrall.getIndex() + " prev index: " + lastAttackedIndex);
			return attackWithRange(playerPoint, thrall, false);
		}

		else if (stinker != null && ranger == null)
		{
			System.out.println("Returning attack with mage vs stinker");
			return attackWithMage(playerPoint, stinker, true);
		}
		else if (ranger != null && brawler != null)
		{
			System.out.println("Returning attack with melee vs ranger");
			return attackWithMelee(playerPoint, ranger);
		}
		else if (Prayers.isEnabled(Prayer.PROTECT_FROM_MAGIC)
			&& magician != null)
		{
			System.out.println("Returning attack with range vs magician");
			return attackWithRange(playerPoint, magician, false);
		}
		else if (nearestOther != null)
		{
			if (nearestOther.getName() != null)
			{
				if (nearestOther.getName().equals("Baboon Thrower"))
				{
					System.out.println("thrower nearest other");
					return attackWithMelee(playerPoint, nearestOther);
				}
				else if (nearestOther.getName().equals("Baboon Brawler"))
				{
					System.out.println("brawler nearest other");
					return attackWithMage(playerPoint, nearestOther, false);
				}
				else if (nearestOther.getName().equals("Baboon Mage"))
				{
					System.out.println("mage nearest other");
					return attackWithRange(playerPoint, nearestOther, false);
				}
			}

		}

		else if (thrall != null)
		{
			System.out.println("Thrall down bottom");
			System.out.println("Thrall index: " + thrall.getIndex() + " prev index: " + lastAttackedIndex);
			return attackWithRange(playerPoint, thrall, false);
		}

		else if (Game.isIdle())
		{
			System.out.println("Game is idle? how is everything else null?");
			WorldPoint breakTile = toaManager.findClosestTile(breakLoSPoints());
			if (breakTile != null && !playerPoint.equals(breakTile))
			{
				toaManager.print("Breaking LoS");
				HashSet<WorldPoint> dangerTiles = new HashSet<>();
				dangerTiles.addAll(toaManager.baba.explosionTiles);
				dangerTiles.addAll(toaManager.baba.poisonTiles);
				toaManager.baba.attackPath = EthanApiPlugin.pathToGoal(breakTile, dangerTiles);
				Walker.stepAlong(toaManager.baba.attackPath);
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
		WorldArea babaPuzzleStatueArea = ObjectUtil.getWorldArea(toaManager.baba.babaPuzzleStatue);
		if (babaPuzzleStatueArea == null)
		{
			System.out.println("Baba puzzle statue world area is somehow null");
			return null;
		}

		WorldPoint nw = WorldAreas.getCenter(babaPuzzleStatueArea).dx(-3).dy(3);
		WorldPoint ne = WorldAreas.getCenter(babaPuzzleStatueArea).dx(3).dy(3);
		WorldPoint sw = WorldAreas.getCenter(babaPuzzleStatueArea).dx(-3).dy(-3);
		WorldPoint se = WorldAreas.getCenter(babaPuzzleStatueArea).dx(3).dy(-3);
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
			HashSet<WorldPoint> dangerTiles = new HashSet<>();
			dangerTiles.addAll(toaManager.baba.explosionTiles);
			dangerTiles.addAll(toaManager.baba.poisonTiles);
			toaManager.baba.attackPath = EthanApiPlugin.pathToGoal(safeTile, dangerTiles);
			Walker.stepAlong(toaManager.baba.attackPath);
			return true;
		}
		if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(targetNPC))
		{
			return false;
		}
		else if (playerPoint.distanceTo(targetNPC.getWorldLocation()) < (isStinker ? 7 : 9))
		{
			toaManager.print("Attacking " + targetNPC.getName());
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(targetNPC, "Attack");
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
		if (safeTile != null && (playerPoint.distanceTo(targetNPC.getWorldLocation()) > 1 || (playerPoint.distanceTo(targetNPC.getWorldLocation()) == 1 && isCornerTile(targetNPC.getWorldLocation(), playerPoint))))
		{
			if (isCornerTile(targetNPC.getWorldLocation(), playerPoint))
			{
				System.out.println("Is corner tile, pathing to not corner tile.");
			}
			HashSet<WorldPoint> dangerTiles = new HashSet<>();
			dangerTiles.addAll(toaManager.baba.explosionTiles);
			dangerTiles.addAll(toaManager.baba.poisonTiles);
			toaManager.baba.attackPath = EthanApiPlugin.pathToGoal(safeTile, dangerTiles);
			Walker.stepAlong(toaManager.baba.attackPath);
			return true;
		}
		else if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(targetNPC))
		{
			return false;
		}
		// TODO: change if we are next to it, not if distance is 1
		else if (playerPoint.distanceTo(targetNPC.getWorldLocation()) == 1 && !toaManager.isDiagonalOf(playerPoint, targetNPC.getWorldLocation()))
		{
			toaManager.print("Attacking " + targetNPC.getName());
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(targetNPC, "Attack");
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
			HashSet<WorldPoint> dangerTiles = new HashSet<>();
			dangerTiles.addAll(toaManager.baba.explosionTiles);
			dangerTiles.addAll(toaManager.baba.poisonTiles);
			toaManager.baba.attackPath = EthanApiPlugin.pathToGoal(safeTile, dangerTiles);
			Walker.stepAlong(toaManager.baba.attackPath);
			return true;


		}
		else if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(targetNPC))
		{
			return false;
		}
		else if (playerPoint.distanceTo(targetNPC.getWorldLocation()) < (isStinker ? 4 : 6))
		{
			toaManager.print("Attacking " + targetNPC.getName());
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(targetNPC, "Attack");
			return true;
		}
		return false;
	}

	private WorldPoint getSafeAttackTile(WorldPoint target, int maxDistance, boolean melee)
	{
		ArrayList<WorldPoint> potentialTiles = new ArrayList<>();
		WorldArea area = WorldAreas.createArea(target.dx(-maxDistance).dy(-maxDistance), target.dx(maxDistance + 1).dy(maxDistance + 1));
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
		return getNearest(potentialTiles, client.getLocalPlayer().getWorldLocation(), WorldAreas.getCenter(Objects.requireNonNull(ObjectUtil.getWorldArea(toaManager.baba.babaPuzzleStatue))));
	}

	public void clickOnPlayerTile()
	{
		WorldPoint walkPoint = client.getLocalPlayer().getWorldLocation();
//		int sceneX = walkPoint.getX() - client.getBaseX();
//		int sceneY = walkPoint.getY() - client.getBaseY();
//		Point canv = Perspective.localToCanvas(client, LocalPoint.fromScene(sceneX, sceneY), client.getPlane());
//		int x = canv != null ? canv.getX() : -1;
//		int y = canv != null ? canv.getY() : -1;
		MousePackets.queueClickPacket();
		Movement.walk(walkPoint);
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
