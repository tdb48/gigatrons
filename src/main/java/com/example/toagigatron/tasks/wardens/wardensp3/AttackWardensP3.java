package com.example.toagigatron.tasks.wardens.wardensp3;

import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Combat;
import com.example.Utility.NPCUtil;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Attack p3",
	priority = 20
)
public class AttackWardensP3 extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public AttackWardensP3(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P3);
	}

	public boolean execute()
	{
		if (toaManager.wardens3.enrage
			|| toaManager.wardens3.warden == null
			|| toaManager.wardens3.warden.getId() != ToaConstants.WARDENS_P3_SKULLS_INACTIVE_ID)
		{
			return false;
		}
		ArrayList<Integer> gearSet = getGearSet();

		if (!toaManager.hasGearEquipped(gearSet))
		{
//			toaManager.print("Equipping gear");
			toaManager.swap(gearSet);
			return true;
		}
		if (!Combat.isSpecEnabled() && Combat.getSpecEnergy() >= bgsBpSpecRequired() && toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItemsBgs()))
		{
			Combat.toggleSpecVoid();
		}
		if (gameTickManager.isAttackWaiting())
		{
			return false;
		}
		if (toaManager.wardens3.warden != null
			&& NPCUtil.hasAction(toaManager.wardens3.warden, "Attack"))
		{
			if (!toaManager.hasGearEquipped(gearSet)
				|| (client.getLocalPlayer().getInteracting() != null
				&& client.getLocalPlayer().getInteracting().equals(toaManager.wardens3.warden)))
			{
				return false;
			}
			toaManager.print("Attacking warden");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(toaManager.wardens3.warden, "Attack");
			return true;
		}

		return false;
	}

	public int bgsBpSpecRequired()
	{
		return toaManager.isAdrenalineActive() ? 25 : 50;
	}

	public ArrayList<Integer> getGearSet()
	{

		if (toaManager.wardens3.bgsHit < 20 && Combat.getSpecEnergy() >= bgsBpSpecRequired())
		{
			return toaManager.meleeSetup.getAllItemsBgs();
		}
		else
		{
			return toaManager.rangeSetup.getAllItemsBp();
		}

	}
}
