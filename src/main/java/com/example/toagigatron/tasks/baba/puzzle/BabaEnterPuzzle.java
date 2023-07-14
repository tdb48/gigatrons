package com.example.toagigatron.tasks.baba.puzzle;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Reachable;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.TileObject;

@TaskDescriptor(
	name = "Baba enter puzzle",
	priority = 1
)
public class BabaEnterPuzzle extends StagedTask
{

	@Inject
	public BabaEnterPuzzle(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE);
	}

	public boolean execute()
	{
		if (toaManager.baba.isPuzzleActive())
		{
			return false;
		}
		if(!Widgets.search().withTextContains("Challenge started: Path of Apmeken").empty()){
			toaManager.print("Already started room i do not need to click again");
			return false;
		}
		TileObject exit = TileObjects.search().withId(ToaConstants.BABA_PUZZLE_EXIT).first().orElse(null);
		if (exit != null && Reachable.isWalkable(exit.getWorldLocation().dx(-1)))
		{
			return false;
		}
		TileObject entry = TileObjects.search().withId(ToaConstants.BARRIER).first().orElse(null);
		if (entry != null)
		{
			toaManager.print("Entering puzzle ");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(entry, false, "Quick-Pass");
			return true;
		}

		return false;
	}
}
