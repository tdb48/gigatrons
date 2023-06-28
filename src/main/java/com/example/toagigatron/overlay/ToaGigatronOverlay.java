package com.example.toagigatron.overlay;

import com.example.toagigatron.ToaGigatronConfig;
import com.example.toagigatron.ToaGigatronPlugin;
import com.google.common.base.Strings;
import net.runelite.api.Point;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.*;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class ToaGigatronOverlay extends Overlay
{

	Client client;

	ToaGigatronPlugin plugin;

	ToaGigatronConfig config;

	Color color = new Color(199, 2, 61, 75);
	Color color2 = new Color(26, 151, 200, 175);
	Stroke stroke = new BasicStroke((float) 2);
	Stroke stroke2 = new BasicStroke((float) 1);

	@Inject
	public ToaGigatronOverlay(Client client, ToaGigatronPlugin plugin, ToaGigatronConfig config)
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
//		if (plugin.toaManager.kephri.firstKephriPuzzle != null)
//		{
//			LocalPoint lp = LocalPoint.fromWorld(client, plugin.toaManager.kephri.firstKephriPuzzle.roomArea.getCenter());
//			assert lp != null;
//			Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 7);
//			if (poly != null)
//			{
//				if (plugin.toaManager.kephri.firstKephriPuzzle.solved)
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly, Color.GREEN, new Color(0, 0, 0, 5), stroke);
//				}
//				else
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly, Color.RED, new Color(0, 0, 0, 5), stroke);
//				}
//			}
//		}
//
//		if (plugin.toaManager.kephri.secondKephriPuzzle != null)
//		{
//			LocalPoint lp2 = LocalPoint.fromWorld(client, plugin.toaManager.kephri.secondKephriPuzzle.roomArea.getCenter());
//			assert lp2 != null;
//			Polygon poly2 = Perspective.getCanvasTileAreaPoly(client, lp2, 7);
//			if (poly2 != null)
//			{
//				if (plugin.toaManager.kephri.secondKephriPuzzle.solved)
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly2, Color.GREEN, new Color(0, 0, 0, 5), stroke);
//				}
//				else
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly2, Color.RED, new Color(0, 0, 0, 5), stroke);
//				}
//			}
//		}
//		if (plugin.toaManager.kephri.finalKephriPuzzle != null)
//		{
//			LocalPoint lp3 = LocalPoint.fromWorld(client, plugin.toaManager.kephri.finalKephriPuzzle.roomArea.getCenter());
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 7);
//			if (poly3 != null)
//			{
//				if (plugin.toaManager.kephri.finalKephriPuzzle.solved)
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly3, Color.GREEN, new Color(0, 0, 0, 5), stroke);
//				}
//				else
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly3, Color.RED, new Color(0, 0, 0, 5), stroke);
//				}
//			}
//		}
//
////		if(plugin.toaManager.zebak.allRoomTiles != null){
////			for(WorldPoint wp : plugin.toaManager.zebak.allRoomTiles){
////				LocalPoint lp = LocalPoint.fromWorld(client, wp);
////				assert lp != null;
////				Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 1);
////				if (poly != null)
////				{
////					OverlayUtil.renderPolygon(graphics2D, poly, Color.YELLOW, new Color(255, 255, 0, 10), stroke);
////
////				}
////			}
////		}
////
////		if(plugin.toaManager.zebak.allWalkableRoomTiles != null){
////			for(WorldPoint wp : plugin.toaManager.zebak.allWalkableRoomTiles){
////				LocalPoint lp = LocalPoint.fromWorld(client, wp);
////				assert lp != null;
////				Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 1);
////				if (poly != null)
////				{
////					OverlayUtil.renderPolygon(graphics2D, poly, Color.GREEN, new Color(0, 255, 0, 10), stroke);
////
////				}
////			}
////		}
////		if(plugin.toaManager.zebak.allWalkableRoomTilesIncludingChompZone != null){
////			for(WorldPoint wp : plugin.toaManager.zebak.allWalkableRoomTilesIncludingChompZone){
////				LocalPoint lp = LocalPoint.fromWorld(client, wp);
////				assert lp != null;
////				Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 1);
////				if (poly != null)
////				{
////					OverlayUtil.renderPolygon(graphics2D, poly, Color.RED, new Color(255, 0, 0, 10), stroke);
////				}
////			}
////		}
//
//		if (plugin.toaManager.zebak.northEastZebakPuzzle != null)
//		{
//			LocalPoint lp3 = LocalPoint.fromWorld(client, plugin.toaManager.zebak.northEastZebakPuzzle.roomArea.getCenter());
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 7);
//			if (poly3 != null)
//			{
//				if (plugin.toaManager.zebak.northEastZebakPuzzle.active)
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly3, Color.GREEN, new Color(0, 0, 0, 5), stroke);
//				}
//				else
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly3, Color.RED, new Color(0, 0, 0, 5), stroke);
//				}
//			}
//		}
//
//		if (plugin.toaManager.zebak.northWestZebakPuzzle != null)
//		{
//			LocalPoint lp3 = LocalPoint.fromWorld(client, plugin.toaManager.zebak.northWestZebakPuzzle.roomArea.getCenter());
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 7);
//			if (poly3 != null)
//			{
//				if (plugin.toaManager.zebak.northWestZebakPuzzle.active)
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly3, Color.GREEN, new Color(0, 0, 0, 5), stroke);
//				}
//				else
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly3, Color.RED, new Color(0, 0, 0, 5), stroke);
//				}
//			}
//		}
//
//		if (plugin.toaManager.zebak.southEastZebakPuzzle != null)
//		{
//			LocalPoint lp3 = LocalPoint.fromWorld(client, plugin.toaManager.zebak.southEastZebakPuzzle.roomArea.getCenter());
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 7);
//			if (poly3 != null)
//			{
//				if (plugin.toaManager.zebak.southEastZebakPuzzle.active)
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly3, Color.GREEN, new Color(0, 0, 0, 5), stroke);
//				}
//				else
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly3, Color.RED, new Color(0, 0, 0, 5), stroke);
//				}
//			}
//		}
//
//		if (plugin.toaManager.zebak.southWestZebakPuzzle != null)
//		{
//			LocalPoint lp3 = LocalPoint.fromWorld(client, plugin.toaManager.zebak.southWestZebakPuzzle.roomArea.getCenter());
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 7);
//			if (poly3 != null)
//			{
//				if (plugin.toaManager.zebak.southWestZebakPuzzle.active)
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly3, Color.GREEN, new Color(0, 0, 0, 5), stroke);
//				}
//				else
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly3, Color.RED, new Color(0, 0, 0, 5), stroke);
//				}
//			}
//		}
//
//		if (plugin.toaManager.zebak.currentZebakPuzzle != null)
//		{
//			LocalPoint lp3 = LocalPoint.fromWorld(client, plugin.toaManager.zebak.currentZebakPuzzle.roomArea.getCenter());
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 7);
//			if (poly3 != null)
//			{
//				if (plugin.toaManager.zebak.currentZebakPuzzle.active)
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly3, Color.PINK, new Color(0, 0, 0, 5), stroke);
//				}
//				else
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly3, Color.RED, new Color(0, 0, 0, 5), stroke);
//				}
//			}
//		}
//
//		if (plugin.toaManager.zebak.currentZebakPuzzle != null)
//		{
//			LocalPoint lp3 = LocalPoint.fromWorld(client, plugin.toaManager.zebak.currentZebakPuzzle.prePathTile);
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTilePoly(client, lp3);
//			if (poly3 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly3, Color.PINK, new Color(0, 0, 0, 5), stroke);
//			}
//		}
////		if (plugin.toaManager.kephri.kephriRoom != null)
////		{
////			LocalPoint lp3 = LocalPoint.fromWorld(client, plugin.toaManager.kephri.kephriRoom.getCenter());
////			assert lp3 != null;
////			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 17);
////			if (poly3 != null)
////			{
////				OverlayUtil.renderPolygon(graphics2D, poly3, Color.GREEN, new Color(0, 0, 0, 5), stroke);
////			}
////		}
//
//		drawPoints(plugin.toaManager.baba.explosionTiles, graphics2D);
//		if (plugin.toaManager.wardens12.warden != null)
//		{
//			drawWorldArea(plugin.toaManager.wardens12.warden.getWorldArea().getCenter(), graphics2D, Color.BLUE, 13);
//		}
//		drawPoints(plugin.toaManager.wardens12.windmillTiles, graphics2D, Color.RED);
//		drawPoints(plugin.toaManager.wardens12.prisonTiles, graphics2D, Color.RED);
//		drawPoints(plugin.toaManager.wardens3.enrageArea, graphics2D, Color.ORANGE);
//		drawPoints(plugin.toaManager.baba.sarcophagusProjectilesTiles, graphics2D, Color.green);
//
//		if (plugin.toaManager.getStage() == Stage.WARDENS_P2)
//		{
//			drawPoint(plugin.toaManager.wardens12.safeTile, graphics2D, Color.CYAN);
//			drawPoint(plugin.toaManager.wardens12.dodgeTile, graphics2D, Color.BLUE);
//		}
////		drawPoints(plugin.toaManager.wardens12.tilesInWardenRange, graphics2D);
////		drawPoints(plugin.toaManager.zebak.poisonWorldPoints, graphics2D);
////		drawPoints(plugin.toaManager.zebak.singleWaves, graphics2D, Color.CYAN);
////		drawPoints(plugin.toaManager.zebak.bloods, graphics2D, Color.RED);
////		drawLocalPoints(plugin.toaManager.zebak.poisonTiles, graphics2D, new Color(Color.green.getRed(), Color.green.getGreen(), Color.green.getBlue(), 50));
////		drawLocalPoints(plugin.toaManager.zebak.rockTiles, graphics2D, Color.BLUE);
////		drawLocalPoints(plugin.toaManager.zebak.staticJugs, graphics2D, Color.RED);
////		drawLocalPoints(plugin.toaManager.zebak.rollingJugs, graphics2D, Color.RED);
////		drawLocalPoints(plugin.toaManager.zebak.safeRockTiles, graphics2D, Color.CYAN);
//		drawNPCPoints(plugin.toaManager.zebak.wavesOne, graphics2D, Color.CYAN);
//		drawNPCPoints(plugin.toaManager.zebak.wavesTwo, graphics2D, Color.BLUE);
//		drawNPCPoints(plugin.toaManager.zebak.wavesThree, graphics2D, Color.GREEN);
//		if (plugin.toaManager.akkha != null)
//		{
//			if (plugin.toaManager.akkha.safeQuadrant != null)
//			{
//				if (plugin.toaManager.akkha.safeQuadrant.centerTile != null)
//				{
//					Color c = Color.GREEN;
//					LocalPoint lp = LocalPoint.fromWorld(client, plugin.toaManager.akkha.safeQuadrant.centerTile);
//					assert lp != null;
//					Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 1);
//					if (poly != null)
//					{
//						OverlayUtil.renderPolygon(graphics2D, poly, c, new Color(c.getRed(), c.getGreen(), c.getBlue(), 125), stroke);
//					}
//				}
//				if (plugin.toaManager.akkha.safeQuadrant.memoryTile != null)
//				{
//					Color c = Color.BLUE;
//					LocalPoint lp = LocalPoint.fromWorld(client, plugin.toaManager.akkha.safeQuadrant.memoryTile);
//					assert lp != null;
//					Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 1);
//					if (poly != null)
//					{
//						OverlayUtil.renderPolygon(graphics2D, poly, c, new Color(c.getRed(), c.getGreen(), c.getBlue(), 125), stroke);
//					}
//				}
//			}
//		}
//
//
//		if (plugin.toaManager.wardens12.wardenRoom != null)
//		{
//			LocalPoint lp = LocalPoint.fromWorld(client, plugin.toaManager.wardens12.wardenRoom.getCenter());
//			assert lp != null;
//			Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 24);
//			if (poly != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly, Color.GREEN, new Color(0, 0, 0, 5), stroke);
//			}
//		}
//		if (plugin.toaManager.wardens12.obeliskTile != null)
//		{
//			Color c = Color.BLUE;
//			LocalPoint lp = LocalPoint.fromWorld(client, plugin.toaManager.wardens12.obeliskTile);
//			assert lp != null;
//			Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 1);
//			if (poly != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly, c, new Color(c.getRed(), c.getGreen(), c.getBlue(), 125), stroke);
//			}
//		}
//		if (plugin.toaManager.wardens12.obeliskArea != null)
//		{
//			Color c = Color.MAGENTA;
//			LocalPoint lp = LocalPoint.fromWorld(client, plugin.toaManager.wardens12.obeliskArea.getCenter());
//			assert lp != null;
//			Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 3);
//			if (poly != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly, c, new Color(c.getRed(), c.getGreen(), c.getBlue(), 125), stroke);
//			}
//		}
//		if (plugin.toaManager.wardens12.blockTile != null)
//		{
//			Color c = Color.YELLOW;
//			LocalPoint lp = LocalPoint.fromWorld(client, plugin.toaManager.wardens12.blockTile);
//			assert lp != null;
//			Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, 1);
//			if (poly != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly, c, new Color(c.getRed(), c.getGreen(), c.getBlue(), 125), stroke);
//			}
//		}
//
//
//		if (plugin.toaManager.zebak.path != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.zebak.path)
//			{
//				drawTile(graphics2D, wp, Color.MAGENTA, 1, 150, 100);
//			}
//		}
//
//		if (plugin.toaManager.zebak.findTheGap(plugin.toaManager.zebak.wavesOne) != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.zebak.findTheGap(plugin.toaManager.zebak.wavesOne).toWorldPointList())
//			{
//				drawTile(graphics2D, wp, Color.RED, 1, 255, 10);
//			}
//		}
//		if (plugin.toaManager.zebak.findTheGap(plugin.toaManager.zebak.wavesTwo) != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.zebak.findTheGap(plugin.toaManager.zebak.wavesTwo).toWorldPointList())
//			{
//				drawTile(graphics2D, wp, Color.RED, 1, 255, 10);
//			}
//		}
//		if (plugin.toaManager.zebak.findTheGap(plugin.toaManager.zebak.wavesThree) != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.zebak.findTheGap(plugin.toaManager.zebak.wavesThree).toWorldPointList())
//			{
//				drawTile(graphics2D, wp, Color.RED, 1, 255, 10);
//			}
//		}
//		if (plugin.toaManager.zebak.wavesOneSafe != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.zebak.wavesOneSafe.toWorldPointList())
//			{
//				drawTile(graphics2D, wp, Color.pink, 1, 255, 10);
//			}
//		}
//
//		if (plugin.toaManager.zebak.wavesTwoSafe != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.zebak.wavesTwoSafe.toWorldPointList())
//			{
//				drawTile(graphics2D, wp, Color.pink, 1, 255, 10);
//			}
//		}
//
//		if (plugin.toaManager.zebak.wavesThreeSafe != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.zebak.wavesThreeSafe.toWorldPointList())
//			{
//				drawTile(graphics2D, wp, Color.pink, 1, 255, 10);
//			}
//		}
//
//		if (plugin.toaManager.zebak.hittableJug != null)
//		{
//			LocalPoint lp3 = plugin.toaManager.zebak.hittableJug.jugTile;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
//			if (poly3 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly3, Color.green, new Color(0, 0, 0, 100), stroke);
//			}
//		}
//
//		if (!plugin.toaManager.baba.targetPillarTiles.isEmpty())
//		{
//			for (WorldPoint wp : plugin.toaManager.baba.targetPillarTiles)
//			{
//				LocalPoint lp3 = LocalPoint.fromWorld(client, wp);
//				assert lp3 != null;
//				Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
//				if (poly3 != null)
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly3, Color.ORANGE, new Color(0, 0, 0, 5), stroke2);
//				}
//			}
//		}
//
//		if (plugin.toaManager.kephri.currentRow != null)
//		{
//			WorldPoint ref = plugin.toaManager.kephri.currentRow.startPoint;
//			LocalPoint lp3 = LocalPoint.fromWorld(client, ref);
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
//			if (poly3 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly3, Color.ORANGE, new Color(0, 0, 0, 5), stroke2);
//			}
//		}
//
//		if (plugin.toaManager.kephri.currentRow != null)
//		{
//			WorldPoint ref = plugin.toaManager.kephri.currentRow.endPoint;
//			LocalPoint lp3 = LocalPoint.fromWorld(client, ref);
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
//			if (poly3 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly3, Color.ORANGE, new Color(0, 0, 0, 5), stroke2);
//			}
//		}
//		if (plugin.toaManager.kephri.currentRow != null)
//		{
//			WorldPoint ref = plugin.toaManager.kephri.currentRow.middlePoint;
//			LocalPoint lp3 = LocalPoint.fromWorld(client, ref);
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
//			if (poly3 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly3, Color.GREEN, new Color(0, 0, 0, 5), stroke2);
//			}
//		}
//		if (plugin.toaManager.kephri.currentRow != null)
//		{
//			WorldPoint ref = plugin.toaManager.kephri.currentRow.prePathPoint;
//			LocalPoint lp3 = LocalPoint.fromWorld(client, ref);
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
//			if (poly3 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly3, Color.YELLOW, new Color(0, 0, 0, 5), stroke2);
//			}
//		}
//
//		if (plugin.toaManager.kephri.previousRow != null)
//		{
//			WorldPoint ref = plugin.toaManager.kephri.previousRow.startPoint;
//			LocalPoint lp3 = LocalPoint.fromWorld(client, ref);
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
//			if (poly3 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly3, Color.RED, new Color(0, 0, 0, 5), stroke2);
//			}
//		}
//
//		if (plugin.toaManager.kephri.previousRow != null)
//		{
//			WorldPoint ref = plugin.toaManager.kephri.previousRow.endPoint;
//			LocalPoint lp3 = LocalPoint.fromWorld(client, ref);
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
//			if (poly3 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly3, Color.RED, new Color(0, 0, 0, 5), stroke2);
//			}
//		}
//		if (plugin.toaManager.kephri.previousRow != null)
//		{
//			WorldPoint ref = plugin.toaManager.kephri.previousRow.middlePoint;
//			LocalPoint lp3 = LocalPoint.fromWorld(client, ref);
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
//			if (poly3 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly3, Color.RED, new Color(0, 0, 0, 5), stroke2);
//			}
//		}
//		if (plugin.toaManager.kephri.previousRow != null)
//		{
//			WorldPoint ref = plugin.toaManager.kephri.previousRow.prePathPoint;
//			LocalPoint lp3 = LocalPoint.fromWorld(client, ref);
//			assert lp3 != null;
//			Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
//			if (poly3 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly3, Color.RED, new Color(0, 0, 0, 5), stroke2);
//			}
//		}
//
////		if (plugin.toaManager.kephri.kephriDungRows != null)
////		{
////			for (KephriDungRow row : plugin.toaManager.kephri.kephriDungRows)
////			{
////				WorldPoint ref = row.startPoint;
////				WorldPoint ref2 = row.endPoint;
////				WorldPoint ref3 = row.prePathPoint;
////				WorldPoint ref4 = row.middlePoint;
////				LocalPoint lp3 = LocalPoint.fromWorld(client, ref);
////				LocalPoint lp4 = LocalPoint.fromWorld(client, ref2);
////				LocalPoint lp5 = LocalPoint.fromWorld(client, ref3);
////				LocalPoint lp6 = LocalPoint.fromWorld(client, ref4);
////				assert lp3 != null;
////				Polygon poly3 = Perspective.getCanvasTileAreaPoly(client, lp3, 1);
////				if (poly3 != null)
////				{
////					OverlayUtil.renderPolygon(graphics2D, poly3, Color.RED, new Color(0, 0, 0, 5), stroke2);
////				}
////				Polygon poly4 = Perspective.getCanvasTileAreaPoly(client, lp4, 1);
////				if (poly4 != null)
////				{
////					OverlayUtil.renderPolygon(graphics2D, poly4, Color.YELLOW, new Color(0, 0, 0, 5), stroke2);
////				}
////				Polygon poly6 = Perspective.getCanvasTileAreaPoly(client, lp5, 1);
////				if (poly6 != null)
////				{
////					OverlayUtil.renderPolygon(graphics2D, poly6, Color.GREEN, new Color(0, 0, 0, 5), stroke2);
////				}
////				Polygon poly7 = Perspective.getCanvasTileAreaPoly(client, lp6, 1);
////				if (poly7 != null)
////				{
////					OverlayUtil.renderPolygon(graphics2D, poly7, Color.PINK, new Color(0, 0, 0, 5), stroke2);
////				}
////
////			}
////		}
//
//		if (plugin.toaManager.baba.specialPath != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.baba.specialPath)
//			{
//				drawTile(graphics2D, wp, Color.RED, 1, 255, 10);
//			}
//		}
//
//		if (plugin.toaManager.baba.bananaTiles != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.baba.bananaTiles)
//			{
//				drawTile(graphics2D, wp, Color.YELLOW, 1, 100, 100);
//			}
//		}
//
//		if (plugin.toaManager.baba.badTiles != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.baba.badTiles)
//			{
//				drawTile(graphics2D, wp, Color.RED, 2, 0, 50);
//			}
//		}
//
//		if (plugin.toaManager.baba.blockTiles != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.baba.blockTiles)
//			{
//				drawTile(graphics2D, wp, Color.ORANGE, 1, 0, 50);
//			}
//		}
//
//
////		if (plugin.toaManager.baba.babaBossRowSafe != null)
////		{
////			for (WorldPoint wp : plugin.toaManager.baba.babaBossRowSafe.toWorldPointList())
////			{
////				drawTile(graphics2D, wp, Color.pink, 1, 255, 10);
////			}
////		}
//
//		if (plugin.toaManager.baba.attackPath != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.baba.attackPath)
//			{
//				drawTile(graphics2D, wp, Color.MAGENTA, 1, 50, 50);
//			}
//		}
//
//		if (plugin.toaManager.baba.safeTile != null)
//		{
//			WorldPoint ref2 = plugin.toaManager.baba.safeTile;
//			LocalPoint lp2 = LocalPoint.fromWorld(client, ref2);
//			assert lp2 != null;
//			Polygon poly2 = Perspective.getCanvasTileAreaPoly(client, lp2, 1);
//			if (poly2 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly2, Color.CYAN, new Color(0, 0, 0, 5), stroke);
//			}
//		}
//
////		if (plugin.toaManager.baba.babaBossRoom != null)
////		{
////			for (WorldPoint wp : plugin.toaManager.baba.babaBossRoom)
////			{
////				drawTile(graphics2D, wp, Color.orange, 1, 255, 10);
////			}
////		}
//
//		if (!plugin.toaManager.baba.shockwaveTiles.isEmpty())
//		{
//			for (WorldPoint wp : plugin.toaManager.baba.shockwaveTiles)
//			{
//				drawTile(graphics2D, wp, Color.orange, 1, 255, 10);
//			}
//		}
//
//		if (!plugin.toaManager.baba.rockfallTiles.isEmpty())
//		{
//			for (WorldPoint wp : plugin.toaManager.baba.rockfallTiles)
//			{
//				drawTile(graphics2D, wp, Color.BLUE, 1, 255, 10);
//			}
//		}
//
//		if (plugin.toaManager.akkha.orbTiles != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.akkha.orbTiles)
//			{
//				drawTile(graphics2D, wp, Color.orange, 1, 255, 100);
//			}
//		}
//
//		if (plugin.toaManager.akkha.akkhaOrbTiles != null)
//		{
//			for (WorldPoint wp : plugin.toaManager.akkha.akkhaOrbTiles)
//			{
//				drawTile(graphics2D, wp, Color.GREEN, 1, 255, 10);
//			}
//		}
//
//		if (plugin.toaManager.akkha.nextQuadrant != null)
//		{
//			WorldPoint ref2 = plugin.toaManager.akkha.nextQuadrant.centerTile;
//			LocalPoint lp2 = LocalPoint.fromWorld(client, ref2);
//			assert lp2 != null;
//			Polygon poly2 = Perspective.getCanvasTileAreaPoly(client, lp2, 1);
//			if (poly2 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly2, Color.RED, new Color(0, 0, 0, 5), stroke);
//			}
//		}
//
//		if (plugin.toaManager.akkha.safeQuadrant != null)
//		{
//			WorldPoint ref2 = plugin.toaManager.akkha.safeQuadrant.memoryTile;
//			LocalPoint lp2 = LocalPoint.fromWorld(client, ref2);
//			assert lp2 != null;
//			Polygon poly2 = Perspective.getCanvasTileAreaPoly(client, lp2, 1);
//			if (poly2 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly2, Color.CYAN, new Color(0, 0, 0, 5), stroke);
//			}
//		}
//
//		if(plugin.toaManager.kephri.dungGraphicTick > 0){
//			LocalPoint lp2 = client.getLocalPlayer().getLocalLocation();
//			assert lp2 != null;
//			Polygon poly2 = Perspective.getCanvasTileAreaPoly(client, lp2, 1);
//			if (poly2 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly2, Color.CYAN, new Color(Color.CYAN.getRed(), Color.cyan.getGreen(), Color.cyan.getBlue(), 15), stroke);
//			}
//		}
//		if (!plugin.toaManager.akkha.memoryTiles.isEmpty())
//		{
//			WorldPoint ref2 = plugin.toaManager.akkha.memoryTiles.get(0);
//			LocalPoint lp2 = LocalPoint.fromWorld(client, ref2);
//			assert lp2 != null;
//			Polygon poly2 = Perspective.getCanvasTileAreaPoly(client, lp2, 1);
//			if (poly2 != null)
//			{
//				OverlayUtil.renderPolygon(graphics2D, poly2, Color.CYAN, new Color(0, 0, 0, 5), stroke);
//			}
//		}
//
//		if (plugin.toaManager.akkha.finalPhasePath != null && !plugin.toaManager.akkha.finalPhasePath.isEmpty())
//		{
//			for (WorldPoint wp : plugin.toaManager.akkha.finalPhasePath)
//			{
//				LocalPoint lp2 = LocalPoint.fromWorld(client, wp);
//				assert lp2 != null;
//				Polygon poly2 = Perspective.getCanvasTileAreaPoly(client, lp2, 1);
//				if (poly2 != null)
//				{
//					OverlayUtil.renderPolygon(graphics2D, poly2, Color.CYAN, new Color(0, 0, 0, 50), stroke);
//				}
//			}
//
//		}
//
////		if (plugin.toaManager.akkha.puzzle != null)
////		{
////			for (AkkhaPuzzleRoomTile roomTile : plugin.toaManager.akkha.puzzle.roomTiles)
////			{
////				WorldPoint wp = roomTile.getWorldPoint();
////				GameObject obj = roomTile.getObject();
////				Color c = Color.PINK;
////				if (obj != null && obj.getId() == 45456)
////				{
////					c = Color.MAGENTA;
////				}
////				if (obj != null && (obj.getId() == 45460 || obj.getId() == 45458))
////				{
////					c = Color.RED;
////				}//45460,45458
////				if (obj != null && (obj.getId() == 45464 || obj.getId() == 45462))
////				{
////					c = Color.ORANGE;
////				}//45464,45462
////				if (obj != null && obj.getId() == 45466)
////				{
////					c = Color.YELLOW;
////				}//45464,45462
////				if (obj != null && obj.getId() == 45455)
////				{
////					c = Color.GREEN;
////				}
////				if (c == Color.PINK)
////				{
//////					drawTile(graphics2D, wp, c, 1, 50, 0);
////				}
////				else
////				{
////					drawTile(graphics2D, wp, c, 1, 255, 255);
////				}
////			}
////		}
//
//		if (plugin.toaManager.akkha.puzzle != null)
//		{
//			if (plugin.toaManager.akkha.puzzle.solution != null && plugin.toaManager.akkha.puzzle.solution.mirrors != null)
//			{
//				for (WorldPoint wp : plugin.toaManager.akkha.puzzle.solution.wallsToMine)
//				{
//					drawTile(graphics2D, wp, Color.pink, 1, 255, 100);
//				}
//				for (GameObject wp : plugin.toaManager.akkha.puzzle.solution.getWrongMirrors())
//				{
//					drawTile(graphics2D, wp.getWorldLocation(), Color.RED, 1, 255, 100);
//				}
//				for (GameObject wp : plugin.toaManager.akkha.puzzle.solution.getCorrectMirrors())
//				{
//					drawTile(graphics2D, wp.getWorldLocation(), Color.GREEN, 1, 255, 100);
//				}
//				for (WorldPoint wp : plugin.toaManager.akkha.puzzle.solution.getPlaceMirrors())
//				{
//					drawTile(graphics2D, wp, Color.BLUE, 1, 255, 100);
//				}
//				if (plugin.toaManager.akkha.puzzle.solution.mineTile != null)
//				{
//					drawTile(graphics2D, plugin.toaManager.akkha.puzzle.solution.mineTile, Color.orange, 1, 255, 100);
//				}
//			}
//		}
//
//		if (plugin.toaManager.fullBlockTiles.size() > 0)
//		{
//			for (Map.Entry<WorldPoint, Boolean> entry : plugin.toaManager.fullBlockTiles.entrySet())
//			{
//				if (entry.getValue())
//				{
//					drawTile(graphics2D, entry.getKey(), Color.RED, "", stroke);
//				}
//				else
//				{
//					drawTile(graphics2D, entry.getKey(), Color.GREEN, "", stroke);
//				}
//
//
//			}
//		}
//
//		if (plugin.toaManager.wardens3.babaBombs.size() > 0)
//		{
//			for (Map.Entry<WorldPoint, Integer> entry : plugin.toaManager.wardens3.babaBombs.entrySet())
//			{
//				if (entry.getValue() > 0)
//				{
//					drawTile(graphics2D, entry.getKey(), Color.CYAN, String.valueOf(entry.getValue()), stroke);
//				}
//			}
//		}
//		if (plugin.toaManager.getStage() == Stage.WARDENS_P3 && plugin.toaManager.wardens3.lightning.size() > 0)
//		{
//			for (Map.Entry<WorldPoint, Integer> entry : plugin.toaManager.wardens3.lightning.entrySet())
//			{
//				if (entry.getValue() > 0)
//				{
//					drawTile(graphics2D, entry.getKey(), Color.RED, String.valueOf(entry.getValue()), stroke);
//				}
//				else
//				{
//					for (GraphicsObject obj : client.getGraphicsObjects())
//					{
//						if (obj.getId() == ToaConstants.NEW_LIGHTNING_GRAPHICS_OBJECT_ID)
//						{
//							WorldPoint wp = new WorldPoint(obj.getLocation().getX(), obj.getLocation().getY(), client.getPlane());
//							if (wp == entry.getKey())
//							{
//								obj.setFinished(true);
//							}
//						}
//					}
//				}
//			}
//		}
//		if (plugin.toaManager.wardens3.primarySafeTile != null)
//		{
//			drawTile(graphics2D, plugin.toaManager.wardens3.primarySafeTile, Color.GREEN, "P", stroke);
//		}
//		if (plugin.toaManager.wardens3.secondarySafeTile != null)
//		{
//			drawTile(graphics2D, plugin.toaManager.wardens3.secondarySafeTile, Color.CYAN, "S", stroke);
//			drawTile(graphics2D, plugin.toaManager.wardens3.secondarySafeTile.dy(1), Color.YELLOW, String.valueOf(plugin.toaManager.wardens3.tileFlipTickCounter), stroke);
//
//		}
//		if (plugin.toaManager.wardens3.nextPrimarySafeTile != null)
//		{
//			drawTile(graphics2D, plugin.toaManager.wardens3.nextPrimarySafeTile, Color.RED, "N P", stroke);
//		}
//		if (plugin.toaManager.wardens3.nextSecondarySafeTile != null)
//		{
//			drawTile(graphics2D, plugin.toaManager.wardens3.nextSecondarySafeTile, Color.BLUE, "N S", stroke);
//		}
//		Point kephriTextLoc = null;
//		if (plugin.toaManager.kephri.kephri != null)
//		{
//			kephriTextLoc = plugin.toaManager.kephri.kephri.getCanvasTextLocation(graphics2D, String.valueOf(plugin.toaManager.kephri.kephriTick), 80);
//		}
//		if (kephriTextLoc != null)
//		{
//			renderTextLocation(graphics2D, String.valueOf(plugin.toaManager.kephri.kephriTick), 14, Font.BOLD, Color.CYAN, kephriTextLoc);
//		}


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
