package com.example.toagigatron.model.puzzlemodel;

import com.example.toagigatron.model.constants.ToaConstants;
import java.util.ArrayList;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.queries.GameObjectQuery;
import net.unethicalite.client.Static;

public class AkkhaPuzzleSolution
{
	public ArrayList<WorldPoint> wallsToMine;
	public ArrayList<Mirror> mirrors;
	public WorldPoint mineTile;

	public AkkhaPuzzleSolution(ArrayList<WorldPoint> wallsToMine, ArrayList<Mirror> mirrors, WorldPoint mineTile)
	{
		this.wallsToMine = wallsToMine;
		this.mirrors = mirrors;
		this.mineTile = mineTile;
	}

	public boolean isSolved()
	{
		return areWallsSolved() && areMirrorsSolved();
	}


	public ArrayList<GameObject> getWrongMirrors()
	{
		ArrayList<GameObject> returnList = new ArrayList<>();
		for (GameObject gameObject : new GameObjectQuery().idEquals(ToaConstants.AKKHA_MOVEABLE_MIRROR).result(Static.getClient()))
		{
			if (!hasWorldPoint(gameObject.getWorldLocation()))
			{
				returnList.add(gameObject);
			}
		}
		return returnList;
	}

	public ArrayList<GameObject> getCorrectMirrors()
	{
		ArrayList<GameObject> returnList = new ArrayList<>();
		for (Mirror m : mirrors)
		{
			WorldPoint wp = m.worldPoint;
			int orientation = m.orientation;
			GameObject mirror = new GameObjectQuery().idEquals(ToaConstants.AKKHA_MOVEABLE_MIRROR).atWorldLocation(wp).result(Static.getClient()).first();
			if (mirror != null && mirror.getOrientation() == orientation)
			{
				returnList.add(mirror);
			}
		}
		return returnList;
	}

	public ArrayList<WorldPoint> getPlaceMirrors()
	{
		ArrayList<WorldPoint> returnList = new ArrayList<>();
		for (Mirror m : mirrors)
		{
			GameObject mirror = new GameObjectQuery().idEquals(ToaConstants.AKKHA_MOVEABLE_MIRROR).atWorldLocation(m.worldPoint).result(Static.getClient()).first();
			if (mirror == null)
			{
				returnList.add(m.worldPoint);
			}
		}
		return returnList;
	}

	public int mirrorsNeeded()
	{
		return mirrors.size() - getCompletedMirrors() - getRotateMirrors().size();
	}

	public ArrayList<Mirror> getRotateMirrors()
	{
		ArrayList<Mirror> returnList = new ArrayList<>();
		for (Mirror m : mirrors)
		{
			WorldPoint wp = m.worldPoint;
			int orientation = m.orientation;
			GameObject mirror = new GameObjectQuery().idEquals(ToaConstants.AKKHA_MOVEABLE_MIRROR).atWorldLocation(wp).result(Static.getClient()).first();
			if (mirror != null && mirror.getOrientation() != orientation)
			{
				returnList.add(m);
			}
		}
		return returnList;
	}

	public int getCompletedMirrors()
	{
		return getCorrectMirrors().size();
	}

	public boolean areWallsSolved()
	{
		for (WorldPoint wp : wallsToMine)
		{
			GameObject minedWall = new GameObjectQuery().idEquals(ToaConstants.AKKHA_MINED_WALL).atWorldLocation(wp).result(Static.getClient()).first();
			if (minedWall == null)
			{
				return false;
			}
		}
		return true;
	}

	public boolean areMirrorsSolved()
	{
		for (Mirror m : mirrors)
		{
			WorldPoint wp = m.worldPoint;
			int orientation = m.orientation;
			GameObject mirror = new GameObjectQuery().idEquals(ToaConstants.AKKHA_MOVEABLE_MIRROR).atWorldLocation(wp).result(Static.getClient()).first();
			if (mirror == null || mirror.getOrientation() != orientation)
			{
				return false;
			}
		}
		return true;
	}

	private boolean hasWorldPoint(WorldPoint wp)
	{
		for (Mirror m : mirrors)
		{
			if (m.worldPoint.equals(wp))
			{
				return true;
			}
		}
		return false;
	}
}
