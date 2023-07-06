package com.example.nexatron;

import java.awt.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("NexatronConfig")
public interface NexatronConfig extends Config
{

	@ConfigSection(
		name = "General",
		description = "General",
		position = 0
	)
	String GENERAL_SETTINGS = "General";

	@ConfigSection(
		name = "Consume Options",
		description = "Different options for consumables",
		position = 20
	)
	String CONSUME_SETTINGS = "ConsumeOptions";




//	@ConfigItem(
//		position = 1,
//		keyName = "useThralls",
//		name = "Use Thralls",
//		description = "Whether to use thralls or not",
//		section = "General"
//	)
//	default boolean useThralls()
//	{
//		return false;
//	}


	@ConfigItem(
		position = 1,
		keyName = "prayFlick",
		name = "Prayer flick",
		description = "Whether to 1 tick flick or not (only at nex itself)",
		section = "General"
	)
	default boolean prayFlick()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "kcOnly",
		name = "Kc only",
		description = "If this is true, the bot will only get kc",
		section = "General"
	)
	default boolean kcOnly()
	{
		return false;
	}

	@ConfigItem(
		position = 20,
		keyName = "brewCount",
		name = "Brews #",
		description = "",
		section = "ConsumeOptions"
	)
	default int brewCount()
	{
		return 5;
	}

	@ConfigItem(
		position = 21,
		keyName = "scbCount",
		name = "SCB #",
		description = "",
		section = "ConsumeOptions"
	)
	default int scbCount()
	{
		return 2;
	}

	@ConfigItem(
		position = 21,
		keyName = "rangeCount",
		name = "Range #",
		description = "",
		section = "ConsumeOptions"
	)
	default int rangeCount()
	{
		return 2;
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
		name = "Show Infobox",
		description = "Show infobox"
	)
	default boolean showInfobox()
	{
		return true;
	}

	@ConfigItem(
		position = 901,
		keyName = "showOverlay",
		name = "Show Overlay",
		description = "Show overlay"
	)
	default boolean showOverlay()
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
