package com.example.nexatron.tasks.general;


import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.BankUtil;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Static;
import com.example.nexatron.taskformat.Task;
import com.example.nexatron.taskformat.TaskDescriptor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	priority = -1,
	name = "Drop vial"
)
public class DropVial extends Task
{

	Client client = Static.getClient();

	public boolean run()
	{
		if (BankUtil.isOpen())
		{
			return false;
		}
		Widget vial = InventoryUtil.getFirst("Vial");
		if (vial != null)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(vial, "Drop");
			return true;
		}
		return false;
	}

	public void print(String msg)
	{
		if (client.isClientThread())
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, "");
		}
	}

}
