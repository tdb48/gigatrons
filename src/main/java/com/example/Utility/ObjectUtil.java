package com.example.Utility;

import com.example.EthanApiPlugin.Collections.TileObjects;

import static com.example.PacketUtils.PacketReflection.client;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.TileObject;
import net.runelite.api.WallObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public class ObjectUtil
{

	public static boolean hasAction(GameObject obj, String action){
		for(String s : client.getObjectDefinition(obj.getId()).getActions()){
			if(s.equalsIgnoreCase(action)){
				return true;
			}
		}
		return false;
	}
	public static GameObject getObject(int id)
	{
		TileObject tileObject = TileObjects.search().withId(id).first().orElse(null);
		if (tileObject instanceof GameObject)
		{
			return (GameObject) tileObject;
		}
		return null;
	}

	public static WallObject getWallObject(int id)
	{
		TileObject tileObject = TileObjects.search().withId(id).first().orElse(null);
		if (tileObject instanceof WallObject)
		{
			return (WallObject) tileObject;
		}
		return null;
	}

	public static GameObject getNearestGameObject(int id)
	{
		TileObject tileObject = TileObjects.search().withId(id).nearestToPlayer().orElse(null);
		if (tileObject instanceof GameObject)
		{
			return (GameObject) tileObject;
		}
		return null;
	}

	public static TileObject getNearestTileObject(int id)
	{
		return TileObjects.search().withId(id).nearestToPlayer().orElse(null);
	}

	public static int distanceTo(GameObject gameObject, WorldPoint worldPoint)
	{
		if (gameObject == null)
		{
			return Integer.MAX_VALUE;
		}
		int startX = gameObject.getX();
		int startY = gameObject.getY();
		int width = java.util.Objects.requireNonNull(ObjectUtil.getWorldArea(gameObject)).getWidth();
		int height = java.util.Objects.requireNonNull(ObjectUtil.getWorldArea(gameObject)).getHeight();
		int diffX = gameObject.getX() + width - startX;
		int diffY = gameObject.getY() + height - startY;
		WorldPoint gameObjectWorldpoint = WorldPoint.fromScene(client, startX + diffX / 2, startY + diffY / 2, gameObject.getPlane());
		return gameObjectWorldpoint.distanceTo(worldPoint);
	}

	public static List<TileObject> getObjects(int... id)
	{
		List<Integer> arrayList = Arrays.stream(id)
			.boxed()
			.collect(Collectors.toList());
		return TileObjects.search().idInList(arrayList).result();
	}

	public static List<GameObject> getGameObjects(int... id)
	{
		List<GameObject> gameObjects = new ArrayList<>();
		for (TileObject tileObject : getObjects(id))
		{
			if (tileObject instanceof GameObject)
			{
				gameObjects.add((GameObject) tileObject);
			}
		}
		return gameObjects;
	}

	public static WorldArea getWorldArea(TileObject object)
	{
		if (!object.getLocalLocation().isInScene())
		{
			return null;
		}
		if (!(object instanceof GameObject))
		{
			return null;
		}
		GameObject obj = (GameObject) object;
		LocalPoint localSWTile = new LocalPoint(
			obj.getLocalLocation().getX() - obj.sizeX() * Perspective.LOCAL_TILE_SIZE / 2,
			obj.getLocalLocation().getY() - obj.sizeY() * Perspective.LOCAL_TILE_SIZE / 2
		);

		LocalPoint localNETile = new LocalPoint(
			obj.getLocalLocation().getX() + obj.sizeX() * Perspective.LOCAL_TILE_SIZE / 2,
			obj.getLocalLocation().getY() + obj.sizeY() * Perspective.LOCAL_TILE_SIZE / 2
		);

		return WorldAreas.createArea(
			WorldPoint.fromLocal(Static.getClient(), localSWTile),
			WorldPoint.fromLocal(Static.getClient(), localNETile)
		);
	}
}
