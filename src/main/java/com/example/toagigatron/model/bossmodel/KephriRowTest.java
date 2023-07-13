package com.example.toagigatron.model.bossmodel;

import com.example.toagigatron.model.constants.Direction;
import net.runelite.api.coords.WorldPoint;

public class KephriRowTest
{
	public WorldPoint meleeTile;
	public WorldPoint stepBack;

	public Direction direction;
	public int index;

	public KephriRowTest(WorldPoint meleeTile, WorldPoint stepBack, Direction direction, int index)
	{
		this.stepBack = stepBack;
		this.index = index;
		this.meleeTile = meleeTile;
		this.direction = direction;
	}
}
