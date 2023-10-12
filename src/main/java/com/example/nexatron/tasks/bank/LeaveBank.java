package com.example.nexatron.tasks.bank;


import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Movement;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Leave bank",
	priority = Integer.MAX_VALUE - 50,
	blocking = true
)
public class LeaveBank extends StagedTask
{
	public static final WorldPoint BANK_TILE = new WorldPoint(2900, 5203, 0);

	@Inject
	public LeaveBank(NexManager nexManager)
	{
		super(nexManager, Stage.BANK);
	}

	public boolean execute()
	{
		if (!nexManager.shouldKc()
			|| !nexManager.socket.readyToStart
			|| nexManager.kcArea.bankDoor == null
			|| !InventoryUtil.isFull())
		{
			return false;
		}
		Widget leaveBank = Widgets.search().withTextContains("Are you sure you want to leave").hiddenState(false).first().orElse(null);
		if (leaveBank != null)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueResumePause(14352385, 1);
			nexManager.print("Leaving bank");
			incrementActionCount();
			return true;
		}
		if (!nexManager.getPlayerPoint().equals(BANK_TILE))
		{
			if (client.getLocalPlayer().getAnimation() == -1)
			{
				nexManager.print("Walking to door");
				Movement.walk(BANK_TILE);
				incrementActionCount();
			}
		}
		else
		{
			nexManager.print("Opening door");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(nexManager.nex.bankDoor, false, "Open");
			incrementActionCount();
		}
		return true;
	}

}
