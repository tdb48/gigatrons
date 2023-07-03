package com.example.toagigatron.tasks.zebak.boss;

import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.ObjectUtil;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.ItemID;

@TaskDescriptor(
	name = "Zebak enter room",
	priority = 1,
	register = true
)
public class ZebakEnterRoom extends StagedTask
{
	@Inject
	public ZebakEnterRoom(ToaManager toaManager)
	{
		super(toaManager, Stage.ZEBAK_BOSS);
	}

	public boolean execute()
	{
		if (toaManager.zebak.isInBossRoom())
		{
			return false;
		}
		if (toaManager.consumableTracker.inventorySaltDoses == 0 && toaManager.consumableTracker.totalSaltDoses > 0)
		{
			toaManager.withdrawFromBag(ItemID.SMELLING_SALTS_2);
			return true;
		}
		ArrayList<Integer> setup = toaManager.meleeSetup.getAllItemsBgs();
		GameObject entry = ObjectUtil.getNearestGameObject(ToaConstants.ZEBAK_BOSS_ENTRY);
		if (entry != null)
		{
			if (!toaManager.hasGearEquipped(setup))
			{
				toaManager.swap(setup);
			}
			toaManager.print("Entering zebak");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(entry, false, "Quick-Use");
			return true;
		}
		return false;
	}
}