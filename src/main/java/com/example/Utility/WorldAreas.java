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

	public static WorldArea createArea(WorldPoint location, int width, int height)
	{
		return new WorldArea(location.getX(),
			location.getY(),
			width,
			height,
			location.getPlane());
	}

	// TODO
	public static WorldPoint getCenter(WorldArea area)
	{
		return new WorldPoint(area.getX() + (area.getWidth() / 2), area.getY() + (area.getHeight() / 2), area.getPlane());
	}
}
