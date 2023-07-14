package com.example.toagigatron.model;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Utility.Movement;
import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.bossmodel.KephriDungRow;
import com.example.toagigatron.model.constants.Direction;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.puzzlemodel.KephriPuzzleRoom;
import com.example.toagigatron.model.puzzlemodel.KephriTilePuzzle;
import com.example.toagigatron.model.bossmodel.KephriRowTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

public class Kephri
{
	public ArrayList<GameObject> memory_tiles = new ArrayList<>();
	public ArrayList<GameObject> memory_completed_tiles = new ArrayList<>();
	public int activeObelisks = 0;
	public List<LocalPoint> obeliskOrder = new ArrayList<>(6);
	public List<NPC> attemptedPillars = new ArrayList<>();
	public WorldArea kephriRoom = null;
	public NPC kephri = null;
	public KephriDungRow previousRow = null;
	public KephriDungRow currentRow = null;
	public ArrayList<KephriDungRow> kephriDungRows = new ArrayList<>();
	public KephriPuzzleRoom firstKephriPuzzle = null;
	public KephriPuzzleRoom secondKephriPuzzle = null;
	public KephriPuzzleRoom finalKephriPuzzle = null;
	public KephriPuzzleRoom currentKephriPuzzle = null;
	public ArrayList<KephriTilePuzzle> kephriTilePuzzles = new ArrayList<>();
	public ArrayList<WorldPoint> solvedTiles = new ArrayList<>();
	public boolean solved;
	public int tileStates = -1;
	public ArrayList<LocalPoint> flips = new ArrayList<>();
	public List<WorldPoint> maths_solution_tiles = new ArrayList<>();
	public List<WorldPoint> maths_solution_tiles_completed = new ArrayList<>();
	public int kephriBombTick = -1;
	public ArrayList<WorldPoint> bombTiles = new ArrayList<>();
	public ArrayList<WorldPoint> kephriMeleeTiles = new ArrayList<>();
	public int dungGraphicTick = 0;
	public boolean isLastPhase = false;
	public int kephriPhase = 1;
	public int previousKephriID = -1;
	public int kephriTick = 0;
	public int ticksSinceChange = -1;
	public int previousKephriAnimation = 0;
	public List<WorldPoint> kephriPath = new ArrayList<>();
	@Inject
	ToaManager toaManager;
	@Inject
	Client client;
	@Inject
	EventBus eventBus;
	@Inject
	GameTickManager gameTickManager;

	///////////////////////MY NEW CODE IM TESTING\\\\\\\\\\\\\\\\\\\\\\\\\\
	/////_____________________________________________________________\\\\\
	public ArrayList<KephriRowTest> kephriRows = new ArrayList<>();

	public ArrayList<WorldPoint> pathToEfficientStartTile = new ArrayList<>();

	public WorldPoint melee = null;
	public WorldPoint stepBack = null;
	public WorldPoint dungedPrepathTile = null;
	;
	public boolean dungEscape = false;

	public WorldPoint playerDungedLocation = null;

	public KephriRowTest optimalRow = null;

	public WorldPoint optimalDungTile = null;

	public ConcurrentHashMap<WorldPoint, GraphicsObject> dungGraphicPoints = new ConcurrentHashMap<>();

	public WorldPoint preDungedTile = null;

	public WorldPoint playerGameTickLoc = null;

	public ArrayList<Integer> straightKnockbackIndexs = new ArrayList<>(Arrays.asList(
		1,2,3,7,8,9,13,14,15,19,20,21
	));
	public ArrayList<Integer> reusableKnockbackIndexs = new ArrayList<>(Arrays.asList(
		0,6,12,18
	));
	public ArrayList<Integer> awkwardKnockbackIndexes = new ArrayList<>(Arrays.asList(
		4,5,10,11,16,17,22,23
	));


	public int clientTickOnWhichPlayerHadDungGraphicSpawnedOnThem = -1;

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
		clientTickOnWhichPlayerHadDungGraphicSpawnedOnThem = -1;
		playerGameTickLoc = null;
		preDungedTile = null;
		dungGraphicPoints = new ConcurrentHashMap<>();
		optimalDungTile = null;
		optimalRow = null;
		playerDungedLocation = null;
		dungEscape = false;
		melee = null;
		stepBack = null;
		dungedPrepathTile = null;
		pathToEfficientStartTile = new ArrayList<>();
		kephriRows = new ArrayList<>();
		memory_tiles = new ArrayList<>();
		memory_completed_tiles = new ArrayList<>();
		activeObelisks = 0;
		obeliskOrder = new ArrayList<>(6);
		attemptedPillars = new ArrayList<>();
		kephriRoom = null;
		kephri = null;
		previousRow = null;
		currentRow = null;
		kephriDungRows = new ArrayList<>();
		firstKephriPuzzle = null;
		secondKephriPuzzle = null;
		finalKephriPuzzle = null;
		currentKephriPuzzle = null;
		solved = false;
		tileStates = -1;
		flips = new ArrayList<>();
		maths_solution_tiles = new ArrayList<>();
		maths_solution_tiles_completed = new ArrayList<>();
		kephriBombTick = -1;
		bombTiles = new ArrayList<>();
		kephriMeleeTiles = new ArrayList<>();
		dungGraphicTick = 0;
		isLastPhase = false;
		previousKephriID = -1;
		kephriPhase = 1;
		kephriTick = 0;
		ticksSinceChange = -1;
		kephriPath = new ArrayList<>();
		previousKephriAnimation = 0;
	}

	public void generateDungRows()
	{
		if (kephri == null)
		{
			return;
		}
		if (kephriMeleeTiles == null || kephriMeleeTiles.isEmpty())
		{
			return;
		}
		for (int i = 0; i < kephriMeleeTiles.size(); i++)
		{
			//int ind = i + 1;
			WorldPoint wp = kephriMeleeTiles.get(i);
			switch (i)
			{
				case 0:
					kephriRows.add(new KephriRowTest(wp, null, Direction.WEST, 5));
					break;
				case 1:
					kephriRows.add(new KephriRowTest(wp, wp.dx(-2), Direction.WEST, 4));
					break;
				case 2:
					kephriRows.add(new KephriRowTest(wp, wp.dx(-2), Direction.WEST, 3));
					break;
				case 3:
					kephriRows.add(new KephriRowTest(wp, wp.dx(-2), Direction.WEST, 2));
					break;
				case 4:
					kephriRows.add(new KephriRowTest(wp, wp.dx(-2), Direction.WEST, 1));
					break;
				case 5:
					kephriRows.add(new KephriRowTest(wp, wp.dx(-2), Direction.WEST, 0));
					break;
				case 6:
					kephriRows.add(new KephriRowTest(wp, null, Direction.SOUTH, 23));
					break;
				case 7:
					kephriRows.add(new KephriRowTest(wp, wp.dy(-2), Direction.NORTH, 6));
					break;
				case 8:
					kephriRows.add(new KephriRowTest(wp, wp.dy(2), Direction.SOUTH, 22));
					break;
				case 9:
					kephriRows.add(new KephriRowTest(wp, wp.dy(-2), Direction.NORTH, 7));
					break;
				case 10:
					kephriRows.add(new KephriRowTest(wp, wp.dy(2), Direction.SOUTH, 21));
					break;
				case 11:
					kephriRows.add(new KephriRowTest(wp, wp.dy(-2), Direction.NORTH, 8));
					break;
				case 12:
					kephriRows.add(new KephriRowTest(wp, wp.dy(2), Direction.SOUTH, 20));
					break;
				case 13:
					kephriRows.add(new KephriRowTest(wp, wp.dy(-2), Direction.NORTH, 9));
					break;
				case 14:
					kephriRows.add(new KephriRowTest(wp, wp.dy(2), Direction.SOUTH, 19));
					break;
				case 15:
					kephriRows.add(new KephriRowTest(wp, wp.dy(-2), Direction.NORTH, 10));
					break;
				case 16:
					kephriRows.add(new KephriRowTest(wp, wp.dy(2), Direction.EAST, 18));
					break;
				case 17:
					kephriRows.add(new KephriRowTest(wp, null, Direction.EAST, 11));
					break;
				case 18:
					kephriRows.add(new KephriRowTest(wp, wp.dx(2), Direction.EAST, 12));
					break;
				case 19:
					kephriRows.add(new KephriRowTest(wp, wp.dx(2), Direction.EAST, 13));
					break;
				case 20:
					kephriRows.add(new KephriRowTest(wp, wp.dx(2), Direction.EAST, 14));
					break;
				case 21:
					kephriRows.add(new KephriRowTest(wp, wp.dx(2), Direction.EAST, 15));
					break;
				case 22:
					kephriRows.add(new KephriRowTest(wp, wp.dx(2), Direction.EAST, 16));
					break;
				case 23:
					kephriRows.add(new KephriRowTest(wp, null, Direction.EAST, 17));
					break;
			}
		}
		kephriRows.sort(Comparator.comparingInt(r -> r.index));
		//this.tasks.sort(Comparator.comparing((t) -> this.descriptorHashMap.get(t).priority()).reversed());
//		System.out.println("Original order-----------------");
//		for(KephriRowTest r : kephriRows){
//			System.out.println(r.index);
//		}
//		System.out.println("");
//		System.out.println("Sorted order------------");
//		kephriRows.sort(Comparator.comparingInt(r -> r.index));
//		for(KephriRowTest r : kephriRows){
//			System.out.println(r.index);
//		}
//		System.out.println("");
//		System.out.println("REVERSE Sorted order------------");
//		Collections.reverse(kephriRows);
//		for(KephriRowTest r : kephriRows){
//			System.out.println(r.index);
//		}
//		for (WorldPoint wp : kephriMeleeTiles)
//		{
//			WorldPoint center = WorldAreas.getCenter(kephri.getWorldArea());
//			boolean west = (center.getX() - wp.getX()) >= 3;
//			boolean east = (wp.getX() - center.getX()) >= 3;
//			boolean north = (wp.getY() - center.getY()) >= 3;
//			boolean south = (center.getY() - wp.getY()) >= 3;
//			if (west)
//			{
//				kephriRows.add(new KephriRowTest(wp, wp.dx(-2), Direction.WEST, index));
//			}
//			else if (east)
//			{
//				kephriRows.add(new KephriRowTest(wp, wp.dx(2), Direction.EAST, index));
//			}
//			else if (north)
//			{
//				kephriRows.add(new KephriRowTest(wp, wp.dy(2), Direction.NORTH, index));
//			}
//			else if (south)
//			{
//				kephriRows.add(new KephriRowTest(wp, wp.dy(-2), Direction.SOUTH, index));
//			}
//			index++;
//		}
		WorldPoint refPoint = WorldAreas.getCenter(kephri.getWorldArea());
		kephriDungRows.add(createDungRow(refPoint, -3, 2, -5, 2, -8, 7, -7, 5, Direction.WEST, 0));
		kephriDungRows.add(createDungRow(refPoint, -3, 1, -5, 1, -8, 1, -6, 1, Direction.WEST, 1));
		kephriDungRows.add(createDungRow(refPoint, -3, 0, -5, 0, -8, 0, -6, 0, Direction.WEST, 2));
		kephriDungRows.add(createDungRow(refPoint, -3, -1, -5, -1, -8, -1, -6, -1, Direction.WEST, 3));
		//kephriDungRows.add(createDungRow(refPoint, -3, -2, -5, -2, -8, -7, -6, -7, Direction.WEST, 4));

		//kephriDungRows.add(createDungRow(refPoint, -2, -3, -2, -5, -7, -8, -5, -7, Direction.SOUTH, 5));
		kephriDungRows.add(createDungRow(refPoint, -1, -3, -1, -5, -1, -8, -1, -6, Direction.SOUTH, 6));
		kephriDungRows.add(createDungRow(refPoint, 0, -3, 0, -5, 0, -8, 0, -6, Direction.SOUTH, 7));
		kephriDungRows.add(createDungRow(refPoint, 1, -3, 1, -5, 1, -8, 1, -6, Direction.SOUTH, 8));
		//kephriDungRows.add(createDungRow(refPoint, 2, -3, 2, -5, 7, -8, 7, -6, Direction.SOUTH, 9));

		//kephriDungRows.add(createDungRow(refPoint, 3, -2, 5, -2, 8, -7, 7, -5, Direction.EAST, 10));
		kephriDungRows.add(createDungRow(refPoint, 3, -1, 5, -1, 8, -1, 6, -1, Direction.EAST, 11));
		kephriDungRows.add(createDungRow(refPoint, 3, 0, 5, 0, 8, 0, 6, 0, Direction.EAST, 12));
		kephriDungRows.add(createDungRow(refPoint, 3, 1, 5, 1, 8, 1, 6, 1, Direction.EAST, 13));
		//kephriDungRows.add(createDungRow(refPoint, 3, 2, 5, 2, 8, 7, 6, 7, Direction.EAST, 14));

		//kephriDungRows.add(createDungRow(refPoint, 2, 3, 2, 5, 7, 8, 5, 7, Direction.NORTH, 15));
		kephriDungRows.add(createDungRow(refPoint, 1, 3, 1, 5, 1, 8, 1, 6, Direction.NORTH, 16));
		kephriDungRows.add(createDungRow(refPoint, 0, 3, 0, 5, 0, 8, 0, 6, Direction.NORTH, 17));
		kephriDungRows.add(createDungRow(refPoint, -1, 3, -1, 5, -1, 8, -1, 6, Direction.NORTH, 18));
		kephriDungRows.add(createDungRow(refPoint, -2, 3, -2, 5, -7, 8, -5, 7, Direction.NORTH, 19));
		toaManager.print("Generated dungrows");
	}


//	public void generateDungRows()
//	{
//		if (kephri == null)
//		{
//			return;
//		}
//		WorldPoint refPoint = WorldAreas.getCenter(kephri.getWorldArea());
//		kephriDungRows.add(createDungRow(refPoint, -3, 2, -5, 2, -8, 7, -7, 5, Direction.WEST, 0));
//		kephriDungRows.add(createDungRow(refPoint, -3, 1, -5, 1, -8, 1, -6, 1, Direction.WEST, 1));
//		kephriDungRows.add(createDungRow(refPoint, -3, 0, -5, 0, -8, 0, -6, 0, Direction.WEST, 2));
//		kephriDungRows.add(createDungRow(refPoint, -3, -1, -5, -1, -8, -1, -6, -1, Direction.WEST, 3));
//		//kephriDungRows.add(createDungRow(refPoint, -3, -2, -5, -2, -8, -7, -6, -7, Direction.WEST, 4));
//
//		//kephriDungRows.add(createDungRow(refPoint, -2, -3, -2, -5, -7, -8, -5, -7, Direction.SOUTH, 5));
//		kephriDungRows.add(createDungRow(refPoint, -1, -3, -1, -5, -1, -8, -1, -6, Direction.SOUTH, 6));
//		kephriDungRows.add(createDungRow(refPoint, 0, -3, 0, -5, 0, -8, 0, -6, Direction.SOUTH, 7));
//		kephriDungRows.add(createDungRow(refPoint, 1, -3, 1, -5, 1, -8, 1, -6, Direction.SOUTH, 8));
//		//kephriDungRows.add(createDungRow(refPoint, 2, -3, 2, -5, 7, -8, 7, -6, Direction.SOUTH, 9));
//
//		//kephriDungRows.add(createDungRow(refPoint, 3, -2, 5, -2, 8, -7, 7, -5, Direction.EAST, 10));
//		kephriDungRows.add(createDungRow(refPoint, 3, -1, 5, -1, 8, -1, 6, -1, Direction.EAST, 11));
//		kephriDungRows.add(createDungRow(refPoint, 3, 0, 5, 0, 8, 0, 6, 0, Direction.EAST, 12));
//		kephriDungRows.add(createDungRow(refPoint, 3, 1, 5, 1, 8, 1, 6, 1, Direction.EAST, 13));
//		//kephriDungRows.add(createDungRow(refPoint, 3, 2, 5, 2, 8, 7, 6, 7, Direction.EAST, 14));
//
//		//kephriDungRows.add(createDungRow(refPoint, 2, 3, 2, 5, 7, 8, 5, 7, Direction.NORTH, 15));
//		kephriDungRows.add(createDungRow(refPoint, 1, 3, 1, 5, 1, 8, 1, 6, Direction.NORTH, 16));
//		kephriDungRows.add(createDungRow(refPoint, 0, 3, 0, 5, 0, 8, 0, 6, Direction.NORTH, 17));
//		kephriDungRows.add(createDungRow(refPoint, -1, 3, -1, 5, -1, 8, -1, 6, Direction.NORTH, 18));
//		kephriDungRows.add(createDungRow(refPoint, -2, 3, -2, 5, -7, 8, -5, 7, Direction.NORTH, 19));
//		toaManager.print("Generated " + kephriDungRows.size() + " dungs");
//	}

	public KephriDungRow createDungRow(WorldPoint refPoint, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, Direction direction, int index)
	{
		WorldPoint start = refPoint.dx(x1).dy(y1);
		WorldPoint middle = refPoint.dx(x2).dy(y2);
		WorldPoint end = refPoint.dx(x3).dy(y3);
		WorldPoint prepath = refPoint.dx(x4).dy(y4);
		return new KephriDungRow(start, middle, prepath, end, direction, index);
	}

	@Subscribe
	public void onNpcChanged(NpcChanged event)
	{
		int id = event.getNpc().getId();
		if (id == 11720 || id == 11719 || id == 11721 && event.getNpc().getName().equalsIgnoreCase("kephri"))
		{
			previousKephriID = id;
			kephriPhase++;
			if(kephriPhase == 5){
				optimalDungTile = getOptimalDungTile();
				optimalRow = getOptimalMeleeRow();
				if(optimalRow != null){
					melee = optimalRow.meleeTile;
					stepBack = optimalRow.stepBack;
				}
			}
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		if (npcSpawned.getNpc().getName().toLowerCase().contains("arcane scarab"))
		{
			isLastPhase = true;
		}

	}

	@Subscribe
	public void onGraphicChanged(GraphicChanged event)
	{
		Actor actor = event.getActor();
		if (actor instanceof Player && event.getActor() != null && event.getActor().getName() != null)
		{
			if (actor.getGraphic() == 2146)
			{
				//kephri fly
				clientTickOnWhichPlayerHadDungGraphicSpawnedOnThem = client.getTickCount();
				dungGraphicTick = 6;
			}
			if (actor.getGraphic() == 245)
			{
				//pood
				dungGraphicTick = 0;
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (toaManager.getStage() != Stage.KEPHRI_BOSS && toaManager.getStage() != Stage.KEPHRI_PUZZLE)
		{
			return;
		}
		playerGameTickLoc = client.getLocalPlayer().getWorldLocation();

		for (Map.Entry<WorldPoint, GraphicsObject> entry : dungGraphicPoints.entrySet())
		{
			if (entry.getValue().finished())
			{
				dungGraphicPoints.remove(entry.getKey());
			}
		}
		if (dungGraphicTick > 0)
		{
			dungGraphicTick--;
		}
		if (kephriBombTick > 0)
		{
			kephriBombTick--;
		}
		if (kephriBombTick <= 0)
		{
			bombTiles = new ArrayList<>();
		}
		if (kephriTick > 0)
		{
			kephriTick--;
		}
		NPC kephri = NPCUtil.findNearest("Kephri");
		if (kephri == null)
		{
			return;
		}
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//TESTING NEW KEPHRI CODE\\

		//Probably change this so its not running every tick? Should be able to only run it as required, so after a knockback
		if(optimalDungTile == null){
			System.out.println("Getting optimal dung tile for the first time.");
			optimalDungTile = getOptimalDungTile();
		}
		if(optimalRow == null){
			optimalRow = getOptimalMeleeRow();
			if (optimalRow == null)
			{
				toaManager.print("Optimal row is null ive done something wrong.");
			}
			else
			{
				toaManager.kephri.melee = optimalRow.meleeTile;
				toaManager.kephri.stepBack = optimalRow.stepBack;
			}
		}


		if (toaManager.kephri.melee != null)
		{
			HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.kephri.bombTiles);
			toaManager.kephri.pathToEfficientStartTile = EthanApiPlugin.pathToGoal(toaManager.kephri.melee, dangerTiles);
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if (toaManager.getStage().equals(Stage.KEPHRI_PUZZLE) && firstKephriPuzzle == null)
		{
			GameObject barrier = ObjectUtil.getNearestGameObject(ToaConstants.BARRIER);
			if (barrier != null)
			{
				toaManager.kephri.generateKephriPuzzleRooms(barrier);
			}
		}
		this.kephri = kephri;
		if (previousKephriID == -1)
		{
			previousKephriID = kephri.getId();
		}
		//toaManager.print("Kephri phase -> " + kephriPhase + "  previous id -> " + previousKephriID);
		WorldPoint pp = client.getLocalPlayer().getWorldLocation();
//		if (currentRow != null && pp.equals(currentRow.endPoint))
//		{
//			toaManager.print("MOVING TO NEXT ROW@!@@@");
//		}
		if (kephriDungRows.isEmpty())
		{
			generateDungRows();
			if (!kephriDungRows.isEmpty())
			{
				currentRow = kephriDungRows.get(0);
				previousRow = currentRow;
			}
		}
		if (kephriRoom == null)
		{
			WorldPoint centerTile = WorldAreas.getCenter(kephri.getWorldArea());
			WorldPoint southWest = new WorldPoint(centerTile.getX() - 8, centerTile.getY() - 8, centerTile.getPlane());
			WorldPoint northEast = new WorldPoint(centerTile.getX() + 9, centerTile.getY() + 9, centerTile.getPlane());
			kephriRoom = WorldAreas.createArea(southWest, northEast);
		}
		if (kephriMeleeTiles.isEmpty())
		{
			WorldArea kephriArea = kephri.getWorldArea();
			WorldPoint centerTile = WorldAreas.getCenter(kephri.getWorldArea());
			WorldPoint southWest = centerTile.dx(-3).dy(-3);
			WorldPoint northEast = centerTile.dx(+4).dy(+4);
			ArrayList<WorldPoint> bigKephriArea = (ArrayList<WorldPoint>) WorldAreas.createArea(southWest, northEast).toWorldPointList();
			bigKephriArea.removeIf(kephriArea::contains);
			//				if (Math.abs(centerTile.getX() - wp.getX()) == 3 && Math.abs(centerTile.getY() - wp.getY()) == 3)
			//				{
			//					continue;
			//				}
			kephriMeleeTiles.addAll(bigKephriArea);
		}
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated graphicsObject)
	{
		GraphicsObject object = graphicsObject.getGraphicsObject();
		if (object.getId() == ToaConstants.KEPHRI_BALL || object.getId() == ToaConstants.KEPHRI_KAMIKAZE)
		{
			kephriBombTick = 5;
			bombTiles.add(WorldPoint.fromLocal(client, object.getLocation()));
		}
		if (object.getId() != ToaConstants.KEPHRI_DUNG_G_OBJECT)
		{
			return;
		}

		dungGraphicPoints.put(WorldPoint.fromLocal(client, object.getLocation()), object);

		WorldPoint refPoint = WorldPoint.fromLocal(client, object.getLocation());
		WorldPoint curStart = currentRow.startPoint;
		WorldPoint curMid = currentRow.middlePoint;
		WorldPoint curPrePath = currentRow.prePathPoint;
		WorldPoint curEnd = currentRow.endPoint;

		//refPoint.equals(curStart) || refPoint.equals(curMid) || refPoint.equals(curPrePath) ||
		if (refPoint.equals(curStart) || refPoint.equals(curMid) || refPoint.equals(curPrePath) || refPoint.equals(curEnd))
		{
			int index = currentRow.index;
			previousRow = currentRow;
			if (index + 1 < kephriDungRows.size())
			{
				currentRow = kephriDungRows.get(index + 1);
			}
		}
		else
		{
			//If we are here it means we got dunged and none of it landed on our end tile so we surmise we are on the wrong row
			//Need to detect which row is the next row and shift current row to that
			KephriDungRow potentialRow = findNewRow(refPoint);
			if (potentialRow != null)
			{
				currentRow = potentialRow;
				previousRow = kephriDungRows.get(currentRow.index - 1);
				//toaManager.print("We found new row!! Great success!!");
			}
			else
			{
				//toaManager.print("We did not find a new row");
			}

		}

	}

	private KephriDungRow findNewRow(WorldPoint dungPoint)
	{
		int startIndex = currentRow.index;
		for (int i = startIndex; i < kephriDungRows.size(); i++)
		{
			KephriDungRow row = kephriDungRows.get(i);
			WorldPoint start = row.startPoint;
			WorldPoint mid = row.middlePoint;
			WorldPoint prepath = row.prePathPoint;
			WorldPoint end = row.endPoint;
			//the dung is on this row, so this cannot be our current row we must set our current row to this row + 1
			//for sanity lets ensure the next row also contains no dung to be safe.
			if (dungPoint.equals(start) || dungPoint.equals(mid) || dungPoint.equals(prepath) || dungPoint.equals(end))
			{
				//Potential null pointer here if we somehow use all rows up until the end
				KephriDungRow potentialRow = kephriDungRows.get(i + 1);
				start = potentialRow.startPoint;
				mid = potentialRow.middlePoint;
				prepath = potentialRow.prePathPoint;
				end = potentialRow.endPoint;
				//There is no dung on this row this can be our new row.
				if (!dungPoint.equals(start) && !dungPoint.equals(mid) && !dungPoint.equals(prepath) && !dungPoint.equals(end))
				{
					//	toaManager.print("Found new row!");
					return potentialRow;
				}
				else
				{
					//toaManager.print("There is dung on the row we want to set to current, moving to next check.");
				}
			}
		}
		//toaManager.print("No good row found, returning null");
		return null;
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM || chatMessage.getType() == ChatMessageType.CONSOLE || chatMessage.getType() == ChatMessageType.ENGINE)
		{
			if (chatMessage.getMessage().toLowerCase().contains("puzzle 2") && firstKephriPuzzle != null)
			{
				toaManager.print("COMPLETED FIRST PUZZLE");
				firstKephriPuzzle.setSolved(true);
				currentKephriPuzzle = secondKephriPuzzle;
			}
			if (chatMessage.getMessage().toLowerCase().contains("puzzle 3") && secondKephriPuzzle != null)
			{
				toaManager.print("COMPLETED SECOND PUZZLE");
				secondKephriPuzzle.setSolved(true);
				currentKephriPuzzle = finalKephriPuzzle;
			}
			if (chatMessage.getMessage().toLowerCase().contains("puzzle 5") && finalKephriPuzzle != null)
			{
				toaManager.print("COMPLETED FINAL PUZZLE");
				finalKephriPuzzle.setSolved(true);
			}
			if (chatMessage.getMessage().toLowerCase().contains("throws you back"))
			{
				isLastPhase = false;
			}
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		GameObject obj = event.getGameObject();
		if (obj.getId() == ToaConstants.KEPHRI_MEMORY_LIGHT_ACTIVATED)
		{
			memory_tiles.add(obj);
		}
	}

	public void generateKephriPuzzleRooms(GameObject barrier)
	{
		if (barrier == null)
		{
			return;
		}
		GameObject ancientButton = ObjectUtil.getObject(45338);
		GameObject ancientTablet = ObjectUtil.getObject(45339);
		GameObject pillar = ObjectUtil.getObject(43876);
		WorldPoint referencePoint = barrier.getWorldLocation();
		if (ancientButton == null || ancientTablet == null || pillar == null)
		{
			return;
		}

		WorldArea firstPuzzleArea = WorldAreas.createArea(
			new WorldPoint(referencePoint.getX() + 7, referencePoint.getY() - 3, referencePoint.getPlane()),
			new WorldPoint(referencePoint.getX() + 14, referencePoint.getY() + 3, referencePoint.getPlane()));

		WorldArea secondPuzzleArea = WorldAreas.createArea(
			new WorldPoint(referencePoint.getX() + 24, referencePoint.getY() + 9, referencePoint.getPlane()),
			new WorldPoint(referencePoint.getX() + 31, referencePoint.getY() + 16, referencePoint.getPlane()));

		WorldArea finalPuzzleArea = WorldAreas.createArea(
			new WorldPoint(referencePoint.getX() + 35, referencePoint.getY() - 4, referencePoint.getPlane()),
			new WorldPoint(referencePoint.getX() + 43, referencePoint.getY() + 16, referencePoint.getPlane()));

		ArrayList<WorldArea> puzzleRooms = new ArrayList<>();
		puzzleRooms.add(firstPuzzleArea);
		puzzleRooms.add(secondPuzzleArea);
		finalKephriPuzzle = new KephriPuzzleRoom(KephriPuzzleRoom.RoomType.FINAL, false, finalPuzzleArea, 2);
		//First room is pillars

		for (int i = 0; i < puzzleRooms.size(); i++)
		{
			WorldArea room = puzzleRooms.get(i);
			if (room.contains(pillar.getWorldLocation()))
			{
				if (i == 0)
				{
					firstKephriPuzzle = new KephriPuzzleRoom(KephriPuzzleRoom.RoomType.PILLAR, false, room, 0);
				}
				else
				{
					secondKephriPuzzle = new KephriPuzzleRoom(KephriPuzzleRoom.RoomType.PILLAR, false, room, 1);
				}
			}
			//First room is memory
			else if (Objects.requireNonNull(ObjectUtil.getWorldArea(ancientButton)).distanceTo(WorldAreas.getCenter(room)) < 6)
			{
				if (i == 0)
				{
					firstKephriPuzzle = new KephriPuzzleRoom(KephriPuzzleRoom.RoomType.MEMORY, false, room, 0);
				}
				else
				{
					secondKephriPuzzle = new KephriPuzzleRoom(KephriPuzzleRoom.RoomType.MEMORY, false, room, 1);
				}
			}
			//First room is math
			else if (Objects.requireNonNull(ObjectUtil.getWorldArea(ancientTablet)).distanceTo(WorldAreas.getCenter(room)) < 6)
			{
				if (i == 0)
				{
					firstKephriPuzzle = new KephriPuzzleRoom(KephriPuzzleRoom.RoomType.MATH, false, room, 0);
				}
				else
				{
					secondKephriPuzzle = new KephriPuzzleRoom(KephriPuzzleRoom.RoomType.MATH, false, room, 1);
				}
			}
			//first room is light puzzle
			else
			{
				if (i == 0)
				{
					firstKephriPuzzle = new KephriPuzzleRoom(KephriPuzzleRoom.RoomType.LIGHT, false, room, 0);
				}
				else
				{
					secondKephriPuzzle = new KephriPuzzleRoom(KephriPuzzleRoom.RoomType.LIGHT, false, room, 1);
				}
			}
		}
		if (firstKephriPuzzle != null)
		{
			currentKephriPuzzle = firstKephriPuzzle;
		}
	}
	//a -1, P 9572, G 2141 while i have dung anim on me n im waiting to get nuked
	//Anim changes to 9578 on kephri dung kick thing

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		Actor a = event.getActor();
		NPC n = null;
		if (a instanceof NPC)
		{
			n = (NPC) event.getActor();
		}
		if (n != null)
		{
			if (n.getName() != null && n.getName().toLowerCase().contains("kep"))
			{
				int anim = n.getAnimation();
				//9577 = standard attack bomb throw
				//9578 = knockback
				//STOP IT THINKING THAT EGG THROW IS KNOCKBACK
				if (anim == 9577 || anim == 9578)
				{
					//boolean eggThrow = Projectiles.getProjectile(2165) != null || Projectiles.getProjectile(2164) != null;
					boolean eggThrow = !maxAgileScarabs() && anim == 9578 && (client.getTickCount() - clientTickOnWhichPlayerHadDungGraphicSpawnedOnThem >= 7);

					if (anim == 9578 && !eggThrow)
					{
						preDungedTile = playerGameTickLoc;
						if (optimalRow != null)
						{
							playerDungedLocation = client.getLocalPlayer().getWorldLocation();
							dungedPrepathTile = getJustGotDungedTile(playerDungedLocation, getTileIndex(preDungedTile));
							dungEscape = true;
							optimalDungTile = getOptimalDungTile();
							optimalRow = getOptimalMeleeRow();
							if(optimalRow != null){
								melee = optimalRow.meleeTile;
								stepBack = optimalRow.stepBack;
							} else {
								toaManager.print("Optimal row is null in animation changed.");
							}

						}
					}
					ticksSinceChange = 0;
					kephriTick = 6;
				}
				//kephri waking up
				if (anim == 9581)
				{
					kephriTick = 7;
				}
				//Demi phase
				if (anim == 9579)
				{
					ticksSinceChange = -1;
				}
				//This happens when kephri dungs us and enough time has passed to allow us to move again
				if (anim == -1 && previousKephriAnimation == 9578)
				{
//					playerDungedLocation = client.getLocalPlayer().getWorldLocation();
//					dungedPrepathTile = getJustGotDungedTile(playerDungedLocation, optimalRow.direction);
//					dungEscape = true;
					ticksSinceChange = 0;
					kephriTick = 5;
				}
				previousKephriAnimation = anim;
			}
		}
	}

	//Index = the index in KephriRows containing the melee tile from which i just got knocked back
	public WorldPoint getJustGotDungedTile(WorldPoint playerLoc, int index)
	{
		WorldPoint wp;
		if (index >= 0 && index < 5)
		{
			if(index == 0 || index == 4){
				wp = playerLoc.dy(-2).dx(1);
			} else {
				wp = playerLoc.dy(-1).dx(2);
			}
			if (wp.isInArea(kephri.getWorldArea()))
			{
				wp = playerLoc.dy(-2);
			}
		}
		//Diagonal dung, we need to path out to the east south (2E, 1S)
		else if (index == 5)
		{
			wp = playerLoc.dy(-1).dx(2);
		}
		else if (index >= 6 && index < 10)
		{
			if(index == 6){
				wp = playerLoc.dy(1).dx(2);
			} else {
				wp = playerLoc.dy(2).dx(1);
			}
			if (wp.isInArea(kephri.getWorldArea()))
			{
				wp = playerLoc.dx(2);
			}
		}
		else if (index == 10 || index == 11)
		{
			wp = playerLoc.dy(2).dx(1);
		}
		else if (index >= 12 && index < 16)
		{
			if(index == 12){
				wp = playerLoc.dy(2).dx(-1);
			} else {
				wp = playerLoc.dy(1).dx(-2);
			}

			if (wp.isInArea(kephri.getWorldArea()))
			{
				wp = playerLoc.dy(2);
			}
		}
		else if (index == 16 || index == 17)
		{
			wp = playerLoc.dy(1).dx(-2);
		}
		else if (index > 17 && index <= 21)
		{
			if(index == 18){
				wp = playerLoc.dy(-1).dx(-2);
			} else {
				wp = playerLoc.dy(-2).dx(-1);
			}
			if (wp.isInArea(kephri.getWorldArea()))
			{
				wp = playerLoc.dx(-2);
			}
		}
		else
		{
			if(index == -1) {
				toaManager.print("Knocked back from an unexpected corner tile");
			}
			if(toaManager.isDiagonalOf(preDungedTile, WorldAreas.getCenter(kephri.getWorldArea())))
			{
				int x = preDungedTile.getX();
				int y = preDungedTile.getY();
				int kephriX = WorldAreas.getCenter(kephri.getWorldArea()).getX();
				int kephriY = WorldAreas.getCenter(kephri.getWorldArea()).getY();
				//South west corner
				if(x < kephriX && y < kephriY)
				{
					wp = playerLoc.dx(2);

				}
				//South east corner
				else if(x > kephriX && y < kephriY)
				{
					wp = playerLoc.dy(2);

				}
				//North east corner
				else if(x > kephriX && y > kephriY){
					wp = playerLoc.dx(-2);
				}
			}
			wp = client.getLocalPlayer().getWorldLocation();
		}
		if (!wp.isInArea(kephriRoom))
		{
			toaManager.print("Something has gone badly wrong, index -> " + index);
		}
		if (!Reachable.isWalkable(wp))
		{
			toaManager.print("We are returning an unreachable tile for dung escape, index -> " + index);
		}

//		if (direction.equals(Direction.WEST))
//		{
//			wp = playerLoc.dy(-2).dx(1);
//		}
//		else if (direction.equals(Direction.SOUTH))
//		{
//			wp = playerLoc.dy(1).dx(2);
//		}
//		else if (direction.equals(Direction.EAST))
//		{
//			wp = playerLoc.dy(2).dx(-1);
//		}
//		else
//		{
//			wp = playerLoc.dy(-1).dx(-2);
//		}
		return wp;
	}


	//Get the optimal tile to be dunged on
	public WorldPoint getOptimalDungTile()
	{
		//Index in the list of rows of the tile i got dunged from
		int index = getTileIndex(preDungedTile);
		System.out.println("Optimal dung tile index -> " + index);
		WorldPoint newDungTile = null;
		if(straightKnockbackIndexs.contains(index))
		{
			newDungTile = kephriRows.get(index+1).meleeTile;
			if(TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(newDungTile).empty()
				&& Reachable.isWalkable(newDungTile))
			{
				return newDungTile;
			} else {
				toaManager.print("Something gone wrong with optimal dung tile method - straightknockback");
			}
		}
		else if(awkwardKnockbackIndexes.contains(index))
		{
			if(index == 4 || index == 5)
			{
				newDungTile = kephriRows.get(6).meleeTile;
			}
			else if(index == 10 || index == 11)
			{
				newDungTile = kephriRows.get(12).meleeTile;
			}
			else if(index == 16 || index == 17)
			{
				newDungTile = kephriRows.get(18).meleeTile;
			}
			else if(index == 22 || index == 23)
			{
				newDungTile = client.getLocalPlayer().getWorldLocation(); //this means death, this should never happen
			}
			if(TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(newDungTile).empty()
				&& Reachable.isWalkable(newDungTile))
			{
				return newDungTile;
			} else {
				toaManager.print("Something gone wrong with optimal dung tile method - straightknockback");
			}
		}
		else if(reusableKnockbackIndexs.contains(index))
		{
			System.out.println("Reusable knockbacks contains index");
			for (KephriRowTest row : toaManager.kephri.kephriRows)
			{
				if (row.index < index)
				{
					toaManager.print("`````````HERE````````````");
					continue;
				}
				newDungTile = row.meleeTile;
				//if dung on start or stepback tile skip it
				if (!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(newDungTile).empty())
				{
					continue;
				}
				if(index == 0)
				{
					if (!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(newDungTile.dy(2).dx(-2)).empty())
					{
						continue;
					}
					if(kephriPhase == 5){
						if (!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(newDungTile.dy(3).dx(-3)).empty())
						{
							continue;
						}
					}
				}
				else if(index == 6)
				{
					if (!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(newDungTile.dy(-2).dx(-2)).empty())
					{
						continue;
					}
					if(kephriPhase == 5){
						if (!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(newDungTile.dy(-3).dx(-3)).empty())
						{
							continue;
						}
					}
				}
				else if(index == 12)
				{
					if (!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(newDungTile.dy(-2).dx(2)).empty())
					{
						continue;
					}
					if(kephriPhase == 5){
						if (!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(newDungTile.dy(-3).dx(3)).empty())
						{
							continue;
						}
					}
				}
				else
				{
					if (!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(newDungTile.dy(2).dx(2)).empty())
					{
						continue;
					}
					if(kephriPhase == 5){
						if (!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(newDungTile.dy(3).dx(3)).empty())
						{
							continue;
						}
					}
				}
				if (Reachable.isWalkable(newDungTile))
				{
					return newDungTile;
				}
			}
		}

		toaManager.print("Returning null in optimal dung tile this is bad");
		return null;
	}

	//Backup original working melee row method

	/**
	 * public KephriRowTest getOptimalMeleeRow()
	 * {
	 * for (KephriRowTest row : toaManager.kephri.kephriRows)
	 * {
	 * WorldPoint melee = row.meleeTile;
	 * WorldPoint stepBack = row.stepBack;
	 * //if dung on start or stepback tile skip it
	 * if (!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(melee).empty() ||
	 * !TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(stepBack).empty())
	 * {
	 * continue;
	 * }
	 * if (Reachable.isWalkable(melee) && Reachable.isWalkable(stepBack))
	 * {
	 * return row;
	 * }
	 * }
	 * return null;
	 * }
	 *
	 * @return
	 */

	public KephriRowTest getOptimalMeleeRow()
	{
		if (optimalDungTile == null)
		{
			toaManager.print("Optimal dung tile is null, returning false in get optimal melee row method");
			//System.out.println("Optimal dung tile is null, returning false in get optimal melee row method");
			return null;
		}
		int dungTileIndex = getTileIndex(optimalDungTile);
		toaManager.print("Dung tile index -> " + dungTileIndex);
		//System.out.println("Dung tile index -> " + dungTileIndex);
		for (KephriRowTest row : toaManager.kephri.kephriRows)
		{

			//If phase = 5 we make the melee tile the same as the dung tile so we dont phase it on the wrong tile and get confused
			if(kephriPhase == 5){
				toaManager.print("Getting optimal melee row on phase 5");
				if(row.meleeTile.equals(optimalDungTile)){
					toaManager.print("Returning melee tile which matches dung tile on p5.");
					return row;
				}
				continue;
			}

			//todo fix this kephriphase != 5 case, refer to video 'p5 issue'
			if ((row.index <= dungTileIndex + 1 && kephriPhase != 5) || (row.index < dungTileIndex && kephriPhase == 5))
			{
				//System.out.println("Skipping over row with index: " + row.index);
				continue;
			}
			WorldPoint melee = row.meleeTile;
			WorldPoint stepBack = row.stepBack;

			if (melee == null || stepBack == null)
			{
				//System.out.println("Skipping row becasue melee or stepback tile is null");
				continue;
			}

			//todo fix this kephriphase != 5 case, refer to video 'p5 issue'
			//If tile distance to dung tile is less than 2 we skip it
			if (melee.distanceTo(optimalDungTile) < 2 && kephriPhase != 5)
			{
				//System.out.println("Skipping row because melee tile distance to dung tile is less than 2, row index -> " + row.index);
				continue;
			}
			//Think is not finding a path while bombs is out
			//ArrayList<WorldPoint> testPath = Movement.pathToGoal(melee, optimalDungTile, new HashSet<>(bombTiles));
			ArrayList<WorldPoint> testPath = Movement.pathToGoal(melee, optimalDungTile, new HashSet<>());
			//size 6 because i think the first index in the path is always the tile you start on so it doesnt count
			if (testPath == null || testPath.size() > 6)
			{
				System.out.println("Path is null or size > 6 so skipping this row");
				continue;
			}
			//TODO prioritise tiles that let us avoid corner pathing
			// low priority, seems fiddly
//			if (melee is not corner tile...etc)
//			{
//
//			}
			//if dung on start or stepback tile skip it
			if (!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(melee).empty() ||
				!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(stepBack).empty())
			{
				//System.out.println("Dung game object on melee or stepback tile, skipping this row");
				continue;
			}
			if (Reachable.isWalkable(melee) && Reachable.isWalkable(stepBack))
			{
				return row;
			}
			else
			{
				if (!Reachable.isWalkable(melee))
				{
					//System.out.println("Melee is not walkable");
				}
				if (!Reachable.isWalkable(stepBack))
				{
					//System.out.println("Stepback is not walkable");
				}
			}
		}
		//System.out.println("Returning null in get optimal melee row");
		return null;
	}

	//Intention of this is to find if there are any rows dunged after the row containing given world point.
	//The assumption is that you should never have dung after/infront of a row and if that happens the row-detection logic is wrong
	public boolean isBlocked(WorldPoint potentialTile)
	{
		int potentialTileIndex = getTileIndex(potentialTile);
		//If we encounter dung before we get to the potential tile index we return true;
		for(int i = kephriRows.size()-1; i >= potentialTileIndex; i--)
		{
			KephriRowTest row = kephriRows.get(i);
			if (!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(row.meleeTile).empty() ||
				!TileObjects.search().withId(ToaConstants.KEPHRI_DUNG_GAME_OBJECT).atLocation(row.stepBack).empty())
			{
				return true;
			}
		}
		return false;
	}


	public int getTileIndex(WorldPoint wp)
	{
		if (wp == null)
		{
			return 0;
		}
		for (KephriRowTest row : toaManager.kephri.kephriRows)
		{
			if (wp.equals(row.meleeTile))
			{
				return row.index;
			}
		}
		return -1;
	}

	public boolean maxAgileScarabs(){
		return NPCUtil.findAll("Agile Scarab").size() >= 5;
	}
}
