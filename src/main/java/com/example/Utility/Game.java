package com.example.Utility;

import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import net.runelite.api.Client;
import net.runelite.api.VarClientInt;
import net.runelite.api.widgets.Widget;

public class Game {

    public static void logout(){
        Client client = Static.getClient();
        if (client.getVarcIntValue(VarClientInt.INVENTORY_TAB) != 10)
        {
            client.runScript(915, 10);
        }
        Widget logoutButton = client.getWidget(182, 8);
        Widget logoutDoorButton = client.getWidget(69, 23);
        if(logoutButton != null){
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(logoutButton, "Logout");
        }
        else if(logoutDoorButton != null){
            MousePackets.queueClickPacket();
            WidgetPackets.queueWidgetAction(logoutDoorButton, "Logout");
        }
    }
}
