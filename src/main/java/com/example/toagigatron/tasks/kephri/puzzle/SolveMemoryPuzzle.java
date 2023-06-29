package com.example.toagigatron.tasks.kephri.puzzle;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Movement;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.puzzlemodel.KephriPuzzleRoom;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Solve Memory Puzzle",
	priority = 1
)
public class SolveMemoryPuzzle extends StagedTask
{
	private final ToaManager toaManager;

	private final GameTickManager gameTickManager;

	@Inject
	private Client client;

	@Inject
	public SolveMemoryPuzzle(ToaManager toaManager, GameTickManager gameTickManager)
	{
		super(toaManager, Stage.KEPHRI_PUZZLE);
		this.toaManager = toaManager;
		this.gameTickManager = gameTickManager;
	}


	@Override
	public boolean execute()
	{
		// || !toaManager.kephri.enteredKephriBarrier
		//Check if its solved or whatever aswell
		if (toaManager.kephri.currentKephriPuzzle == null || !toaManager.kephri.currentKephriPuzzle.roomType.equals(KephriPuzzleRoom.RoomType.MEMORY))
		{
			//toaManager.print("Puzzleroom type is not memory");
			return false;
		}
		GameObject button = ObjectUtil.getNearestGameObject(ToaConstants.KEPHRI_ANCIENT_BUTTON);


		// if(client.getLocalPlayer().isMoving()){
		//  toaManager.print("player is moving");
		// return false;
		// }
		if (button == null)
		{
//			toaManager.print("Button is null");
			return false;
		}
		//Need to push the button as we have no tiles in our list
		if (toaManager.kephri.memory_tiles.size() == 0)
		{
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(button, false, "Push");
			gameTickManager.setTickWait(6);
			return false;
		}
		else if (toaManager.kephri.memory_tiles.size() != 5)
		{
			//toaManager.print("Somehow less than 5 memory tiles");
			return false;
		}
		else if (toaManager.kephri.memory_completed_tiles.size() == 5)
		{
			//toaManager.print("Puzzle completed, moving on");
			return true;
		}
		else
		{
			//Local player location and the location of the 4 tiles to be used as intermediate locations between memory tiles
			WorldPoint playerWorldLoc = client.getLocalPlayer().getWorldLocation();
			WorldPoint northSafeTile = new WorldPoint(button.getX() + 4, button.getY() - 1, button.getPlane());
			WorldPoint southSafeTile = new WorldPoint(button.getX() + 4, button.getY() - 3, button.getPlane());
			WorldPoint eastSafeTile = new WorldPoint(button.getX() + 5, button.getY() - 2, button.getPlane());
			WorldPoint westSafeTile = new WorldPoint(button.getX() + 3, button.getY() - 2, button.getPlane());
			ArrayList<WorldPoint> safeTiles = new ArrayList<>(List.of(northSafeTile, eastSafeTile, westSafeTile, southSafeTile));

			WorldPoint nextMemoryTileLoc = null;
			GameObject nextMemoryTileObject = null;

			for (GameObject memoryLocation : toaManager.kephri.memory_tiles)
			{
				if (!toaManager.kephri.memory_completed_tiles.contains(memoryLocation) && nextMemoryTileLoc == null)
				{
					nextMemoryTileLoc = memoryLocation.getWorldLocation();
					nextMemoryTileObject = memoryLocation;
				}
			}
			if (nextMemoryTileLoc == null)
			{
				//toaManager.print("Next memory tile is null");
				return false;
			}

			//not on safe tile, not on memory tile meaning we have just pressed the button
			//move to starting loc
			if (!standingOnTile(safeTiles, playerWorldLoc) && !standingOnTile(generateMemoryTileLocs(toaManager.kephri.memory_tiles), playerWorldLoc))
			{
				Movement.walk(westSafeTile);
				//toaManager.print("Moving to west safe tile (start tile)");
				return false;
			}
			//standing on a safe tile and distance to next memory tile is 1 meaning we can safely walk to it
			else if (standingOnTile(safeTiles, playerWorldLoc) && playerWorldLoc.distanceTo(nextMemoryTileLoc) == 1)
			{
				Movement.walk(nextMemoryTileLoc);
				//toaManager.print("Moving to next memory tile");
				toaManager.kephri.memory_completed_tiles.add(nextMemoryTileObject);
				return false;
			}
			else if (standingOnTile(safeTiles, playerWorldLoc) && playerWorldLoc.distanceTo(nextMemoryTileLoc) > 1)
			{
				WorldPoint nextSafeTile = determineBestSafeTile(nextMemoryTileLoc, playerWorldLoc, safeTiles, 1);
				if (nextSafeTile != null)
				{
					Movement.walk(nextSafeTile);
					//toaManager.print("Moving to closer safe tile");
				}
				else
				{
					//toaManager.print("Next safe tile is somehow null");
				}
				return false;
			}//else if player on memory tile, move to nearest safe tile, or to next memory tile if its within 1 tile distance
			else if (standingOnTile(generateMemoryTileLocs(toaManager.kephri.memory_tiles), playerWorldLoc))
			{
				if (playerWorldLoc.distanceTo(nextMemoryTileLoc) == 1)
				{
					Movement.walk(nextMemoryTileLoc);
					//toaManager.print("Moving to next memory tile");
					toaManager.kephri.memory_completed_tiles.add(nextMemoryTileObject);
				}
				else
				{
					WorldPoint nextSafeTile = determineBestSafeTile(nextMemoryTileLoc, playerWorldLoc, safeTiles, 2);
					if (nextSafeTile != null)
					{
						Movement.walk(nextSafeTile);
						//toaManager.print("Moving to closer safe tile");
					}
					else
					{
						//toaManager.print("Next safe tile is somehow null");
					}
				}
				return false;

			}

		}
		return false;
	}

	private boolean standingOnTile(List<WorldPoint> tiles, WorldPoint loc)
	{
		for (WorldPoint p : tiles)
		{
			if (p.equals(loc))
			{
				return true;
			}
		}
		return false;
	}

	private List<WorldPoint> generateMemoryTileLocs(List<GameObject> objects)
	{
		List<WorldPoint> returnList = new ArrayList<>();
		for (GameObject obj : objects)
		{
			returnList.add(obj.getWorldLocation());
		}
		return returnList;
	}

	private WorldPoint determineBestSafeTile(WorldPoint nextMemoryTileLoc, WorldPoint playerLoc, List<WorldPoint> safeTiles, int maxTravelDistance)
	{
		WorldPoint worldPoint = null;
		int distance = Integer.MAX_VALUE;
		for (WorldPoint wp : safeTiles)
		{
			if (wp.distanceTo(playerLoc) <= maxTravelDistance)
			{
				if (wp.distanceTo(nextMemoryTileLoc) < distance)
				{
					distance = wp.distanceTo(nextMemoryTileLoc);
					worldPoint = wp;
				}
			}
		}
		return worldPoint;
	}

}
