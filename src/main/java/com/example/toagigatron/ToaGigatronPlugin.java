package com.example.toagigatron;

import com.example.Utility.Dialog;
import com.example.Utility.Game;
import com.example.Utility.Movement;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.overlay.ConsumablesTrackerInfobox;
import com.example.toagigatron.overlay.ToaGigatronInfoBox;
import com.example.toagigatron.overlay.ToaGigatronOverlay;
import com.example.toagigatron.taskformat.TaskManager;
import com.example.toagigatron.tasks.*;
import com.example.toagigatron.tasks.baba.BabaConsumables;
import com.example.toagigatron.tasks.baba.puzzle.*;
import com.example.toagigatron.tasks.outside.Bank;
import com.example.toagigatron.tasks.outside.ClaimDeath;
import com.example.toagigatron.tasks.outside.ClaimLootOutside;
import com.example.toagigatron.tasks.outside.CreateParty;
import com.example.toagigatron.tasks.outside.EnterRaid;
import com.example.toagigatron.tasks.outside.GetSupplies;
import com.example.toagigatron.tasks.outside.PickupPet;
import com.example.toagigatron.tasks.outside.Prepot;
import com.example.toagigatron.tasks.outside.RechargeItems;
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
    ToaGigatronInfoBox toaGigatronInfoBox;

    @Inject
    ToaGigatronOverlay toaGigatronOverlay;

    @Inject
    ConsumablesTrackerInfobox consumablesTrackerInfobox;
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

    @Provides
    ToaGigatronConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ToaGigatronConfig.class);
    }
    protected Class<?>[] tasks() {
        return new Class[]{
			BabaPuzzlePrayerHandler.class,
			BabaFixPillar.class,
			BabaFixVent.class,
			BabaAvoidExplosion.class,
			BabaMoveOffPoison.class,
			BabaAttackMonkey.class,
			BabaEnterPuzzle.class,
			BabaGetHammerPotion.class,
			BabaConsumables.class,
			BabaExitPuzzle.class,
			ProgressStage.class,
			CheckCharges.class,
//			DisablePrayers.class,
//			DropVial.class,
//			LeaveBossRoom.class,
//			RefillSupplies.class,
			TakeOffGear.class,
//			ToggleRun.class,
			Bank.class,
			ClaimDeath.class,
			ClaimLootOutside.class,
			CreateParty.class,
			EnterRaid.class,
			GetSupplies.class,
			PickupPet.class,
			Prepot.class,
			RechargeItems.class,
//			BabaAttackMonkey.class,
        };
    }

    @Override
    protected void startUp()
    {
        finishRaid = false;
        stopPlugin = false;
        startState();
        overlayManager.add(toaGigatronInfoBox);
        overlayManager.add(toaGigatronOverlay);
        overlayManager.add(consumablesTrackerInfobox);
        Class<?>[] tasks = this.tasks();
        this.manager.registerTasks(this.getInjector(), tasks);
        this.manager.start();

        resetAllModels();

        this.gameTickManager.register();
        this.toaManager.register();
        this.toaManager.chargesTracker.register();
//        this.toaManager.kephri.register();
        this.toaManager.baba.register();
//        this.toaManager.akkha.register();
        this.toaManager.overall.register();
//        this.toaManager.zebak.register();
//        this.toaManager.wardens12.register();
//        this.toaManager.wardens3.register();
//        this.toaManager.inside.register();
        this.toaManager.outside.register();
        this.toaManager.consumableTracker.register();
        this.toaManager.overall.fullReset();
    }
    @Override
    protected void shutDown()
    {
        finishRaid = false;
        stopPlugin = false;
        stopState();
        overlayManager.remove(toaGigatronInfoBox);
        overlayManager.remove(toaGigatronOverlay);
        overlayManager.remove(consumablesTrackerInfobox);
        this.manager.stop();

        resetAllModels();

//        this.toaManager.wardens12.unregister();
//        this.toaManager.wardens3.unregister();
//        this.toaManager.kephri.unregister();
        this.toaManager.baba.unregister();
        this.toaManager.chargesTracker.reset();
        this.toaManager.chargesTracker.unregister();
//        this.toaManager.akkha.unregister();
        this.toaManager.overall.unregister();
        this.toaManager.unregister();
        this.gameTickManager.unregister();
//        this.toaManager.zebak.unregister();
//        this.toaManager.inside.unregister();
        this.toaManager.outside.unregister();
        this.toaManager.consumableTracker.unregister();
        this.toaManager.overall.fullReset();
    }
    public void resetAllModels()
    {
       this.toaManager.overall.reset();
        this.toaManager.initialiseSetups();
//        this.toaManager.zebak.resetVariables();
//        this.toaManager.kephri.resetVariables();
        this.toaManager.baba.resetVariables();
//        this.toaManager.akkha.resetVariables();
        this.toaManager.inside.resetVariables();
        this.toaManager.outside.resetVariables();
//        this.toaManager.wardens12.resetVariables();
//        this.toaManager.wardens3.resetVariables();
    }

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
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
    public void onGameStateChanged(GameStateChanged event){
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

