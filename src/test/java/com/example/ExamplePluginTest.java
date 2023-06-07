package com.example;

import com.example.AutoTele.AutoTele;
import com.example.E3t4g.e3t4g;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.LavaRunecrafter.LavaRunecrafterPlugin;
import com.example.NightmareHelper.NightmareHelperPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import com.example.PrayerFlicker.EthanPrayerFlickerPlugin;
import com.example.UpkeepPlugin.UpkeepPlugin;
import com.example.gauntletFlicker.gauntletFlicker;
import com.example.harpoon2ticker.SwordFish2Tick;
import com.example.superglass.SuperGlassMakerPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(EthanApiPlugin.class, PacketUtilsPlugin.class,UpkeepPlugin.class);
        RuneLite.main(args);
    }
}