package com.example.nexatron.model;

import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

public class Lobby
{
	public GameObject kcAreaDoor = null;
	public GameObject deathChest = null;
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

	public void reset()
	{

	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
	{
		GameObject gameObject = gameObjectSpawned.getGameObject();
		if (gameObject.getId() == NexConst.KC_AREA_DOOR)
		{
			nexManager.print("kc area door spawned");
			kcAreaDoor = gameObject;
		}
		if (gameObject.getId() == NexConst.DEATH_CHEST)
		{
			deathChest = gameObject;
		}
	}

	public void fullReset()
	{
		kcAreaDoor = null;
		deathChest = null;
	}
}
