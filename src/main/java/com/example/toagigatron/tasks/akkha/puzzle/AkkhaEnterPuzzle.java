package com.example.toagigatron.tasks.akkha.puzzle;


import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Reachable;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Akkha enter puzzle",
	priority = 1
)
public class AkkhaEnterPuzzle extends StagedTask
{
	@Inject
	public AkkhaEnterPuzzle(ToaManager toaManager)
	{
		super(toaManager, Stage.AKKHA_PUZZLE);
	}

	public boolean execute()
	{
		if (toaManager.akkha.isPuzzleActive())
		{
			return false;
		}
		TileObject exit = TileObjects.search().withId(ToaConstants.AKKHA_PUZZLE_EXIT).nearestToPlayer().orElse(null);
		if (exit == null || Reachable.isWalkable(exit.getWorldLocation().dx(1)))
		{
			return false;
		}
		TileObject pickaxeStatue = TileObjects.search().withId(ToaConstants.AKKHA_PICKAXE_STATUE).nearestToPlayer().orElse(null);
		TileObject barrier = TileObjects.search().withId(ToaConstants.BARRIER).nearestToPlayer().orElse(null);

		if (pickaxeStatue == null || barrier == null)
		{
			return false;
		}
		if (!toaManager.akkha.hasPickaxe())
		{
			if (EthanApiPlugin.isMoving())
			{
				return false;
			}
			else
			{
				Widget brew = Consumables.getBrew();
				if (InventoryUtil.isFull() && brew != null)
				{
					MousePackets.queueClickPacket();
					WidgetPackets.queueWidgetAction(brew, "Drop");
				}
				toaManager.print("Grabbing pickaxe");
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(pickaxeStatue, false, "Take-pickaxe");
			}
		}
		else
		{
			if (EthanApiPlugin.isMoving())
			{
				return false;
			}
			else
			{
				toaManager.print("Entering barrier");
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(barrier, false, "Quick-Pass");
			}
		}
		return true;
	}


}
