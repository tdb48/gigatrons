package com.example.toagigatron.tasks.wardens.wardensp3;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Movement;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.GameObject;

@TaskDescriptor(
	name = "Leave boss",
	blocking = true
)
public class LeaveBoss extends StagedTask
{
	@Inject
	public LeaveBoss(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P3);
	}

	public boolean execute()
	{
		GameObject exit = ObjectUtil.getNearestGameObject(ToaConstants.WARDENS_EXIT);
		if (exit != null)
		{
			if (client.getTickCount() % 3 == 0)
			{
				toaManager.print("Entering chest room");
			}
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(exit, false, "Use");
			return true;
		}
		return false;
	}
}
