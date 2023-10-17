package com.example.nexatron.tasks.bank;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Utility.Game;
import com.example.Utility.Hopping;
import com.example.Utility.WorldAreas;
import com.example.nexatron.NexatronPlugin;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.WorldService;

@TaskDescriptor(
	name = "Break or log",
	priority = Integer.MAX_VALUE,
	blocking = true,
	register = true
)
public class BreakLogHop extends StagedTask
{
	@Inject
	WorldService worldService;
	public static final WorldPoint SOUTH_WEST = new WorldPoint(2902, 5200, 0);
	public static final WorldPoint NORTH_EAST = new WorldPoint(2908, 5206, 0);
	public static final WorldArea BREAK_AREA = WorldAreas.createArea(SOUTH_WEST, NORTH_EAST);
	WorldPoint logOutTile = new WorldPoint(2902, 5200, 0);
	@Inject
	NexatronPlugin plugin;

	@Inject
	GameTickManager gameTickManager;

	@Inject
	public BreakLogHop(NexManager nexManager)
	{
		super(nexManager, Stage.BANK);
	}

	public boolean execute()
	{
		if (plugin.finishKill)
		{
			if (gameTickManager.isTickWaiting())
			{
				return true;
			}
			gameTickManager.setTickWait(4);
			nexManager.print("Logging out");
			Game.logout();
			return true;
		}
		if (nexManager.socket.world != nexManager.socket.otherWorld
			&& nexManager.socket.otherWorld != -1
			&& nexManager.socket.isSlave()
			&& !nexManager.shouldKc())
		{
			if (gameTickManager.isTickWaiting())
			{
				return true;
			}
			gameTickManager.setTickWait(4);
			nexManager.print("Hopping to master world which is " + nexManager.socket.otherWorld);
			Hopping.hop(nexManager.socket.otherWorld, worldService);
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
