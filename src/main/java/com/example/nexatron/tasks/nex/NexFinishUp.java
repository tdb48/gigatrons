package com.example.nexatron.tasks.nex;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileItems;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.TileItemPackets;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Movement;
import com.example.Utility.TileItemUtil;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;

@TaskDescriptor(
	name = "Nex finish up",
	priority = 1
)
public class NexFinishUp extends StagedTask
{
	@Inject
	ItemManager itemManager;

	@Inject
	GameTickManager gameTickManager;

	@Inject
	Consumable consumable;

	@Inject
	public NexFinishUp(NexManager nexManager)
	{
		super(nexManager, Stage.NEX_DEAD);
	}

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null)
		{
			return false;
		}
		// Dodge mushroom, tick gets set in ProgressStage task
		if (nexManager.nex.invincibleTick > 0)
		{
			if (!nexManager.getPlayerPoint().equals(nexManager.nex.masterMainTile))
			{
				nexManager.print("Walking to safety (wrath)");
				Movement.walk(nexManager.nex.masterMainTile);
			}
			nexManager.print("Waiting for wrath to disappear");
			return true;
		}

		Widget healingPotion = Consumable.getBrew();
		Widget prayerRestore = Consumable.getRestore();
		if (Consumable.isDrained(Skill.PRAYER)
			&& prayerRestore != null
			&& !gameTickManager.isPotionWaiting())
		{
			nexManager.print("Drinking restore (panic)");
			consumable.consume(prayerRestore);
		}
		else if (Consumable.isDrained(Skill.HITPOINTS)
			&& healingPotion != null
			&& !gameTickManager.isPotionWaiting())
		{
			nexManager.print("Drinking brew");
			consumable.consume(healingPotion);
		}

		ETileItem loot = findLoot();
		NPC pet = NPCs.search().withAction("Pick-up").interactingWith(client.getLocalPlayer()).first().orElse(null);
		if (loot != null
			&& !InventoryUtil.isFull())
		{
			nexManager.print("Picking up " + itemManager.getItemComposition(loot.tileItem.getId()).getName());
			MousePackets.queueClickPacket();
			TileItemPackets.queueTileItemAction(loot, false);
		}
		else if (pet != null
			&& !gameTickManager.isTickWaiting()
			&& !InventoryUtil.isFull())
		{
			nexManager.print("Attempting to pick up " + pet.getName());
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(pet, "Pick-up");
			gameTickManager.setTickWait(2);
		}
		else
		{
			String why = "";
			if (loot == null)
			{
				why = "no loot left";
			}
			if (InventoryUtil.isFull())
			{
				why = "inventory is full";
			}
			nexManager.print("Setting teleport out to true because " + why);
			nexManager.nex.teleportOut = true;
		}
		return true;
	}

	public ETileItem findLoot()
	{
		ArrayList<ETileItem> potentialLoot = TileItemUtil.getAllETileItems(NexConst.HIGH_PRIO_LOOT);
		if (!potentialLoot.isEmpty())
		{
			return potentialLoot.get(0);
		}
		potentialLoot = TileItemUtil.getAllETileItems(NexConst.LOW_PRIO_LOOT);
		if (!potentialLoot.isEmpty())
		{
			return potentialLoot.get(0);
		}
		potentialLoot = (ArrayList<ETileItem>) TileItems.search().stackAboveXValue(1000000).result();
		if (!potentialLoot.isEmpty())
		{
			return potentialLoot.get(0);
		}
		return null;
	}

}

