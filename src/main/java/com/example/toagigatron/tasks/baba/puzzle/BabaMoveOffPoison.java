package com.example.toagigatron.tasks.baba.puzzle;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Utility.Movement;
import com.example.Utility.Reachable;
import com.example.Utility.Tiles;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.Tile;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

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
		TileObject poison = TileObjects.search().withId(ToaConstants.BABA_PUZZLE_POISON).atLocation(playerPoint).first().orElse(null);
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
		WorldArea area = WorldAreas.createArea(player.dx(-2).dy(-2), player.dx(3).dy(3));
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


