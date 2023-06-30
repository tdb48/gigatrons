package com.example.toagigatron.tasks;


import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.BankUtil;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Static;
import com.example.toagigatron.taskformat.Task;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	priority = -1,
	name = "Drop vial"
)
public class DropVial extends Task
{

	Client client = Static.getClient();
	public boolean run()
	{
		if (BankUtil.isOpen())
		{
			return false;
		}
		Widget vial = InventoryUtil.getFirst("Vial");
		NPC interactingPlayer = playerInteractingWith();
		if (vial != null)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(vial, "Drop");
			reAttack(interactingPlayer);
			return true;
		}
		return false;
	}

	public void reAttack(NPC npc){
		if(npc != null){
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(npc, "Attack");
			print("Re-Attacking " + npc.getName() + " In drop vial task");
		} else {
			print("NPC IS NULL IN RE-ATTACK IN DROP VIAL TASK");
		}
	}

	public NPC playerInteractingWith(){
		Player p = client.getLocalPlayer();
		if(p.getInteracting() == null) {
			return null;
		}

		if(p.getInteracting() instanceof NPC){
			return (NPC) p.getInteracting();
		}

		return null;
	}
	public void print(String msg)
	{
		if (client.isClientThread())
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, "");
		}
	}

}
