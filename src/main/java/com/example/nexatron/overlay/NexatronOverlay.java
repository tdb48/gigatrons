package com.example.nexatron.overlay;

import com.example.nexatron.NexatronConfig;
import com.example.nexatron.NexatronPlugin;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class NexatronOverlay extends Overlay
{
	Client client;
	NexatronPlugin plugin;
	NexatronConfig config;
	Stroke stroke = new BasicStroke((float) 2);
	Stroke stroke2 = new BasicStroke((float) 1);

	@Inject
	public NexatronOverlay(Client client, NexatronPlugin plugin, NexatronConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.HIGH);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics2D)
	{
		if (!config.showOverlay())
		{
			return null;
		}
		drawPoint(plugin.nexManager.nex.centerPoint, graphics2D, Color.PINK);
		if (plugin.nexManager.nex.altar != null)
		{
			drawPoint(plugin.nexManager.nex.altar.getWorldLocation(), graphics2D, Color.RED);
		}
		return null;
	}

	protected void drawPointsMap(Map<WorldPoint, Integer> map, Graphics2D graphics2D)
	{
		if (!map.isEmpty())
		{
			for (Map.Entry<WorldPoint, Integer> entry : map.entrySet())
			{
				LocalPoint localPoint = LocalPoint.fromWorld(client, entry.getKey());
				assert localPoint != null;
				Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, localPoint, 1);
				if (poly3 != null)
				{
					OverlayUtil.renderPolygon(graphics2D, poly3, Color.GREEN, new Color(0, 0, 0, 5), stroke);
				}
			}
		}
	}

	protected void drawPoint(WorldPoint point, Graphics2D graphics2D, Color color)
	{
		ArrayList<WorldPoint> list = new ArrayList<>(Collections.singleton(point));
		drawPoints(list, graphics2D, color);
	}

	protected void drawPoints(ArrayList<WorldPoint> list, Graphics2D graphics2D, Color color)
	{
		if (!list.isEmpty())
		{
			for (WorldPoint wp : list)
			{
				if (wp != null)
				{
					LocalPoint lp3 = LocalPoint.fromWorld(client, wp);
					if (lp3 != null)
					{
						Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
						if (poly3 != null)
						{
							OverlayUtil.renderPolygon(graphics2D, poly3, color, new Color(0, 0, 0, 5), stroke);
						}
					}
				}
			}
		}
	}

	protected void drawNPCPoints(ArrayList<NPC> list, Graphics2D graphics2D, Color color)
	{
		if (!list.isEmpty())
		{
			for (NPC npc : list)
			{
				LocalPoint lp = npc.getLocalLocation();
				assert lp != null;
				Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp, 1);
				if (poly3 != null)
				{
					OverlayUtil.renderPolygon(graphics2D, poly3, color, new Color(0, 0, 0, 5), stroke);
				}
			}
		}
	}

	protected void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint)
	{
		graphics.setFont(new Font("Arial", fontStyle, fontSize));
		if (canvasPoint != null)
		{
			final Point canvasCenterPoint = new Point(canvasPoint.getX(), canvasPoint.getY());
			final Point canvasCenterPointShadow = new Point(canvasPoint.getX() + 1, canvasPoint.getY() + 1);

			OverlayUtil.renderTextLocation(graphics, canvasCenterPointShadow, txtString, Color.BLACK);
			OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, txtString, fontColor);
		}
	}


}
