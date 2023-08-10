package com.example.nexatron.model.constants;

public enum Lanes
{
	WEST(511),
	NORTH(1023),
	SOUTH(0),
	EAST(1537),
	NONE(-1);

	public final int orientation;

	Lanes(int orientation)
	{
		this.orientation = orientation;
	}
}