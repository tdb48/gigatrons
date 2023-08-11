package com.example.toagigatron.model;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.Utility.Combat;
import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Prayers;
import com.example.Utility.Reachable;
import com.example.Utility.WorldAreas;
import com.example.Utility.WorldPoints;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.puzzlemodel.BabaPuzzleSpecial;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

public class Baba
{
	public BabaPuzzleSpecial currentSpecial = BabaPuzzleSpecial.NULL;
	public ArrayList<WorldPoint> babaPuzzleRoom = new ArrayList<>();
	public ArrayList<WorldPoint> babaBossRoom = new ArrayList<>();
	public WorldArea babaBossRowOne = null;
	public WorldArea babaBossRowTwo = null;
	public WorldArea babaBossRowThree = null;
	public WorldArea babaBossRowFour = null;
	public WorldArea babaBossRowFive = null;
	public WorldArea babaBossRowGap = null;
	public WorldPoint prePathTile = null;
	public WorldArea babaBossRowSafe = null;
	public GameObject babaPuzzleStatue = null;
	public GameObject babaEntry = null;
	public GameObject targetPillar = null;
	public TileObject targetVent = null;
	public ArrayList<WorldPoint> targetPillarTiles = new ArrayList<>();
	public ArrayList<WorldPoint> poisonTiles = new ArrayList<>();
	public int explosionTick = 0;
	public ArrayList<WorldPoint> explosionTiles = new ArrayList<>();
	public ArrayList<WorldPoint> blockTiles = new ArrayList<>();
	public WorldPoint safeTile = null;
	public ArrayList<WorldPoint> shockwaveTiles = new ArrayList<>();
	public ArrayList<WorldPoint> rockfallTiles = new ArrayList<>();
	public ArrayList<WorldPoint> bananaTiles = new ArrayList<>();
	public ArrayList<WorldPoint> rockFromCeiling = new ArrayList<>();
	public HashSet<Projectile> sarcophagusProjectiles = new HashSet<>();
	public ArrayList<WorldPoint> badTiles = new ArrayList<>();
	public ArrayList<WorldPoint> sarcophagusProjectilesTiles = new ArrayList<>();
	public int shockwaveTick = 0;
	public int ceilingTick = 0;
	public int rockfallTick = 0;
	public NPC babaBoss = null;
	public int bgsHit = 0;
	public boolean shouldTripleBrew = false;
	public int brewSipsNeeded = 0;
	public boolean solvingSpecial = false;
	public int bouldersKilled = 0;
	public int puzzleSpecialTickTimer = 0;
	public boolean touchedPrePathTile = false;
	public List<WorldPoint> attackPath = new ArrayList<>();
	public List<WorldPoint> specialPath = new ArrayList<>();

	public ArrayList<WorldPoint> diagonalRockFallTiles = new ArrayList<>();
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

	public void resetVariables()
	{
		diagonalRockFallTiles = new ArrayList<>();
		attackPath = new ArrayList<>();
		specialPath = new ArrayList<>();
		currentSpecial = BabaPuzzleSpecial.NULL;
		babaPuzzleRoom = new ArrayList<>();
		babaBossRoom = new ArrayList<>();
		babaBossRowOne = null;
		babaBossRowTwo = null;
		babaBossRowThree = null;
		babaBossRowFour = null;
		babaBossRowFive = null;
		babaBossRowGap = null;
		babaBossRowSafe = null;
		babaPuzzleStatue = null;
		babaEntry = null;
		targetPillar = null;
		targetVent = null;
		targetPillarTiles = new ArrayList<>();
		poisonTiles = new ArrayList<>();
		explosionTick = 0;
		ceilingTick = 0;
		explosionTiles = new ArrayList<>();
		blockTiles = new ArrayList<>();
		safeTile = null;
		shockwaveTiles = new ArrayList<>();
		rockfallTiles = new ArrayList<>();
		bananaTiles = new ArrayList<>();
		rockFromCeiling = new ArrayList<>();
		badTiles = new ArrayList<>();
		shockwaveTick = 0;
		rockfallTick = 0;
		babaBoss = null;
		bgsHit = 0;
		shouldTripleBrew = false;
		brewSipsNeeded = 0;
		solvingSpecial = false;
		bouldersKilled = 0;
		sarcophagusProjectiles = new HashSet<>();
		sarcophagusProjectilesTiles = new ArrayList<>();
		prePathTile = null;
		puzzleSpecialTickTimer = 0;
		touchedPrePathTile = false;
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
	{
		if (hitsplatApplied.getActor().equals(babaBoss)
			&& client.getLocalPlayer().getAnimation() == ToaConstants.BGS_SPEC_ANIMATION)
		//TODO ALL EQUIPMENT MANAGEMENT N UNCOMMENT THIS LINE
		//&& toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItemsBgs()))
		{
			toaManager.print("Hit bgs with " + hitsplatApplied.getHitsplat().getAmount());
			bgsHit = hitsplatApplied.getHitsplat().getAmount();
		}
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved projectileSpawned)
	{
		Projectile p = projectileSpawned.getProjectile();
		if (p.getId() == ToaConstants.BABA_SARCOPHAGUS_ATTACK_PROJECTILE_ID)
		{
			sarcophagusProjectiles.add(p);
		}
	}


	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject obj = event.getGameObject();
		int id = obj.getId();
		if (id == ToaConstants.BABA_PUZZLE_POISON)
		{
			poisonTiles.add(obj.getWorldLocation());
		}
		if (id == ToaConstants.BABA_BANANA)
		{
			bananaTiles.add(obj.getWorldLocation());
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		GameObject obj = event.getGameObject();
		int id = obj.getId();
		if (id == ToaConstants.BABA_PUZZLE_POISON)
		{
			poisonTiles.remove(obj.getWorldLocation());
		}
		if (id == ToaConstants.BABA_BANANA)
		{
			bananaTiles.remove(obj.getWorldLocation());
		}
	}

	public ArrayList<WorldPoint> tilesUnderBoss()
	{
		ArrayList<WorldPoint> returnList = new ArrayList<>();
		if (babaBoss == null)
		{
			return returnList;
		}
		return (ArrayList<WorldPoint>) babaBoss.getWorldArea().toWorldPointList();
	}


	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObjectCreated)
	{
		if (graphicsObjectCreated.getGraphicsObject().getId() != ToaConstants.BABA_SHOCKWAVE_CENTER && graphicsObjectCreated.getGraphicsObject().getId() != ToaConstants.BABA_ROCKFALL_SHADOW)
		{
			return;
		}
		if (graphicsObjectCreated.getGraphicsObject().getId() == ToaConstants.BABA_ROCKFALL_SHADOW)
		{
			int rocks = ObjectUtil.getObjects(ToaConstants.BABA_BOSS_ROCKFALL).size();
			WorldPoint refPoint = WorldPoint.fromLocal(client, graphicsObjectCreated.getGraphicsObject().getLocation());
			if (rocks >= 10)
			{
				rockFromCeiling.add(refPoint);
			}
			else
			{
				WorldPoint southWest = refPoint.dx(-1).dy(-1);
				WorldPoint northEast = refPoint.dx(2).dy(2);
				rockFromCeiling.addAll(WorldAreas.createArea(southWest, northEast).toWorldPointList());
			}
			ceilingTick = 6;
		}
		else
		{
			WorldPoint refPoint = WorldPoint.fromLocal(client, graphicsObjectCreated.getGraphicsObject().getLocation());
			WorldPoint southWest = refPoint.dx(-2).dy(-2);
			WorldPoint northEast = refPoint.dx(3).dy(3);
			shockwaveTiles = (ArrayList<WorldPoint>) WorldAreas.createArea(southWest, northEast).toWorldPointList();
			shockwaveTiles.add(new WorldPoint(refPoint.getX() + 3, refPoint.getY(), client.getPlane()));
			shockwaveTiles.add(new WorldPoint(refPoint.getX() - 3, refPoint.getY(), client.getPlane()));
			shockwaveTiles.add(new WorldPoint(refPoint.getX(), refPoint.getY() + 3, client.getPlane()));
			shockwaveTiles.add(new WorldPoint(refPoint.getX(), refPoint.getY() - 3, client.getPlane()));
			shockwaveTick = 5;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (!gameStateChanged.getGameState().equals(GameState.LOADING))
		{
			return;
		}
		resetVariables();
	}

	public boolean shouldTripleBrew()
	{
		int bossHp = toaManager.getBossHp();
		int missingHealth = bossHp < 200 ? 70 : 50;
		if (Combat.getMissingHealth() > missingHealth)
		{
			brewSipsNeeded = 3;
			return true;
		}
		if (brewSipsNeeded == 0)
		{
			return false;
		}
		return shouldTripleBrew;
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC deadBoulder = npcDespawned.getNpc();
		if (deadBoulder.getId() == ToaConstants.WEAK_BOULDER)
		{
			bouldersKilled++;
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npcThatSpawned = npcSpawned.getNpc();
		if (npcThatSpawned.getId() == ToaConstants.WEAK_BOULDER)
		{
			Prayers.disableOverheads();
		}
	}


	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (toaManager.getStage() != Stage.BABA_BOSS && toaManager.getStage() != Stage.BABA_PUZZLE)
		{
			return;
		}
		sarcophagusProjectiles.removeIf(n -> n.getRemainingCycles() <= 0);
		sarcophagusProjectilesTiles.clear();
		for (Projectile p : sarcophagusProjectiles)
		{
			sarcophagusProjectilesTiles.add(WorldPoint.fromLocal(client, p.getTarget()));
		}
		shouldTripleBrew = shouldTripleBrew();

		babaBoss = NPCs.search().nameContains("Ba-Ba").first().orElse(null);
		if (babaEntry != null)
		{
			blockTiles = new ArrayList<>();
			ArrayList<NPC> boulders = (ArrayList<NPC>) NPCs.search().idInList(List.of(ToaConstants.STRONG_BOULDER, ToaConstants.WEAK_BOULDER)).result();
			if (!boulders.isEmpty())
			{
				for (NPC boulder : boulders)
				{
					WorldPoint centerTile = WorldAreas.getCenter(boulder.getWorldArea());
					WorldArea boulderTiles = WorldAreas.createArea(
						centerTile.dx(-2).dy(-1),
						centerTile.dx(2).dy(2));
					blockTiles.addAll(boulderTiles.toWorldPointList());
				}
			}
		}
		boolean hasFallingRock = false;
		for (GraphicsObject obj : client.getGraphicsObjects())
		{
			if (obj.getId() == ToaConstants.BABA_ROCKFALL_SHADOW)
			{
				//could check if the rock has fallen and clear it earlier by checking animation frame probably
				hasFallingRock = true;
			}
		}
		if (!hasFallingRock)
		{
			rockFromCeiling.clear();
		}
		rockfallTiles.clear();
		diagonalRockFallTiles.clear();
		for (TileObject gameObject : ObjectUtil.getObjects(ToaConstants.BABA_BOSS_ROCKFALL)
//			new GameObjectQuery().idEquals(ToaConstants.BABA_BOSS_ROCKFALL).result(client)
		)
		{
			WorldArea objectArea = ObjectUtil.getWorldArea(gameObject);
			WorldPoint refPoint = WorldAreas.getCenter(java.util.Objects.requireNonNull(objectArea));
//			WorldPoint refPoint = gameObject.getWorldArea().getCenter();
			WorldPoint southWest = refPoint.dx(-2).dy(-2);
			WorldPoint northEast = refPoint.dx(3).dy(3);
			for (WorldPoint worldPoint : WorldAreas.createArea(southWest, northEast).toWorldPointList())
			{
				if (Reachable.isWalkable(worldPoint))
				{
					rockfallTiles.add(worldPoint);
				}
				if (WorldPoints.isDiagonalOf(worldPoint, refPoint) && Reachable.isWalkable(worldPoint))
				{
					diagonalRockFallTiles.add(worldPoint);
				}
			}
		}

		if (shockwaveTick > 0)
		{
			shockwaveTick--;
		}

		else if (!shockwaveTiles.isEmpty())
		{
			shockwaveTiles.clear();
		}
		if (ceilingTick > 0)
		{
			ceilingTick--;
		}
		if (rockfallTick > 0)
		{
			rockfallTick--;
		}

		if (explosionTick > 0)
		{
			explosionTick--;
		}
		// Clear list once there is no more explosion
		else if (!explosionTiles.isEmpty())
		{
			explosionTiles = new ArrayList<>();
		}
		if (babaPuzzleStatue == null)
		{

			babaPuzzleStatue = ObjectUtil.getObject(ToaConstants.BABA_PUZZLE_STATUE);
		}
		if (babaEntry == null)
		{
			babaEntry = ObjectUtil.getObject(ToaConstants.BABA_BOSS_ENTRY);
		}
		if (babaBossRowOne == null && babaEntry != null)
		{
			generateRows(babaEntry.getWorldLocation());
		}
		if (babaBossRoom.isEmpty() && babaEntry != null)
		{
			WorldPoint refPoint = babaEntry.getWorldLocation();
			babaBossRoom = (ArrayList<WorldPoint>) WorldAreas.createArea(
				refPoint.dx(4).dy(-7),
				refPoint.dx(26).dy(8)).toWorldPointList();
			babaBossRoom.removeIf(x -> !Reachable.isWalkable(x));
		}
		if (babaPuzzleRoom.isEmpty() && babaPuzzleStatue != null)
		{

			WorldPoint centerTile = WorldAreas.getCenter(java.util.Objects.requireNonNull(ObjectUtil.getWorldArea(babaPuzzleStatue)));
			WorldPoint southWest = new WorldPoint(centerTile.getX() - 11, centerTile.getY() - 8, centerTile.getPlane());
			WorldPoint northEast = new WorldPoint(centerTile.getX() + 12, centerTile.getY() + 10, centerTile.getPlane());
			ArrayList<WorldPoint> worldArea = (ArrayList<WorldPoint>) WorldAreas.createArea(southWest, northEast).toWorldPointList();
			for (WorldPoint wp : worldArea)
			{
				if (Reachable.isWalkable(wp))
				{
					babaPuzzleRoom.add(wp);
				}
			}
		}

		if (targetPillarTiles.isEmpty() && targetPillar != null && babaPuzzleStatue != null)
		{
			WorldPoint centerTile = WorldAreas.getCenter(java.util.Objects.requireNonNull(ObjectUtil.getWorldArea(targetPillar)));
			WorldPoint statueTile = babaPuzzleStatue.getWorldLocation();
			// Pillar is south of target statue

			if (centerTile.getY() < statueTile.getY())
			{
				targetPillarTiles.add(centerTile.dx(-1).dy(2));
				targetPillarTiles.add(centerTile.dx(0).dy(2));
				targetPillarTiles.add(centerTile.dx(1).dy(2));
			}
			else
			{
				targetPillarTiles.add(centerTile.dx(-1).dy(-2));
				targetPillarTiles.add(centerTile.dx(0).dy(-2));
				targetPillarTiles.add(centerTile.dx(1).dy(-2));
			}
		}
		badTiles.clear();
		if (hasProcced())
		{
			rockfallTick = 0;
		}
		if (rockfallTick > 0)
		{
			badTiles = new ArrayList<>(babaBossRoom);
			badTiles.removeIf(rockfallTiles::contains);
			badTiles.addAll(sarcophagusProjectilesTiles);
			badTiles.addAll(rockFromCeiling);
			badTiles.addAll(shockwaveTiles);
		}
		else
		{
			if (!hasProcced())
			{
				badTiles.addAll(rockFromCeiling);
			}
			badTiles.addAll(shockwaveTiles);
			badTiles.addAll(sarcophagusProjectilesTiles);
		}
		NPC monkey = NPCUtil.findNearest(ToaConstants.BABA_BOSS_MONKEY);
		if (shockwaveTick == 0 && ceilingTick == 0 && rockfallTick == 0 && monkey == null)
		{
			badTiles.addAll(tilesUnderBoss());
		}
		NPC deadMonkey = NPCs.search().withId(ToaConstants.BABA_BOSS_MONKEY).filter
			(x -> (x.getHealthRatio() == 0 || x.getAnimation() == ToaConstants.BABA_BOSS_MONKEY_DEATH_ANIMATION)).first().orElse(null);
		if (deadMonkey != null)
		{
			toaManager.print("Found a DEAD monkey");
			System.out.println("Found a DEAD monkey");
			badTiles.add(deadMonkey.getWorldLocation());
		}

		// Add corner tiles of the rocks so that it doesn't get bouldered for 90s
		badTiles.addAll(diagonalRockFallTiles);
		badTiles.addAll(bananaTiles);
		// If theres two rocks out, mark worldpoints 12 or more tiles away as bad
		// For some reason this tile objects query returns 1 object per tile the rock is on
		//So each tile = 0 objects in list l0l
		//Cant be bothered looking into it so added hacky fix of using '10' magic number
		//as '9' = one rock
		int rocks = ObjectUtil.getObjects(ToaConstants.BABA_BOSS_ROCKFALL).size();
		if (rocks >= 10)
		{
			for (WorldPoint wp : babaBossRoom)
			{
				boolean bool = false;
				for (WorldPoint wp2 : rockfallTiles)
				{
					if (badTiles.contains(wp2))
					{
						continue;
					}
					if (wp.distanceTo(wp2) <= 11)
					{
						bool = true;
						break;
					}
				}
				if (!bool)
				{
					if (!badTiles.contains(wp))
					{
						badTiles.add(wp);
					}
				}
			}
		}
		if (puzzleSpecialTickTimer > 0)
		{
			puzzleSpecialTickTimer--;
		}
	}

	private ArrayList<WorldPoint> nearestRockfallTiles()
	{
		ArrayList<WorldPoint> returnList = new ArrayList<>();
		GameObject obj = ObjectUtil.getNearestGameObject(ToaConstants.BABA_BOSS_ROCKFALL);
		if (obj != null)
		{

			WorldPoint refPoint = WorldAreas.getCenter(java.util.Objects.requireNonNull(ObjectUtil.getWorldArea(obj)));
			WorldPoint southWest = refPoint.dx(-2).dy(-2);
			WorldPoint northEast = refPoint.dx(3).dy(3);
			for (WorldPoint worldPoint : WorldAreas.createArea(southWest, northEast).toWorldPointList())
			{
				if (Reachable.isWalkable(worldPoint))
				{
					returnList.add(worldPoint);
				}
			}
		}
		return returnList;
	}

	public ArrayList<WorldPoint> getTrueBabaRoom()
	{
		/**
		 * Will this work??
		 */
		//TODO make sure this works
		//Original line
		//ArrayList<WorldPoint> babaRoomTiles = new ArrayList<>(toamanager.baba.babaPuzzleRoom); //all tiles
		ArrayList<WorldPoint> babaRoomTiles = new ArrayList<>(this.babaPuzzleRoom); //all tiles
		babaRoomTiles.removeIf(x -> !Reachable.isWalkable(x));
		return babaRoomTiles;
	}

	public WorldPoint getSafeTile(WorldArea boulder)
	{
		int distance = Integer.MAX_VALUE;
		WorldPoint returnLoc = null;
		ArrayList<WorldPoint> candidates = new ArrayList<>();
		WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();
		for (WorldPoint wp : toaManager.baba.babaBossRoom)
		{
			if (wp.distanceTo(playerLoc) == 2)
			{
				if (wp.distanceTo(boulder) < distance
					&& !toaManager.baba.blockTiles.contains(wp)
					&& !toaManager.baba.sarcophagusProjectilesTiles.contains(wp)
					&& !toaManager.baba.sarcophagusProjectilesTiles.contains(wp.dx(-1))
					&& !toaManager.baba.bananaTiles.contains(wp)
					&& !toaManager.baba.bananaTiles.contains(wp.dx(-1))
					&& !toaManager.baba.bananaTiles.contains(wp.dx(-2)))
				{
					distance = wp.distanceTo(boulder);
					candidates = new ArrayList<>();
					candidates.add(wp);
				}
				else if (wp.distanceTo(boulder) == distance)
				{
					candidates.add(wp);
				}
			}
		}
		distance = Integer.MAX_VALUE;
		for (WorldPoint wp : candidates)
		{
			if (wp.distanceTo(toaManager.baba.babaBossRowSafe) < distance)
			{
				distance = wp.distanceTo(toaManager.baba.babaBossRowSafe);
				returnLoc = wp;
			}
		}

		return returnLoc;
	}

	public int getPhase()
	{
		if (bouldersKilled < 7)
		{
			return 1;
		}
		if (bouldersKilled < 14)
		{
			return 2;
		}
		return 3;
	}

	public boolean hasProcced()
	{
		int bossHp = toaManager.getBossHp();
		if (bossHp == 0)
		{
			return false;
		}
		int p1ThreshHold = (int) (toaManager.getBossMaxHp() * 0.66);
		int p2ThreshHold = (int) (toaManager.getBossMaxHp() * 0.33);
		int phase = getPhase();
		if (phase == 1)
		{
			return bossHp <= p1ThreshHold;
		}
		else if (phase == 2)
		{
			return bossHp <= p2ThreshHold;
		}
		return false;
	}

	public boolean closeToProccing()
	{
		int bossHp = toaManager.getBossHp();
		if (bossHp == 0)
		{
			return false;
		}
		int maxHit = 60;
		int p1ThreshHold1 = (int) (toaManager.getBossMaxHp() * 0.66) + maxHit;
		int p1ThreshHold2 = (int) (toaManager.getBossMaxHp() * 0.66) - maxHit;

		int p2ThreshHold1 = (int) (toaManager.getBossMaxHp() * 0.33) + maxHit;
		int p2ThreshHold2 = (int) (toaManager.getBossMaxHp() * 0.33) - maxHit;
		int phase = getPhase();

		if (phase == 1)
		{
			return p1ThreshHold1 > bossHp && p1ThreshHold2 < bossHp;
		}

		return phase == 2 && p2ThreshHold1 > bossHp && p2ThreshHold2 < bossHp;
	}

	private void generateRows(WorldPoint refPoint)
	{
		babaBossRowOne = WorldAreas.createArea(
			refPoint.dx(4).dy(5),
			refPoint.dx(26).dy(8));
		babaBossRowTwo = WorldAreas.createArea(
			refPoint.dx(4).dy(2),
			refPoint.dx(26).dy(5));
		babaBossRowThree = WorldAreas.createArea(
			refPoint.dx(4).dy(-1),
			refPoint.dx(26).dy(2));
		babaBossRowFour = WorldAreas.createArea(
			refPoint.dx(4).dy(-4),
			refPoint.dx(26).dy(-1));
		babaBossRowFive = WorldAreas.createArea(
			refPoint.dx(4).dy(-7),
			refPoint.dx(26).dy(-4));
		babaBossRowGap = WorldAreas.createArea(
			refPoint.dx(4).dy(-2),
			refPoint.dx(26).dy(3));
		prePathTile = refPoint.dx(8);
	}

	public boolean isPuzzleActive()
	{
		return client.getVarbitValue(ToaConstants.VARBIT_BABA_PUZZLE) == 1;
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged animationChanged)
	{
		if (animationChanged.getActor().getAnimation() != ToaConstants.VOLATILE_EXPLOSION)
		{
			return;
		}
		WorldPoint explosionCenter = animationChanged.getActor().getWorldLocation();
		WorldPoint southWest = explosionCenter.dx(-1).dy(-1);
		WorldPoint northEast = explosionCenter.dx(2).dy(2);
		WorldArea explosionArea = WorldAreas.createArea(southWest, northEast);
		explosionTiles.addAll(explosionArea.toWorldPointList());
		explosionTick = 4;
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM || chatMessage.getType() == ChatMessageType.CONSOLE || chatMessage.getType() == ChatMessageType.ENGINE)
		{
			String message = chatMessage.getMessage().toLowerCase();
			if (message.contains("ba-ba throws a large boulder at you."))
			{
				rockfallTick = 8;
			}
			if (message.contains(ToaConstants.BREW_MESSAGE.toLowerCase()))
			{
				brewSipsNeeded--;
			}
			if (message.contains(ToaConstants.BABA_KNOCKBACK.toLowerCase()))
			{
				touchedPrePathTile = false;
				shockwaveTiles.clear();
				rockfallTiles.clear();
				rockFromCeiling.clear();
				badTiles.clear();
			}
			if (message.contains("you sense some strange fumes coming from holes in the floor"))
			{
				puzzleSpecialTickTimer = 20;
				currentSpecial = BabaPuzzleSpecial.VENT;

				TileObject potentialVent = ObjectUtil.getNearestTileObject(currentSpecial.objectId);
				if (potentialVent != null)
				{
					targetVent = potentialVent;
				}
			}
			if (message.contains("you sense an issue with the roof supports"))
			{
				puzzleSpecialTickTimer = 20;
				currentSpecial = BabaPuzzleSpecial.PILLAR;

				GameObject potentialPillar = ObjectUtil.getNearestGameObject(currentSpecial.objectId);
				if (potentialPillar != null)
				{
					targetPillar = potentialPillar;
				}
			}
			if (message.contains("you neutralise the fumes coming from the hole")
				|| message.contains("you repair the damaged roof support")
				|| message.contains("the fumes filling the room suddenly ignite")
				|| message.contains("damaged roof supports cause some debris to fall on you")
			)
			{
				targetVent = null;
				targetPillar = null;
				targetPillarTiles = new ArrayList<>();
				currentSpecial = BabaPuzzleSpecial.NULL;
				puzzleSpecialTickTimer = 0;
			}
		}
	}


}