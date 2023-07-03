package com.example.toagigatron.tasks.outside;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Claim Loot Outside",
	priority = 999,
	blocking = true
)
public class ClaimLootOutside extends StagedTask
{

	@Inject
	public ClaimLootOutside(ToaManager toaManager)
	{
		super(toaManager, Stage.OUTSIDE);
	}

	public boolean execute()
	{
		TileObject wallLoot = TileObjects.search().withId(46224).first().orElse(null);
		if (wallLoot == null)
		{
			return false;
		}
		//Id needs to be 46082 which indicates we have loot in the wall chest outside ready to be collected
		int imposterID = client.getObjectDefinition(wallLoot.getId()).getImpostor().getId();
		if (imposterID != 46082)
		{
			return false;
		}
		if (!isChestInterfaceOpen())
		{
			toaManager.print("Clicking loot chest");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(wallLoot, false, "Claim");
			return true;
		}
		Widget bankAll = client.getWidget(771, 4);
		if (bankAll != null && !bankAll.isHidden() && bankAll.getActions() != null)
		{
			toaManager.print("Banking all loot");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(bankAll, "Bank-all");
			return true;
		}
		return false;


	}

	public boolean isChestInterfaceOpen()
	{
		Widget chestWidget = client.getWidget(771, 0);
		return (chestWidget != null && !chestWidget.isHidden());
	}
}
