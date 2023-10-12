package com.example.nexatron.tasks.lobby;


import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Movement;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Enter Kc Area",
	priority = 1
)
public class EnterKcArea extends StagedTask
{
	public static final WorldPoint KC_TILE = new WorldPoint(2861, 5219, 0);

	@Inject
	public EnterKcArea(NexManager nexManager)
	{
		super(nexManager, Stage.LOBBY);
	}

	public boolean execute()
	{
		if (!hasAncientItem())
		{
			nexManager.print("We have no ancient item");
			return true;
		}
		if (nexManager.nex.kcDoor != null)
		{
			if (!nexManager.getPlayerPoint().equals(KC_TILE))
			{
				if (client.getLocalPlayer().getAnimation() == -1)
				{
					nexManager.print("Walking to KC door");
					Movement.walk(KC_TILE);
					incrementActionCount();
				}
			}
			else
			{
				nexManager.print("Opening door");
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(nexManager.nex.kcDoor, false, "Open");
				incrementActionCount();
			}
			return true;
		}
		return false;
	}

	public boolean hasAncientItem()
	{
		Widget ancientItem = Equipment.search().nameContains("ncient").first().orElse(null);
		if (ancientItem == null)
		{
			ancientItem = Equipment.search().nameContains("aryte").first().orElse(null);
		}
		return ancientItem != null;
	}
}
