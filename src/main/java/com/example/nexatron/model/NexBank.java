package com.example.nexatron.model;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.BankUtil;
import com.example.Utility.InventoryUtil;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.model.setup.Setup;
import java.util.ArrayList;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;

/*
banking unused items
pickup pet
get required items
heal back up
hop world
prepot
withdraw pots
enter instance
 */
public class NexBank
{
	public GameObject barrier = null;
	public NPC banker = null;
	public boolean usingGucciRunePouch = false;
	@Inject
	NexManager nexManager;

	@Inject
	Client client;

	@Inject
	ItemManager itemManager;
	@Inject
	EventBus eventBus;

	@Inject
	GameTickManager gameTickManager;


	public void register()
	{
		this.eventBus.register(this);
	}

	public void unregister()
	{
		this.eventBus.unregister(this);
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (nexManager.getStage() != Stage.BANK)
		{
			return;
		}
		if (BankUtil.isOpen()
			&& !BankUtil.isMainTabOpen())
		{
			nexManager.print("Opening main tab");
			BankUtil.openMainTab();
		}
		if (!usingGucciRunePouch &&
			(InventoryUtil.contains(ItemID.DIVINE_RUNE_POUCH)
				|| (Bank.isOpen() && BankUtil.contains(ItemID.DIVINE_RUNE_POUCH))))
		{
			usingGucciRunePouch = true;
		}
		if (Bank.isOpen() && nexManager.config.autoDecide())
		{
			if (nexManager.nex.helm == -1)
			{
				nexManager.nex.helm = nexManager.setup.findBestSlot(Setup.HELM);
				nexManager.print("Set helm as " + itemManager.getItemComposition(nexManager.nex.helm).getName());
			}
			if (nexManager.nex.meleeCape == -1)
			{
				nexManager.nex.meleeCape = nexManager.setup.findBestSlot(Setup.MELEE_CAPE);
				nexManager.print("Set melee cape as " + itemManager.getItemComposition(nexManager.nex.meleeCape).getName());
			}
			if (nexManager.nex.rangeCape == -1)
			{
				nexManager.nex.rangeCape = nexManager.setup.findBestSlot(Setup.RANGE_CAPE);
				nexManager.print("Set range cape as " + itemManager.getItemComposition(nexManager.nex.rangeCape).getName());
			}
			if (nexManager.nex.meleeOffhand == -1)
			{
				nexManager.nex.meleeOffhand = nexManager.setup.findBestSlot(Setup.MELEE_OFFHAND);
				nexManager.print("Set melee offhand as " + itemManager.getItemComposition(nexManager.nex.meleeOffhand).getName());

			}
		}
	}

	public ArrayList<Integer> requiredItems()
	{
		ArrayList<Integer> requiredItems = new ArrayList<>(nexManager.setup.rangeNex());
		requiredItems.addAll(nexManager.setup.meleeNex());
		if (nexManager.useThralls())
		{
			requiredItems.add(ItemID.BOOK_OF_THE_DEAD);
			if (usingGucciRunePouch)
			{
				requiredItems.add(ItemID.DIVINE_RUNE_POUCH);
			}
			else
			{
				requiredItems.add(ItemID.RUNE_POUCH);
			}
		}
		requiredItems.add(ItemID.RUBY_DRAGON_BOLTS_E);
		requiredItems.add(ItemID.DIAMOND_DRAGON_BOLTS_E);
		return (ArrayList<Integer>) requiredItems.stream().distinct().collect(Collectors.toList());
	}

	public void reset()
	{
		usingGucciRunePouch = false;
	}

	public void fullReset()
	{
		reset();
		barrier = null;
		banker = null;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
	{
		GameObject gameObject = gameObjectSpawned.getGameObject();
		if (gameObject.getId() == NexConst.ACTIVE_BARRIER)
		{
			barrier = gameObject;
		}
		if (gameObject.getId() == NexConst.INACTIVE_BARRIER)
		{
			barrier = gameObject;
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned)
	{
		GameObject gameObject = gameObjectDespawned.getGameObject();
		if (gameObject.getId() == NexConst.ACTIVE_BARRIER)
		{
			barrier = null;
		}
		if (gameObject.getId() == NexConst.INACTIVE_BARRIER)
		{
			barrier = null;
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();
		if (npc.getId() == NexConst.BANKER)
		{
			nexManager.print("Banker spawned");
			banker = npc;
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();
		if (npc.getId() == NexConst.BANKER)
		{
			nexManager.print("Banker despawned");
			banker = null;
		}
	}

	public boolean openBank()
	{
		if (banker == null)
		{
			return false;
		}
		nexManager.print("Opening bank");
		MousePackets.queueClickPacket();
		NPCPackets.queueNPCAction(banker, "Bank");
		return true;
	}
}
