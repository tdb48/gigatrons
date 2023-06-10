package com.example.toagigatron.model.puzzlemodel;

public enum KephriTilePuzzle
{
	KNIVES("Knives", 45357, 45366, 45389),
	DIAMOND("Diamond", 45359, 45368, 45391),
	STAR("Star", 45361, 45370, 45387),
	WIGGLE("Wiggle", 45363, 45372, 45394),
	BOOT("Boot", 45364, 45373, 45395);

	public final String tileName;
	public final int unflipped;
	public final int flipped;
	public final int light;


	KephriTilePuzzle(String tileName, int unflipped, int flipped, int light)
	{
		this.light = light;
		this.tileName = tileName;
		this.flipped = flipped;
		this.unflipped = unflipped;
	}
}