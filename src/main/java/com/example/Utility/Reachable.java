package com.example.Utility;


import com.example.EthanApiPlugin.EthanApiPlugin;
import net.runelite.api.*;
import net.runelite.api.coords.Direction;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.util.*;
import java.util.stream.Collectors;

public class Reachable
{
	private static final int MAX_ATTEMPTED_TILES = 64 * 64;

	public static boolean check(int flag, int checkFlag)
	{
		return (flag & checkFlag) != 0;
	}

	public static boolean isObstacle(int endFlag)
	{
		return check(endFlag, 0x100 | 0x20000 | 0x200000 | 0x1000000);
	}

	public static boolean isObstacle(WorldPoint worldPoint)
	{
		return isObstacle(getCollisionFlag(worldPoint));
	}

	public static int getCollisionFlag(WorldPoint point)
	{
		CollisionData[] collisionMaps = Static.getClient().getCollisionMaps();
		if (collisionMaps == null)
		{
			return 0xFFFFFF;
		}

		CollisionData collisionData = collisionMaps[Static.getClient().getPlane()];
		if (collisionData == null)
		{
			return 0xFFFFFF;
		}

		LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient(), point);
		if (localPoint == null)
		{
			return 0xFFFFFF;
		}

		return collisionData.getFlags()[localPoint.getSceneX()][localPoint.getSceneY()];
	}

	public static boolean isWalled(Direction direction, int startFlag)
	{
		switch (direction)
		{
			case NORTH:
				return check(startFlag, 0x2);
			case SOUTH:
				return check(startFlag, 0x20);
			case WEST:
				return check(startFlag, 0x80);
			case EAST:
				return check(startFlag, 0x8);
			default:
				throw new IllegalArgumentException();
		}
	}

	public static boolean isWalled(WorldPoint source, WorldPoint destination)
	{
		return isWalled(Tiles.getAt(source), Tiles.getAt(destination));
	}

	public static boolean isWalled(Tile source, Tile destination)
	{
		WallObject wall = source.getWallObject();
		if (wall == null)
		{
			return false;
		}

		WorldPoint a = source.getWorldLocation();
		WorldPoint b = destination.getWorldLocation();

		switch (wall.getOrientationA())
		{
			case 1:
				return a.dx(-1).equals(b) || a.dx(-1).dy(1).equals(b) || a.dx(-1).dy(-1).equals(b);
			case 2:
				return a.dy(1).equals(b) || a.dx(-1).dy(1).equals(b) || a.dx(1).dy(1).equals(b);
			case 4:
				return a.dx(1).equals(b) || a.dx(1).dy(1).equals(b) || a.dx(1).dy(-1).equals(b);
			case 8:
				return a.dy(-1).equals(b) || a.dx(-1).dy(-1).equals(b) || a.dx(-1).dy(1).equals(b);
			default:
				return false;
		}
	}

	public static boolean hasDoor(WorldPoint source, Direction direction)
	{
		Tile tile = Tiles.getAt(source);
		if (tile == null)
		{
			return false;
		}

		return hasDoor(tile, direction);
	}

	public static boolean hasDoor(Tile source, Direction direction)
	{
		WallObject wall = source.getWallObject();
		if (wall == null)
		{
			return false;
		}
		return isWalled(direction, getCollisionFlag(source.getWorldLocation())) && (Arrays.stream(Static.getClient().getObjectDefinition(wall.getId()).getActions()).anyMatch(x -> (x.contains("Open") || x.contains("Close"))));
	}

	public static boolean isDoored(Tile source, Tile destination)
	{
		WallObject wall = source.getWallObject();
		if (wall == null)
		{
			return false;
		}

		return isWalled(source, destination) && (Arrays.stream(Static.getClient().getObjectDefinition(wall.getId()).getActions()).anyMatch(x -> (x.contains("Open"))));
	}

	public static boolean canWalk(Direction direction, int startFlag, int endFlag)
	{
		if (isObstacle(endFlag))
		{
			return false;
		}

		return !isWalled(direction, startFlag);
	}


	public static boolean isWalkable(WorldPoint worldPoint)
	{
		if(worldPoint == null){
			return false;
		}
		if(!worldPoint.isInScene(Static.getClient())){
			return false;
		}
		if(LocalPoint.fromWorld(Static.getClient(), worldPoint) == null){
			return false;
		}
		if(worldPoint.getPlane() != Static.getClient().getPlane()){
			return false;
		}
		return EthanApiPlugin.canPathToTile(worldPoint).isReachable();
	}
}

