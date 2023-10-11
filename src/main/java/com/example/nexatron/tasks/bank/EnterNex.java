package com.example.nexatron.tasks.bank;

import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Dialog;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Enter Nex",
	priority = Integer.MAX_VALUE - 50,
	blocking = true
)
public class EnterNex extends StagedTask
{
	@Inject
	Consumable consumable;
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public EnterNex(NexManager nexManager)
	{
		super(nexManager, Stage.BANK);
	}

	public boolean execute()
	{
		if (!nexManager.socket.readyToStart
			|| !nexManager.socket.otherReadyToStart
			|| !nexManager.isPrePotted()
			|| nexManager.nexBank.barrier == null)
		{
			return false;
		}
		if (gameTickManager.isTickWaiting())
		{
			return true;
		}
		Widget createInstance = Widgets.search().withTextContains("Start a private fight").hiddenState(false).first().orElse(null);
		if (nexManager.socket.isMaster)
		{
			if (createInstance != null)
			{
				MousePackets.queueClickPacket();
				WidgetPackets.queueResumePause(14352385, 1);
				nexManager.print("Creating instance");
			}
			else
			{
				nexManager.print("Interacting with barrier");
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(nexManager.nexBank.barrier, false, "Pass (private)");
			}
			return true;
		}
		else if (nexManager.socket.isSlave())
		{
			Widget join = Widgets.search().withTextContains("Join a private fight").hiddenState(false).first().orElse(null);
			Widget nameInstance = Widgets.search().withTextContains("Whose fight would you like").hiddenState(false).first().orElse(null);
			if (nameInstance != null)
			{
				if (!nexManager.socket.otherIsInside)
				{
					nexManager.print("Waiting for master to enter");
					return true;
				}
				nexManager.print("Entering instance of " + nexManager.socket.otherName);
				Dialog.type(nexManager.socket.otherName, true);
				gameTickManager.setTickWait(5);
				// Enter instance
			}
			else if (join != null)
			{
				MousePackets.queueClickPacket();
				WidgetPackets.queueResumePause(14352385, 2);
				gameTickManager.setTickWait(2);
				nexManager.print("Attempting to join instance");
			}
			else if(nexManager.socket.otherIsInside)
			{
				nexManager.print("Interacting with barrier");
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(nexManager.nexBank.barrier, false, "Pass (private)");
			}
			return true;
		}
		return false;
	}
}