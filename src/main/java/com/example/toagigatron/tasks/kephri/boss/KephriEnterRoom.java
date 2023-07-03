package com.example.toagigatron.tasks.kephri.boss;


import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.TileObject;

@TaskDescriptor(
	name = "Kephri enter",
	priority = 10
)
public class KephriEnterRoom extends StagedTask
{
	@Inject
	public KephriEnterRoom(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_BOSS);
	}

	public boolean execute()
	{
		if (toaManager.kephri.kephriRoom == null || toaManager.kephri.kephriRoom.contains(Static.getClient().getLocalPlayer().getWorldLocation()))
		{
			return false;
		}
		if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItems()))
		{
			toaManager.swap(toaManager.meleeSetup.getAllItems());
			return true;
		}
		TileObject entry = TileObjects.search().withId(ToaConstants.KEPHRI_BOSS_ENTRY).first().orElse(null);
		//todo check this world point is accurate
		if (entry != null && Reachable.isWalkable(entry.getWorldLocation().dx(-1)))
		{
			toaManager.print("Entering boss fight");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(entry, false, "Quick-Use");
			return true;
		}

		return false;
	}
}