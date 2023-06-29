package com.example.toagigatron.tasks.baba.boss;

import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;

import java.util.ArrayList;

@TaskDescriptor(
	name = "Baba solve shockwave",
	priority = 1
)
public class OLD_BabaSolveShockwave extends StagedTask
{
	@Inject
	public OLD_BabaSolveShockwave(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_BOSS);
	}

	public boolean execute()
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();

		if (toaManager.baba.babaBoss == null
			|| (toaManager.baba.shockwaveTiles.isEmpty() && toaManager.baba.rockFromCeiling.isEmpty())
			|| (!toaManager.baba.shockwaveTiles.contains(playerPoint) && !toaManager.baba.rockFromCeiling.contains(playerPoint)))
		{
			return false;
		}
		WorldPoint refPoint = toaManager.baba.babaBoss.getWorldArea().getCenter();
		WorldPoint southWest = refPoint.dx(-3).dy(-3);
		WorldPoint northEast = refPoint.dx(4).dy(4);
		ArrayList<WorldPoint> babaMeleeTiles = (ArrayList<WorldPoint>) new WorldArea(southWest, northEast).toWorldPointList();
		babaMeleeTiles.removeAll(toaManager.baba.shockwaveTiles);
		babaMeleeTiles.removeAll(toaManager.baba.rockFromCeiling);
		babaMeleeTiles.removeAll(toaManager.baba.babaBoss.getWorldArea().toWorldPointList());
		babaMeleeTiles.removeAll(toaManager.baba.bananaTiles);
		babaMeleeTiles.removeIf(n -> !Reachable.isWalkable(n));
		babaMeleeTiles.removeIf(n -> !toaManager.isDiagonalOf(n, toaManager.baba.babaBoss.getWorldArea().getCenter()));
		WorldPoint targetPoint = toaManager.findClosestTile(babaMeleeTiles);
		if (playerPoint.equals(targetPoint))
		{
			toaManager.baba.solvingSpecial = false;
			return false;
		}
		if (targetPoint != null)
		{
			toaManager.print("Shockwave moving to " + toaManager.worldPointString(targetPoint));
			Movement.walk(targetPoint);
			toaManager.baba.solvingSpecial = true;
		}
		else
		{
			ArrayList<WorldPoint> panicTiles = toaManager.baba.babaBossRoom;
			panicTiles.removeAll(toaManager.baba.shockwaveTiles);
			WorldPoint panicPoint = toaManager.findClosestTile(panicTiles);
			if(panicPoint == null){
				System.out.println("Panic point is somehow null in baba solve shoickwave");
				return false;
			}
			toaManager.print("SAOMETIHNG WENT VERY WRONG to " + toaManager.worldPointString(panicPoint));
			Movement.walk(panicPoint);
		}
		return true;
	}
}
