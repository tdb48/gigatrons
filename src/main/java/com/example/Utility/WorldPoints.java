package com.example.Utility;

import net.runelite.api.coords.WorldPoint;

public class WorldPoints {

    public static boolean isDiagonalOf(WorldPoint wp, WorldPoint wp2)
    {
        int xDifference = Math.abs(wp.getX() - wp2.getX());
        int yDifference = Math.abs(wp.getY() - wp2.getY());
        return yDifference == xDifference;
    }
}
