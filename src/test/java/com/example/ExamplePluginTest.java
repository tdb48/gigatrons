package com.example;

import com.example.ChinBreakHandler.ChinBreakHandlerPlugin;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.PathingTesting.PathingTesting;
import com.example.TestingSuite.TestingSuitePlugin;
import com.example.betterprofiles.BetterProfilesPlugin;
import com.example.nexatron.NexatronPlugin;
import com.example.socket.SocketPlugin;
import com.example.steroidtoa.SteroidToaPlugin;
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
			PathingTesting.class,
//			TestingSuitePlugin.class,
			SocketPlugin.class,
			SteroidToaPlugin.class);
		RuneLite.main(args);
	}
}