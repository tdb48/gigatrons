package com.example.toagigatron.model.puzzlemodel;

import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public class ZebakWaterfallRoom
{
	public RoomType roomType;
	public boolean active;
	public GameObject waterfall;
	public WorldArea roomArea;
	public WorldPoint prePathTile;

	public ZebakWaterfallRoom(RoomType roomType, boolean active, WorldArea roomArea, GameObject waterfall, WorldPoint prePathTile)
	{
		this.waterfall = waterfall;
		this.roomType = roomType;
		this.active = active;
		this.roomArea = roomArea;
		this.prePathTile = prePathTile;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	@Override
	public String toString()
	{
		return "PuzzleRoom{" +
			"roomType=" + roomType +
			", active=" + active +
			", roomArea=" + roomArea +
			'}';
	}

	public enum RoomType
	{
		NE,
		NW,
		SE,
		SW
	}
}
