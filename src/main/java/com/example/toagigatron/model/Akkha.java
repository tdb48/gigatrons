package com.example.toagigatron.model;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Prayers;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.bossmodel.AkkhaQuadrant;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.puzzlemodel.AkkhaPuzzle;
import com.example.toagigatron.model.puzzlemodel.AkkhaPuzzleRoomTile;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
public class Akkha
{
	@Inject
	ToaManager toaManager;

	@Inject
	Client client;

	@Inject
	EventBus eventBus;

	public AkkhaQuadrant neQuadrant = null;
	public AkkhaQuadrant nwQuadrant = null;
	public AkkhaQuadrant seQuadrant = null;
	public AkkhaQuadrant swQuadrant = null;
	public AkkhaQuadrant safeQuadrant = null;
	public AkkhaQuadrant nextQuadrant = null;
	public ArrayList<WorldPoint> memoryTiles = new ArrayList<>();
	public int memoryTick = -1;
	public ArrayList<NPC> activeShadows = new ArrayList<>();
	public NPC akkhaBoss = null;
	public List<NPC> orbNpcs = new ArrayList<>();
	public ArrayList<WorldPoint> orbTiles = new ArrayList<>();
	public ArrayList<WorldPoint> orbThirdTiles = new ArrayList<>();
	public ArrayList<WorldPoint> akkhaOrbTiles = new ArrayList<>();
	public WorldPoint targetPoint = null;

	//TESTING PUZZLE CODE
	public AkkhaPuzzle puzzle = null;


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
		activeShadows.clear();
		neQuadrant = null;
		nwQuadrant = null;
		seQuadrant = null;
		swQuadrant = null;
		safeQuadrant = null;
		nextQuadrant = null;
		memoryTiles = new ArrayList<>();
		akkhaBoss = null;
		orbNpcs = new ArrayList<>();
		orbTiles = new ArrayList<>();
		akkhaOrbTiles = new ArrayList<>();
		targetPoint = null;
		orbThirdTiles = new ArrayList<>();

		//TESTING PUZZLE CODE
		puzzle = null;
	}

	public boolean isPuzzleActive()
	{
		GameObject exit = ObjectUtil.getNearestGameObject(ToaConstants.AKKHA_PUZZLE_EXIT);
		if (exit == null)
		{
			return false;
		}
		if (Reachable.isWalkable(exit.getWorldLocation().dx(4)))
		{
			return false;
		}
		return Reachable.isWalkable(exit.getWorldLocation().dx(12));
	}

	private void getOrbTiles(int akkhaOrientation, WorldPoint sw)
	{
		//north west
		if (akkhaOrientation == 1792)
		{
			akkhaOrbTiles.add(sw.dy(-1));
			akkhaOrbTiles.add(sw.dx(1).dy(-1));
			akkhaOrbTiles.add(sw.dx(2).dy(-1));
			akkhaOrbTiles.add(sw.dx(3));
			akkhaOrbTiles.add(sw.dx(3).dy(1));
			akkhaOrbTiles.add(sw.dx(3).dy(2));
		}
		//south west
		else if (akkhaOrientation == 1280)
		{
			akkhaOrbTiles.add(sw.dy(3));
			akkhaOrbTiles.add(sw.dx(1).dy(3));
			akkhaOrbTiles.add(sw.dx(2).dy(3));
			akkhaOrbTiles.add(sw.dx(3));
			akkhaOrbTiles.add(sw.dx(3).dy(1));
			akkhaOrbTiles.add(sw.dx(3).dy(2));
		}
		//north east
		else if (akkhaOrientation == 256)
		{
			akkhaOrbTiles.add(sw.dy(-1));
			akkhaOrbTiles.add(sw.dx(1).dy(-1));
			akkhaOrbTiles.add(sw.dx(2).dy(-1));
			akkhaOrbTiles.add(sw.dx(-1));
			akkhaOrbTiles.add(sw.dx(-1).dy(1));
			akkhaOrbTiles.add(sw.dx(-1).dy(2));
		}
		//south east
		else if (akkhaOrientation == 768)
		{
			akkhaOrbTiles.add(sw.dy(3));
			akkhaOrbTiles.add(sw.dx(1).dy(3));
			akkhaOrbTiles.add(sw.dx(2).dy(3));
			akkhaOrbTiles.add(sw.dx(-1));
			akkhaOrbTiles.add(sw.dx(-1).dy(1));
			akkhaOrbTiles.add(sw.dx(-1).dy(2));
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (toaManager.getStage() != Stage.AKKHA_BOSS && toaManager.getStage() != Stage.AKKHA_PUZZLE)
		{
			return;
		}

		if (isPuzzleActive() && toaManager.getStage() == Stage.AKKHA_PUZZLE)
		{
			generatePuzzle();
		}

		NPC akkha = NPCUtil.findNearest(ToaConstants.FINAL_AKKHA);
		if (akkha != null)
		{
			memoryTiles.clear();
//			toaManager.print("Clearing orb tiles");
			akkhaOrbTiles.clear();
			int orientation = akkha.getOrientation();
//			toaManager.print("Akkha orientation -> " + akkha.getOrientation());
			getOrbTiles(orientation, akkha.getWorldLocation());
		}
		if (orbNpcs.size() > 0)
		{
			orbTiles = generateOrbPaths(orbNpcs);
			orbThirdTiles = generateOrbThirdTiles(orbNpcs);
		}
		if (neQuadrant == null)
		{
			generateQuadrant();
		}
//		if (!memoryTiles.isEmpty())
//		{
//			int count = 1;
//			System.out.println("---");
//			for (WorldPoint w : memoryTiles)
//			{
//				System.out.println("Memory tile " + count + ": " + toaManager.worldPointString(w));
//				count++;
//			}
//		}
		if (memoryTick > 0)
		{
//			toaManager.print("Decrementing memory tick from: " + memoryTick + " to: " + (memoryTick-1));
//			System.out.println("Decrementing memory tick from: " + memoryTick + " to: " + (memoryTick-1));
			memoryTick--;
		}
		if (memoryTick == 1 && !memoryTiles.isEmpty())
		{
//			toaManager.print("Memory tick is 1, removing index 0 which is -> " + memoryTiles.get(0));
			memoryTiles.remove(0);
		}
		activeShadows.clear();
		for (NPC npc : Static.getClient().getNpcs())
		{
			if (npc.getId() == ToaConstants.SHADOW_AKKHA && npc.getModel() != null && npc.getModel().getOverrideAmount() == 0)
			{
				activeShadows.add(npc);
			}
		}
		setSafeQuadrant();
		akkhaBoss = NPCUtil.findNearest("Akkha");
	}

	public boolean hasPickaxe()
	{
		return !Inventory.search().nameContains("pickaxe").empty();
	}

	public void generatePuzzle()
	{
		GameObject wall = ObjectUtil.getNearestGameObject(ToaConstants.AKKHA_PUZZLE_HARD_WALL);
		if (puzzle == null && wall != null)
		{
			GameObject statue = ObjectUtil.getNearestGameObject(ToaConstants.AKKHA_SHIELD_STATUE);

			if (statue != null)
			{
				WorldArea puzzleRoomArea = WorldAreas.createArea(statue.getWorldLocation().dx(-12).dy(-9), statue.getWorldLocation().dx(7).dy(10));
				int mirrorID = 45456;
				int[] wallIds = {45460, 45458};
				int[] minedWallIds = {45464, 45462, 45466};
				int moveableMirroID = 45455;
				int minedWall = ToaConstants.AKKHA_MINED_WALL;
				//45466 = mined wall
				ArrayList<AkkhaPuzzleRoomTile> mirrors = new ArrayList<>();
				ArrayList<AkkhaPuzzleRoomTile> walls = new ArrayList<>();
				ArrayList<AkkhaPuzzleRoomTile> minedWalls = new ArrayList<>();
				ArrayList<AkkhaPuzzleRoomTile> moveableMirrors = new ArrayList<>();
				for (GameObject obj : ObjectUtil.getGameObjects(moveableMirroID))
				{
					moveableMirrors.add(new AkkhaPuzzleRoomTile(obj.getWorldLocation(), obj));
				}
				for (GameObject obj : ObjectUtil.getGameObjects(mirrorID))
				{
					mirrors.add(new AkkhaPuzzleRoomTile(obj.getWorldLocation(), obj));
				}
				for (GameObject obj : ObjectUtil.getGameObjects(wallIds))
				{
					walls.add(new AkkhaPuzzleRoomTile(obj.getWorldLocation(), obj));
				}
				for (GameObject obj : ObjectUtil.getGameObjects(minedWallIds))
				{
					minedWalls.add(new AkkhaPuzzleRoomTile(obj.getWorldLocation(), obj));
				}
				puzzle = new AkkhaPuzzle(puzzleRoomArea, walls, minedWalls, mirrors, moveableMirrors);
				//puzzle.constructRoomTiles();
			}
		}
		else if (puzzle != null)
		{
			if (puzzle.roomMatrix.equals(""))
			{
				puzzle.generateMatrix();
			}
			else
			{
				puzzle.matches();
			}

		}
	}

	public boolean orbSpecialActive()
	{
		return client.getLocalPlayer().getModel().getOverrideAmount() == 112;
	}

	public void setSafeQuadrant()
	{
		ArrayList<NPC> shadows = (ArrayList<NPC>) NPCUtil.findAll(ToaConstants.SHADOW_AKKHA);
		if (shadows.size() != 3)
		{
			return;
		}
		if (!hasNPCsInIt(shadows, nwQuadrant.area))
		{
			safeQuadrant = nwQuadrant;
			nextQuadrant = swQuadrant;
		}
		else if (!hasNPCsInIt(shadows, neQuadrant.area))
		{
			safeQuadrant = neQuadrant;
			nextQuadrant = nwQuadrant;
		}
		else if (!hasNPCsInIt(shadows, seQuadrant.area))
		{
			safeQuadrant = seQuadrant;
			nextQuadrant = neQuadrant;
		}
		else if (!hasNPCsInIt(shadows, swQuadrant.area))
		{
			safeQuadrant = swQuadrant;
			nextQuadrant = seQuadrant;
		}
	}

	// If the area has the npc tile in it, return true
	public boolean hasNPCsInIt(ArrayList<NPC> npcs, ArrayList<WorldPoint> area)
	{
		for (NPC npc : npcs)
		{
			if (area.contains(npc.getWorldLocation()))
			{
				return true;
			}
		}
		return false;
	}

	public NPC findNpcInArea(ArrayList<NPC> npcs, ArrayList<WorldPoint> area)
	{
		for (NPC npc : npcs)
		{
			if (area.contains(npc.getWorldLocation()))
			{
				return npc;
			}
		}
		return null;
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (event.getNpc().getId() == 11804 || event.getNpc().getId() == 11708)
		{
			orbNpcs.remove(event.getNpc());
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (event.getNpc().getId() == 11804 || event.getNpc().getId() == 11708)
		{
			orbNpcs.add(event.getNpc());
		}
	}


	@Subscribe
	public void onGraphicObjectCreated(GraphicsObjectCreated graphicsObjectCreated)
	{

		ArrayList<NPC> shadows = (ArrayList<NPC>) NPCUtil.findAll(ToaConstants.SHADOW_AKKHA);
		for (NPC shadow : shadows)
		{
			if (shadow.getAnimation() == ToaConstants.SHADOW_AKKHA_EXPLODE)
			{
				return;
			}
		}
		int graphicsObjectId = graphicsObjectCreated.getGraphicsObject().getId();
		if (graphicsObjectId == ToaConstants.NE_QUADRANT_GRAPHIC_OBJECT
			|| graphicsObjectId == ToaConstants.NW_QUADRANT_GRAPHIC_OBJECT
			|| graphicsObjectId == ToaConstants.SW_QUADRANT_GRAPHIC_OBJECT
			|| graphicsObjectId == ToaConstants.SE_QUADRANT_GRAPHIC_OBJECT)
		{
			if (memoryTick <= 1)
			{
				toaManager.print("Resetting memory tick to three");
				System.out.println("Resetting memory tick to three, current tick -> " + memoryTick);
				memoryTick = 3;
			}

		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		GameObject gameObject = event.getGameObject();
		if (gameObject != null && puzzle != null)
		{
			if (gameObject.getId() == 45455)
			{
				toaManager.print("MIRROR REMOVED");
				puzzle.updateLocation(gameObject.getWorldLocation(), gameObject, "moveablemirrordespawned");
			}
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
	{
		GameObject gameObject = gameObjectSpawned.getGameObject();
		if (gameObject == null)
		{
			return;
		}
		//TESTING PUZZLE CODE
		if (puzzle != null)
		{
			if (gameObject.getId() == ToaConstants.AKKHA_MINED_WALL)
			{
				puzzle.updateLocation(gameObject.getWorldLocation(), gameObject, "mineablewall");
			}
			if (gameObject.getId() == 45455)
			{
				puzzle.updateLocation(gameObject.getWorldLocation(), gameObject, "moveablemirror");
			}
			if (gameObject.getId() == ToaConstants.AKKHA_PUZZLE_HARD_WALL)
			{
				puzzle = null;
			}
		}

		if (gameObject.getId() != ToaConstants.NE_QUADRANT_GAME_OBJECT
			&& gameObject.getId() != ToaConstants.NW_QUADRANT_GAME_OBJECT
			&& gameObject.getId() != ToaConstants.SE_QUADRANT_GAME_OBJECT
			&& gameObject.getId() != ToaConstants.SW_QUADRANT_GAME_OBJECT
		)
		{
			return;
		}
		if (neQuadrant == null || nwQuadrant == null || seQuadrant == null || swQuadrant == null)
		{
			toaManager.print("Quadrants somehow null");
			return;
		}
		Prayers.disableOverheads();
		if (gameObject.getId() == ToaConstants.NE_QUADRANT_GAME_OBJECT)
		{
			toaManager.print("Found NE memory tile");
			System.out.println("Found NE memory tile");
			memoryTiles.add(neQuadrant.memoryTile);
		}
		else if (gameObject.getId() == ToaConstants.NW_QUADRANT_GAME_OBJECT)
		{
			toaManager.print("Found NW memory tile");
			System.out.println("Found NW memory tile");
			memoryTiles.add(nwQuadrant.memoryTile);
		}
		else if (gameObject.getId() == ToaConstants.SE_QUADRANT_GAME_OBJECT)
		{
			toaManager.print("Found SE memory tile");
			System.out.println("Found SE memory tile");
			memoryTiles.add(seQuadrant.memoryTile);
		}
		else if (gameObject.getId() == ToaConstants.SW_QUADRANT_GAME_OBJECT)
		{
			toaManager.print("Found SW memory tile");
			System.out.println("Found SW memory tile");
			memoryTiles.add(swQuadrant.memoryTile);
		}
//		if (neQuadrant.area.contains(gameObjectWorldPoint))
//		{
//			memoryTiles.add(neQuadrant.memoryTile);
//		}
//		else if (nwQuadrant.area.contains(gameObjectWorldPoint))
//		{
//			memoryTiles.add(nwQuadrant.memoryTile);
//		}
//		else if (seQuadrant.area.contains(gameObjectWorldPoint))
//		{
//			memoryTiles.add(seQuadrant.memoryTile);
//		}
//		else if (swQuadrant.area.contains(gameObjectWorldPoint))
//		{
//			memoryTiles.add(swQuadrant.memoryTile);
//		}
	}

	public boolean isNotInBossRoom()
	{
		GameObject g = ObjectUtil.getNearestGameObject(ToaConstants.AKKHA_BOSS_ENTRY);
		if (g == null)
		{
			return true;
		}
		return Reachable.isWalkable(g.getWorldLocation().dx(2));
	}

	public void generateQuadrant()
	{
		GameObject g = ObjectUtil.getNearestGameObject(ToaConstants.AKKHA_BOSS_ENTRY);
		if (g != null)
		{
			WorldPoint refPoint = g.getWorldLocation();
			WorldArea neQuadrantArea = WorldAreas.createArea(
				refPoint.dx(-13).dy(1),
				refPoint.dx(-2).dy(12));
			neQuadrant = new AkkhaQuadrant((
				ArrayList<WorldPoint>) neQuadrantArea.toWorldPointList(),
				refPoint.dx(-13).dy(1),
				refPoint.dx(-11).dy(3));

			WorldArea nwQuadrantArea = WorldAreas.createArea(
				refPoint.dx(-24).dy(1),
				refPoint.dx(-13).dy(14));
			nwQuadrant = new AkkhaQuadrant((
				ArrayList<WorldPoint>) nwQuadrantArea.toWorldPointList(),
				refPoint.dx(-14).dy(1),
				refPoint.dx(-16).dy(3));

			WorldArea seQuadrantArea = WorldAreas.createArea(
				refPoint.dx(-13).dy(-11),
				refPoint.dx(-1).dy(1));
			seQuadrant = new AkkhaQuadrant(
				(ArrayList<WorldPoint>) seQuadrantArea.toWorldPointList(),
				refPoint.dx(-13),
				refPoint.dx(-11).dy(-2));

			WorldArea swQuadrantArea = WorldAreas.createArea(
				refPoint.dx(-24).dy(-10),
				refPoint.dx(-13).dy(1));
			swQuadrant = new AkkhaQuadrant((
				ArrayList<WorldPoint>) swQuadrantArea.toWorldPointList(),
				refPoint.dx(-14),
				refPoint.dx(-16).dy(-2));
			safeQuadrant = neQuadrant;
			nextQuadrant = neQuadrant;
		}
	}

	public boolean isBossActive()
	{
		GameObject teleportCrystal = ObjectUtil.getNearestGameObject(ToaConstants.AKKHA_BOSS_ENTRY);
		if (teleportCrystal == null)
		{
			return false;
		}
		return Reachable.isWalkable(teleportCrystal.getWorldLocation().dx(-12));
	}

	private WorldPoint createWpFromLpWithOffset(LocalPoint lp, int xOffset, int yOffset)
	{
		return WorldPoint.fromLocal(client, lp.getX() + xOffset, lp.getY() + yOffset, client.getPlane());
	}

	public ArrayList<WorldPoint> generateOrbThirdTiles(Collection<NPC> orbNpcList)
	{
		ArrayList<WorldPoint> returnList = new ArrayList<>();
		for (NPC n : orbNpcList)
		{
			LocalPoint lp = n.getLocalLocation();
			//South
			if (n.getOrientation() <= 254)
			{
				returnList.add(createWpFromLpWithOffset(lp, 0, -512));
			}
			//South West
			else if (n.getOrientation() <= 510)
			{

				returnList.add(createWpFromLpWithOffset(lp, -512, -512));

			}
			//West
			else if (n.getOrientation() <= 766)
			{
				returnList.add(createWpFromLpWithOffset(lp, -512, 0));
			}
			//North West
			else if (n.getOrientation() <= 1022)
			{

				returnList.add(createWpFromLpWithOffset(lp, -512, 512));

			}
			//North
			else if (n.getOrientation() <= 1279)
			{
				returnList.add(createWpFromLpWithOffset(lp, 0, 512));

			}
			//North east
			else if (n.getOrientation() <= 1535)
			{
				returnList.add(createWpFromLpWithOffset(lp, 512, 512));
			}
			//East
			else if (n.getOrientation() <= 1791)
			{
				returnList.add(createWpFromLpWithOffset(lp, 512, 0));
			}
			//South east
			else
			{
				returnList.add(createWpFromLpWithOffset(lp, 512, -512));
			}
		}
		return returnList;
	}

	public ArrayList<WorldPoint> generateOrbPaths(Collection<NPC> orbNpcList)
	{
		HashMap<WorldPoint, Color> returnMap = new HashMap<>();
		ArrayList<WorldPoint> returnList = new ArrayList<>();
		for (NPC n : orbNpcList)
		{
			LocalPoint lp = n.getLocalLocation();
			//returnList.add(WorldPoint.fromLocal(client, lp));
			//South
			if (n.getOrientation() <= 254)
			{
				//Tiles 1 and 2 infront of the orb, dangerous
				//returnList.add(createWpFromLpWithOffset(lp, 0, -128));
				returnList.add(createWpFromLpWithOffset(lp, 0, -256));
				returnList.add(createWpFromLpWithOffset(lp, 0, -384));
				returnMap.put(createWpFromLpWithOffset(lp, 0, -128), Color.RED);
				returnMap.put(createWpFromLpWithOffset(lp, 0, -256), Color.RED);
				//Tiles 3 and 4 infront of the orb, less dangerous
				WorldPoint lp2 = createWpFromLpWithOffset(lp, 0, -384);
				WorldPoint lp3 = createWpFromLpWithOffset(lp, 0, -512);
				if (!returnMap.containsKey(lp2))
				{
					returnMap.put(lp2, Color.ORANGE);
				}
				if (!returnMap.containsKey(lp3))
				{
					returnMap.put(lp3, Color.ORANGE);
				}
			}
			//South West
			else if (n.getOrientation() <= 510)
			{

				//returnList.add(createWpFromLpWithOffset(lp, -128, -128));
				returnList.add(createWpFromLpWithOffset(lp, -256, -256));
				returnList.add(createWpFromLpWithOffset(lp, -384, -384));
				returnMap.put(createWpFromLpWithOffset(lp, -128, -128), Color.RED);
				returnMap.put(createWpFromLpWithOffset(lp, -256, -256), Color.RED);
				WorldPoint lp2 = createWpFromLpWithOffset(lp, -384, -384);
				WorldPoint lp3 = createWpFromLpWithOffset(lp, -512, -512);
				if (!returnMap.containsKey(lp2))
				{
					returnMap.put(lp2, Color.ORANGE);
				}
				if (!returnMap.containsKey(lp3))
				{
					returnMap.put(lp3, Color.ORANGE);
				}
			}
			//West
			else if (n.getOrientation() <= 766)
			{
				//returnList.add(createWpFromLpWithOffset(lp, -128, 0));
				returnList.add(createWpFromLpWithOffset(lp, -256, 0));
				returnList.add(createWpFromLpWithOffset(lp, -384, 0));
				returnMap.put(createWpFromLpWithOffset(lp, -128, 0), Color.RED);
				returnMap.put(createWpFromLpWithOffset(lp, -256, 0), Color.RED);
				WorldPoint lp2 = createWpFromLpWithOffset(lp, -384, 0);
				WorldPoint lp3 = createWpFromLpWithOffset(lp, -512, 0);
				if (!returnMap.containsKey(lp2))
				{
					returnMap.put(lp2, Color.ORANGE);
				}
				if (!returnMap.containsKey(lp3))
				{
					returnMap.put(lp3, Color.ORANGE);
				}
			}
			//North West
			else if (n.getOrientation() <= 1022)
			{

				//returnList.add(createWpFromLpWithOffset(lp, -128, 128));
				returnList.add(createWpFromLpWithOffset(lp, -256, 256));
				returnList.add(createWpFromLpWithOffset(lp, -384, 384));
				returnMap.put(createWpFromLpWithOffset(lp, -128, 128), Color.RED);
				returnMap.put(createWpFromLpWithOffset(lp, -256, 256), Color.RED);
				WorldPoint lp2 = createWpFromLpWithOffset(lp, -384, 384);
				WorldPoint lp3 = createWpFromLpWithOffset(lp, -512, 512);
				if (!returnMap.containsKey(lp2))
				{
					returnMap.put(lp2, Color.ORANGE);
				}
				if (!returnMap.containsKey(lp3))
				{
					returnMap.put(lp3, Color.ORANGE);
				}
			}
			//North
			else if (n.getOrientation() <= 1279)
			{

				//returnList.add(createWpFromLpWithOffset(lp, 0, 128));
				returnList.add(createWpFromLpWithOffset(lp, 0, 256));
				returnList.add(createWpFromLpWithOffset(lp, 0, 384));
				returnMap.put(createWpFromLpWithOffset(lp, 0, 128), Color.RED);
				returnMap.put(createWpFromLpWithOffset(lp, 0, 256), Color.RED);
				WorldPoint lp2 = createWpFromLpWithOffset(lp, 0, 384);
				WorldPoint lp3 = createWpFromLpWithOffset(lp, 0, 512);
				if (!returnMap.containsKey(lp2))
				{
					returnMap.put(lp2, Color.ORANGE);
				}
				if (!returnMap.containsKey(lp3))
				{
					returnMap.put(lp3, Color.ORANGE);
				}
			}
			//North east
			else if (n.getOrientation() <= 1535)
			{
				//returnList.add(createWpFromLpWithOffset(lp, 128, 128));
				returnList.add(createWpFromLpWithOffset(lp, 256, 256));
				returnList.add(createWpFromLpWithOffset(lp, 384, 384));
				returnMap.put(createWpFromLpWithOffset(lp, 128, 128), Color.RED);
				returnMap.put(createWpFromLpWithOffset(lp, 256, 256), Color.RED);
				WorldPoint lp2 = createWpFromLpWithOffset(lp, 384, 384);
				WorldPoint lp3 = createWpFromLpWithOffset(lp, 512, 512);
				if (!returnMap.containsKey(lp2))
				{
					returnMap.put(lp2, Color.ORANGE);
				}
				if (!returnMap.containsKey(lp3))
				{
					returnMap.put(lp3, Color.ORANGE);
				}
			}
			//East
			else if (n.getOrientation() <= 1791)
			{
				//returnList.add(createWpFromLpWithOffset(lp, 128, 0));
				returnList.add(createWpFromLpWithOffset(lp, 256, 0));
				returnList.add(createWpFromLpWithOffset(lp, 384, 0));
				returnMap.put(createWpFromLpWithOffset(lp, 128, 0), Color.RED);
				returnMap.put(createWpFromLpWithOffset(lp, 256, 0), Color.RED);
				WorldPoint lp2 = createWpFromLpWithOffset(lp, 384, 0);
				WorldPoint lp3 = createWpFromLpWithOffset(lp, 512, 0);
				if (!returnMap.containsKey(lp2))
				{
					returnMap.put(lp2, Color.ORANGE);
				}
				if (!returnMap.containsKey(lp3))
				{
					returnMap.put(lp3, Color.ORANGE);
				}
			}
			//South east
			else
			{
				//returnList.add(createWpFromLpWithOffset(lp, 128, -128));
				returnList.add(createWpFromLpWithOffset(lp, 256, -256));
				returnList.add(createWpFromLpWithOffset(lp, 384, -384));
				returnMap.put(createWpFromLpWithOffset(lp, 128, -128), Color.RED);
				returnMap.put(createWpFromLpWithOffset(lp, 256, -256), Color.RED);
				WorldPoint lp2 = createWpFromLpWithOffset(lp, 384, -384);
				WorldPoint lp3 = createWpFromLpWithOffset(lp, 512, -512);
				if (!returnMap.containsKey(lp2))
				{
					returnMap.put(lp2, Color.ORANGE);
				}
				if (!returnMap.containsKey(lp3))
				{
					returnMap.put(lp3, Color.ORANGE);
				}
			}
		}
		return returnList;
	}
}
