package com.example.toagigatron;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("ToaGigatronConfig")
public interface ToaGigatronConfig extends Config {


	@ConfigItem(
		position = 2,
		keyName = "brewCount",
		name = "Brews #",
		description = "",
		section = "General"
	)
	default int brewCount()
	{
		return 5;
	}

	@ConfigItem(
		position = 9999,
		keyName = "debug",
		name = "Debug",
		description = "Posts debug message to chat if having problems"
	)
	default boolean debug()
	{
		return true;
	}

	@ConfigItem(
		position = 900,
		keyName = "showInfobox",
		name = "Show Overlay",
		description = "Show overlay"
	)
	default boolean showInfobox()
	{
		return true;
	}

	@ConfigItem(
		position = 900,
		keyName = "showConsumablesInfobox",
		name = "Consumables Infobox",
		description = "Show consumables overlay"
	)
	default boolean showConsumablesInfobox()
	{
		return true;
	}
}
