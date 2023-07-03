package com.example.toagigatron.model.puzzlemodel;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.toagigatron.model.constants.ToaConstants;
import java.util.ArrayList;
import net.runelite.api.GameObject;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;

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
		for (TileObject gameObject : TileObjects.search().withId(ToaConstants.AKKHA_MOVEABLE_MIRROR).result())
		{
			if (!(gameObject instanceof GameObject))
			{
				continue;
			}
			if (!hasWorldPoint(gameObject.getWorldLocation()))
			{
				returnList.add((GameObject) gameObject);
			}
		}
//		for (GameObject gameObject : new GameObjectQuery().idEquals(ToaConstants.AKKHA_MOVEABLE_MIRROR).result(Static.getClient()))
//		{
//			if (!hasWorldPoint(gameObject.getWorldLocation()))
//			{
//				returnList.add(gameObject);
//			}
//		}
		return returnList;
	}

	public ArrayList<GameObject> getCorrectMirrors()
	{
		ArrayList<GameObject> returnList = new ArrayList<>();
		for (Mirror m : mirrors)
		{
			WorldPoint wp = m.worldPoint;
			int orientation = m.orientation;
			GameObject mirror = (GameObject) TileObjects.search().withId(ToaConstants.AKKHA_MOVEABLE_MIRROR).atLocation(wp).first().orElse(null);
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

			GameObject mirror = (GameObject) TileObjects.search().withId(ToaConstants.AKKHA_MOVEABLE_MIRROR).atLocation(m.worldPoint).first().orElse(null);
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
			GameObject mirror = (GameObject) TileObjects.search().withId(ToaConstants.AKKHA_MOVEABLE_MIRROR).atLocation(wp).first().orElse(null);
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
			GameObject minedWall = (GameObject) TileObjects.search().withId(ToaConstants.AKKHA_MINED_WALL).atLocation(wp).first().orElse(null);
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
			GameObject mirror = (GameObject) TileObjects.search().withId(ToaConstants.AKKHA_MOVEABLE_MIRROR).atLocation(wp).first().orElse(null);
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
