package com.example.nexatron.tasks.nex;


import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Combat;
import com.example.Utility.Prayers;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.Player;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Nex Abort",
	priority = 100,
	blocking = true
)
public class NexAbort extends StagedTask
{
	@Inject
	public NexAbort(NexManager nexManager)
	{
		super(nexManager,
//			Stage.NEX_DEAD,
			Stage.MINION_SMOKE,
			Stage.NEX_SMOKE,
			Stage.MINION_SHADOW,
			Stage.NEX_SHADOW,
			Stage.MINION_BLOOD,
			Stage.NEX_BLOOD,
			Stage.MINION_ICE,
			Stage.NEX_ICE,
			Stage.NEX_START,
			Stage.NEX_ZAROS);
	}

	public boolean execute()
	{
		if (nexManager.nex.altar == null
			|| !shouldTeleport())
		{
			return false;
		}
		nexManager.enableRun(true);
		// To make sure we have an ancient item equipped when leaving!
		if (!nexManager.hasGearEquipped(nexManager.nex.setup.rangeNex()))
		{
			nexManager.print("Equipping range gear");
			nexManager.swap(nexManager.nex.setup.rangeNex());
		}
		MousePackets.queueClickPacket();
		ObjectPackets.queueObjectAction(nexManager.nex.altar, false, "Teleport");
		return true;
	}

	public boolean shouldTeleport()
	{
		Widget brew = Consumable.getBrew();
		Widget restore = Consumable.getRestore();
		Player otherPlayer = nexManager.socket.getOtherPlayer();
		return nexManager.nex.teleportOut
			|| (restore == null && Prayers.getPoints() <= 5)
			|| (brew == null && Combat.getCurrentHealth() <= 60)
			|| (otherPlayer == null
			&& !nexManager.getStage().equals(Stage.NEX_ZAROS)
			&& !nexManager.getStage().equals(Stage.NEX_DEAD)
			&& !nexManager.getStage().equals(Stage.NEX_START));
	}

}
