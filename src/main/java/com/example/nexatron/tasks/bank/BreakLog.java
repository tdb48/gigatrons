package com.example.nexatron.tasks.bank;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Utility.Game;
import com.example.nexatron.NexatronPlugin;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Break or log",
	priority = Integer.MAX_VALUE,
	blocking = true,
	register = true
)
public class BreakLog extends StagedTask
{
	@Inject
	NexatronPlugin plugin;

	@Inject
	public BreakLog(NexManager nexManager)
	{
		super(nexManager, Stage.BANK);
	}

	public boolean execute()
	{
		if (plugin.finishKill)
		{
			nexManager.print("Logging out");
			Game.logout();
			return true;
		}
		return false;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN)
		{
			if (plugin.finishKill)
			{
				plugin.finishKill = false;
				EthanApiPlugin.stopPlugin(plugin);
			}
		}
	}
}
