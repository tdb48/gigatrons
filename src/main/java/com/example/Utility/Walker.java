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
			return;
		}
		List<WorldPoint> reachablePath = reachablePath(path);
		if (reachablePath.isEmpty())
		{
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
			LocalPoint walkPointLocal = LocalPoint.fromWorld(client, worldPoint.getX(), worldPoint.getY());
			if (walkPointLocal == null)
			{
				System.out.println("Local walkpoint is null");
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
			Tile tile = Tiles.getAt(p);
			if (tile == null)
			{
				break;
			}
			out.add(p);
		}

		return (!out.isEmpty() && (out.size() != 1 || !(out.get(0)).equals(local.getWorldLocation())) ? out : Collections.emptyList());
	}
}
