package com.example.toagigatron.tasks.baba.puzzle;

import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.GameObject;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.queries.GameObjectQuery;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.scene.Tiles;

import javax.inject.Inject;

@TaskDescriptor(
	name = "Baba move off poison",
	priority = 0,
	blocking = true
)
public class BabaMoveOffPoison extends StagedTask
{
	private final ToaManager toaManager;

	@Inject
	public BabaMoveOffPoison(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE);
		this.toaManager = toaManager;
	}

	public boolean execute()
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		GameObject poison = new GameObjectQuery().idEquals(ToaConstants.BABA_PUZZLE_POISON).atWorldLocation(playerPoint).result(client).first();
		if (poison == null)
		{
			return false;
		}
		Tile currentTile = Tiles.getAt(playerPoint);
		if (currentTile != null)
		{
			if (currentTile.getGameObjects() != null)
			{
				toaManager.print("poison tile gameobjects size -> " + currentTile.getGameObjects().length);
				for (GameObject obj : currentTile.getGameObjects())
				{
					if (obj == null)
					{
						toaManager.print("Obj somehow null?");
					}
					else
					{
						toaManager.print("Obj id -> " + obj.getId());
					}

				}
			}
		}
		WorldPoint safeTile = getSafeTile(playerPoint);
		if (safeTile != null)
		{
			toaManager.print("STOOD ON POISON!!!!!!! MOVING TO " + safeTile);
			Movement.walk(safeTile);
			return true;
		}
		return false;
	}

	private WorldPoint getSafeTile(WorldPoint player)
	{
		WorldArea area = new WorldArea(player.dx(-2).dy(-2), player.dx(3).dy(3));
		for (WorldPoint wp : area.toWorldPointList())
		{
			if (wp.distanceTo(player) > 3)
			{
				continue;
			}
			Tile currentTile = Tiles.getAt(wp);
			if (currentTile.getGameObjects() != null
				&& !toaManager.containsObjectBaba(currentTile.getGameObjects())
				&& Reachable.isWalkable(wp))
			{
				return wp;
			}
		}
		return null;
	}
}


