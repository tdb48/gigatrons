package com.example.nexatron.tasks.general;


import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.BankUtil;
import com.example.Utility.InventoryUtil;
import com.example.nexatron.taskformat.Task;
import com.example.nexatron.taskformat.TaskDescriptor;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Drop vial"
)
public class DropVial extends Task
{
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

}
