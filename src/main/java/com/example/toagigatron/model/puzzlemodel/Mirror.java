package com.example.toagigatron.model.puzzlemodel;

import net.runelite.api.coords.WorldPoint;

public class Mirror
{
	public WorldPoint worldPoint;
	public int orientation;

	public Mirror(WorldPoint worldPoint, int orientation)
	{
		this.worldPoint = worldPoint;
		this.orientation = orientation;
	}
}
