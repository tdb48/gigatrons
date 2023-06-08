package com.example.Utility;

import com.example.EthanApiPlugin.Collections.TileObjects;
import java.util.ArrayList;
import java.util.List;
import net.runelite.api.GameObject;
import net.runelite.api.TileObject;

public class GameObjects
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

	public static List<TileObject> getObjects(int id)
	{
		return TileObjects.search().withId(id).result();
	}
}
