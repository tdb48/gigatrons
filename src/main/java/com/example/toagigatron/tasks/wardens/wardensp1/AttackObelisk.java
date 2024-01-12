package com.example.toagigatron.tasks.wardens.wardensp1;

import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Combat;
import com.example.Utility.ObjectUtil;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.GameObject;

@TaskDescriptor(
	name = "Attack obelisk",
	priority = 10
)
public class AttackObelisk extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public AttackObelisk(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P1);
	}

	public boolean execute()
	{
		GameObject yellowUFO = ObjectUtil.getNearestGameObject(ToaConstants.YELLOW_UFO);
		ArrayList<Integer> gearSet;
		if (yellowUFO != null)
		{
			gearSet = toaManager.rangeSetup.getAllItemsBp();
		}
		else
		{
			if (toaManager.wardens12.bgsHit < 20 && Combat.getSpecEnergy() == 100)
			{
				gearSet = toaManager.meleeSetup.getAllItemsBgs();
			}
			else
			{
				gearSet = toaManager.meleeSetup.getAllItems();
			}
		}
		if (!toaManager.hasGearEquipped(gearSet))
		{
			toaManager.print("Equipping new gear set");
			toaManager.swap(gearSet);
		}
		if (!Combat.isSpecEnabled() && Combat.getSpecEnergy() == 100 && toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItemsBgs()))
		{
			Combat.toggleSpecVoid();
		}
		if (gameTickManager.isAttackWaiting())
		{
			return false;
		}
		if (toaManager.wardens12.obelisk != null)
		{
			if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(toaManager.wardens12.obelisk))
			{
				return false;
			}
			toaManager.print("Attacking obelisk");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(toaManager.wardens12.obelisk, "Attack");
			return true;
		}
		return false;
	}

	public int bgsSpecRequired()
	{
		return toaManager.isAdrenalineActive() ? 25 : 50;
	}
}
