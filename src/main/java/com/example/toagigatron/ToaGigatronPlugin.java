package com.example.toagigatron;

import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.taskformat.TaskManager;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
        name = "ToA Megatron",
        description = "DOES TOA FOR YOU",
        tags = "toagigatron,toa,amascut,tron")
@Slf4j
public class ToaGigatronPlugin extends Plugin {

    @Inject
    ToaGigatronConfig config;
    @Inject
    Client client;
    @Inject
    PluginManager pluginManager;
    @Inject
    OverlayManager overlayManager;
    @Inject
    private TaskManager manager;
    @Inject
    private GameTickManager gameTickManager;
    @Inject
    private ReflectBreakHandler chinBreakHandler;
    public boolean stopPlugin = false;
    public boolean finishRaid = false;

    //TODO - add the overlays and ToaManager
    @Provides
    ToaGigatronConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ToaGigatronConfig.class);
    }
}
