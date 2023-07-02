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
import com.example.toagigatron.tasks.akkha.boss.*;
import com.example.toagigatron.tasks.akkha.puzzle.AkkhaEnterPuzzle;
import com.example.toagigatron.tasks.akkha.puzzle.AkkhaExitPuzzle;
import com.example.toagigatron.tasks.akkha.puzzle.AkkhaSolvePuzzle;
import com.example.toagigatron.tasks.baba.BabaConsumables;
import com.example.toagigatron.tasks.baba.boss.*;
import com.example.toagigatron.tasks.baba.puzzle.*;
import com.example.toagigatron.tasks.inside.ClaimLoot;
import com.example.toagigatron.tasks.inside.ClaimSupplies;
import com.example.toagigatron.tasks.inside.EnterPath;
import com.example.toagigatron.tasks.inside.Resign;
import com.example.toagigatron.tasks.kephri.boss.*;
import com.example.toagigatron.tasks.kephri.puzzle.*;
import com.example.toagigatron.tasks.outside.Bank;
import com.example.toagigatron.tasks.outside.ClaimDeath;
import com.example.toagigatron.tasks.outside.ClaimLootOutside;
import com.example.toagigatron.tasks.outside.CreateParty;
import com.example.toagigatron.tasks.outside.EnterRaid;
import com.example.toagigatron.tasks.outside.GetSupplies;
import com.example.toagigatron.tasks.outside.PickupPet;
import com.example.toagigatron.tasks.outside.Prepot;
import com.example.toagigatron.tasks.outside.RechargeItems;
import com.example.toagigatron.tasks.wardens.wardensp1.*;
import com.example.toagigatron.tasks.wardens.wardensp2.*;
import com.example.toagigatron.tasks.wardens.wardensp3.*;
import com.example.toagigatron.tasks.zebak.puzzle.EnterPuzzleZebak;
import com.example.toagigatron.tasks.zebak.puzzle.FinishPuzzleZebak;
import com.example.toagigatron.tasks.zebak.puzzle.SolvePuzzleZebak;
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
                ProgressStage.class,
                EnterPuzzleKephri.class,
                FinishPuzzleKephri.class,
                SolveLightPuzzle.class,
                SolveMemoryPuzzle.class,
                SolveMathPuzzle.class,
                SolvePillarPuzzle.class,
                CreateParty.class,
                EnterRaid.class,
                EnterPath.class,
                SwitchPuzzleKephri.class,
                EnterPuzzleZebak.class,
                SolvePuzzleZebak.class,
                FinishPuzzleZebak.class,
                SolveFinalPuzzle.class,
                KephriPrayerHandler.class,
                ToggleRun.class,
                KephriEnterRoom.class,
                KephriConsumables.class,
                LeaveBossRoom.class,
                BabaPuzzlePrayerHandler.class,
                KephriDodgeDung.class,
                KephriAttackBoss.class,
                BabaFixPillar.class,
                BabaFixVent.class,
                BabaAvoidExplosion.class,
                BabaMoveOffPoison.class,
                BabaAttackMonkey.class,
                AkkhaPrayerHandler.class,
                AkkhaDodgeMemory.class,
                BabaEnterPuzzle.class,
                BabaGetHammerPotion.class,
                BabaConsumables.class,
                BabaExitPuzzle.class,
                BabaHitBoulder.class,
                AkkhaAttackDemi.class,
                AkkhaAttackBoss.class,
                AkkhaEnterBoss.class,
                AkkhaAttackBossOrbs.class,
                AkkhaConsumables.class,
                BabaEnterBoss.class,
                BabaPrayerHandler.class,
                BabaDodgeSpecial.class,
                BabaAttackBoss.class,
                BabaAttackBossMonkey.class,
                ClaimSupplies.class,
                AkkhaEnterPuzzle.class,
                AkkhaExitPuzzle.class,
                AkkhaSolvePuzzle.class,
                Prepot.class,
                Bank.class,
                GetSupplies.class,
                ClaimDeath.class,
                DisablePrayers.class,
                StartBoss.class,
                ClaimLoot.class,
                PickupPet.class,
                WardensP1PrayerHandler.class,
                SolveOrbUfo.class,
                AttackObelisk.class,
                WardensP2PrayerHandler.class,
                AttackCore.class,
                AttackWardensP2.class,
                RefillSupplies.class,
                WardensP2DodgeSpecial.class,
                AttackWardensP3.class,
                LeaveBoss.class,
                WardenP3SkullSkip.class,
                WardenP3DodgeFloor.class,
                WardensP3Enrage.class,
                WardensP3PrayerHandler.class,
                DropVial.class,
                WardenP1Consumables.class,
                WardenP23Consumables.class,
                CheckCharges.class,
                RechargeItems.class,
                Resign.class,
                TakeOffGear.class,
                ClaimLootOutside.class,
                KephriAttackDemi.class
        };
    }

    @Override
    protected void startUp() throws Exception
    {
        finishRaid = false;
        stopPlugin = false;
        super.startUp();
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
        this.toaManager.chargesTracker.reset();
        this.toaManager.chargesTracker.register();
        this.toaManager.kephri.register();
        this.toaManager.baba.register();
        this.toaManager.akkha.register();
        this.toaManager.overall.register();
        this.toaManager.zebak.register();
        this.toaManager.wardens12.register();
        this.toaManager.wardens3.register();
        this.toaManager.inside.register();
        this.toaManager.outside.register();
        this.toaManager.consumableTracker.register();
        this.toaManager.overall.fullReset();
    }
    @Override
    protected void shutDown() throws Exception {
        finishRaid = false;
        stopPlugin = false;
        overlayManager.remove(toaGigatronInfoBox);
        overlayManager.remove(toaGigatronOverlay);
        overlayManager.remove(consumablesTrackerInfobox);
        this.manager.stop();
        super.shutDown();
        stopState();

        resetAllModels();

        this.toaManager.wardens12.unregister();
        this.toaManager.wardens3.unregister();
        this.toaManager.kephri.unregister();
        this.toaManager.baba.unregister();
        this.toaManager.chargesTracker.reset();
        this.toaManager.chargesTracker.unregister();
        this.toaManager.akkha.unregister();
        this.toaManager.overall.unregister();
        this.toaManager.unregister();
        this.gameTickManager.unregister();
        this.toaManager.zebak.unregister();
        this.toaManager.inside.unregister();
        this.toaManager.outside.unregister();
        this.toaManager.consumableTracker.unregister();
        this.toaManager.overall.fullReset();
    }
    public void resetAllModels()
    {
       this.toaManager.overall.reset();
        this.toaManager.initialiseSetups();
        this.toaManager.zebak.resetVariables();
        this.toaManager.kephri.resetVariables();
        this.toaManager.baba.resetVariables();
        this.toaManager.akkha.resetVariables();
        this.toaManager.inside.resetVariables();
        this.toaManager.outside.resetVariables();
        this.toaManager.wardens12.resetVariables();
        this.toaManager.wardens3.resetVariables();
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

