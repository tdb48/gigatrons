package com.example.toagigatron.tasks.baba.boss;

import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;

import java.util.ArrayList;

@TaskDescriptor(
	name = "Baba solve rockfall",
	priority = 1,
	blocking = true
)
public class OLD_BabaSolveRockfall extends StagedTask
{
	@Inject
	public OLD_BabaSolveRockfall(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_BOSS);
	}

	public boolean execute()
	{
		if (toaManager.baba.rockfallTiles.isEmpty() || toaManager.baba.rockfallTick == 0)
		{
			return false;
		}
		ArrayList<WorldPoint> targetTiles = toaManager.baba.rockfallTiles;
		targetTiles.removeAll(toaManager.baba.shockwaveTiles);
		targetTiles.removeAll(toaManager.baba.bananaTiles);
		targetTiles.removeIf(n -> !Reachable.isWalkable(n));
		WorldPoint targetPoint = toaManager.findClosestTile(targetTiles);
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		if (targetPoint == null || playerPoint.equals(targetPoint))
		{
			toaManager.baba.solvingSpecial = false;
			return false;
		}
		toaManager.print("Rockfall moving to " + toaManager.worldPointString(targetPoint));
		Movement.walk(targetPoint);
		toaManager.baba.solvingSpecial = true;
		return true;
	}
}
