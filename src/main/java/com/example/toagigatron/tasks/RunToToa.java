package com.example.toagigatron.tasks;

import com.example.EthanApiPlugin.PathFinding.GlobalCollisionMap;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Walker;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Running to TOA",
	priority = 80
)
public class RunToToa extends StagedTask
{
	private final static WorldPoint PYRAMID_ENTRANCE = new WorldPoint(3356, 2713, 0);
	private final static int STEPPING_STONE_ONE = 43989;
	private final static int STEPPING_STONE_TWO = 43990;
	private final static WorldArea ISLAND_ONE = WorldAreas.createArea(
		new WorldPoint(3281, 2699, 0),
		new WorldPoint(3290, 2707, 0));
	private final static WorldArea ISLAND_TWO = WorldAreas.createArea(
		new WorldPoint(3292, 2698, 0),
		new WorldPoint(3298, 2705, 0));
	private final static int TOA_PYRAMID_ENTRY = 44596;

	@Inject
	public RunToToa(ToaManager toaManager)
	{
		super(toaManager, Stage.OUTSIDE_TOA);
	}


	public boolean execute()
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		TileObject steppingStoneOne = ObjectUtil.getTileObjectAt(STEPPING_STONE_ONE, new WorldPoint(3291, 2700, 0));
		TileObject steppingStoneTwo = ObjectUtil.getNearestTileObject(STEPPING_STONE_TWO);

		if (ISLAND_ONE.contains(playerPoint)
			&& steppingStoneOne != null
			&& ObjectUtil.hasAction(steppingStoneOne, "Cross"))
		{
			if (client.getLocalPlayer().getAnimation() == ToaConstants.STEPPING_STONE_ANIMATION)
			{
				return true;
			}
			toaManager.print("Crossing first stone at " + toaManager.worldPointString(steppingStoneOne.getWorldLocation()));
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(steppingStoneOne, false, "Cross");
			return true;
		}
		else if (ISLAND_TWO.contains(playerPoint)
			&& steppingStoneTwo != null
			&& ObjectUtil.hasAction(steppingStoneTwo, "Cross"))
		{
			if (client.getLocalPlayer().getAnimation() == ToaConstants.STEPPING_STONE_ANIMATION)
			{
				return true;
			}
			toaManager.print("Crossing second stone at " + toaManager.worldPointString(steppingStoneTwo.getWorldLocation()));
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(steppingStoneTwo, false, "Cross");
			return true;
		}
		GameObject entry = ObjectUtil.getNearestGameObject(TOA_PYRAMID_ENTRY);
		if (entry != null
			&& entry.getWorldLocation().distanceTo(playerPoint) <= 10)
		{
			toaManager.print("Clicking entrance");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(entry, false, "Enter");
		}
		else
		{
			ArrayList<WorldPoint> path = (ArrayList<WorldPoint>) GlobalCollisionMap.findPath(PYRAMID_ENTRANCE);
			toaManager.print("Walking next to tunnel");
			Walker.stepAlongBigSteps(path);
		}
		return true;
	}
}
