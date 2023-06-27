package com.example.toagigatron;

import com.example.Utility.Dialog;
import com.example.Utility.Game;
import com.example.Utility.Movement;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.TaskManager;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
        name = "ToA Gigatron",
        description = "DOES TOA FOR YOU",
        tags = "toagigatron,toa,amascut,tron")
@Slf4j
public class ToaGigatronPlugin extends Plugin {

    @Inject
    public ToaGigatronConfig config;
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
    public ToaManager toaManager;
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

    @Override
    protected void startUp() throws Exception
    {
        stopPlugin = false;
        startState();
    }
    @Override
    protected void shutDown() throws Exception
    {
        stopPlugin = false;
        stopState();
    }
    public void resetAllModels()
    {
//        this.toaManager.overall.reset();
        this.toaManager.initialiseSetups();
//        this.toaManager.zebak.resetVariables();
//        this.toaManager.kephri.resetVariables();
//        this.toaManager.baba.resetVariables();
//        this.toaManager.akkha.resetVariables();
//        this.toaManager.inside.resetVariables();
//        this.toaManager.outside.resetVariables();
//        this.toaManager.wardens12.resetVariables();
//        this.toaManager.wardens3.resetVariables();
    }

	@Subscribe
	public void onConfigChange(ConfigChanged configChanged)
	{
		this.toaManager.initialiseSetups();
	}

    private void startState()
    {
        chinBreakHandler.registerPlugin(this);
        chinBreakHandler.startPlugin(this);
        toaManager.allowedToBreak = false;
    }

    private void stopState()
    {
        chinBreakHandler.unregisterPlugin(this);
        chinBreakHandler.stopPlugin(this);
        toaManager.allowedToBreak = false;
    }

    @Subscribe
    public void onGameStateChange(GameStateChanged event){
        if (event.getGameState().equals(GameState.LOADING)
                || event.getGameState().equals(GameState.LOGIN_SCREEN))
        {
            this.resetAllModels();
        }
        if (event.getGameState().equals(GameState.LOGIN_SCREEN) && chinBreakHandler.isBreakActive(this))
        {
            toaManager.allowedToBreak = false;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) throws PluginInstantiationException
    {
        if ((stopPlugin || finishRaid) && !toaManager.overall.died && toaManager.getStage() == Stage.OUTSIDE)
        {
            WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
            if (!playerPoint.equals(ToaConstants.BREAK_TILE))
            {
                Movement.walk(ToaConstants.BREAK_TILE);
                return;
            }
            toaManager.print("Turning off plugin");
            Game.logout();
            this.pluginManager.stopPlugin(this);
            finishRaid = false;
            stopPlugin = false;
        }
        if (chinBreakHandler.isBreakActive(this))
        {
            if (chinBreakHandler.isBreakActive(this))
            {
                if (client.getGameState().equals(GameState.LOGIN_SCREEN) && toaManager.allowedToBreak)
                {
                    toaManager.allowedToBreak = false;
                }
                return;
            }
        }
        if (chinBreakHandler.shouldBreak(this) && toaManager.allowedToBreak)
        {
            WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
            if (!playerPoint.equals(ToaConstants.BREAK_TILE))
            {
                Movement.walk(ToaConstants.BREAK_TILE);
                return;
            }
            toaManager.print("Should break, starting to break");
            chinBreakHandler.startBreak(this);
            return;
        }
        if (Dialog.canLevelUpContinue())
        {
            Dialog.continueSpace();
        }
    }
}

