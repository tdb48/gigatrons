package com.example.nexatron.tasks.kcArea;


import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Movement;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Enter bank",
	priority = 1
)
public class EnterBank extends StagedTask
{
	public static final WorldPoint BANK_TILE = new WorldPoint(2898, 5203, 0);

	@Inject
	public EnterBank(NexManager nexManager)
	{
		super(nexManager, Stage.KC_AREA);
	}

	public boolean execute()
	{
		if (!nexManager.config.kcMode()
			&& nexManager.nex.bankDoor != null)
		{
			if (!nexManager.getPlayerPoint().equals(BANK_TILE))
			{
				if (client.getLocalPlayer().getAnimation() == -1)
				{
					nexManager.print("Walking to door");
					Movement.walk(BANK_TILE);
				}
			}
			else
			{
				nexManager.print("Opening door");
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(nexManager.nex.bankDoor, false, "Open");
			}
			return true;
		}
		return false;
	}

}
