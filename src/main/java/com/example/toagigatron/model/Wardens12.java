package com.example.toagigatron.model;


import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
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

public class Wardens12
{
	@Inject
	ToaManager toaManager;

	@Inject
	Client client;

	@Inject
	EventBus eventBus;

	public int bgsHit = 0;
	public int orbsTanked = 0;
	public NPC obelisk = null;
	public GameObject obeliskObject = null;
	public NPC warden = null;
	public WorldArea wardenRoom = null;
	public ArrayList<WorldPoint> tilesInWardenRange = new ArrayList<>();
	public ArrayList<WorldPoint> beamTiles = new ArrayList<>();
	public ConcurrentHashMap<WorldPoint, Integer> dangerTiles = new ConcurrentHashMap<>();
	public ArrayList<WorldPoint> windmillTiles = new ArrayList<>();
	public ArrayList<WorldPoint> prisonTiles = new ArrayList<>();
	public int windMillTick = 0;
	public int beamTick = 0;
	public int prisonTick = 0;
	public WorldPoint safeTile = null;
	public WorldPoint dodgeTile = null;
	public boolean p2Completed = false;
	public int ballTick = 0;
	public WorldPoint obeliskTile = null;
	public WorldArea obeliskArea = null;
	public WorldPoint blockTile = null;
	public boolean bagOpened = false;
	public int coreTick = 0;

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
		bgsHit = 0;
		coreTick = 0;
		orbsTanked = 0;
		obelisk = null;
		warden = null;
		wardenRoom = null;
		beamTiles.clear();
		dangerTiles.clear();
		windmillTiles.clear();
		prisonTiles.clear();
		tilesInWardenRange.clear();
		windMillTick = 0;
		beamTick = 0;
		prisonTick = 0;
		safeTile = null;
		dodgeTile = null;
		p2Completed = false;
		ballTick = 0;
		bagOpened = false;
		obeliskTile = null;
		obeliskArea = null;
		blockTile = null;
		obeliskObject = null;
	}

	@Subscribe
	public void onNPCSpawn(NpcSpawned npcSpawned)
	{
		if (npcSpawned.getNpc().getName().equals("Core"))
		{
			coreTick = getCoreTick();
		}
	}

	@Subscribe
	public void onNPCDespawn(NpcDespawned npcDespawned)
	{
		if (npcDespawned.getNpc().getName().equals("Core"))
		{
			coreTick = 0;
		}
	}


	public int getCoreTick()
	{
		/*
		FROM WIKI: https://oldschool.runescape.wiki/w/Core
		Warden HP	Core Exposure
		100%-80%	21 ticks (12.6s)
		80%-60%	29 ticks (17.4s)
		60%-40%	37 ticks (22.2s)
		40%-20%	45 ticks (27s)
		20%-0%	53 ticks (31.8s)
		*/

		int wardenHp = toaManager.getBossHp();
		int maxWardenHp = toaManager.getBossMaxHp();
		int threshold80 = (int) (maxWardenHp * 0.8);
		int threshold60 = (int) (maxWardenHp * 0.6);
		int threshold40 = (int) (maxWardenHp * 0.4);
		int threshold20 = (int) (maxWardenHp * 0.2);
		// Add +1 to everything?
		if (wardenHp > threshold80)
		{
			return 22;
		}
		if (wardenHp > threshold60)
		{
			return 30;
		}
		if (wardenHp > threshold40)
		{
			return 38;
		}
		if (wardenHp > threshold20)
		{
			return 46;
		}
		return 54;
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved projectileMoved)
	{
		Projectile p = projectileMoved.getProjectile();
		int id = p.getId();
		if (id == ToaConstants.WARDEN_P2_SKULL_PROJECTILE_ID)
		{
			ArrayList<WorldPoint> dangerTiles = generateDangerTiles(p);
			for (WorldPoint wp : dangerTiles)
			{
				this.dangerTiles.put(wp, (p.getRemainingCycles() / 30) + 3);
			}
		}
		if (id == ToaConstants.WARDENS_P2_PRISON)
		{
			WorldPoint target = WorldPoint.fromLocal(client, p.getTarget());
			if (!prisonTiles.contains(target))
			{
				prisonTiles.add(target);
			}
			prisonTick = (p.getRemainingCycles() / 30) + 2;
			dangerTiles.put(WorldPoint.fromLocal(client, p.getTarget()), (p.getRemainingCycles() / 30) + 2);
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		//GameObject obelisk = new GameObjectQuery().idEquals(ToaConstants.WARDEN_OBELISK).result(client).first();
		GameObject obj = event.getGameObject();
		if (obj != null && obj.getId() == ToaConstants.WARDEN_OBELISK && obeliskObject == null)
		{
			obeliskObject = event.getGameObject();
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		GameObject obj = event.getGameObject();
		if (obj != null && obj.getId() == ToaConstants.WARDEN_OBELISK && obeliskObject != null)
		{
			obeliskObject = null;
		}
	}

	public ArrayList<WorldPoint> generateDangerTiles(Projectile p)
	{
		WorldPoint refPoint = WorldPoint.fromLocal(client, p.getTarget());
		toaManager.print("Skull spawned at " + toaManager.worldPointString(refPoint));
		WorldPoint southWest = refPoint.dx(-3).dy(-3);
		WorldPoint northEast = refPoint.dx(4).dy(4);
		ArrayList<WorldPoint> returnList = (ArrayList<WorldPoint>) WorldAreas.createArea(southWest, northEast).toWorldPointList();
		returnList.removeAll(generateWorldPoints(refPoint));
		return returnList;
	}

	public ArrayList<WorldPoint> wardenMeleeTiles()
	{
		if (warden == null)
		{
			return new ArrayList<>();
		}
		WorldPoint refPoint = WorldAreas.getCenter(warden.getWorldArea());
		WorldPoint southWest = refPoint.dx(-3).dy(-3);
		WorldPoint northEast = refPoint.dx(4).dy(4);
		return (ArrayList<WorldPoint>) WorldAreas.createArea(southWest, northEast).toWorldPointList();
	}

	public WorldPoint defaultSafeTile()
	{
		GameObject obelisk = ObjectUtil.getNearestGameObject(ToaConstants.WARDEN_OBELISK);
		if (obelisk == null)
		{
			return null;
		}
		return WorldAreas.getCenter(Objects.requireNonNull(ObjectUtil.getWorldArea(obelisk))).dx(-5).dy(3);
	}

	public WorldPoint defaultDodgeTile()
	{
		GameObject obelisk = ObjectUtil.getNearestGameObject(ToaConstants.WARDEN_OBELISK);
		if (obelisk == null)
		{
			return null;
		}
		return WorldAreas.getCenter(Objects.requireNonNull(ObjectUtil.getWorldArea(obelisk))).dx(-3).dy(5);
	}

	public WorldPoint secondarySafeTile()
	{
		GameObject obelisk = ObjectUtil.getNearestGameObject(ToaConstants.WARDEN_OBELISK);
		if (obelisk == null)
		{
			return null;
		}
		return WorldAreas.getCenter(Objects.requireNonNull(ObjectUtil.getWorldArea(obelisk))).dx(-6).dy(4);
	}

	public WorldPoint secondaryDodgeTile()
	{
		GameObject obelisk = ObjectUtil.getNearestGameObject(ToaConstants.WARDEN_OBELISK);
		if (obelisk == null)
		{
			return null;
		}
		return WorldAreas.getCenter(Objects.requireNonNull(ObjectUtil.getWorldArea(obelisk))).dx(-4).dy(6);
	}

	@Subscribe
	public void onGraphicObjectCreated(GraphicsObjectCreated graphicsObjectCreated)
	{
		GraphicsObject g = graphicsObjectCreated.getGraphicsObject();
		if (g.getId() == ToaConstants.WARDENS_P2_WINDMILL && windMillTick == 0)
		{
			windMillTick = 50;
		}
		if (g.getId() == ToaConstants.WARDENS_P2_BEAM && beamTick == 0)
		{
			beamTick = 50;
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getMessage().toLowerCase().contains("a large ball of energy is shot your way"))
		{
			toaManager.print("Setting balltick to 20");
			ballTick = 20;
		}
		if (event.getMessage().contains("Elidinis' Warden uses the last of its power to restore Tumeken's Warden!"))
		{
			p2Completed = true;
		}
	}


	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (toaManager.getStage() == Stage.WARDENS_P3)
		{
			obeliskTile = null;
			obeliskArea = null;
			blockTile = null;
			if (obeliskObject != null)
			{
				toaManager.print("Obelisk object not despawned when we started p3 for some reason");
				System.out.println("Obelisk object not despawned when we started p3 for some reason");
				obeliskObject = null;
			}
		}

		if (toaManager.getStage() != Stage.WARDENS_P1 && toaManager.getStage() != Stage.WARDENS_P2)
		{
			return;
		}
		if (toaManager.getStage() == Stage.WARDENS_P2)
		{
			toaManager.print("Core tick is " + coreTick);
		}
		obelisk = NPCUtil.findNearest("Obelisk");
		warden = NPCUtil.findNearest(
			ToaConstants.WARDENS_P2_ACTIVE_RANGE_MELEE,
			ToaConstants.WARDENS_P2_ACTIVE_MAGE_MELEE,
			ToaConstants.WARDENS_P2_DOWNED);
		if (obeliskTile == null)
		{
			obeliskTile = obeliskTile();
		}
		if (obeliskArea == null)
		{
			obeliskArea = obeliskArea();
		}
		if (blockTile == null)
		{
			blockTile = blockTile();
		}
		if (windMillTick > 0)
		{
			windMillTick--;
		}
		if (ballTick > 0)
		{
			ballTick--;
		}
		if (coreTick > 0)
		{
			coreTick--;
		}
		if (beamTick > 0)
		{
			beamTick--;
		}
		if (prisonTick > 0)
		{
			prisonTick--;
		}
		if (prisonTick == 0 && !prisonTiles.isEmpty())
		{
			prisonTiles.clear();
		}
//		toaManager.print("Beam: " + beamTick + ", Windmill: " + windMillTick + ", Prison: " + prisonTick);
		if (wardenRoom == null)
		{
			setWardenRoom();
		}
		tilesInWardenRange = tilesInWardenRange();
		for (Map.Entry<WorldPoint, Integer> entry : dangerTiles.entrySet())
		{
			if (entry.getValue() <= 1)
			{
				dangerTiles.remove(entry.getKey());
			}
			else
			{
				entry.setValue(entry.getValue() - 1);
			}
		}
		setTiles();
	}

	public void setTiles()
	{
		if (prisonTiles.contains(defaultSafeTile()) || prisonTiles.contains(defaultDodgeTile()))
		{
			safeTile = secondarySafeTile();
			dodgeTile = secondaryDodgeTile();
		}
		else
		{
			safeTile = defaultSafeTile();
			dodgeTile = defaultDodgeTile();
		}
	}

	public ArrayList<WorldPoint> tilesInWardenRange()
	{
		ArrayList<WorldPoint> returnList = new ArrayList<>();
		if (warden == null)
		{
			return returnList;
		}
		WorldPoint refPoint = WorldAreas.getCenter(warden.getWorldArea());
		WorldPoint southWest = refPoint.dx(-6).dy(-6);
		WorldPoint northEast = refPoint.dx(7).dy(7);
		returnList = new ArrayList<>(WorldAreas.createArea(southWest, northEast).toWorldPointList());
		returnList.removeIf(n -> !Reachable.isWalkable(n));
		return returnList;
	}

	public ArrayList<WorldPoint> generateWorldPoints(WorldPoint projectilePoint)
	{
		ArrayList<WorldPoint> returnVal = new ArrayList<>();
		for (int i = 1; i < 3; i += 1)
		{
			WorldPoint newLp1 = new WorldPoint(projectilePoint.getX(), projectilePoint.getY() + i, projectilePoint.getPlane());
			WorldPoint newLp2 = new WorldPoint(projectilePoint.getX() + i, projectilePoint.getY(), projectilePoint.getPlane());
			WorldPoint newLp3 = new WorldPoint(projectilePoint.getX(), projectilePoint.getY() - i, projectilePoint.getPlane());
			WorldPoint newLp4 = new WorldPoint(projectilePoint.getX() - i, projectilePoint.getY(), projectilePoint.getPlane());
			returnVal.add(newLp1);
			returnVal.add(newLp2);
			returnVal.add(newLp3);
			returnVal.add(newLp4);
		}
		return returnVal;
	}

	public int distanceToWarden()
	{
		if (warden == null)
		{
			return 1000;
		}
		return warden.getWorldArea().distanceTo(client.getLocalPlayer().getWorldLocation());
	}

	public void setWardenRoom()
	{
		WorldPoint refPoint = obeliskTile();
		if (refPoint == null)
		{
			return;
		}
		WorldPoint southWest = refPoint.dx(-11).dy(-12);
		WorldPoint northEast = refPoint.dx(12).dy(13);
		wardenRoom = WorldAreas.createArea(southWest, northEast);
	}

	public WorldPoint obeliskTile()
	{
		//.filter(x -> wardenRoom.contains(x.getWorldLocation())) potentially a fix
		if (obeliskObject == null)
		{
			return null;
		}
//		GameObject obelisk = new GameObjectQuery().idEquals(ToaConstants.WARDEN_OBELISK).result(client).first();
//		if (obelisk == null || obelisk.getWorldArea() == null || obelisk.getWorldArea().getCenter() == null)
//		{
//			return null;
//		}
//		return new WorldPoint(obelisk.getWorldX(), obelisk.getWorldY(), client.getPlane());
		return obeliskObject.getWorldLocation();
	}

	public WorldArea obeliskArea()
	{
		if (obeliskObject == null)
		{
			return null;
		}
		return (ObjectUtil.getWorldArea(obeliskObject));
//		GameObject obelisk = new GameObjectQuery().idEquals(ToaConstants.WARDEN_OBELISK).result(client).first();
//		if (obelisk == null || obelisk.getWorldArea() == null || obelisk.getWorldArea().getCenter() == null)
//		{
//			return null;
//		}
//		return WorldAreas.createArea(new WorldPoint(obelisk.getWorldX() - 1, obelisk.getWorldY() - 1,obeliskTile.getPlane()), new WorldPoint(obelisk.getWorldX() + 2, obelisk.getWorldY() + 2, obeliskTile.getPlane()));
		//return obelisk.getWorldArea();
	}

	public WorldPoint defaultTile()
	{
		if (obeliskObject == null)
		{
			return null;
		}
//		if (obeliskTile() != null)
//		{
//			return new WorldPoint(obeliskTile().getWorldX(), obeliskTile().getWorldY() + 2, obeliskTile.getPlane());
//			//return obeliskTile().dy(2);
//		}
//		toaManager.print("Obelisk tile method is returning null (defaultTile method)");
		return obeliskObject.getWorldLocation().dy(2);
	}

	public WorldPoint dodgeUFO()
	{
		if (obeliskObject == null)
		{
			return null;
		}

		return obeliskObject.getWorldLocation().dx(-2).dy(2);
//		if (obeliskTile() != null)
//		{
//			return new WorldPoint(obeliskTile().getWorldX() - 2, obeliskTile().getWorldY() + 2, obeliskTile.getPlane());
//			//return obeliskTile().dy(2).dx(-2);
//		}
//		toaManager.print("Obelisk tile method is returning null (dodgeUFO method)");
//		return null;

	}

	public WorldPoint blockTile()
	{
		if (obeliskObject == null)
		{
			return null;
		}
		return obeliskObject.getWorldLocation().dx(2);

//		if (obeliskTile() != null)
//		{
//			return new WorldPoint(obeliskTile().getWorldX() + 2, obeliskTile().getWorldY(), obeliskTile.getPlane());
//			//return obeliskTile().dx(2);
//		}
//		toaManager.print("Obelisk tile method is returning null (blockTile method)");
//		return null;
	}

	@Subscribe
	public void onHitSplat(HitsplatApplied hitsplatApplied)
	{
		if (hitsplatApplied.getActor().equals(obelisk) && toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItemsBgs()))
		{
			toaManager.print("Hit bgs with " + hitsplatApplied.getHitsplat().getAmount());
			bgsHit = hitsplatApplied.getHitsplat().getAmount();
		}
		if (hitsplatApplied.getActor().equals(client.getLocalPlayer()) && hitsplatApplied.getHitsplat().getAmount() == 3)
		{
			orbsTanked++;
		}
	}
}
