package com.example.toagigatron.model.bossmodel;

import com.example.toagigatron.model.constants.Direction;
import java.util.ArrayList;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;

public class ZebakJug
{

	public NPC jug = null;
	public LocalPoint jugTile;
	public Direction direction;
	public LocalPoint pushTile;
	public LocalPoint pullTile;
	public boolean push;
	public boolean valid;

	public ZebakJug(NPC jug, Direction direction, ArrayList<LocalPoint> poisonTiles, LocalPoint jugTile)
	{
		if (jug == null)
		{
			this.jugTile = jugTile;
		}
		else
		{
			this.jug = jug;
			this.jugTile = jug.getLocalLocation();
		}
		this.pushTile = getDirectionTile(direction, jugTile);
		this.pullTile = getDirectionTile(getOppositeDirection(direction), jugTile);
		this.direction = direction;
		this.valid = isPushOrPullFree(poisonTiles);

	}

	public static LocalPoint getDirectionTile(Direction direction, LocalPoint localPoint)
	{
		if (direction == Direction.NORTH)
		{
			return new LocalPoint(localPoint.getX(), localPoint.getY() - 128);
		}
		if (direction == Direction.SOUTH)
		{
			return new LocalPoint(localPoint.getX(), localPoint.getY() + 128);
		}
		if (direction == Direction.EAST)
		{
			return new LocalPoint(localPoint.getX(), localPoint.getY() - 128);
		}
		if (direction == Direction.WEST)
		{
			return new LocalPoint(localPoint.getX(), localPoint.getY() + 128);
		}
		if (direction == Direction.NORTH_EAST)
		{
			return new LocalPoint(localPoint.getX() - 128, localPoint.getY() - 128);
		}
		if (direction == Direction.NORTH_WEST)
		{
			return new LocalPoint(localPoint.getX() + 128, localPoint.getY() - 128);
		}
		if (direction == Direction.SOUTH_EAST)
		{
			return new LocalPoint(localPoint.getX() - 128, localPoint.getY() + 128);
		}
		if (direction == Direction.SOUTH_WEST)
		{
			return new LocalPoint(localPoint.getX() + 128, localPoint.getY() + 128);
		}
		return localPoint;
	}

	public LocalPoint getSolveTile()
	{
		if (push)
		{
			return pushTile;
		}
		return pullTile;
	}

	public boolean isPushOrPullFree(ArrayList<LocalPoint> poison)
	{
		if (!poison.contains(pushTile) && !poison.contains(pullTile))
		{
			push = true;
			return true;
		}
		if (poison.contains(pushTile) && !poison.contains(pullTile))
		{
			push = false;
			return true;
		}
		if (!poison.contains(pushTile) && poison.contains(pullTile))
		{
			push = true;
			return true;
		}
		return false;
	}

	public Direction getOppositeDirection(Direction direction)
	{
		switch (direction)
		{
			case SOUTH:
				return Direction.NORTH;
			case NORTH:
				return Direction.SOUTH;
			case WEST:
				return Direction.EAST;
			case EAST:
				return Direction.WEST;
			case NORTH_EAST:
				return Direction.SOUTH_WEST;
			case NORTH_WEST:
				return Direction.SOUTH_EAST;
			case SOUTH_EAST:
				return Direction.NORTH_WEST;
			case SOUTH_WEST:
				return Direction.NORTH_EAST;
		}
		return direction;
	}
}
