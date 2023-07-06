package com.example.nexatron;

import com.example.Utility.Dialog;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.overlay.NexatronInfoBox;
import com.example.nexatron.overlay.NexatronOverlay;
import com.example.nexatron.taskformat.TaskManager;
import com.example.nexatron.tasks.general.DisablePrayers;
import com.example.nexatron.tasks.general.DropVial;
import com.example.nexatron.tasks.general.ProgressStage;
import com.example.nexatron.tasks.general.Reattack;
import com.example.nexatron.tasks.general.ToggleRun;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
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

@PluginDescriptor(
	name = "Nexatron",
	description = "DOES Nex FOR YOU",
	tags = "nexatron,nex,tron")
@Slf4j
public class NexatronPlugin extends Plugin
{

	@Inject
	public NexatronConfig config;
	@Inject
	public NexManager nexManager;
	public boolean stopPlugin = false;
	public boolean finishKill = false;
	@Inject
	Client client;
	@Inject
	PluginManager pluginManager;
	@Inject
	NexatronInfoBox nexatronInfoBox;
	@Inject
	NexatronOverlay nexatronOverlay;
	@Inject
	OverlayManager overlayManager;
	@Inject
	private TaskManager manager;
	@Inject
	private GameTickManager gameTickManager;
	@Inject
	private ReflectBreakHandler chinBreakHandler;

	@Provides
	NexatronConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(NexatronConfig.class);
	}

	protected Class<?>[] tasks()
	{
		return new Class[]{
			ProgressStage.class,
			ToggleRun.class,
			DropVial.class,
			DisablePrayers.class,
			Reattack.class,
		};
	}

	@Override
	protected void startUp() throws Exception
	{
		chinBreakHandler.registerPlugin(this);
		chinBreakHandler.startPlugin(this);
		nexManager.allowedToBreak = false;
		finishKill = false;
		stopPlugin = false;
		super.startUp();
		overlayManager.add(nexatronInfoBox);
		overlayManager.add(nexatronOverlay);
		Class<?>[] tasks = this.tasks();
		this.manager.registerTasks(this.getInjector(), tasks);
		this.manager.start();
		this.gameTickManager.register();
		this.nexManager.register();
		this.nexManager.chargesTracker.reset();
		this.nexManager.chargesTracker.register();
		this.nexManager.nex.register();
		this.nexManager.overall.register();
		this.nexManager.overall.fullReset();
	}

	@Override
	protected void shutDown() throws Exception
	{
		chinBreakHandler.unregisterPlugin(this);
		chinBreakHandler.stopPlugin(this);
		nexManager.allowedToBreak = false;
		finishKill = false;
		stopPlugin = false;
		overlayManager.remove(nexatronInfoBox);
		overlayManager.remove(nexatronOverlay);
		this.manager.stop();
		super.shutDown();
		this.nexManager.chargesTracker.unregister();
		this.nexManager.overall.unregister();
		this.nexManager.unregister();
		this.gameTickManager.unregister();
		this.nexManager.nex.unregister();
		this.nexManager.overall.unregister();
		this.nexManager.chargesTracker.reset();
		this.nexManager.overall.fullReset();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged configChanged)
	{
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState().equals(GameState.LOGIN_SCREEN) && chinBreakHandler.isBreakActive(this))
		{
			nexManager.allowedToBreak = false;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) throws PluginInstantiationException
	{

		if (Dialog.canLevelUpContinue())
		{
			Dialog.continueSpace();
		}
	}
}

