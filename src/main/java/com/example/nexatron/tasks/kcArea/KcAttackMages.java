package com.example.nexatron.tasks.kcArea;


import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.Players;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.TileItemPackets;
import com.example.Utility.Combat;
import com.example.Utility.Hopping;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Prayers;
import com.example.Utility.TileItemUtil;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.widgets.Widget;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.WorldService;

@TaskDescriptor(
	name = "KC Attack",
	priority = 1
)
public class KcAttackMages extends StagedTask
{
	@Inject
	WorldService worldService;
	@Inject
	GameTickManager gameTickManager;
	@Inject
	ItemManager itemManager;

	@Inject
	public KcAttackMages(NexManager nexManager)
	{
		super(nexManager, Stage.KC_AREA);
	}

	public boolean execute()
	{
		if (!nexManager.shouldKc())
		{
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
				incrementActionCount();
			}
			return false;
		}

		nexManager.kcArea.shouldHop = shouldHop();
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
				incrementActionCount();
				return true;
			}
			if (nexManager.kcArea.shouldHop)
			{
				if (gameTickManager.isTickWaiting())
				{
					return true;
				}
				gameTickManager.setTickWait(4);
				nexManager.print("We should be hopping, gonna hop to " + Hopping.getValidWorld(true, worldService));
				Hopping.hop(Hopping.getValidWorld(true, worldService), worldService);
				return true;
			}
		}

		if (Combat.getSpecEnergy() >= 50
			&& !Combat.isSpecEnabled()
			&& Equipment.search().withId(ItemID.TOXIC_BLOWPIPE).first().orElse(null) != null)
		{
			nexManager.print("Toggling spec");
			setActionCount(getActionCount() + Combat.toggleSpec());
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
			incrementActionCount();
			return true;
		}
		return false;
	}

	public NPC getNPC()
	{
		return nexManager.kcArea.getTarget();
	}

	public boolean shouldHop()
	{
		List<Player> players = Players.search().notLocalPlayer().result();
		for (Player player : players)
		{
			if (player != null
				&& player.isInteracting()
				&& player.getInteracting() != null
				&& player.getInteracting().getName() != null
				&& player.getInteracting().getName().contains("Mage"))
			{
				nexManager.print("Found someone hitting a mage, we're gonna hop worlds");
				return true;
			}
		}
		if (nexManager.socket.world == nexManager.socket.otherWorld
			&& nexManager.socket.otherWorld != -1
			&& nexManager.socket.isSlave())
		{
			return true;
		}
		return nexManager.kcArea.shouldHop;
	}

}
