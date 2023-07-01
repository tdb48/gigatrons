package com.example.toagigatron.tasks.inside;

import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Claim loot",
	priority = 1,
	blocking = true
)
public class ClaimLoot extends StagedTask
{
	@Inject
	public ClaimLoot(ToaManager toaManager)
	{
		super(toaManager, Stage.INSIDE);
	}

	public boolean execute()
	{
		/*
		 * 1. If loot is claimed, leave with npc
		 * 2. If loot interface is open and its empty, set loot claimed to true
		 * 3. If loot interface is open and its not empty, click bank all widget
		 * 4. If loot interface is not open, click chest
		 * */

		// Leave NPC
		NPC osmumten = NPCUtil.findNearest("Osmunten");
		if (osmumten == null)
		{
			return false;
		}
		//TODO - Make sure this works (copied from zebak which works)
		if (!Widgets.search().withTextContains("Yes.").empty())
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueResumePause(14352385, 1);
			return true;
		}
		toaManager.overall.lootClaimed = isLootClaimed();
		// Step 1.
		if (toaManager.overall.lootClaimed)
		{
			toaManager.print("Leaving chestroom");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(osmumten, "Leave");
			return true;
		}
//		GameObject chestTest = new GameObjectQuery().idEquals(ToaConstants.PURPLE_CHEST).result(client).first();
//		if(chestTest != null){
//			int count = 1;
//			for(int id : client.getObjectDefinition(chestTest.getId()).getImpostorIds()){
//				toaManager.print("Imposter " + count + ": " + id);
//				count++;
//			}
//			toaManager.print("Current imposter id -> " + client.getObjectDefinition(chestTest.getId()).getImpostor().getId());
//			toaManager.print("Current game object id -> " + chestTest.getId());
//		}

		GameObject chest = ObjectUtil.getObject(ToaConstants.PURPLE_CHEST);
		if (chest != null)
		{
			if (client.getObjectDefinition(chest.getId()).getImpostor().getId() == 44825)
			{
				chest = ObjectUtil.getObject(ToaConstants.WHITE_CHEST_LOOTED);
			}
		}
		if (chest == null)
		{
			chest = ObjectUtil.getObject(ToaConstants.PURPLE_CHEST_OPEN);
		}
		if (chest == null)
		{
			chest = ObjectUtil.getObject(ToaConstants.WHITE_CHEST_LOOTED);
		}
		if (chest == null)
		{
			chest = ObjectUtil.getObject(ToaConstants.WHITE_CHEST_LOOTED2);
		}
		if (chest == null)
		{
			chest = ObjectUtil.getObject(ToaConstants.WHITE_CHEST_LOOTED3);
		}
		if (chest == null)
		{
			chest = ObjectUtil.getObject(ToaConstants.WHITE_CHEST);
		}
		if (chest == null)
		{
			toaManager.print("Something went wrong in loot task");
			return false;
		}
		// Step 4.
		if (!isLootWidgetOpen())
		{

			if (ObjectUtil.hasAction(chest, "Open"))
			{
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(chest, false,"Open");
			}
			else if (ObjectUtil.hasAction(chest, "Search"))
			{
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(chest, false,"Search");
			}
			return true;
		}
		else
		{
			//TODO - Make sure this works
			Widget bankAll = client.getWidget(771, 4);
			if (bankAll != null && !bankAll.isHidden())
			{
				toaManager.print("Banking all");
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(bankAll, "Bank-all");
				return true;
			}
		}
		return false;
	}

	public boolean isLootClaimed()
	{
		Widget chestLoot = client.getWidget(771, 10);
		if (chestLoot == null)
		{
			return toaManager.overall.lootClaimed;
		}
		Widget firstSlot = chestLoot.getChild(0);
		if (firstSlot == null)
		{
			return toaManager.overall.lootClaimed;
		}
		// If its null, then theres still loot in the first slot which means chest is unlooted
		return firstSlot.getActions() == null;
	}

	public boolean isLootWidgetOpen()
	{
		Widget chestLoot = client.getWidget(771, 2);
		return chestLoot != null && !chestLoot.isHidden();
	}
}