package com.example.nexatron.model;

import com.example.nexatron.manager.NexManager;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.PluginManager;

public class ChargesTracker
{
	public int bloodyFury = -1;
	EventBus eventBus;
	Client client;
	PluginManager pluginManager;
	NexManager nexManager;
	@Inject
	ItemManager itemManager;

	@Inject
	public ChargesTracker(EventBus eventBus, Client client, PluginManager pluginManager, NexManager nexManager)
	{
		this.eventBus = eventBus;
		this.client = client;
		this.pluginManager = pluginManager;
		this.nexManager = nexManager;
	}

	public void register()
	{
		this.eventBus.register(this);
	}

	public void unregister()
	{
		this.eventBus.unregister(this);
	}

	public void reset()
	{
		bloodyFury = -1;
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM || chatMessage.getType() == ChatMessageType.CONSOLE || chatMessage.getType() == ChatMessageType.ENGINE)
		{
			String message = chatMessage.getMessage().toLowerCase();

		}
	}

}
