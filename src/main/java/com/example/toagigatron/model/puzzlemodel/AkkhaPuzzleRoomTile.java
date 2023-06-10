package com.example.toagigatron.model.puzzlemodel;

import javax.annotation.Nullable;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

public class AkkhaPuzzleRoomTile
{

	WorldPoint worldPoint;

	@Nullable
	GameObject object;

	public AkkhaPuzzleRoomTile(WorldPoint worldPoint, GameObject obj)
	{
		this.worldPoint = worldPoint;
		this.object = obj;
	}

	public WorldPoint getWorldPoint()
	{
		return worldPoint;
	}

	public GameObject getObject()
	{
		return object;
	}

}

