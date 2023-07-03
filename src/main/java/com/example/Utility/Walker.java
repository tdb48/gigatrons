package com.example.Utility;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Walker
{

	static Client client = Static.getClient();

	public static void stepAlong(List<WorldPoint> path)
	{
		if(path == null || path.isEmpty()){
			System.out.println("Path paramater is empty!!");
			return;
		}
		List<WorldPoint> reachablePath = reachablePath(path);
		if (reachablePath.isEmpty())
		{
			System.out.println("Reachable path is empty!!");
			return;
		}
		if (reachablePath.size() > 1)
		{
			walk(reachablePath.get(1));
		}
		else
		{
			walk(reachablePath.get(0));
		}
	}

	public static void walk(WorldPoint worldPoint)
	{
		Player local = client.getLocalPlayer();
		if (local != null)
		{
			WorldPoint walkPoint = worldPoint;
			LocalPoint walkPointLocal = LocalPoint.fromWorld(client, worldPoint);
			if (walkPointLocal == null)
			{
				System.out.println("Local walkpoint is null in WALKER");
			}
			else
			{
				Tile destinationTile = Tiles.getAt(walkPointLocal);
				if (destinationTile == null)
				{
					System.out.println("Destination: " + worldPoint + " is not in the scene.");
					Tile nearestInScene = Tiles.getAll().stream()
						.min(Comparator.comparingInt((xx) -> xx.getWorldLocation().distanceTo(local.getWorldLocation())))
						.orElse(null);
					if (nearestInScene == null)
					{
						System.out.println("Could not find the nearest walkable tile");
						return;
					}
					System.out.println("Returning nearest in scene instead -> " + nearestInScene.getWorldLocation());
					walkPoint = nearestInScene.getWorldLocation();
				}
				//System.out.println("Sending movement packet to -> " + walkPoint);
				MousePackets.queueClickPacket();
				MovementPackets.queueMovement(walkPoint);
			}
		}
	}

	public static List<WorldPoint> reachablePath(List<WorldPoint> remainingPath)
	{
		Player local = client.getLocalPlayer();
		List<WorldPoint> out = new ArrayList<>();

		for (WorldPoint p : remainingPath)
		{
			Tile testTile = Tiles.getAt(p);
			if (testTile == null)
			{
				System.out.println("TEST TILE IS NULL IN REACHABLE PATH!!!! AKKHA BUG THING!!!");
			}
			LocalPoint lp = LocalPoint.fromWorld(client, p.getX(), p.getY());
			if (lp == null)
			{
				System.out.println("Localpoint is null in reachable path (this means its truly unreachable)");
				break;
			}
			Tile tile = Tiles.getAt(lp);
			if (tile == null)
			{
				System.out.println("Tile is null in reachable path even though lp was not null");
				break;
			}
			System.out.println("Adding worldpoint to list -> " + p);
			out.add(p);
		}

		if (out.isEmpty() || out.size() == 1 && out.get(0).equals(local.getWorldLocation()))
		{
			System.out.println("Returning empty list in reachable path");
			if(out.isEmpty()){
				System.out.println("'out' is empty.");
			} else if((out.size() == 1 && out.get(0).equals(local.getWorldLocation()))){
				System.out.println("'out' is size 1 and index 0 is the player world location.");
			}
			return Collections.emptyList();
		}
		System.out.println("Returning out");
		return out;
	}
}
