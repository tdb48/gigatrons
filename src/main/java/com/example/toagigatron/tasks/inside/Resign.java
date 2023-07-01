package com.example.toagigatron.tasks.inside;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Dialog;
import com.example.Utility.ObjectUtil;
import com.example.toagigatron.ToaGigatronPlugin;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Entering path",
	priority = 999999,
	blocking = true
)
public class Resign extends StagedTask
{

	@Inject
	ToaGigatronPlugin plugin;

	@Inject
	public Resign(ToaManager toaManager)
	{
		super(toaManager, Stage.INSIDE);
	}

	public static final String ABANDON_MESSAGE = "Yes, abandon the raid.";

	public boolean execute()
	{
		if (toaManager.inside.canClaimSupplies())
		{
			return false;
		}
		GameObject exit = ObjectUtil.getObject(ToaConstants.RAID_EXIT);
		if (exit == null)
		{
			exit = ObjectUtil.getObject(ToaConstants.BABA_BOSS_EXIT);

		}
		if (!shouldResign() || exit == null || !ObjectUtil.hasAction(exit, "Leave"))
		{
			return false;
		}
		if (!Widgets.search().withTextContains(ABANDON_MESSAGE).empty())
		{
			toaManager.overall.totalResigns++;
			MousePackets.queueClickPacket();
			WidgetPackets.queueResumePause(14352385, 1);
			return true;
		}
		if (Dialog.canContinue())
		{
			Dialog.continueSpace();
			return true;
		}
		toaManager.print("Leaving the raid");
		MousePackets.queueClickPacket();
		ObjectPackets.queueObjectAction(exit, false, "Leave");

		return true;
	}

	public int potionDoses(int oneDose, int twoDose, int threeDose, int fourDose)
	{
		int doses = 0;

		doses += Inventory.getItemAmount(oneDose);
		doses += Inventory.getItemAmount(twoDose) * 2;
		doses += Inventory.getItemAmount(threeDose) * 3;
		doses += Inventory.getItemAmount(fourDose) * 4;
		return doses;
	}

	public int brewDoses()
	{
		int regularDoses = potionDoses(ItemID.SARADOMIN_BREW1, ItemID.SARADOMIN_BREW2, ItemID.SARADOMIN_BREW3, ItemID.SARADOMIN_BREW4);
		int raidDoses = toaManager.consumableTracker.totalRaidBrewDoses;
		return regularDoses + raidDoses;
	}

	public int restoreDoses()
	{
		int regularDoses = potionDoses(ItemID.SUPER_RESTORE1, ItemID.SUPER_RESTORE2, ItemID.SUPER_RESTORE3, ItemID.SUPER_RESTORE4);
		int raidDoses = toaManager.consumableTracker.totalRaidRestoreDoses;
		int dosesAsScarab = toaManager.consumableTracker.totalScarabDoses * 2;
		int dosesAmbrosia = toaManager.consumableTracker.totalAmbrosiaDoses * 3;
		return regularDoses + raidDoses + dosesAsScarab + dosesAmbrosia;
	}

	public boolean shouldResign()
	{
		if (plugin.stopPlugin)
		{
			return true;
		}
		GameObject akkhaPath = ObjectUtil.getObject(ToaConstants.ACTIVE_DOOR_AKKHA);
		if (brewDoses() <= 4 && akkhaPath != null)
		{
			toaManager.print("Resigning because not enough brew");
			return true;
		}
		GameObject wardensPath = ObjectUtil.getObject(ToaConstants.ACTIVE_DOOR_WARDENS);
		if (wardensPath != null && restoreDoses() <= 18)
		{
			toaManager.print("Resigning because not enough prayer");
			return true;
		}
		return false;
	}
}
