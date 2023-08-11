package com.example.toagigatron.tasks.akkha.puzzle;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Reachable;
import com.example.Utility.TileItemUtil;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.TileObject;
import org.apache.commons.lang3.ArrayUtils;


@TaskDescriptor(
	name = "Akkha exit puzzle",
	priority = 1
)
public class AkkhaExitPuzzle extends StagedTask
{
	@Inject
	public AkkhaExitPuzzle(ToaManager toaManager)
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
		if (exit == null || !Reachable.isWalkable(exit.getWorldLocation().dx(1)))
		{
			return false;
		}
		TileObject pickaxeStatue = TileObjects.search().withId(ToaConstants.AKKHA_PICKAXE_STATUE).nearestToPlayer().orElse(null);
		if (pickaxeStatue == null)
		{
			return false;
		}
		if (toaManager.akkha.hasPickaxe())
		{
			toaManager.print("Depositing pickaxe");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(pickaxeStatue, false, "Deposit-pickaxe");
			return true;
		}
//		int[] potentialPotions = Consumables.BREW.stream().mapToInt(i -> i).toArray();
//		int[] potentialPotions2 = Consumables.RESTORE.stream().mapToInt(i -> i).toArray();
//		int[] listPotions = ArrayUtils.addAll(potentialPotions, potentialPotions2);
//		ArrayList<ETileItem> tileItems = TileItemUtil.getAllETileItems(listPotions);
		ArrayList<ETileItem> tileItems = toaManager.getTileItemSupplies();
		if (!tileItems.isEmpty() && !InventoryUtil.isFull())
		{
			if (Reachable.isWalkable(tileItems.get(0).location))
			{
				MousePackets.queueClickPacket();
				tileItems.get(0).interact(false);
				return true;
			}
		}
		toaManager.print("Entering akkha");
		if (!EthanApiPlugin.isMoving())
		{
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(exit, false, "Quick-Enter");
		}

		return true;
	}
}
