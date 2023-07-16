package com.example.toagigatron.tasks.inside;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;

@TaskDescriptor(
	name = "Entering path",
	priority = 1
)
public class EnterPath extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public EnterPath(ToaManager toaManager)
	{
		super(toaManager, Stage.INSIDE);
	}

	public boolean execute()
	{
		NPC supplyGhost = NPCUtil.findNearest(ToaConstants.HELPFUL_SPIRIT);
		if (gameTickManager.isTickWaiting() || (toaManager.inside.canClaimSupplies() && supplyGhost != null))
		{
			return false;
		}
		if(EthanApiPlugin.isMoving()){
			toaManager.print("I am moving rn i don't need to be clicking anything.");
			return false;
		}

		GameObject kephriPath = ObjectUtil.getObject(ToaConstants.ACTIVE_DOOR_KEPHRI);
		GameObject babaPath = ObjectUtil.getObject(ToaConstants.ACTIVE_DOOR_BABA);
		GameObject akkhaPath = ObjectUtil.getObject(ToaConstants.ACTIVE_DOOR_AKKHA);
		GameObject zebakPath = ObjectUtil.getObject(ToaConstants.ACTIVE_DOOR_ZEBAK);
		GameObject wardensPath = ObjectUtil.getObject(ToaConstants.ACTIVE_DOOR_WARDENS);
		if (babaPath != null)
		{
			toaManager.print("Entering ba-ba");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(babaPath, false, "Quick-Enter");
		}
		else if (kephriPath != null)
		{
			toaManager.print("Entering kephri");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(kephriPath, false, "Quick-Enter");
		}
		else if (zebakPath != null)
		{
			toaManager.print("Entering Zebak");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(zebakPath, false, "Quick-Enter");
		}
		else if (akkhaPath != null)
		{
			toaManager.print("Entering Akkha");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(akkhaPath, false, "Quick-Enter");
		}
		else if (wardensPath != null)
		{
			toaManager.print("Entering wardens");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(wardensPath, false, "Quick-Enter");
		}
		else
		{
			return false;
		}
		return true;
	}
}
