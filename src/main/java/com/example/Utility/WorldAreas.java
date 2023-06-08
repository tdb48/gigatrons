package com.example.Utility;

import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public class WorldAreas
{
	public static WorldArea createArea(WorldPoint swLocation, WorldPoint neLocation)
	{
		return new WorldArea(swLocation.getX(),
			swLocation.getY(),
			neLocation.getX() - swLocation.getX(),
			neLocation.getY() - swLocation.getY(),
			swLocation.getPlane());
	}
}
