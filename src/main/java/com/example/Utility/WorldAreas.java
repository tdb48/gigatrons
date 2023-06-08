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

	// TODO
//	public static WorldPoint getCenter(WorldArea area)
//	{
//		return new WorldPoint(this.x + this.width / 2, this.y + this.height / 2, this.plane);
//	}
}
