package com.example.nexatron.model;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

public class NexBank
{
	public GameObject barrier = null;
	public NPC banker = null;
	@Inject
	NexManager nexManager;

	@Inject
	Client client;

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
		if (Bank.isOpen())
		{
			nexManager.print("Inv size: " + Inventory.search().result().size());
		}
		else
		{
			nexManager.print("Inv size: " + Inventory.search().result().size());
		}
	}

	public void reset()
	{

	}

	public void fullReset()
	{
		barrier = null;
		banker = null;
	}
}
