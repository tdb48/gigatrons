package com.example;

import com.example.ChinBreakHandler.ChinBreakHandlerPlugin;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.PathingTesting.PathingTesting;
import com.example.betterprofiles.BetterProfilesPlugin;
import com.example.nexatron.NexatronPlugin;
import com.example.toagigatron.ToaGigatronPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(
			EthanApiPlugin.class,
			NexatronPlugin.class,
			PacketUtilsPlugin.class,
			ToaGigatronPlugin.class,
//			ChinBreakHandlerPlugin.class,
			BetterProfilesPlugin.class,
			PathingTesting.class);
		RuneLite.main(args);
	}
}