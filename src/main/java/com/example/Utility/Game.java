package com.example.Utility;

import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.VarClientInt;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;

public class Game
{

	static Client client = Static.getClient();

	public static boolean isWelcomeVisible()
	{
		Widget playButton = Static.getClient().getWidget(WidgetID.LOGIN_CLICK_TO_PLAY_GROUP_ID, 77);
		return playButton != null
			&& !playButton.isHidden()
			&& playButton.getText().equals("CLICK HERE TO PLAY");
	}

	public static void logout()
	{
		if (client.getVarcIntValue(VarClientInt.INVENTORY_TAB) != 10)
		{
			client.runScript(915, 10);
		}
		Widget logoutButton = client.getWidget(182, 8);
		Widget logoutDoorButton = client.getWidget(69, 25);
		if (logoutButton != null)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(logoutButton, "Logout");
		}
		else if (logoutDoorButton != null)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(logoutDoorButton, "Logout");
		}
	}

	public static boolean isIdle()
	{
		Player p = client.getLocalPlayer();
		return (p.getIdlePoseAnimation() == p.getPoseAnimation() && p.getAnimation() == -1) && p.getInteracting() == null;
	}
}
