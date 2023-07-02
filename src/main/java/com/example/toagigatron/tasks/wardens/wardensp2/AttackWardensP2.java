package com.example.toagigatron.tasks.wardens.wardensp2;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Movement;
import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.HeadIcon;

@TaskDescriptor(
	name = "Attack Wardens p2",
	priority = 10
)
public class AttackWardensP2 extends StagedTask
{
	@Inject
	public AttackWardensP2(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P2);
	}

	public boolean execute()
	{
		if (toaManager.wardens12.warden == null
			|| toaManager.wardens12.warden.getAnimation() == ToaConstants.WARDEN_P2_DROPPING_CORE_ANIMATION
			|| toaManager.wardens12.warden.getId() == ToaConstants.WARDENS_P2_DOWNED)
		{
			return false;
		}
		if (toaManager.wardens12.warden == null || toaManager.wardens12.warden.getTransformedComposition() == null)
		{
			return false;
		}
		HeadIcon wardenOverhead = EthanApiPlugin.getHeadIcon(toaManager.wardens12.warden);
		ArrayList<Integer> gearSet;
		if (wardenOverhead != null && wardenOverhead.equals(HeadIcon.RANGE_MELEE))
		{
			gearSet = toaManager.mageSetup.getAllItems();
		}
		else
		{
			gearSet = toaManager.rangeSetup.getAllItems();
		}

		if (!toaManager.hasGearEquipped(gearSet))
		{
			toaManager.print("P2 - Swapping to warden gear");
			toaManager.swap(gearSet);
			return true;
		}
		if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(toaManager.wardens12.warden))
		{
			return false;
		}
		// 9663 is the start up animation
		if (NPCUtil.hasAction(toaManager.wardens12.warden, "Attack")
			&& toaManager.wardens12.warden.getAnimation() != 9663)
		{
			toaManager.print("P2 - Attacking warden");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(toaManager.wardens12.warden, "Attack");
			return true;
		}
		return false;
	}
}

