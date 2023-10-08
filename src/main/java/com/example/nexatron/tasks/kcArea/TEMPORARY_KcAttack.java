package com.example.nexatron.tasks.kcArea;


import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.TileItemPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Combat;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Prayers;
import com.example.Utility.TileItemUtil;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;

@TaskDescriptor(
	name = "KC Attack",
	priority = 1,
	register = true
)
public class TEMPORARY_KcAttack extends StagedTask
{
	@Inject
	ItemManager itemManager;
	@Inject
	public TEMPORARY_KcAttack(NexManager nexManager)
	{
		super(nexManager, Stage.KC_AREA);
	}

	public boolean execute()
	{
		if (!nexManager.config.kcMode())
		{
			if (nexManager.nex.teleportOut
				&& nexManager.kcArea.bankDoor != null)
			{
				nexManager.print("Clicking door");
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(nexManager.kcArea.bankDoor, false, "Open");
			}
			return false;
		}
		Widget restore = Consumable.getRestore();
		NPC npcInteractingWithUs = NPCs.search().alive().interactingWithLocal().first().orElse(null);
		if (Prayers.getPoints() == 0
			|| restore == null)
		{
			if (npcInteractingWithUs != null)
			{
				nexManager.print("Attacking npc that's attacking us, then stopping");
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(npcInteractingWithUs, "Attack");
			}
			return false;
		}

		if (npcInteractingWithUs == null
			&& client.getLocalPlayer().getInteracting() == null)
		{
			ETileItem loot = TileItemUtil.getClosestETileItem(NexConst.KC_LOOT);
			if (loot != null
				&& !InventoryUtil.isFull())
			{
				nexManager.print("Picking up " + itemManager.getItemComposition(loot.tileItem.getId()).getName());
				MousePackets.queueClickPacket();
				TileItemPackets.queueTileItemAction(loot, false);
				return true;
			}
		}

		if (Prayers.getPoints() <= 50)
		{
			nexManager.print("Drinking restore pot");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(restore, "Drink");
			return true;
		}

		Widget rangePot = Consumable.getRange();
		if (client.getBoostedSkillLevel(Skill.RANGED) - client.getRealSkillLevel(Skill.RANGED) <= 5
			&& rangePot != null)
		{
			nexManager.print("Drinking range pot");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(rangePot, "Drink");
			return true;
		}

		Widget anti = Consumable.getAnti();
		if (Combat.isPoisoned()
			&& anti != null)
		{
			nexManager.print("Drinking anti pot");
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(anti, "Drink");
			return true;
		}

		if (Combat.getSpecEnergy() >= 50
			&& !Combat.isSpecEnabled()
			&& Equipment.search().withId(ItemID.TOXIC_BLOWPIPE).first().orElse(null) != null)
		{
			nexManager.print("Toggling spec");
			Combat.toggleSpec();
		}

		NPC targetNPC = getNPC();
		if (targetNPC != null)
		{
			if (client.getLocalPlayer().isInteracting())
			{
				return false;
			}
			nexManager.print("Attacking " + targetNPC.getName());
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(targetNPC, "Attack");
			return true;
		}

		return false;
	}

	public NPC getNPC()
	{
		NPC returnNPC = NPCs.search().interactingWithLocal().alive().first().orElse(null);
		if (returnNPC != null)
		{
			return returnNPC;
		}
		String npcName = client.getBoostedSkillLevel(Skill.SLAYER) >= 83 ? "Mage" : "Reaver";
		return NPCs.search().nameContains(npcName).alive().nearestToPlayer().orElse(null);
	}


}
