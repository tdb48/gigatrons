package com.example.Utility;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import java.util.Comparator;

@Slf4j
public class Movement
{

	public static boolean isRunEnabled() {
		return Static.getClient().getVarpValue(173) == 1;
	}
	public static void toggleRun() {
		Widget widget = Static.getClient().getWidget(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB);
		if (widget != null) {
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(widget, "Toggle Run");
		}

	}

	public static int getRunEnergy() {
		return Static.getClient().getEnergy() / 100;
	}
	public static void walk(WorldPoint worldPoint)
	{
		Client client = Static.getClient();
		Player local = client.getLocalPlayer();
		if (local != null)
		{
			WorldPoint walkPoint = worldPoint;
			LocalPoint walkPointLocal = LocalPoint.fromWorld(client, worldPoint.getX(), worldPoint.getY());
			if (walkPointLocal == null)
			{
				System.out.println("Null localpoint in walk method");
			}
			else
			{
				Tile destinationTile = Tiles.getAt(walkPointLocal);
				if (destinationTile == null)
				{
					Tile nearestInScene = Tiles.getAll().stream().min(Comparator.comparingInt((xx) ->
						xx.getWorldLocation().distanceTo(local.getWorldLocation()))).orElse(null);
					if (nearestInScene == null)
					{
						log.debug("Couldn't find nearest walkable tile");
						System.out.println("Couldn't find nearest walkable tile");
						return;
					}
					System.out.println("Returning nearest in scene instead -> " + nearestInScene.getWorldLocation());
					walkPoint = nearestInScene.getWorldLocation();
				}
				//Original devious method of walking replaced by click packet and movement packet
//                int sceneX = walkPoint.getX() - client.getBaseX();
//                int sceneY = walkPoint.getY() - client.getBaseY();
//                Point canv = Perspective.localToCanvas(client, LocalPoint.fromScene(sceneX, sceneY), client.getPlane());
//                int x = canv != null ? canv.getX() : -1;
//                int y = canv != null ? canv.getY() : -1;
//                client.interact(0, MenuAction.WALK.getId(), sceneX, sceneY, x, y);
				MousePackets.queueClickPacket();
				MovementPackets.queueMovement(walkPoint);
			}
		}
	}

	public static boolean isStaminaBoosted()
	{
		return Static.getClient().getVarbitValue(25) == 1;
	}
}
