package com.example.nexatron.tasks.bank;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Utility.Game;
import com.example.nexatron.NexatronPlugin;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.client.game.ItemManager;

@TaskDescriptor(
	name = "Break or log",
	priority = Integer.MAX_VALUE,
	blocking = true
)
public class BreakLog extends StagedTask
{
	@Inject
	ItemManager itemManager;

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
			EthanApiPlugin.stopPlugin(plugin);
			return true;
		}
		return false;

	}
}
