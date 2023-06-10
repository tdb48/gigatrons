package com.example.toagigatron.model.bossmodel;

import java.util.ArrayList;
import net.runelite.api.coords.WorldPoint;

public class AkkhaQuadrant
{
	public boolean done = false;
	public ArrayList<WorldPoint> area = new ArrayList<>();
	public WorldPoint memoryTile = null;
	public WorldPoint centerTile = null;

	public AkkhaQuadrant(ArrayList<WorldPoint> area, WorldPoint memoryTile, WorldPoint centerTile)
	{
		this.centerTile = centerTile;
		this.area = area;
		this.memoryTile = memoryTile;
	}
}
