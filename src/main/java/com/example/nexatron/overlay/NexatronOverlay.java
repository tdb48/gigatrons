package com.example.nexatron.overlay;

import com.example.nexatron.NexatronConfig;
import com.example.nexatron.NexatronPlugin;
import com.example.nexatron.model.Reaver;
import com.google.common.base.Strings;
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
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

public class NexatronOverlay extends Overlay
{
	Client client;
	NexatronPlugin plugin;
	NexatronConfig config;
	ModelOutlineRenderer modelOutlineRenderer;
	Stroke stroke = new BasicStroke((float) 2);
	Stroke stroke2 = new BasicStroke((float) 1);

	@Inject
	public NexatronOverlay(Client client, NexatronPlugin plugin, NexatronConfig config, ModelOutlineRenderer modelOutlineRenderer)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.modelOutlineRenderer = modelOutlineRenderer;
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
			drawPoint(plugin.nexManager.nex.masterMainTile, graphics2D, Color.RED);
			drawPoint(plugin.nexManager.nex.masterStepUnderTile, graphics2D, Color.GREEN);
			drawPoint(plugin.nexManager.nex.masterDodgeTile, graphics2D, Color.BLUE);
			drawPoint(plugin.nexManager.nex.slaveDodgeTile, graphics2D, Color.BLUE);
			drawPoint(plugin.nexManager.nex.slaveStepUnderTile, graphics2D, Color.GREEN);
			drawPoint(plugin.nexManager.nex.slaveMainTile, graphics2D, Color.RED);
			drawPoints(plugin.nexManager.nex.sacrificeTiles, graphics2D, Color.GRAY);

			for (Map.Entry<NPC, Integer> entry : plugin.nexManager.nex.reavers.entrySet())
			{
				Point textLocation = entry.getKey().getCanvasTextLocation(graphics2D, String.valueOf(entry.getValue()), 0);
				renderTextLocation(graphics2D, String.valueOf(entry.getValue()), 14, Font.BOLD, Color.PINK, textLocation);
			}
		}

		///////////Reaver overlay\\\\\\\\\\\\
		if(config.showKcOverlay())
		{
			for(Reaver reaver : plugin.reaverManager.reavers.values())
			{
				if(reaver == null || reaver.getReaver() == null || !reaver.getReaver().getWorldLocation().isInScene(client))
				{
					continue;
				}
				//Location and npc index
				drawTile(graphics2D, reaver.getCurrentLocation(), Color.GREEN, 5, String.valueOf(reaver.getIndex()), stroke);

				//Hitpoints
				String text = "HP: " + reaver.getHitpoints();
				Point textLocation = Perspective.getCanvasTextLocation(client, graphics2D, reaver.getReaver().getLocalLocation(), text, 160);
				renderTextLocation(graphics2D, text, 15, Font.PLAIN, Color.GREEN, textLocation);

				//Distance to any center area tile
				int distance = reaver.getCurrentLocation().distanceTo(plugin.reaverManager.centralArea);
				String distanceText = "Dist: " + distance;
				Point distanceTextLocation = Perspective.getCanvasTextLocation(client, graphics2D, reaver.getReaver().getLocalLocation(), distanceText, 25);
				renderTextLocation(graphics2D, distanceText, 15, Font.PLAIN, Color.CYAN, distanceTextLocation);

				//Spawn locations
				drawTile(graphics2D, reaver.getSpawnLocation(), Color.BLUE, 50, String.valueOf(reaver.getIndex()), stroke);
			}
			for(WorldPoint wp : plugin.reaverManager.centralArea.toWorldPointList())
			{
				if(wp.isInScene(client))
				{
					drawTile(graphics2D, wp, Color.GREEN, 0, "", stroke2);
					//drawPoint(wp, graphics2D, Color.GREEN);
				}

			}
			for(WorldPoint wp : plugin.reaverManager.southWestArea.toWorldPointList())
			{
				if(wp.isInScene(client))
				{
					drawTile(graphics2D, wp, Color.GREEN, 0, "", stroke2);
					//drawPoint(wp, graphics2D, Color.GREEN);
				}
			}
			if(plugin.nexManager.reaverTest != null && plugin.nexManager.reaverTest.getWorldLocation().isInScene(client))
			{
				modelOutlineRenderer.drawOutline(plugin.nexManager.reaverTest, 2, Color.MAGENTA, 2);
				//int distance = plugin.nexManager.reaverTest.getWorldLocation().distanceTo(client.getLocalPlayer().getWorldLocation());
				//String text = "Dist: " + distance;
				//Point textLocation = Perspective.getCanvasTextLocation(client, graphics2D, plugin.nexManager.reaverTest.getLocalLocation(), text, 0);
				//renderTextLocation(graphics2D, text, 15, Font.PLAIN, Color.GREEN, textLocation);

			}
			/////////////////////////////////////////
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

	private void drawTile(Graphics2D graphics, WorldPoint point, Color color, int alpha, String label, Stroke borderStroke)
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
			OverlayUtil.renderPolygon(graphics, poly, color, new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha), borderStroke);
		}
		if (!Strings.isNullOrEmpty(label))
		{
			Point canvasTextLocation = Perspective.getCanvasTextLocation(client, graphics, lp, label, 0);
			if (canvasTextLocation != null)
			{
				graphics.setFont(new Font("Arial", Font.PLAIN, 13));
				OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, color);
			}
		}
	}

}
