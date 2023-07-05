package com.example.nexatron.tasks;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.Utility.Static;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.model.constants.NexConstants;
import com.example.nexatron.taskformat.Task;
import com.example.nexatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import java.util.ArrayList;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	priority = 20,
	name = "Updating stage"
)
public class ProgressStage extends Task
{
	private final NexManager nexManager;

	@Inject
	public ProgressStage(NexManager nexManager)
	{
		this.nexManager = nexManager;
	}

	public boolean run()
	{
		ArrayList<Integer> regions = new ArrayList<>();
		for (int i : Static.getClient().getMapRegions())
		{
			regions.add(i);
		}
		if (regions.contains(99999))
		{
			nexManager.setStage(Stage.KC_AREA);
			return true;
		}
		else
		{
			nexManager.setStage(Stage.NONE);
			return false;
		}
	}
}
