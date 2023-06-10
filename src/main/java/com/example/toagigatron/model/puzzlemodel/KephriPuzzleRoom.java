package com.example.toagigatron.model.puzzlemodel;

import net.runelite.api.coords.WorldArea;

public class KephriPuzzleRoom
{
	public RoomType roomType;
	public boolean solved;
	public WorldArea roomArea;
	public int index;

	public enum RoomType
	{
		MATH,
		MEMORY,
		PILLAR,
		LIGHT,
		FINAL
	}

	public KephriPuzzleRoom(RoomType roomType, boolean solved, WorldArea roomArea, int index)
	{
		this.roomType = roomType;
		this.solved = solved;
		this.roomArea = roomArea;
		this.index = index;
	}

	public void setSolved(boolean solved)
	{
		this.solved = solved;
	}

	@Override
	public String toString()
	{
		return "PuzzleRoom{" +
			"roomType=" + roomType +
			", solved=" + solved +
			", roomArea=" + roomArea +
			'}';
	}


}
