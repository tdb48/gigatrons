package com.example.nexatron.tasks.nex;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Nex Thrall",
	priority = 1
)
public class NexThrall extends StagedTask
{
	@Inject
	public NexThrall(NexManager nexManager)
	{
		super(nexManager,
			Stage.MINION_SMOKE,
			Stage.NEX_SMOKE,
			Stage.MINION_SHADOW,
			Stage.NEX_SHADOW,
			Stage.MINION_BLOOD,
			Stage.NEX_BLOOD,
			Stage.MINION_ICE,
			Stage.NEX_ICE,
			Stage.NEX_ZAROS);
	}

	public boolean execute()
	{
		if (client.getVarbitValue(NexConst.SPELLBOOK_VARB) == 3
			&& Inventory.search().withName("Book of the dead").first().orElse(null) != null
			&& Inventory.search().nameContains("une pouch").first().orElse(null) != null
			&& nexManager.isThrallOffCD())
		{
			nexManager.print("Spawning mage thrall");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(client.getWidget(WidgetInfoExtended.SPELL_RESURRECT_GREATER_GHOST.getPackedId()), "Cast");
			return true;
		}
		return false;
	}

}
