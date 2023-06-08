package com.example.toagigatron.model.puzzlemodel;

public enum BabaPuzzleSpecial
{

	PILLAR(45494),
	VENT(45499),
	NULL(-1);
	public final int objectId;

	BabaPuzzleSpecial(int objectId)
	{
		this.objectId = objectId;
	}
}
