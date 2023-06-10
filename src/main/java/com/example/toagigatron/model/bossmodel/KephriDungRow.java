package com.example.toagigatron.model.bossmodel;

import net.runelite.api.coords.Direction;
import net.runelite.api.coords.WorldPoint;

public class KephriDungRow
{
	public WorldPoint startPoint;
	public WorldPoint middlePoint;
	public WorldPoint prePathPoint;
	public WorldPoint endPoint;

	public Direction direction;
	public int index;

	public KephriDungRow(WorldPoint startPoint, WorldPoint middlePoint, WorldPoint prePathPoint, WorldPoint endPoint, Direction direction, int index)
	{
		this.middlePoint = middlePoint;
		this.prePathPoint = prePathPoint;
		this.index = index;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.direction = direction;
	}
}
