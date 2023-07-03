package com.example.toagigatron.tasks.outside;


import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.InventoryUtil;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.NPC;

@TaskDescriptor(
	name = "Picking up pet",
	priority = 300,
	blocking = true
)
public class PickupPet extends StagedTask
{

	GameTickManager gameTickManager;

	@Inject
	public PickupPet(ToaManager toaManager, GameTickManager gameTickManager)
	{
		super(toaManager, Stage.OUTSIDE);
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
		toaManager.print("Attempting to pick up petington.");
		MousePackets.queueClickPacket();
		NPCPackets.queueNPCAction(pet, "Pick-up");
		gameTickManager.setTickWait(2);
		return false;
	}
}