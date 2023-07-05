package com.example.nexatron.overlay;

import com.example.nexatron.NexatronConfig;
import com.example.nexatron.NexatronPlugin;
import com.google.common.base.Strings;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Map;
import javax.annotation.Nullable;
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
		if (point != null)
		{
			LocalPoint lp3 = LocalPoint.fromWorld(client, point);
			assert lp3 != null;
			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
			if (poly3 != null)
			{
				OverlayUtil.renderPolygon(graphics2D, poly3, color, new Color(0, 0, 0, 5), stroke);
			}
		}
	}

	protected void drawPoints(ArrayList<WorldPoint> list, Graphics2D graphics2D)
	{
		if (!list.isEmpty())
		{
			for (WorldPoint wp : list)
			{
				LocalPoint lp3 = LocalPoint.fromWorld(client, wp);
				assert lp3 != null;
				Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
				if (poly3 != null)
				{
					OverlayUtil.renderPolygon(graphics2D, poly3, Color.GREEN, new Color(0, 0, 0, 5), stroke);
				}
			}
		}
	}

	protected void drawPoints(ArrayList<WorldPoint> list, Graphics2D graphics2D, Color color)
	{
		if (!list.isEmpty())
		{
			for (WorldPoint wp : list)
			{
				LocalPoint lp3 = LocalPoint.fromWorld(client, wp);
				assert lp3 != null;
				Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
				if (poly3 != null)
				{
					OverlayUtil.renderPolygon(graphics2D, poly3, color, new Color(0, 0, 0, 5), stroke);
				}
			}
		}
	}

	protected void drawWorldArea(WorldPoint center, Graphics2D graphics2D, Color color, int size)
	{
		if (center != null)
		{
			LocalPoint lp2 = LocalPoint.fromWorld(client, center);
			if (lp2 == null)
			{
				return;
			}
			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp2, size);
			if (poly3 != null)
			{
				OverlayUtil.renderPolygon(graphics2D, poly3, color, new Color(0, 0, 0, 5), stroke);
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

	protected void drawLocalPoints(ArrayList<LocalPoint> list, Graphics2D graphics2D, Color color)
	{
		if (!list.isEmpty())
		{
			for (LocalPoint lp : list)
			{

				assert lp != null;
				Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp, 1);
				if (poly3 != null)
				{
					OverlayUtil.renderPolygon(graphics2D, poly3, color, new Color(0, 0, 0, 5), stroke);
				}
			}
		}
	}

	protected void drawTile(Graphics2D graphics, WorldPoint point, Color color, int strokeWidth, int outlineAlpha, int fillAlpha)
	{
		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		if (point.distanceTo(playerLocation) >= 32)
		{
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, point);
		if (lp == null)
		{
			return;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly == null)
		{
			return;
		}

		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), outlineAlpha));
		graphics.setStroke(new BasicStroke(strokeWidth));
		graphics.draw(poly);
		graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), fillAlpha));
		graphics.fill(poly);
	}

	private void drawTile(Graphics2D graphics, WorldPoint point, Color color, @Nullable String label, Stroke borderStroke)
	{
		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

		if (point.distanceTo(playerLocation) >= 32)
		{
			return;
		}

		LocalPoint lp = LocalPoint.fromWorld(client, point);
		if (lp == null)
		{
			return;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, lp);
		if (poly != null)
		{
			OverlayUtil.renderPolygon(graphics, poly, color, new Color(color.getRed(), color.getGreen(), color.getBlue(), 150), borderStroke);
		}

		if (!Strings.isNullOrEmpty(label))
		{
			Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, lp, label, 0);
			if (canvasTextLocation != null)
			{
				graphics.setFont(new Font("Arial", 1, 15));
				OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, color);
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
