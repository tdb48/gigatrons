package com.example.toagigatron.model;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.ToaConstants;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.TileObject;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;

public class Inside
{
	@Inject
	ToaManager toaManager;

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


	public void resetVariables()
	{
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{

	}

	public boolean canClaimSupplies()
	{
		return client.getVarbitValue(ToaConstants.VARBIT_CLAIMED_SUPPLIES) == 0;
	}


	public int availableDoors()
	{
		int counter = 0;

		TileObject kephriPath = TileObjects.search().withId(ToaConstants.ACTIVE_DOOR_KEPHRI).first().orElse(null);
		TileObject babaPath = TileObjects.search().withId(ToaConstants.ACTIVE_DOOR_BABA).first().orElse(null);
		TileObject akkhaPath = TileObjects.search().withId(ToaConstants.ACTIVE_DOOR_AKKHA).first().orElse(null);
		TileObject zebakPath = TileObjects.search().withId(ToaConstants.ACTIVE_DOOR_ZEBAK).first().orElse(null);

		if (kephriPath != null)
		{
			counter++;
		}
		if (babaPath != null)
		{
			counter++;
		}
		if (akkhaPath != null)
		{
			counter++;
		}
		if (zebakPath != null)
		{
			counter++;
		}
		return counter;
	}

}
