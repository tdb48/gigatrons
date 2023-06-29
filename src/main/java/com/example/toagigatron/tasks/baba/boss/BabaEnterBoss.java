package com.example.toagigatron.tasks.baba.boss;

import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.Item;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.widgets.Prayers;

import javax.inject.Inject;

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
		Item prayerRestore = Consumables.getRestore();
		if (Prayers.getPoints() <= 50 && prayerRestore != null)
		{
			toaManager.print("Drinking restore");
			prayerRestore.interact("Drink");
		}
		if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItemsBgs()))
		{
			toaManager.swap(toaManager.meleeSetup.getAllItemsBgs());
		}
		toaManager.baba.babaEntry.interact("Quick-Use");
		return true;
	}
}