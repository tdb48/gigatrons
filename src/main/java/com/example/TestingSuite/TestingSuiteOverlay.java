package com.example.TestingSuite;


import com.google.common.base.Strings;
import com.google.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class TestingSuiteOverlay extends Overlay {

    TestingSuitePlugin plugin;

    Client client;

    TestingSuiteConfig config;

    Stroke stroke = new BasicStroke((float) 1);

    Stroke thickStroke = new BasicStroke((float) 5);

    @Inject
    TestingSuiteOverlay(Client client, TestingSuitePlugin plugin, TestingSuiteConfig config) {
        this.plugin = plugin;
        this.client = client;
        this.config = config;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        setPriority(OverlayPriority.HIGH);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        return null;
    }

    private void renderTextLocation(Graphics2D graphics, String txtString, int fontSize, int fontStyle, Color fontColor, Point canvasPoint) {
        graphics.setFont(new Font("Arial", fontStyle, fontSize));
        if (canvasPoint != null) {
            Point canvasCenterPoint = new Point(canvasPoint.getX(), canvasPoint.getY());
            Point canvasCenterPointShadow = new Point(canvasPoint.getX() + 1, canvasPoint.getY() + 1);
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
                graphics.setFont(new Font("Arial", 1, 15));
                OverlayUtil.renderTextLocation(graphics, canvasTextLocation, label, color);
            }
        }
    }
}
