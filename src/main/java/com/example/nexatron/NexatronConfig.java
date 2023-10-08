package com.example.nexatron;

import com.example.nexatron.model.constants.ThrallMode;
import com.example.nexatron.model.setup.Helm;
import com.example.nexatron.model.setup.MeleeCape;
import com.example.nexatron.model.setup.MeleeOffhand;
import com.example.nexatron.model.setup.RangeCape;
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

	@ConfigSection(
		name = "Setup",
		description = "Settings for setup",
		position = 100
//		closedByDefault = true
	)
	String SETUP = "Setup";

	@ConfigItem(
		position = 1,
		keyName = "autoDecide",
		name = "Auto decide setup",
		description = "Automatically decides the setup based on items in inventory, bank and what's equipped",
		section = "Setup"
	)
	default boolean autoDecide()
	{
		return false;
	}

	@ConfigItem(
		position = 101,
		keyName = "helm",
		name = "Helm",
		description = "",
		section = "Setup"
	)
	default Helm helm()
	{
		return Helm.FACEGUARD;
	}

	@ConfigItem(
		position = 102,
		keyName = "meleeCape",
		name = "Melee Cape",
		description = "",
		section = "Setup"
	)
	default MeleeCape meleeCape()
	{
		return MeleeCape.INFERNAL_CAPE;
	}

	@ConfigItem(
		position = 103,
		keyName = "rangeCape",
		name = "Range Cape",
		description = "",
		section = "Setup"
	)
	default RangeCape rangeCape()
	{
		return RangeCape.ASSEMBLER;
	}

	@ConfigItem(
		position = 104,
		keyName = "meleeOffhand",
		name = "Melee Offhand",
		description = "",
		section = "Setup"
	)
	default MeleeOffhand meleeOffhand()
	{
		return MeleeOffhand.DDEF;
	}


	@ConfigItem(
		position = 1,
		keyName = "thralls",
		name = "Thralls",
		description = "Whether to use thralls or not, auto means it will look at quest completion",
		section = "General"
	)
	default ThrallMode thralls()
	{
		return ThrallMode.No;
	}


	@ConfigItem(
		position = 1,
		keyName = "prayFlick",
		name = "Pray Flick",
		description = "Whether to 1 tick flick or not (only at nex itself)",
		section = "General"
	)
	default boolean prayFlick()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "kcMode",
		name = "KC Mode",
		description = "If this is true, the bot will only do tasks to get kc",
		section = "General"
	)
	default boolean kcMode()
	{
		return false;
	}

	@ConfigItem(
		position = 1,
		keyName = "prayFlickKc",
		name = "Pray Flick KC",
		description = "Whether to 1 tick flick or not (only at KC itself)",
		section = "General"
	)
	default boolean prayFlickKc()
	{
		return false;
	}

	@ConfigItem(
		position = 20,
		keyName = "restoreCount",
		name = "Restore #",
		description = "",
		section = "ConsumeOptions"
	)
	default int restoreCount()
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
		return 3;
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
		position = 905,
		keyName = "showSocketInfobox",
		name = "Show Socket Infobox",
		description = "Show socket infobox"
	)
	default boolean showSocketInfobox()
	{
		return false;
	}

	@ConfigItem(
		position = 910,
		keyName = "showOverlay",
		name = "Show Overlay",
		description = "Show overlay"
	)
	default boolean showOverlay()
	{
		return true;
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
}
