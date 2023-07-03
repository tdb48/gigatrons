package com.example.toagigatron.tasks.baba.boss;


import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Prayers;
import com.example.Utility.Reachable;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Baba enter boss",
	priority = 1
)
public class BabaEnterBoss extends StagedTask
{
	@Inject
	public BabaEnterBoss(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_BOSS);
	}

	public boolean execute()
	{
		if (toaManager.baba.babaEntry == null || !Reachable.isWalkable(toaManager.baba.babaEntry.getWorldLocation().dy(-1)))
		{
			return false;
		}
		Widget prayerRestore = Consumables.getRestore();
		if (Prayers.getPoints() <= 50 && prayerRestore != null)
		{
			toaManager.print("Drinking restore");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(prayerRestore, "Drink");
		}
		if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItemsBgs()))
		{
			toaManager.swap(toaManager.meleeSetup.getAllItemsBgs());
		}
		MousePackets.queueClickPacket();
		ObjectPackets.queueObjectAction(toaManager.baba.babaEntry, false, "Quick-Use");
		return true;
	}
}