package com.example.toagigatron.tasks.outside;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Static;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Creating party",
	priority = 50
)
public class CreateParty extends StagedTask
{
	// Make party widget
	private final ToaManager toaManager;

	@Inject
	public CreateParty(ToaManager toaManager)
	{
		super(toaManager, Stage.OUTSIDE);
		this.toaManager = toaManager;
	}

	public boolean execute()
	{
		if (!toaManager.readyToEnterRaid())
		{
			return false;
		}
		if (toaManager.onBreak())
		{
			return false;
		}
		if (toaManager.needsBreak() && !toaManager.allowedToBreak)
		{
			toaManager.allowedToBreak = true;
			return false;
		}
		TileObject obelisk = TileObjects.search().withId(ToaConstants.GROUPING_OBELISK).first().orElse(null);
		if (obelisk == null || Static.getClient().getVarbitValue(14345) != 0)
		{
			return false;
		}
		Widget createParty = Static.getClient().getWidget(772, 64);
		if (createParty != null && !createParty.isHidden())
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(createParty,"Make Party");
			toaManager.print("Clicked make party widget");
		}
		else
		{
			toaManager.print("Clicking obelisk");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(obelisk, false,"Inspect");
		}
		return true;
	}
}
