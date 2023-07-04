package com.example.toagigatron.tasks.outside;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Dialog;
import com.example.Utility.Static;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.TileObject;

@TaskDescriptor(
	name = "Entering raid",
	priority = 80
)
public class EnterRaid extends StagedTask
{

	@Inject
	public EnterRaid(ToaManager toaManager)
	{
		super(toaManager, Stage.OUTSIDE);
	}

	public boolean execute()
	{
		if (!toaManager.readyToEnterRaid())
		{
			return false;
		}
		if (toaManager.onBreak())
		{
			return false;
		}
		if (toaManager.needsBreak() && !toaManager.allowedToBreak)
		{
			toaManager.allowedToBreak = true;
			return false;
		}
		TileObject entry = TileObjects.search().withId(ToaConstants.BARRIER_ENTER_RAID).first().orElse(null);
		if (entry == null || Static.getClient().getVarbitValue(14345) != 1)
		{
			return false;
		}
//		if (Dialog.canContinue())
//		{
//			Dialog.continueSpace();
//			return true;
//		}
//		else if (Dialog.isOpen())
//		{
//			Dialog.type();
//			return true;
//		}
//		if (Dialog.canContinueTOAResign())
//		{
//			MousePackets.queueClickPacket();
//		}
		MousePackets.queueClickPacket();
		ObjectPackets.queueObjectAction(entry, false, "Enter");
		toaManager.print("Entering raid");
		return true;
	}
}
