package com.example.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.Client;
import net.runelite.api.Constants;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class Tiles
{


	public static Tile getAt(WorldPoint worldPoint)
	{
		return getAt(worldPoint.getX(), worldPoint.getY(), worldPoint.getPlane());
	}

	public static Tile getAt(LocalPoint localPoint)
	{
		return Static.getClient().getScene().getTiles()[Static.getClient().getPlane()][localPoint.getSceneX()][localPoint.getSceneY()];
	}

	public static Tile getAt(int worldX, int worldY, int plane)
	{
		Client client = Static.getClient();
		int correctedX = worldX < Constants.SCENE_SIZE ? worldX + client.getBaseX() : worldX;
		int correctedY = worldY < Constants.SCENE_SIZE ? worldY + client.getBaseY() : worldY;

		if (!WorldPoint.isInScene(client, correctedX, correctedY))
		{
			return null;
		}

		int x = correctedX - client.getBaseX();
		int y = correctedY - client.getBaseY();

		return client.getScene().getTiles()[plane][x][y];
	}

	public static List<Tile> getAll(Predicate<Tile> filter)
	{
		List<Tile> out = new ArrayList<>();
		for (int x = 0; x < 104; ++x)
		{
			for (int y = 0; y < 104; ++y)
			{
				Tile tile = Static.getClient().getScene().getTiles()[Static.getClient().getPlane()][x][y];
				if (tile != null && filter.test(tile))
				{
					out.add(tile);
				}
			}
		}

		return out;
	}

	public static List<Tile> getAll()
	{
		return getAll((x) -> {
			return true;
		});
	}
}
