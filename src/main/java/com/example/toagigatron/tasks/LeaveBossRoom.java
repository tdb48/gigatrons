package com.example.toagigatron.tasks;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.TileItemPackets;
import com.example.Utility.*;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.Task;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.TileItem;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import java.util.ArrayList;

@TaskDescriptor(
	priority = 20,
	name = "Leaving boss",
	register = true
)
public class LeaveBossRoom extends Task
{
	private final ToaManager toaManager;

	@Inject
	public LeaveBossRoom(ToaManager toaManager)
	{
		this.toaManager = toaManager;
	}

	public boolean run()
	{
		if (EthanApiPlugin.isMoving())
		{
			return false;
		}
		if (Dialog.canContinue())
		{
			toaManager.print("Continuing dialogue");
			Dialog.continueSpace();
			return true;
		}

		NPC leaveNPC = NPCUtil.findNearest(
			ToaConstants.SCABARAS,
			ToaConstants.OSMUMTEN,
			ToaConstants.APMEKEN,
			ToaConstants.CRONDIS,
			ToaConstants.HET);
		if (leaveNPC != null && Reachable.isWalkable(leaveNPC.getWorldLocation()))
		{
			ArrayList<ETileItem> tileItems = TileItemUtil.getAllETileItems("Saradomin brew(4)", "Super restore(4)");
			if (!tileItems.isEmpty() && !InventoryUtil.isFull())
			{
				MousePackets.queueClickPacket();
				TileItemPackets.queueTileItemAction(tileItems.get(0), false);
				return true;
			}
			else if (NPCUtil.hasAction(leaveNPC, "Proceed"))
			{
				toaManager.print("Proceeding to the lobby");
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(leaveNPC, "Proceed");
				return true;
			}
			else if (NPCUtil.hasAction(leaveNPC, "Talk-to"))
			{
				toaManager.print("Proceeding to the lobby");
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(leaveNPC, "Talk-to");
				return true;
			}
		}
		return false;
	}

	@Subscribe
	public void onNPCSpawned(NpcSpawned npcSpawned)
	{
		NPC spawned = npcSpawned.getNpc();
		if (spawned.getName() != null && spawned.getName().equalsIgnoreCase("osmumten"))
		{
			toaManager.disableOverheadsIfEnabled();
			toaManager.disableOffensiveIfEnabled();
		}
	}
}
