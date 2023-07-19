package com.example.TestingSuite;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("testingSuite")
public interface TestingSuiteConfig extends Config {
    @ConfigItem(
            keyName = "debug",
            name = "Debug",
            description = "Enable debug messages in console"
    )
    default boolean debug() {
        return true;
    }

	@ConfigItem(
		keyName = "showInfobox",
		name = "Show Infobox",
		description = ""
	)
	default boolean showInfobox() {
		return true;
	}
}