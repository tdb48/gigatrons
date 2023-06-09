package com.example.Utility;

import com.example.EthanApiPlugin.Collections.TileObjects;

import java.util.List;
import net.runelite.api.GameObject;
import net.runelite.api.Perspective;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public class Objects
{
	public static GameObject getObject(int id)
	{
		TileObject tileObject = TileObjects.search().withId(id).first().orElse(null);
		if (tileObject instanceof GameObject)
		{
			return (GameObject) tileObject;
		}
		return null;
	}

	public static GameObject getNearestGameObject(int id){
		TileObject tileObject = TileObjects.search().withId(id).nearestToPlayer().orElse(null);
		if (tileObject instanceof GameObject)
		{
			return (GameObject) tileObject;
		}
		return null;
	}
	public static TileObject getNearestTileObject(int id){
		return TileObjects.search().withId(id).nearestToPlayer().orElse(null);
	}

	public static List<TileObject> getObjects(int id)
	{
		return TileObjects.search().withId(id).result();
	}

	public static WorldArea getWorldArea(TileObject object){
		if(!object.getLocalLocation().isInScene()){
			return null;
		}
		if(!(object instanceof GameObject)){
			return null;
		}
		GameObject obj = (GameObject) object;
		LocalPoint localSWTile = new LocalPoint(
				obj.getLocalLocation().getX() - obj.sizeX()* Perspective.LOCAL_TILE_SIZE / 2,
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
