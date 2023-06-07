package com.example.Utility;

import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.overlay.OverlayManager;

@Singleton
public class Static
{
	@Getter
	private static String[] scriptArgs = new String[0];

	@Inject
	@Getter
	private static EventBus eventBus;

	@Inject
	@Getter
	private static ClientThread clientThread;

	@Inject
	@Getter
	private static Client client;

	@Inject
	@Getter
	private static ItemManager itemManager;

	@Inject
	@Getter
	private static WorldService worldService;

	@Inject
	@Getter
	private static ChatMessageManager chatMessageManager;

	@Inject
	@Getter
	private static PluginManager pluginManager;

	@Inject
	@Getter
	private static ConfigManager configManager;

	@Inject
	@Getter
	private static KeyManager keyManager;

	@Inject
	@Getter
	private static OverlayManager overlayManager;

	@Inject
	@Getter
	private static ClientToolbar clientToolbar;

	@Inject
	@Getter
	private static SpriteManager spriteManager;

	public static void setScriptArgs(String[] scriptArgs)
	{
		Static.scriptArgs = scriptArgs;
	}

}
