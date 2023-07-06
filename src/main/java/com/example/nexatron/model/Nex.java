package com.example.nexatron.model;

import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

public class Nex
{
	public GameObject icePrisonSpike = null;
	public NPC nex = null;
	public NPC fumus = null;
	public NPC umbra = null;
	public NPC glacies = null;
	public NPC cruor = null;
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
	}

	public void bankReset()
	{

	}

	public void fullReset()
	{
		icePrisonSpike = null;
		 nex = null;
		fumus = null;
	umbra = null;
		 glacies = null;
		 cruor = null;
	}
}
