package com.example.toagigatron.tasks.baba.puzzle;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.TileItemPackets;
import com.example.Utility.Reachable;
import com.example.Utility.TileItemUtil;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.TileObject;

@TaskDescriptor(
	name = "Baba exit puzzle",
	priority = 1
)
public class BabaExitPuzzle extends StagedTask
{
	@Inject
	public BabaExitPuzzle(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE);
	}

	public boolean execute()
	{
		if (toaManager.baba.isPuzzleActive())
		{
			return false;
		}
		if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItems()))
		{
			toaManager.print("Switching to melee gear");
			toaManager.swap(toaManager.meleeSetup.getAllItems());
		}
		TileObject exit = TileObjects.search().withId(ToaConstants.BABA_PUZZLE_EXIT).first().orElse(null);
		ArrayList<ETileItem> tileItems = TileItemUtil.getAllETileItems("Saradomin brew(4)", "Super restore(4)");
		if (!tileItems.isEmpty() && Inventory.getEmptySlots() != 0)
		{
			MousePackets.queueClickPacket();
			TileItemPackets.queueTileItemAction(tileItems.get(0), false);
			return true;
		}
		else if (exit != null && Reachable.isWalkable(exit.getWorldLocation().dx(-1)))
		{
			toaManager.print("Exiting baba puzzle");

			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(exit, false, "Quick-Enter");
			return true;
		}

		return false;
	}
}