package com.example.Utility;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.WidgetID;
import com.example.PacketUtils.WidgetInfoExtended;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Dialog {
    public static boolean isVisible(Widget widget){
        return WidgetUtility.isVisible(widget);
    }

    public static boolean canContinue()
    {
        return canContinueNPC() || canContinuePlayer() || canContinueDeath()
                || canSpriteContinue() || canSprite2Continue()
                || canContinue1() || canContinue2() || canLevelUpContinue();
    }


    public static boolean canLevelUpContinue()
    {

        return isVisible(EthanApiPlugin.getClient().getWidget(WidgetInfo.LEVEL_UP_LEVEL));
    }

    public static boolean canSpriteContinue()
    {

        return isVisible(EthanApiPlugin.getClient().getWidget(193, 0));
    }

    public static boolean canSprite2Continue()
    {

        return isVisible(EthanApiPlugin.getClient().getWidget(WidgetInfoExtended.DIALOG2_SPRITE_CONTINUE.getPackedId()));
    }


    public static boolean canContinue1()
    {
        return isVisible(EthanApiPlugin.getClient().getWidget(193, 3));
    }

    public static boolean canContinue2()
    {
        return isVisible(EthanApiPlugin.getClient().getWidget(633, 0));
    }

    public static boolean canContinueNPC()
    {
        return isVisible(EthanApiPlugin.getClient().getWidget(WidgetID.DIALOG_NPC_GROUP_ID, 4));
    }

    public static boolean canContinuePlayer()
    {

        return isVisible(EthanApiPlugin.getClient().getWidget(WidgetID.DIALOG_PLAYER_GROUP_ID, 3));
    }

    public static boolean canContinueDeath()
    {
        Widget widget = EthanApiPlugin.getClient().getWidget(663, 0);
        return isVisible(widget) && widget.getChild(2) != null && !isVisible(widget.getChild(2));
    }

    public static boolean isOpen()
    {
        return isVisible(EthanApiPlugin.getClient().getWidget(162, 559));
    }

    public static void continueSpace()
    {
        if (isOpen())
        {
            sendSpace();
        }
    }

    public static void sendSpace()
    {
        type((char) KeyEvent.VK_SPACE);
    }
    public static void type(char c)
    {
        Canvas canvas = EthanApiPlugin.getClient().getCanvas();
        long time = System.currentTimeMillis();
        int keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
        KeyEvent pressed = new KeyEvent(canvas, KeyEvent.KEY_PRESSED, time, 0, keyCode, c, KeyEvent.KEY_LOCATION_STANDARD);
        KeyEvent typed = new KeyEvent(canvas, KeyEvent.KEY_TYPED, time, 0, 0, c, KeyEvent.KEY_LOCATION_UNKNOWN);
        canvas.dispatchEvent(pressed);
        canvas.dispatchEvent(typed);
        sleep(10);
        KeyEvent released = new KeyEvent(
                canvas,
                KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                0,
                keyCode,
                c,
                KeyEvent.KEY_LOCATION_STANDARD
        );

        canvas.dispatchEvent(released);
    }

    public static boolean sleep(long ms)
    {
        if (EthanApiPlugin.getClient().isClientThread())
        {
            return false;
        }

        try
        {
            Thread.sleep(ms);
            return true;
        }
        catch (InterruptedException e)
        {

        }

        return false;
    }

}
