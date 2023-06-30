package com.example.toagigatron.model;

import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.bossmodel.KephriDungRow;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.puzzlemodel.KephriPuzzleRoom;
import com.example.toagigatron.model.puzzlemodel.KephriTilePuzzle;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.Direction;
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


	public void resetVariables()
	{
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
		previousKephriAnimation = 0;
	}

	public void generateDungRows()
	{
		if (kephri == null)
		{
			return;
		}
		WorldPoint refPoint = WorldAreas.getCenter(kephri.getWorldArea());
		kephriDungRows.add(createDungRow(refPoint, -3, 2, -5, 2, -8, 7, -7, 5, Direction.WEST, 0));
		kephriDungRows.add(createDungRow(refPoint, -3, 1, -5, 1, -8, 1, -6, 1, Direction.WEST, 1));
		kephriDungRows.add(createDungRow(refPoint, -3, 0, -5, 0, -8, 0, -6, 0, Direction.WEST, 2));
		kephriDungRows.add(createDungRow(refPoint, -3, -1, -5, -1, -8, -1, -6, -1, Direction.WEST, 3));
		kephriDungRows.add(createDungRow(refPoint, -3, -2, -5, -2, -8, -7, -6, -7, Direction.WEST, 4));

		kephriDungRows.add(createDungRow(refPoint, -2, -3, -2, -5, -7, -8, -5, -7, Direction.SOUTH, 5));
		kephriDungRows.add(createDungRow(refPoint, -1, -3, -1, -5, -1, -8, -1, -6, Direction.SOUTH, 6));
		kephriDungRows.add(createDungRow(refPoint, 0, -3, 0, -5, 0, -8, 0, -6, Direction.SOUTH, 7));
		kephriDungRows.add(createDungRow(refPoint, 1, -3, 1, -5, 1, -8, 1, -6, Direction.SOUTH, 8));
		kephriDungRows.add(createDungRow(refPoint, 2, -3, 2, -5, 7, -8, 7, -6, Direction.SOUTH, 9));

		kephriDungRows.add(createDungRow(refPoint, 3, -2, 5, -2, 8, -7, 7, -5, Direction.EAST, 10));
		kephriDungRows.add(createDungRow(refPoint, 3, -1, 5, -1, 8, -1, 6, -1, Direction.EAST, 11));
		kephriDungRows.add(createDungRow(refPoint, 3, 0, 5, 0, 8, 0, 6, 0, Direction.EAST, 12));
		kephriDungRows.add(createDungRow(refPoint, 3, 1, 5, 1, 8, 1, 6, 1, Direction.EAST, 13));
		kephriDungRows.add(createDungRow(refPoint, 3, 2, 5, 2, 8, 7, 6, 7, Direction.EAST, 14));

		kephriDungRows.add(createDungRow(refPoint, 2, 3, 2, 5, 7, 8, 5, 7, Direction.NORTH, 15));
		kephriDungRows.add(createDungRow(refPoint, 1, 3, 1, 5, 1, 8, 1, 6, Direction.NORTH, 16));
		kephriDungRows.add(createDungRow(refPoint, 0, 3, 0, 5, 0, 8, 0, 6, Direction.NORTH, 17));
		kephriDungRows.add(createDungRow(refPoint, -1, 3, -1, 5, -1, 8, -1, 6, Direction.NORTH, 18));
		kephriDungRows.add(createDungRow(refPoint, -2, 3, -2, 5, -7, 8, -5, 7, Direction.NORTH, 19));
		toaManager.print("Generated " + kephriDungRows.size() + " dungs");
	}

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
			kephriRoom = WorldAreas.createArea(southWest,northEast);
		}
		if (kephriMeleeTiles.isEmpty())
		{
			WorldArea kephriArea = kephri.getWorldArea();
			WorldPoint centerTile = WorldAreas.getCenter(kephri.getWorldArea());
			WorldPoint southWest = centerTile.dx(-3).dy(-3);
			WorldPoint northEast = centerTile.dx(+4).dy(+4);
			ArrayList<WorldPoint> bigKephriArea = (ArrayList<WorldPoint>) WorldAreas.createArea(southWest,northEast).toWorldPointList();
			bigKephriArea.removeIf(kephriArea::contains);
			for (WorldPoint wp : bigKephriArea)
			{
				if (Math.abs(centerTile.getX() - wp.getX()) == 3 && Math.abs(centerTile.getY() - wp.getY()) == 3)
				{
					continue;
				}
				kephriMeleeTiles.add(wp);
			}
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
		WorldPoint refPoint = WorldPoint.fromLocal(client, object.getLocation());
		if (refPoint.equals(currentRow.endPoint))
		{
			int index = currentRow.index;
			previousRow = currentRow;
			if (index + 1 < kephriDungRows.size())
			{
				currentRow = kephriDungRows.get(index + 1);
			}
		}
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
		GameObject ancientButton = ObjectUtil.getNearestGameObject(ToaConstants.KEPHRI_ANCIENT_BUTTON);
		GameObject ancientTablet = ObjectUtil.getNearestGameObject(ToaConstants.KEPHRI_ANCIENT_TABLET);
		GameObject pillar = ObjectUtil.getNearestGameObject(ToaConstants.ZEBAK_ROAR_ROCK);
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
			else if (ObjectUtil.distanceTo(ancientButton, WorldAreas.getCenter(room)) < 6)
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
			else if (ObjectUtil.distanceTo(ancientTablet, WorldAreas.getCenter(room)) < 6)
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
				if (anim == 9577 || anim == 9578)
				{
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
				if (anim == -1 && previousKephriAnimation == 9578)
				{
					ticksSinceChange = 0;
					kephriTick = 5;
				}
				previousKephriAnimation = anim;
			}
		}

	}
}
