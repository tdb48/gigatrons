package com.example.toagigatron.model;


import com.example.Utility.NPCUtil;
import com.example.Utility.Reachable;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Direction;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.Scene;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

public class Wardens3
{
	@Inject
	ToaManager toaManager;

	@Inject
	Client client;

	@Inject
	EventBus eventBus;
	public boolean enrage;
	public NPC warden = null;
	public Direction tileflip = Direction.NONE;

	public WorldPoint primarySafeTile = null;
	public WorldPoint secondarySafeTile = null;
	public WorldPoint nextPrimarySafeTile = null;
	public WorldPoint nextSecondarySafeTile = null;
	public Map<WorldPoint, Integer> babaBombs = new ConcurrentHashMap<>();
	public Map<WorldPoint, Integer> lightning = new ConcurrentHashMap<>();
	public int babaTick = 0;
	public WorldPoint playerLoc = null;
	public int skullTick = 0;
	public int bgsHit = 0;
	public int tileFlipTickCounter = 3;
	public boolean stayOnGreen;
	public ArrayList<WorldPoint> enrageArea = new ArrayList<>();

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
		warden = null;
		enrage = false;
		tileflip = Direction.NONE;
		primarySafeTile = null;
		secondarySafeTile = null;
		nextPrimarySafeTile = null;
		nextSecondarySafeTile = null;
		babaBombs.clear();
		lightning.clear();
		playerLoc = null;
		bgsHit = 0;
		tileFlipTickCounter = 3;
		babaTick = 0;
		stayOnGreen = false;
		enrageArea.clear();
	}

	@Subscribe
	public void onHitSplat(HitsplatApplied hitsplatApplied)
	{
		if (client.getLocalPlayer().getAnimation() == ToaConstants.BGS_SPEC_ANIMATION
			&& warden != null
			&& hitsplatApplied.getActor().equals(warden)
			&& toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItemsBgs()))
		{
			toaManager.print("Hit bgs with " + hitsplatApplied.getHitsplat().getAmount());
			bgsHit = hitsplatApplied.getHitsplat().getAmount();
		}
	}

	public WorldPoint wardenRefPoint()
	{
		if (warden == null)
		{
			return null;
		}
		return WorldAreas.getCenter(warden.getWorldArea());
	}

	public ArrayList<WorldPoint> tilesInRunRange(ArrayList<WorldPoint> list)
	{
		ArrayList<WorldPoint> returnList = new ArrayList<>();
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		for (WorldPoint wp : list)
		{
			if (playerPoint.distanceTo(wp) <= 2)
			{
				returnList.add(wp);
			}
		}

		return returnList;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (babaBombs.size() > 0)
		{
			for (Map.Entry<WorldPoint, Integer> entry : babaBombs.entrySet())
			{
				if (entry.getValue() == 0)
				{
					babaBombs.remove(entry.getKey());
				}
				else
				{
					entry.setValue(entry.getValue() - 1);
				}
			}
		}
		if (lightning.size() > 0)
		{
			for (Map.Entry<WorldPoint, Integer> entry : lightning.entrySet())
			{
				if (entry.getValue() == 0)
				{
					lightning.remove(entry.getKey());
				}
				else
				{
					entry.setValue(entry.getValue() - 1);
				}
			}
		}
		if (toaManager.getStage() != Stage.WARDENS_P3)
		{
			return;
		}
		if (tileFlipTickCounter > 0)
		{
			tileFlipTickCounter--;
		}
		if (babaTick > 0)
		{
			babaTick--;
		}
		skullTick++;
		warden = NPCUtil.findNearest("Tumeken's Warden");
		if (warden != null && warden.getAnimation() == ToaConstants.WARDENS_P3_ENRAGED_ANIMATION_ID)
		{
			enrage = true;
		}
		primarySafeTile = findSafeTile(tileflip, true);
		secondarySafeTile = findSafeTile(tileflip, false);
		nextPrimarySafeTile = findNextSafeTile(tileflip, true);
		nextSecondarySafeTile = findNextSafeTile(tileflip, false);
		playerLoc = client.getLocalPlayer().getWorldLocation();
		enrageArea = getEnrageArea();
	}

	public boolean isTileWalkable(ArrayList<Integer> objectIDs, WorldPoint wp)
	{
		List<Tile> tilesList = new ArrayList<>();
		Scene scene = client.getScene();
		Tile[][][] tiles = scene.getTiles();
		int z = client.getPlane();
		for (int x = 0; x < 104; ++x)
		{
			for (int y = 0; y < 104; ++y)
			{
				Tile tile = tiles[z][x][y];
				if (tile == null)
				{
					continue;
				}
				tilesList.add(tile);
			}
		}
		for (Tile tile : tilesList)
		{
			if (tile.getWorldLocation().equals(wp))
			{
				if (tile.getGroundObject() != null)
				{
					if (objectIDs.contains(tile.getGroundObject().getId()))
					{
						return false;
					}
				}
			}
		}
		return true;
	}

	public ArrayList<WorldPoint> getEnrageArea()
	{
		WorldPoint reference = wardenRefPoint();
		if (reference == null)
		{
			return new ArrayList<>();
		}
		WorldPoint thirdRow = reference.dy(5);
		WorldPoint fourthRow = reference.dy(6);
		WorldPoint northEast = reference.dx(7).dy(6);
		// Only row one and two
		//toaManager.print("Is fourth row walkable -> " + !Reachable.isWalkable(fourthRow));
		if (!isTileWalkable(ToaConstants.WARDENS_P3_UNWALKABLE_TILES, fourthRow))
		{
			northEast = reference.dx(7).dy(5);
		}
		// Only row one
		if (!isTileWalkable(ToaConstants.WARDENS_P3_UNWALKABLE_TILES, thirdRow))
		{
			northEast = reference.dx(7).dy(4);
		}
		WorldPoint southWest = reference.dx(-6).dy(3);
		ArrayList<WorldPoint> returnList = (ArrayList<WorldPoint>) WorldAreas.createArea(southWest, northEast).toWorldPointList();
		returnList.removeIf(n -> !Reachable.isWalkable(n));
		return returnList;
	}

	@Subscribe
	public void onGraphicObjectCreated(GraphicsObjectCreated graphicsObjectCreated)
	{
		GraphicsObject g = graphicsObjectCreated.getGraphicsObject();
		WorldPoint lightningTile = WorldPoint.fromLocal(client, g.getLocation());
		if (g.getId() == 1446) //shadow ID which is what we want
		//if (g.getId() == ToaConstants.LIGHTNING_GRAPHICS_OBJECT_ID)
		{
			lightning.put(lightningTile, 4);
		}
	}

	@Subscribe
	public void onNPCSpawn(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();
		if (npc.getName().equalsIgnoreCase("energy siphon"))
		{
			stayOnGreen = true;
			skullTick = 0;
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (event.getActor() instanceof NPC)
		{
			NPC npc = (NPC) event.getActor();
			if (npc.getId() == ToaConstants.WARDENS_P3_SKULLS_INACTIVE_ID)
			{
				if (npc.getAnimation() == ToaConstants.WARDENS_P3_ENRAGED_ANIMATION_ID)
				{
					bgsHit = 0;
					enrage = true;
				}
				if (npc.getAnimation() == ToaConstants.WARDENS_P3_EAST_TILE_FLIP_ANIMATION_ID)
				{ //East/Left
					stayOnGreen = false;
					tileFlipTickCounter = 3;
					tileflip = Direction.EAST;
				}
				if (npc.getAnimation() == ToaConstants.WARDENS_P3_WEST_TILE_FLIP_ANIMATION_ID)
				{//Middle
					stayOnGreen = false;
					tileflip = Direction.WEST;
					tileFlipTickCounter = 3;
				}
				if (npc.getAnimation() == ToaConstants.WARDENS_P3_MIDDLE_TILE_FLIP_ANIMATION_ID)
				{//West/Right
					stayOnGreen = false;
					tileflip = Direction.NORTH;
					tileFlipTickCounter = 3;
				}
				if (npc.getAnimation() == ToaConstants.WARDENS_P3_SKULL_SPAWNING_ANIMATION)
				{ //spawning skulls so move tilefip to +1 on previous phase
					//System.out.print("\nSwitching tileflip Loc from -> " + tileflip + " to -> ");
					switch (tileflip)
					{
						case EAST:
							//System.out.print("West\n");
							tileflip = Direction.WEST;
							break;
						case WEST:
							//System.out.print("Middle\n");
							tileflip = Direction.NORTH;
							break;
						case NORTH:
							//System.out.print("East\n");
							tileflip = Direction.EAST;
							break;
					}
				}
			}
			if (npc.getId() == ToaConstants.BABA_PHANTOM_ID)
			{ //baba phantom
				if (npc.getAnimation() == ToaConstants.BABA_PHANTOM_ROCKTHROW_ANIMATION_ID)
				{
					if (!babaBombs.containsKey(playerLoc))
					{
						babaBombs.put(playerLoc, 5);
						babaTick = 5;
					}
				}
			}
		}
	}

	private WorldPoint findNextSafeTile(Direction tileFlip, boolean primary)
	{
		WorldPoint wp = null;
		if (warden == null)
		{
			return null;
		}
		WorldPoint wardenLoc = warden.getWorldLocation();
		if (!enrage)
		{
			switch (tileFlip)
			{
				case EAST:
					if (primary)
					{
						wp = new WorldPoint(wardenLoc.getX() + 3, wardenLoc.getY() + 5, client.getPlane());
					}
					else
					{
						wp = new WorldPoint(wardenLoc.getX() + 3, wardenLoc.getY() + 6, client.getPlane());
					}
					break;
				case WEST:
					if (primary)
					{
						wp = new WorldPoint(wardenLoc.getX() + 2, wardenLoc.getY() + 5, client.getPlane());
					}
					else
					{
						wp = new WorldPoint(wardenLoc.getX() + 2, wardenLoc.getY() + 6, client.getPlane());
					}
					break;
				case NORTH:
					if (primary)
					{
						wp = new WorldPoint(wardenLoc.getX() + 1, wardenLoc.getY() + 5, client.getPlane());
					}
					else
					{
						wp = new WorldPoint(wardenLoc.getX() + 1, wardenLoc.getY() + 6, client.getPlane());
					}
					break;
			}
		}
		return wp;
	}


	private WorldPoint findSafeTile(Direction tileFlip, boolean primary)
	{
		WorldPoint wp = null;
		if (warden == null)
		{
			return null;
		}
		WorldPoint wardenLoc = warden.getWorldLocation();
		if (!enrage)
		{
			switch (tileFlip)
			{
				case NONE:
				case EAST:
					if (tileFlip.equals(Direction.NONE))
					{
						toaManager.print("Tileflip is NONE meaning we just entered");
					}
					if (primary)
					{
						wp = new WorldPoint(wardenLoc.getX() + 1, wardenLoc.getY() + 5, client.getPlane());
					}
					else
					{
						wp = new WorldPoint(wardenLoc.getX() + 1, wardenLoc.getY() + 6, client.getPlane());
					}
					break;
				case WEST:
					if (primary)
					{
						wp = new WorldPoint(wardenLoc.getX() + 3, wardenLoc.getY() + 5, client.getPlane());
					}
					else
					{
						wp = new WorldPoint(wardenLoc.getX() + 3, wardenLoc.getY() + 6, client.getPlane());
					}
					break;
				case NORTH:
					if (primary)
					{
						wp = new WorldPoint(wardenLoc.getX() + 2, wardenLoc.getY() + 5, client.getPlane());
					}
					else
					{
						wp = new WorldPoint(wardenLoc.getX() + 2, wardenLoc.getY() + 6, client.getPlane());
					}
					break;
			}
		}
		return wp;
	}
}
