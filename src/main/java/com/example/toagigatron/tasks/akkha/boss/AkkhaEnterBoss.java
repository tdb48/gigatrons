package com.example.toagigatron.tasks.akkha.boss;

import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.ObjectUtil;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.ItemID;

@TaskDescriptor(
	name = "Akkha enter boss",
	priority = 1
)
public class AkkhaEnterBoss extends StagedTask
{

	@Inject
	public AkkhaEnterBoss(ToaManager toaManager)
	{
		super(toaManager, Stage.AKKHA_BOSS);
	}

	public boolean execute()
	{
		if (toaManager.consumableTracker.inventorySaltDoses == 0 && toaManager.consumableTracker.totalSaltDoses > 0)
		{
			toaManager.withdrawFromBag(ItemID.SMELLING_SALTS_2);
			return true;
		}
		GameObject bossEntry = ObjectUtil.getNearestGameObject(ToaConstants.AKKHA_BOSS_ENTRY);
		if (bossEntry != null && toaManager.akkha.isNotInBossRoom())
		{
			if (!toaManager.hasGearEquipped(toaManager.mageSetup.getAllItems()))
			{
				toaManager.swap(toaManager.mageSetup.getAllItems());
			}
			toaManager.print("Entering");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(bossEntry, false, "Quick-use");
			return true;
		}
		return false;
	}
}