package com.example.toagigatron.tasks.baba.puzzle;

import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;

import javax.inject.Inject;
import java.util.List;

@TaskDescriptor(
	name = "Baba avoid explosion",
	priority = 0,
	blocking = true
)
public class BabaAvoidExplosion extends StagedTask
{
	private final ToaManager toaManager;

	@Inject
	public BabaAvoidExplosion(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE);
		this.toaManager = toaManager;
	}

	public boolean execute()
	{
		if (!toaManager.baba.isPuzzleActive() || toaManager.baba.explosionTick != 3)
		{
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		if (!toaManager.baba.explosionTiles.contains(playerPoint))
		{
			toaManager.print("No need to dodge explosion");
			return false;
		}
		WorldArea dodgeArea = new WorldArea(playerPoint.dx(-2).dy(-2), playerPoint.dx(3).dy(3));
		WorldPoint targetPoint = safeTile(dodgeArea.toWorldPointList());
		if (targetPoint != null)
		{
			toaManager.print("Walking to non explosion tile");
			Movement.walk(targetPoint);
			return true;
		}
		return false;
	}

	private WorldPoint safeTile(List<WorldPoint> potentialTiles)
	{
		WorldPoint targetPoint = null;
		WorldPoint player = client.getLocalPlayer().getWorldLocation();
		for (WorldPoint wp : potentialTiles)
		{
			int distance = wp.distanceTo(player);
			if (!toaManager.baba.poisonTiles.contains(wp)
				&& !toaManager.baba.explosionTiles.contains(wp)
				&& isDiagonalVerticalHorizontal(player, wp)
				&& Reachable.isWalkable(wp)
					&& hasClearPath(player, wp, distance))
			{
				toaManager.print("Found safetile at " + wp);
				targetPoint = wp;
				break;
			}
		}
		return targetPoint;
	}

	public boolean hasClearPath(WorldPoint playerPoint, WorldPoint destination, int distance){

		int pX = playerPoint.getX();
		int pY = playerPoint.getY();
		int dX = destination.getX();
		int dY = destination.getY();
		WorldPoint candidateTile;
		WorldPoint candidateTileTwo;
		WorldPoint candidateTileThree;
		WorldPoint candidateTileFour;
		WorldPoint candidateTileFive;
		if(Math.abs(playerPoint.getX() - destination.getX()) == Math.abs(playerPoint.getY() - destination.getY())){
			if(distance == 2){
				if(pX > dX && pY > dY){
					//South west
					candidateTile = playerPoint.dy(-1);
					candidateTileTwo = playerPoint.dy(-1).dx(-1);
					candidateTileThree = playerPoint.dx(-1);
					candidateTileFour = playerPoint.dx(-1).dy(-2);
					candidateTileFive = playerPoint.dx(-2).dy(-1);
				}
				else if(pX > dX && pY < dY){
					//North west
					candidateTile = playerPoint.dy(1);
					candidateTileTwo = playerPoint.dy(1).dx(-1);
					candidateTileThree = playerPoint.dx(-1);
					candidateTileFour = playerPoint.dx(-1).dy(2);
					candidateTileFive = playerPoint.dx(-2).dy(1);
				}
				else if(pX < dX && pY < dY){
					//North east
					candidateTile = playerPoint.dy(1);
					candidateTileTwo = playerPoint.dy(1).dx(1);
					candidateTileThree = playerPoint.dx(1);
					candidateTileFour = playerPoint.dx(1).dy(2);
					candidateTileFive = playerPoint.dx(2).dy(1);
				}
				//
				else {
					//South East
					candidateTile = playerPoint.dy(-1);
					candidateTileTwo = playerPoint.dy(-1).dx(1);
					candidateTileThree = playerPoint.dx(1);
					candidateTileFour = playerPoint.dx(1).dy(-2);
					candidateTileFive = playerPoint.dx(2).dy(-1);
				}
				return (Reachable.isWalkable(candidateTile) && !Reachable.isObstacle(candidateTile)) &&
						(Reachable.isWalkable(candidateTileTwo) && !Reachable.isObstacle(candidateTileTwo)) &&
						(Reachable.isWalkable(candidateTileThree) && !Reachable.isObstacle(candidateTileThree)) &&
						(Reachable.isWalkable(candidateTileFour) && !Reachable.isObstacle(candidateTileFour)) &&
						(Reachable.isWalkable(candidateTileFive) && !Reachable.isObstacle(candidateTileFive));
			} else {
				if(pX > dX && pY > dY){
					//South west
					candidateTile = playerPoint.dy(-1);
					candidateTileTwo = playerPoint.dx(-1);
				}
				else if(pX > dX && pY < dY){
					//North west
					candidateTile = playerPoint.dy(1);
					candidateTileTwo = playerPoint.dx(-1);
				}
				else if(pX < dX && pY < dY){
					//North east
					candidateTile = playerPoint.dy(1);
					candidateTileTwo = playerPoint.dx(1);
				}
				//
				else {
					//South East
					candidateTile = playerPoint.dy(-1);
					candidateTileTwo = playerPoint.dx(1);
				}
				return (Reachable.isWalkable(candidateTile) && !Reachable.isObstacle(candidateTile)) &&
						(Reachable.isWalkable(candidateTileTwo) && !Reachable.isObstacle(candidateTileTwo));
			}
		} else {
			if(distance == 1){
				return true;
			}
			//vertical or horizontal straight line
			if(pX == dX){
				//North or south
				if(pY > dY){
					//South
					candidateTile = playerPoint.dy(-1);
				} else {
					//North
					candidateTile = playerPoint.dy(1);
				}
			} else {
				//East or west
				if(pX < dX){
					//east
					candidateTile = playerPoint.dx(1);
				} else {
					//west
					candidateTile = playerPoint.dx(-1);
				}
			}
			return Reachable.isWalkable(candidateTile) && !Reachable.isObstacle(candidateTile);
		}
	}

	public boolean isDiagonalVerticalHorizontal(WorldPoint playerPoint, WorldPoint destinationPoint)
	{
		if (playerPoint.getY() == destinationPoint.getY())
		{
			return true;
		}
		if (playerPoint.getX() == destinationPoint.getX())
		{
			return true;
		}
		return Math.abs(playerPoint.getX() - destinationPoint.getX()) == Math.abs(playerPoint.getY() - destinationPoint.getY());
	}

	private boolean containsObject(GameObject[] objects)
	{
		for (int i = 0; i < objects.length; i++)
		{
			if (objects[i] != null && objects[i].getId() == ToaConstants.BABA_PUZZLE_POISON)
			{
				return true;
			}
		}
		return false;
	}
}
