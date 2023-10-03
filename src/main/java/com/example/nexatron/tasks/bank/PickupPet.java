package com.example.nexatron.tasks.bank;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.InventoryUtil;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.NPC;

@TaskDescriptor(
	name = "Picking up pet",
	priority = Integer.MAX_VALUE,
	blocking = true
)
public class PickupPet extends StagedTask
{

	GameTickManager gameTickManager;

	@Inject
	public PickupPet(NexManager nexManager, GameTickManager gameTickManager)
	{
		super(nexManager, Stage.BANK);
		this.gameTickManager = gameTickManager;
	}

	public boolean execute()
	{
		if (gameTickManager.isTickWaiting())
		{
			return false;
		}
		NPC pet = NPCs.search().withAction("Pick-up").interactingWith(client.getLocalPlayer()).first().orElse(null);
		if (pet == null || InventoryUtil.isFull())
		{
			return false;
		}
		nexManager.print("Attempting to pick up " + pet.getName());
		MousePackets.queueClickPacket();
		NPCPackets.queueNPCAction(pet, "Pick-up");
		gameTickManager.setTickWait(2);
		return true;
	}
}
