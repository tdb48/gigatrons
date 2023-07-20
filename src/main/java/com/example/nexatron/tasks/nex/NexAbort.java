package com.example.nexatron.tasks.nex;


import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Nex teleport out",
	priority = Integer.MAX_VALUE - 1,
	blocking = true
)
public class NexAbort extends StagedTask
{
	@Inject
	public NexAbort(NexManager nexManager)
	{
		super(nexManager,
			Stage.NEX_DEAD,
			Stage.MINION_SMOKE,
			Stage.NEX_SMOKE,
			Stage.MINION_SHADOW,
			Stage.NEX_SHADOW,
			Stage.MINION_BLOOD,
			Stage.NEX_BLOOD,
			Stage.MINION_ICE,
			Stage.NEX_ICE);
	}

	public boolean execute()
	{
		if (nexManager.nex.altar == null
			|| !nexManager.nex.teleportOut)
		{
			return false;
		}
		MousePackets.queueClickPacket();
		ObjectPackets.queueObjectAction(nexManager.nex.altar, false, "Teleport");
		return true;
	}

}
