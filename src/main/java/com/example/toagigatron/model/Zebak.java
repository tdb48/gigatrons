 package com.example.toagigatron.model;


import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.bossmodel.ZebakJug;
import com.example.toagigatron.model.constants.Direction;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.puzzlemodel.ZebakWaterfallRoom;
import java.util.ArrayList;
import java.util.Objects;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

public class Zebak
{
	@Inject
	ToaManager toaManager;
	@Inject
	Client client;
	@Inject
	EventBus eventBus;
	@Inject
	GameTickManager gameTickManager;

	public void register()
	{
		this.eventBus.register(this);
	}

	public void unregister()
	{
		this.eventBus.unregister(this);
	}

	public ZebakWaterfallRoom northEastZebakPuzzle = null;
	public ZebakWaterfallRoom northWestZebakPuzzle = null;
	public ZebakWaterfallRoom southEastZebakPuzzle = null;
	public ZebakWaterfallRoom southWestZebakPuzzle = null;
	public ZebakWaterfallRoom currentZebakPuzzle = null;
	public int bloodBarrageTick = 0;
	public NPC zebakBoss = null;
	public int bgsHit = 0;
	public ArrayList<LocalPoint> poisonTiles = new ArrayList<>();
	public ArrayList<WorldPoint> poisonWorldPoints = new ArrayList<>();
	public ArrayList<LocalPoint> rockTiles = new ArrayList<>();
	public ArrayList<LocalPoint> diagonalRockTiles = new ArrayList<>();
	public ArrayList<LocalPoint> staticJugs = new ArrayList<>();
	public ArrayList<LocalPoint> rollingJugs = new ArrayList<>();
	public ArrayList<LocalPoint> safeRockTiles = new ArrayList<>();
	public ArrayList<WorldArea> jugSplashArea = new ArrayList<>();
	public ArrayList<WorldPoint> waves = new ArrayList<>();
	public ArrayList<WorldPoint> waves2 = new ArrayList<>();
	public ArrayList<WorldPoint> singleWaves = new ArrayList<>();
	public ArrayList<NPC> wavesOne = new ArrayList<>();
	public ArrayList<NPC> wavesTwo = new ArrayList<>();
	public ArrayList<NPC> wavesThree = new ArrayList<>();
	public WorldArea wavesOneSafe = null;
	public WorldArea wavesTwoSafe = null;
	public WorldArea wavesThreeSafe = null;
	public boolean wavesSolved = false;

	public ArrayList<WorldPoint> bloods = new ArrayList<>();
	public ZebakJug hittableJug = null;
	public ArrayList<WorldPoint> blowpipeTiles = new ArrayList<>();
	public ArrayList<WorldPoint> allRoomTiles = new ArrayList<>();
	public ArrayList<WorldPoint> allWalkableRoomTiles = new ArrayList<>();
	public ArrayList<WorldPoint> allWalkableRoomTilesIncludingChompZone = new ArrayList<>();
	public ArrayList<WorldPoint> zebakEastTiles = new ArrayList<>();

	public void resetVariables()
	{
		northEastZebakPuzzle = null;
		northWestZebakPuzzle = null;
		southEastZebakPuzzle = null;
		southWestZebakPuzzle = null;
		currentZebakPuzzle = null;
		bloodBarrageTick = 0;
		zebakBoss = null;
		bgsHit = 0;
		poisonTiles = new ArrayList<>();
		poisonWorldPoints = new ArrayList<>();
		rockTiles = new ArrayList<>();
		diagonalRockTiles = new ArrayList<>();
		staticJugs = new ArrayList<>();
		rollingJugs = new ArrayList<>();
		safeRockTiles = new ArrayList<>();
		jugSplashArea = new ArrayList<>();
		waves = new ArrayList<>();
		waves2 = new ArrayList<>();
		singleWaves = new ArrayList<>();
		wavesOne = new ArrayList<>();
		wavesTwo = new ArrayList<>();
		wavesThree = new ArrayList<>();
		wavesOneSafe = null;
		wavesTwoSafe = null;
		wavesThreeSafe = null;
		wavesSolved = false;
		bloods = new ArrayList<>();
		hittableJug = null;
		blowpipeTiles = new ArrayList<>();
		allRoomTiles = new ArrayList<>();
		allWalkableRoomTiles = new ArrayList<>();
		allWalkableRoomTilesIncludingChompZone = new ArrayList<>();
		zebakEastTiles = new ArrayList<>();
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved projectilemoved)
	{
		Projectile projectile = projectilemoved.getProjectile();
		LocalPoint targetTile = projectile.getTarget();
		int id = projectile.getId();
		if (id == ToaConstants.ZEBAK_JUG_PROJECTILE && !staticJugs.contains(targetTile))
		{
			staticJugs.add(targetTile);
		}
		if (id == ToaConstants.ZEBAK_ROCK_PROJECTILE && !rockTiles.contains(targetTile))
		{
			rockTiles.add(targetTile);
		}
		if (ToaConstants.ZEBAK_POISON_PROJECTILE.contains(id) && !poisonTiles.contains(targetTile))
		{
			poisonTiles.add(targetTile);
		}

	}

	@Subscribe
	public void onNPCSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();
		if (Objects.requireNonNull(npc.getName()).equalsIgnoreCase("wave"))
		{
			if (wavesOne.size() < (toaManager.getRoomLevel() > 1 ? 19 : 18))
			{
				wavesSolved = false;
				wavesOne.add(npc);
			}
			else if (wavesTwo.size() < (toaManager.getRoomLevel() > 1 ? 19 : 18))
			{
				wavesTwo.add(npc);
			}
			else if (wavesThree.size() < (toaManager.getRoomLevel() > 1 ? 19 : 18))
			{
				wavesThree.add(npc);
			}
		}
	}

	@Subscribe
	public void onNPCDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();
		wavesOne.remove(npc);
		wavesTwo.remove(npc);
		wavesThree.remove(npc);
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (toaManager.getStage() != Stage.ZEBAK_PUZZLE && toaManager.getStage() != Stage.ZEBAK_BOSS)
		{
			return;
		}
		safeRockTiles = generateRockSafeTiles();
		jugSplashArea = generateSplashJugTiles();
		diagonalRockTiles = generateDiagonalRockTiles();
		rollingJugs = npcsToWorldpoints((ArrayList<NPC>) NPCUtil.findAll(ToaConstants.ZEBAK_ROLLING_JUG));
		waves.clear();
		waves2.clear();
		singleWaves.clear();
		if (zebakEastTiles.isEmpty() && zebakBoss != null)
		{
			zebakEastTiles.addAll(zebakEastTiles());
		}
		for (NPC wave : NPCUtil.findAll("Wave"))
		{
			// Going south
			if (wave.getOrientation() == 0)
			{
				waves.add(wave.getWorldLocation().dy(-1));
				waves.add(wave.getWorldLocation().dy(1));
				waves.add(wave.getWorldLocation().dy(2));

				waves2.add(wave.getWorldLocation().dy(-1));
				waves2.add(wave.getWorldLocation().dy(-2));
				waves2.add(wave.getWorldLocation().dy(1));
				waves2.add(wave.getWorldLocation().dy(2));
			}
			// Going north
			else
			{
				waves.add(wave.getWorldLocation().dy(1));
				waves.add(wave.getWorldLocation().dy(-1));
				waves.add(wave.getWorldLocation().dy(-2));

				waves2.add(wave.getWorldLocation().dy(1));
				waves2.add(wave.getWorldLocation().dy(2));
				waves2.add(wave.getWorldLocation().dy(-1));
				waves2.add(wave.getWorldLocation().dy(-2));
			}
			singleWaves.add(wave.getWorldLocation());
			waves.add(wave.getWorldLocation());
			waves2.add(wave.getWorldLocation());
		}
		bloods.clear();
		for (NPC blood : NPCs.search().nameContains("blood").filter(n -> n.getHealthRatio() != 0).result())
		{
			bloods.add(blood.getWorldLocation());
		}
		boolean isJugProjectile = false;
		for (Projectile p : client.getProjectiles())
		{
			if (p.getId() == ToaConstants.ZEBAK_JUG_PROJECTILE)
			{
				isJugProjectile = true;
			}
		}
		if (!isJugProjectile)
		{
			staticJugs = npcsToWorldpoints((ArrayList<NPC>) NPCUtil.findAll(ToaConstants.ZEBAK_STATIC_JUG));
		}
		zebakBoss = NPCUtil.findNearest("Zebak");
		if (bloodBarrageTick > 0)
		{
			bloodBarrageTick--;
		}
		hittableJug = findHittableJug();
		poisonWorldPoints = toaManager.lpToWp(poisonTiles);
		GameObject entry = ObjectUtil.getNearestGameObject(ToaConstants.ZEBAK_BOSS_ENTRY);
		if (entry != null && allRoomTiles.size() == 0)
		{
			WorldPoint southWest = entry.getWorldLocation().dx(-27).dy(-9);
			WorldPoint northEast = entry.getWorldLocation().dx(-11).dy(11);
			WorldArea zebakArea = WorldAreas.createArea(southWest, northEast);
			allRoomTiles = (ArrayList<WorldPoint>) zebakArea.toWorldPointList();
		}
		if (entry != null && blowpipeTiles.size() == 0)
		{
			WorldPoint southWest = entry.getWorldLocation().dx(-27).dy(-8);
			WorldPoint northEast = entry.getWorldLocation().dx(-22).dy(11);
			WorldArea blowpipeArea = WorldAreas.createArea(southWest, northEast);
			blowpipeTiles = (ArrayList<WorldPoint>) blowpipeArea.toWorldPointList();
		}
		manuallyTrimZebakRoom(allRoomTiles, WorldAreas.getCenter(zebakBoss.getWorldArea()));
		if (allRoomTiles.size() > 0)
		{
			allWalkableRoomTiles.clear();
			allWalkableRoomTilesIncludingChompZone.clear();
			for (WorldPoint wp : allRoomTiles)
			{
				if (!Reachable.isWalkable(wp) ||
					Reachable.isObstacle(wp) ||
					poisonTiles.contains(LocalPoint.fromWorld(client, wp)) ||
					rockTiles.contains(LocalPoint.fromWorld(client, wp)) ||
					waves.contains(wp) ||
					staticJugs.contains(LocalPoint.fromWorld(client, wp)))
				{
					continue;
				}
				allWalkableRoomTilesIncludingChompZone.add(wp);

				if (wp.distanceTo(zebakBoss.getWorldArea()) > 1)
				{
					allWalkableRoomTiles.add(wp);
				}

			}
		}
		generateSafeWaves();
	}

	public void generateSafeWaves()
	{
		wavesOneSafe = null;
		wavesTwoSafe = null;
		wavesThreeSafe = null;
		boolean levelTwoZebak = toaManager.getRoomLevel() > 1;
		wavesOneSafe = generateSafeWaves(levelTwoZebak, wavesOne);
		wavesTwoSafe = generateSafeWaves(levelTwoZebak, wavesTwo);
		wavesThreeSafe = generateSafeWaves(levelTwoZebak, wavesThree);
	}

	public boolean isPuzzleActive()
	{
		GameObject exit = ObjectUtil.getNearestGameObject(ToaConstants.ZEBAK_PUZZLE_EXIT);
		if (exit == null)
		{
			return false;
		}
		if (Reachable.isWalkable(exit.getWorldLocation().dx(3)))
		{
			return false;
		}
		GameObject entrance = ObjectUtil.getNearestGameObject(ToaConstants.ZEBAK_PUZZLE_ENTRANCE);
		if (entrance == null)
		{
			return false;
		}
		return !Reachable.isWalkable(entrance.getWorldLocation().dx(-2));
	}

	public boolean meleeRange(WorldPoint wp)
	{
		for (WorldPoint zebakEastTile : zebakEastTiles())
		{
			if (wp.distanceTo(zebakEastTile) <= 1)
			{
				return true;
			}
		}
		return false;
	}

	public ArrayList<WorldPoint> zebakEastTiles()
	{
		ArrayList<WorldPoint> tiles = new ArrayList<>();
		if (zebakBoss == null)
		{
			return tiles;
		}
		int x = WorldAreas.getCenter(zebakBoss.getWorldArea()).dx(4).getX();
		for (WorldPoint wp : zebakBoss.getWorldArea().toWorldPointList())
		{
			if (wp.getX() == x)
			{
				tiles.add(wp);
			}
		}
		return tiles;
	}

	public WorldPoint centerOfZebakRoom()
	{
		GameObject entry = ObjectUtil.getObject(ToaConstants.ZEBAK_BOSS_ENTRY);
		if (entry == null)
		{
			return null;
		}
		// Didn't test but looked at vod - 25 tiles west of the entry obelisk
		WorldPoint entryWorldPoint = entry.getWorldLocation();
		return entryWorldPoint.dx(-25);
	}

	public int distanceToBlood()
	{
		if (bloods.isEmpty())
		{
			return Integer.MAX_VALUE;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		WorldPoint closestBlood = toaManager.findClosestTile(bloods, playerPoint);
		if (closestBlood == null)
		{
			toaManager.print("Weird error in distanceToBlood");
			return Integer.MAX_VALUE;
		}
		return playerPoint.distanceTo(closestBlood);
	}

	public int distanceToBlood(WorldPoint targetPoint)
	{
		if (bloods.isEmpty())
		{
			return Integer.MAX_VALUE;
		}
		WorldPoint closestBlood = toaManager.findClosestTile(bloods, targetPoint);
		if (closestBlood == null)
		{
			toaManager.print("Weird error in distanceToBlood");
			return Integer.MAX_VALUE;
		}
		return targetPoint.distanceTo(closestBlood);
	}

	public WorldPoint closestBlood()
	{
		if (bloods.isEmpty())
		{
			return null;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		return toaManager.findClosestTile(bloods, playerPoint);
	}

	public boolean isPastThirdWave()
	{
		if (wavesThree.isEmpty())
		{
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		NPC wave = wavesThree.get(0);
		int orientation = wave.getOrientation();
		if (orientation == 0)
		{
			return wave.getWorldLocation().getY() < (playerPoint.getY() - 1);
		}
		else
		{
			return wave.getWorldLocation().getY() > (playerPoint.getY() + 1);
		}
	}

	public int getAttackDistance()
	{
		if (Equipment.search().withId(toaManager.mageSetup.weapon).first().orElse(null) != null)
		{
			return ToaConstants.SANG_DISTANCE;
		}
		return ToaConstants.BLOWPIPE_DISTANCE;
	}

	public ArrayList<WorldPoint> getAreaPastThirdWave()
	{
		ArrayList<WorldPoint> returnList = new ArrayList<>(allRoomTiles);
		if (wavesThree.isEmpty())
		{
			return returnList;
		}
		NPC wave = wavesThree.get(0);
		int orientation = wave.getOrientation();
		if (orientation == 0)
		{
			returnList.removeIf(n -> (n.getY() - 2) < wave.getWorldLocation().getY());
		}
		else
		{
			returnList.removeIf(n -> (n.getY() + 2) > wave.getWorldLocation().getY());
		}
		return returnList;
	}

	public WorldArea generateSafeWaves(boolean levelTwo, ArrayList<NPC> waves)
	{
		if (!waves.isEmpty())
		{
			boolean south = waves.get(0).getOrientation() == 0;
			ArrayList<WorldPoint> gap = (ArrayList<WorldPoint>) findTheGap(waves).toWorldPointList();
			if (findTheGap(waves).toWorldPointList() == null)
			{
				toaManager.print("error123213");
			}
			if (south)
			{
				WorldPoint northEast = gap.get(gap.size() - 1).dx(1);
				WorldPoint southWest = northEast.dx(levelTwo ? -2 : -3).dy(-4);
				return WorldAreas.createArea(southWest, northEast);
			}
			else
			{
				WorldPoint southWest = gap.get(0).dy(1);
				WorldPoint northEast = southWest.dx(levelTwo ? 2 : 3).dy(5);
				return WorldAreas.createArea(southWest, northEast);
			}
		}
		return null;
	}


	public WorldArea findTheGap(ArrayList<NPC> waves)
	{
		for (int i = 0; i < waves.size() - 1; i++)
		{
			WorldPoint curr = waves.get(i).getWorldLocation();
			WorldPoint next = waves.get(i + 1).getWorldLocation();
			if (next.getX() > curr.getX() + 1)
			{
				return WorldAreas.createArea(curr.dx(1), next.dy(1));
			}
		}
		return null;
	}

	public ZebakJug findHittableJug()
	{
		ArrayList<ZebakJug> solutions = new ArrayList<>();

		/*
		 * 1. Jugs that will directly hit a rock
		 * 2. Jugs that are NOT in the back of the room, that need to be hit
		 * 3. Jugs that are in the back of the room, that need to be hit.
		 *  */

		/*
		 * Best jug is closest one that has a free tile for pulling OR pushing
		 * */
		Direction direction;
		if (rockTiles.isEmpty())
		{
			return null;
		}
		for (LocalPoint rockTile : rockTiles)
		{
			int x = rockTile.getX();
			for (LocalPoint staticJugTile : staticJugs)
			{
				int x2 = staticJugTile.getX();
				if (x == x2)
				{
					ArrayList<NPC> npcs = (ArrayList<NPC>) NPCs.search().filter(n -> n.getLocalLocation().getX() == staticJugTile.getX() && n.getId() == ToaConstants.ZEBAK_STATIC_JUG).result();
					if (!npcs.isEmpty())
					{
						for (NPC npc : npcs)
						{
							if (npc.getLocalLocation().getY() < rockTile.getY())
							{
								direction = Direction.NORTH;
								solutions.add(new ZebakJug(npc, direction, poisonTiles, npc.getLocalLocation()));
							}
							else if (npc.getLocalLocation().getY() > rockTile.getY())
							{
								direction = Direction.SOUTH;
								solutions.add(new ZebakJug(npc, direction, poisonTiles, npc.getLocalLocation()));
							}
						}
					}
					else
					{
						if (staticJugTile.getY() < rockTile.getY())
						{
							direction = Direction.NORTH;
							solutions.add(new ZebakJug(null, direction, poisonTiles, staticJugTile));
						}
						else if (staticJugTile.getY() > rockTile.getY())
						{
							direction = Direction.SOUTH;
							solutions.add(new ZebakJug(null, direction, poisonTiles, staticJugTile));
						}
					}
				}
			}
		}
		for (LocalPoint rockTile : diagonalRockTiles)
		{
			int x = rockTile.getX();
			int y = rockTile.getY();

			for (LocalPoint staticJugTile : staticJugs)
			{
				int x2 = staticJugTile.getX();
				int y2 = staticJugTile.getY();
				if (isDiagonalOf(rockTile, staticJugTile))
				{
					ArrayList<NPC> npcs = (ArrayList<NPC>) NPCs.search().filter(n -> staticJugTile.equals(n.getLocalLocation()) && n.getId() == ToaConstants.ZEBAK_STATIC_JUG).result();
					if (!npcs.isEmpty())
					{

						for (NPC npc : npcs)
						{
							if (x2 > x && y2 < y)
							{
								direction = Direction.NORTH_WEST;
								solutions.add(new ZebakJug(npc, direction, poisonTiles, npc.getLocalLocation()));
							}
							if (x2 > x && y2 > y)
							{
								direction = Direction.SOUTH_WEST;
								solutions.add(new ZebakJug(npc, direction, poisonTiles, npc.getLocalLocation()));
							}
							if (x2 < x && y2 < y)
							{
								direction = Direction.NORTH_EAST;
								solutions.add(new ZebakJug(npc, direction, poisonTiles, npc.getLocalLocation()));
							}
							if (x2 < x && y2 > y)
							{
								direction = Direction.SOUTH_EAST;
								solutions.add(new ZebakJug(npc, direction, poisonTiles, npc.getLocalLocation()));
							}
						}
					}
					else
					{
						if (x2 > x && y2 < y)
						{
							direction = Direction.NORTH_WEST;
							solutions.add(new ZebakJug(null, direction, poisonTiles, staticJugTile));
						}
						if (x2 > x && y2 > y)
						{
							direction = Direction.SOUTH_WEST;
							solutions.add(new ZebakJug(null, direction, poisonTiles, staticJugTile));
						}
						if (x2 < x && y2 < y)
						{
							direction = Direction.NORTH_EAST;
							solutions.add(new ZebakJug(null, direction, poisonTiles, staticJugTile));
						}
						if (x2 < x && y2 > y)
						{
							direction = Direction.SOUTH_EAST;
							solutions.add(new ZebakJug(null, direction, poisonTiles, staticJugTile));
						}
					}
				}
			}
		}
		if (solutions.isEmpty())
		{
			solutions.addAll(findcantthinkofanem());
		}
		solutions.removeIf(n -> !n.valid);
		solutions.removeIf(n -> isBlocked(n.pushTile) && isBlocked(n.pullTile));
		if (!solutions.isEmpty())
		{
			ZebakJug result = toaManager.findClosestNPC(solutions);
			if (result == null)
			{
				return solutions.get(0);
			}
			else
			{
				return result;
			}
		}
		return null;
	}

	public boolean isBlocked(LocalPoint lp)
	{
		WorldPoint wp = WorldPoint.fromLocal(client, lp);
		if (poisonWorldPoints.contains(wp))
		{
			return true;
		}
		return !Reachable.isWalkable(wp);
	}


	public LocalPoint getSafeRoarTile()
	{
		ArrayList<LocalPoint> potentialTiles = new ArrayList<>();
		for (LocalPoint lp : safeRockTiles)
		{
			if (!poisonTiles.contains(lp))
			{
				potentialTiles.add(lp);
			}
		}
		if (!potentialTiles.isEmpty() && toaManager.zebak.zebakBoss != null)
		{
			LocalPoint zebakTile = LocalPoint.fromWorld(client, WorldAreas.getCenter(toaManager.zebak.zebakBoss.getWorldArea()));
			return toaManager.findClosestTile(potentialTiles, zebakTile);
		}
		else
		{
			toaManager.print("SOMETHING WRONG IN GET SAFE ROAR TILE");
		}
		return null;
	}

	public boolean isRoarSolved()
	{
		return getSafeRoarTile() != null;
	}

	public ArrayList<ZebakJug> findcantthinkofanem()
	{
		ArrayList<ZebakJug> solutions = new ArrayList<>();
		Direction direction;
		for (LocalPoint rockTile : safeRockTiles)
		{
			int x = rockTile.getX();
			for (WorldArea worldArea : jugSplashArea)
			{
				ArrayList<WorldPoint> worldAreaPoint = (ArrayList<WorldPoint>) worldArea.toWorldPointList();
				for (WorldPoint wp : worldAreaPoint)
				{
					LocalPoint staticJugTile = LocalPoint.fromWorld(client, wp);
					if (staticJugTile == null)
					{
						continue;
					}
					int x2 = staticJugTile.getX();
					if (x == x2)
					{
						LocalPoint centerTile = LocalPoint.fromWorld(client, WorldAreas.getCenter(worldArea));
						if (centerTile == null)
						{
							continue;
						}
						ArrayList<NPC> npcs = (ArrayList<NPC>) NPCs.search().filter(n -> n.getLocalLocation().getX() == centerTile.getX() && n.getId() == ToaConstants.ZEBAK_STATIC_JUG).result();
						if (!npcs.isEmpty())
						{
							for (NPC npc : npcs)
							{
//						toaManager.print("Found potential jug at " + npc.getWorldLocation());
								if (npc.getLocalLocation().getY() < rockTile.getY())
								{
									direction = Direction.NORTH;
									solutions.add(new ZebakJug(npc, direction, poisonTiles, npc.getLocalLocation()));
								}
								else if (npc.getLocalLocation().getY() > rockTile.getY())
								{
									direction = Direction.SOUTH;
									solutions.add(new ZebakJug(npc, direction, poisonTiles, npc.getLocalLocation()));
								}
							}
						}
						else
						{
							if (staticJugTile.getY() < rockTile.getY())
							{
								direction = Direction.NORTH;
								solutions.add(new ZebakJug(null, direction, poisonTiles, centerTile));
							}
							else if (staticJugTile.getY() > rockTile.getY())
							{
								direction = Direction.SOUTH;
								solutions.add(new ZebakJug(null, direction, poisonTiles, centerTile));
							}
						}
					}
				}
			}
		}

		if (solutions.isEmpty())
		{
			for (LocalPoint rockTile : safeRockTiles)
			{
				int x = rockTile.getX();
				int y = rockTile.getY();

				for (WorldArea worldArea : jugSplashArea)
				{
					ArrayList<WorldPoint> worldAreaPoint = (ArrayList<WorldPoint>) worldArea.toWorldPointList();
					for (WorldPoint wp : worldAreaPoint)
					{
						LocalPoint centerTile = LocalPoint.fromWorld(client, WorldAreas.getCenter(worldArea));
						LocalPoint staticJugTile = LocalPoint.fromWorld(client, wp);
						if (staticJugTile == null)
						{
							continue;
						}
						int x2 = staticJugTile.getX();
						int y2 = staticJugTile.getY();
						if (isDiagonalOf(rockTile, staticJugTile))
						{
							ArrayList<NPC> npcs = (ArrayList<NPC>) NPCs.search().filter(n -> staticJugTile.equals(n.getLocalLocation()) && n.getId() == ToaConstants.ZEBAK_STATIC_JUG).result();
							if (!npcs.isEmpty())
							{
								for (NPC npc : npcs)
								{
									if (x2 > x && y2 < y)
									{
										direction = Direction.NORTH_WEST;
										solutions.add(new ZebakJug(npc, direction, poisonTiles, npc.getLocalLocation()));
									}
									if (x2 > x && y2 > y)
									{
										direction = Direction.SOUTH_WEST;
										solutions.add(new ZebakJug(npc, direction, poisonTiles, npc.getLocalLocation()));
									}
									if (x2 < x && y2 < y)
									{
										direction = Direction.NORTH_EAST;
										solutions.add(new ZebakJug(npc, direction, poisonTiles, npc.getLocalLocation()));
									}
									if (x2 < x && y2 > y)
									{
										direction = Direction.SOUTH_EAST;
										solutions.add(new ZebakJug(npc, direction, poisonTiles, npc.getLocalLocation()));
									}
								}
							}
							else
							{
								if (x2 > x && y2 < y)
								{
									direction = Direction.NORTH_WEST;
									solutions.add(new ZebakJug(null, direction, poisonTiles, centerTile));
								}
								if (x2 > x && y2 > y)
								{
									direction = Direction.SOUTH_WEST;
									solutions.add(new ZebakJug(null, direction, poisonTiles, centerTile));
								}
								if (x2 < x && y2 < y)
								{
									direction = Direction.NORTH_EAST;
									solutions.add(new ZebakJug(null, direction, poisonTiles, centerTile));
								}
								if (x2 < x && y2 > y)
								{
									direction = Direction.SOUTH_EAST;
									solutions.add(new ZebakJug(null, direction, poisonTiles, centerTile));
								}
							}
						}
					}
				}
			}
		}

		return solutions;
	}

	public boolean isDiagonalOf(LocalPoint lp, LocalPoint lp2)
	{
		int xDifference = Math.abs(lp.getX() - lp2.getX());
		int yDifference = Math.abs(lp.getY() - lp2.getY());
		// Roll max 8 tiles
		return yDifference == xDifference && xDifference < 1152;
	}

	public ArrayList<LocalPoint> generateDiagonalRockTiles()
	{
		ArrayList<LocalPoint> returnList = new ArrayList<>();
		if (rockTiles.isEmpty())
		{
			return returnList;
		}
		for (LocalPoint lp : rockTiles)
		{
			returnList.add(new LocalPoint(lp.getX() + 128, lp.getY()));
			returnList.add(new LocalPoint(lp.getX() - 128, lp.getY()));
			returnList.add(new LocalPoint(lp.getX(), lp.getY() + 128));
			returnList.add(new LocalPoint(lp.getX(), lp.getY() - 128));
			returnList.add(lp);
		}
		return returnList;
	}

	public ArrayList<WorldArea> generateSplashJugTiles()
	{
		ArrayList<WorldArea> returnList = new ArrayList<>();
		for (LocalPoint lp : staticJugs)
		{
			WorldPoint localPoint = WorldPoint.fromLocal(client, lp);
			WorldPoint southWest = localPoint.dx(-2).dy(-2);
			WorldPoint northEast = localPoint.dx(3).dy(3);
			returnList.add(WorldAreas.createArea(southWest, northEast));
		}
		for (LocalPoint lp : rollingJugs)
		{
			WorldPoint localPoint = WorldPoint.fromLocal(client, lp);
			WorldPoint southWest = localPoint.dx(-2).dy(-2);
			WorldPoint northEast = localPoint.dx(3).dy(3);
			returnList.add(WorldAreas.createArea(southWest, northEast));
		}
		return returnList;
	}

	public ArrayList<LocalPoint> generateRockSafeTiles()
	{
		ArrayList<LocalPoint> returnList = new ArrayList<>();
		for (LocalPoint lp : rockTiles)
		{
			returnList.add(new LocalPoint(lp.getX() + 128, lp.getY()));
			returnList.add(new LocalPoint(lp.getX() + 256, lp.getY()));
			returnList.add(new LocalPoint(lp.getX() + 384, lp.getY()));
		}
		return returnList;
	}

	public ArrayList<LocalPoint> npcsToWorldpoints(ArrayList<NPC> npcs)
	{
		ArrayList<LocalPoint> returnList = new ArrayList<>();
		for (NPC npc : npcs)
		{
			returnList.add(npc.getLocalLocation());
		}
		return returnList;
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectCreated)
	{
		GraphicsObject graphicsObject = graphicsObjectCreated.getGraphicsObject();
		if (graphicsObject.getId() == ToaConstants.ZEBAK_BLOOD_BARRAGE)
		{
			if (toaManager.zebak.zebakBoss != null && toaManager.zebak.zebakBoss.getId() == ToaConstants.ZEBAK_ENRAGE)
			{
				bloodBarrageTick = 3;
			}
			else
			{
				bloodBarrageTick = 4;
			}
		}
	}

	public boolean wavesActive()
	{
		return !wavesOne.isEmpty() || !wavesTwo.isEmpty() || !wavesThree.isEmpty();
	}

	public boolean allWavesActive()
	{
		return !wavesOne.isEmpty() && !wavesTwo.isEmpty() && !wavesThree.isEmpty();
	}

	public boolean isInBossRoom()
	{
		GameObject entry = ObjectUtil.getNearestGameObject(ToaConstants.ZEBAK_BOSS_ENTRY);
		if (entry == null)
		{
			return false;
		}
		WorldPoint entryWorldPoint = entry.getWorldLocation();
		return !Reachable.isWalkable(entryWorldPoint.dx(1));
	}

	@Subscribe
	public void onHitSplat(HitsplatApplied hitsplatApplied)
	{
		if (hitsplatApplied.getActor().equals(zebakBoss)
			&& client.getLocalPlayer().getAnimation() == ToaConstants.BGS_SPEC_ANIMATION
			&& toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItemsBgs()))
		{
			toaManager.print("Hit bgs with " + hitsplatApplied.getHitsplat().getAmount());
			bgsHit = hitsplatApplied.getHitsplat().getAmount();
		}
	}

	@Subscribe
	public void onGameObjectDespawn(GameObjectDespawned gameObjectDespawned)
	{
		WorldPoint refPoint = gameObjectDespawned.getGameObject().getWorldLocation();
		LocalPoint localRefPoint = gameObjectDespawned.getGameObject().getLocalLocation();
		if (ToaConstants.ZEBAK_POISON_GAME_OBJECT.contains(gameObjectDespawned.getGameObject().getId()))
		{
			poisonTiles.remove(localRefPoint);
		}
		if (gameObjectDespawned.getGameObject().getId() == ToaConstants.ZEBAK_ROAR_ROCK)
		{
			rockTiles.remove(localRefPoint);
		}
	}

	@Subscribe
	public void onGameObjectSpawn(GameObjectSpawned gameObjectSpawned)
	{
		WorldPoint refPoint = gameObjectSpawned.getGameObject().getWorldLocation();
		LocalPoint localRefPoint = gameObjectSpawned.getGameObject().getLocalLocation();
		if (ToaConstants.ZEBAK_POISON_GAME_OBJECT.contains(gameObjectSpawned.getGameObject().getId()) && !poisonTiles.contains(localRefPoint))
		{
			poisonTiles.add(localRefPoint);
		}
		if (gameObjectSpawned.getGameObject().getId() == ToaConstants.ZEBAK_ROAR_ROCK && !rockTiles.contains(localRefPoint))
		{
			rockTiles.add(localRefPoint);
		}
		if (gameObjectSpawned.getGameObject().getId() == ToaConstants.INACTIVE_ZEBAK_WATERFALL && northEastZebakPuzzle != null)
		{
			if (refPoint.isInArea(northEastZebakPuzzle.roomArea))
			{
				northEastZebakPuzzle.active = false;
				currentZebakPuzzle = northWestZebakPuzzle;
			}
			if (refPoint.isInArea(northWestZebakPuzzle.roomArea))
			{
				northWestZebakPuzzle.active = false;
				currentZebakPuzzle = southEastZebakPuzzle;
			}
			if (refPoint.isInArea(southEastZebakPuzzle.roomArea))
			{
				southEastZebakPuzzle.active = false;
				currentZebakPuzzle = southWestZebakPuzzle;
			}
			if (refPoint.isInArea(southWestZebakPuzzle.roomArea))
			{
				southWestZebakPuzzle.active = false;
				currentZebakPuzzle = northEastZebakPuzzle;
			}
		}
		else if (gameObjectSpawned.getGameObject().getId() == ToaConstants.ACTIVE_ZEBAK_WATERFALL && northEastZebakPuzzle != null)
		{
			if (refPoint.isInArea(northEastZebakPuzzle.roomArea))
			{
				northEastZebakPuzzle.active = true;
				currentZebakPuzzle = northEastZebakPuzzle;
			}
			if (refPoint.isInArea(northWestZebakPuzzle.roomArea))
			{
				northWestZebakPuzzle.active = true;
				currentZebakPuzzle = northWestZebakPuzzle;
			}
			if (refPoint.isInArea(southEastZebakPuzzle.roomArea))
			{
				southEastZebakPuzzle.active = true;
				currentZebakPuzzle = southEastZebakPuzzle;
			}
			if (refPoint.isInArea(southWestZebakPuzzle.roomArea))
			{
				southWestZebakPuzzle.active = true;
				currentZebakPuzzle = southWestZebakPuzzle;
			}
		}
	}

	public int distanceToZebak()
	{
		if (zebakBoss == null)
		{
			return 1000;
		}
		return zebakBoss.getWorldArea().distanceTo(client.getLocalPlayer().getWorldLocation());
	}

	public void generateZebakWaterfallRooms(GameObject tree)
	{
		if (tree == null)
		{
			return;
		}
		WorldPoint refPoint = tree.getWorldLocation();
		WorldArea northEast = WorldAreas.createArea(
			new WorldPoint(refPoint.getX() + 4, refPoint.getY() + 23, refPoint.getPlane()),
			new WorldPoint(refPoint.getX() + 11, refPoint.getY() + 30, refPoint.getPlane())
		);
		WorldPoint northEastPrePath = new WorldPoint(refPoint.getX() + 8, refPoint.getY() + 13, refPoint.getPlane());

		WorldArea northWest = WorldAreas.createArea(
			new WorldPoint(refPoint.getX() - 10, refPoint.getY() + 23, refPoint.getPlane()),
			new WorldPoint(refPoint.getX() - 3, refPoint.getY() + 30, refPoint.getPlane())
		);
		WorldPoint northWestPrePath = new WorldPoint(refPoint.getX() - 5, refPoint.getY() + 11, refPoint.getPlane());


		WorldArea southEast = WorldAreas.createArea(
			new WorldPoint(refPoint.getX() + 4, refPoint.getY() - 29, refPoint.getPlane()),
			new WorldPoint(refPoint.getX() + 11, refPoint.getY() - 22, refPoint.getPlane())
		);
		WorldPoint southEastPrePath = new WorldPoint(refPoint.getX() + 5, refPoint.getY() - 11, refPoint.getPlane());


		WorldArea southWest = WorldAreas.createArea(
			new WorldPoint(refPoint.getX() - 10, refPoint.getY() - 29, refPoint.getPlane()),
			new WorldPoint(refPoint.getX() - 3, refPoint.getY() - 22, refPoint.getPlane())
		);
		WorldPoint southWestPrePath = new WorldPoint(refPoint.getX() - 5, refPoint.getY() - 12, refPoint.getPlane());

		ArrayList<GameObject> waterFalls = (ArrayList<GameObject>) ObjectUtil.getGameObjects(ToaConstants.ACTIVE_ZEBAK_WATERFALL);
		if (waterFalls.isEmpty())
		{
			return;
		}
		for (GameObject waterFall : waterFalls)
		{
			WorldPoint waterfallPoint = waterFall.getWorldLocation();
			if (waterfallPoint.isInArea(northEast))
			{
				northEastZebakPuzzle = new ZebakWaterfallRoom(ZebakWaterfallRoom.RoomType.NE, true, northEast, waterFall, northEastPrePath);
			}
			if (waterfallPoint.isInArea(northWest))
			{
				northWestZebakPuzzle = new ZebakWaterfallRoom(ZebakWaterfallRoom.RoomType.NW, true, northWest, waterFall, northWestPrePath);
			}
			if (waterfallPoint.isInArea(southEast))
			{
				southEastZebakPuzzle = new ZebakWaterfallRoom(ZebakWaterfallRoom.RoomType.SE, true, southEast, waterFall, southEastPrePath);
			}
			if (waterfallPoint.isInArea(southWest))
			{
				southWestZebakPuzzle = new ZebakWaterfallRoom(ZebakWaterfallRoom.RoomType.SW, true, southWest, waterFall, southWestPrePath);
			}
		}
		currentZebakPuzzle = northEastZebakPuzzle;
	}

	private void manuallyTrimZebakRoom(ArrayList<WorldPoint> allTiles, WorldPoint refTile)
	{
		ArrayList<WorldPoint> newAllTiles = new ArrayList<>(allTiles);
		for (WorldPoint wp : allTiles)
		{
			if ((wp.getX() > (refTile.getX() + 4)) && (wp.getX() < (refTile.getX() + 8)))
			{
				if (wp.getY() < (refTile.getY() - 7))
				{
					newAllTiles.remove(wp);
				}
			}
			if ((wp.getX() > (refTile.getX() + 7)))
			{
				if (wp.getY() < (refTile.getY() - 8))
				{
					newAllTiles.remove(wp);
				}
			}
			if ((wp.getX() > (refTile.getX() + 4)) && (wp.getX() < (refTile.getX() + 7)))
			{
				if (wp.getY() > (refTile.getY() + 7))
				{
					newAllTiles.remove(wp);
				}
			}
			if ((wp.getX() > (refTile.getX() + 6)))
			{
				if (wp.getY() > (refTile.getY() + 9))
				{
					newAllTiles.remove(wp);
				}
			}
		}
		allTiles.clear();
		allTiles.addAll(newAllTiles);
	}
}
