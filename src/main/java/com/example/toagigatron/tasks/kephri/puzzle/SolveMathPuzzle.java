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
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Solve math puzzle",
	priority = 1,
	register = true
)
public class SolveMathPuzzle extends StagedTask
{
	private final ToaManager toaManager;

	@Inject
	private Client client;

	@Inject
	public SolveMathPuzzle(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_PUZZLE);
		this.toaManager = toaManager;
	}

	public boolean execute()
	{
		if (toaManager.kephri.currentKephriPuzzle == null || !toaManager.kephri.currentKephriPuzzle.roomType.equals(KephriPuzzleRoom.RoomType.MATH))
		{
			return false;
		}
		GameObject ancientTablet = ObjectUtil.getObject(ToaConstants.KEPHRI_ANCIENT_TABLET);
		if (ancientTablet == null)
		{
			return false;
		}
		toaManager.print("solve math");
		//if maths solution tile size == 0, need to read the tablet
		if (toaManager.kephri.maths_solution_tiles.size() == 0)
		{
			//if player is not on start tile, move to start tile
			WorldPoint startTile = new WorldPoint(ancientTablet.getWorldLocation().getX(), ancientTablet.getWorldLocation().getY() - 1, client.getPlane());
			if (!client.getLocalPlayer().getWorldLocation().equals(startTile))
			{
				toaManager.print("Moving to start tile " + toaManager.worldPointString(startTile));
				Movement.walk(startTile);
				return true;
			}
			//press tablet
			toaManager.print("Inspecting tablet");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(ancientTablet, false, "Inspect");
			return true;
		}
		//if player is on solution tile, add solution tile to completed tiles
		if (stoodOnTile(toaManager.kephri.maths_solution_tiles, client.getLocalPlayer().getWorldLocation()))
		{
			toaManager.print("On solution tile, adding to completed list");
			toaManager.kephri.maths_solution_tiles_completed.add(client.getLocalPlayer().getWorldLocation());
		}
		//Still more solution tiles to stand on
		if (toaManager.kephri.maths_solution_tiles_completed.size() < toaManager.kephri.maths_solution_tiles.size())
		{
			WorldPoint nextTile = getNextTile(toaManager.kephri.maths_solution_tiles, toaManager.kephri.maths_solution_tiles_completed);
			if (nextTile == null)
			{
				toaManager.print("Next  tile is somehow null");
				return false;
			}
			Movement.walk(nextTile);
			return true;
		}
		else
		{
			//solution tiles same size as solve tiles, all we need to do is run to the end and we are done
			//generate the eastmost tile of the puzzle area that is on same Y axis as player loc
			WorldArea puzzleArea = toaManager.kephri.currentKephriPuzzle.roomArea;
			List<WorldPoint> puzzlePoints = puzzleArea.toWorldPointList();
			WorldPoint tabletLoc = ancientTablet.getWorldLocation();
			WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();
			WorldPoint exitTile = determineExitTile(puzzlePoints, playerLoc, tabletLoc, 7);
			if (exitTile != null && !playerLoc.equals(exitTile))
			{
				Movement.walk(exitTile);
//				toaManager.print("Puzzle solved");
				return true;
			}
		}


		//if maths solution tiles size > 0


		//get the next tile that player needs to stand on (in solution but not completed list)
		//click tile
		//if solution tiles all completed, generate the eastmost tile of the puzzle area that is on same Y axis as player loc
		//move to that tile
		//puzzle should complete


		return true;
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getMessage().contains("hastily chipped into the stone"))
		{
			toaManager.kephri.maths_solution_tiles.clear();
			String number = event.getMessage().substring(23, 25);
			toaManager.kephri.maths_solution_tiles.addAll(mathSolver(Integer.parseInt(number)));
		}
	}

	private List<WorldPoint> mathSolver(int target)
	{
		List<WorldPoint> solutionTiles = new ArrayList<>();

		WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();
		int x = playerLoc.getX();
		int y = playerLoc.getY();

		switch (target)
		{
			case 20:
			case 26:
				solutionTiles.add(new WorldPoint(x + 4, y - 2, client.getPlane()));
				break;
			case 21:
				solutionTiles.add(new WorldPoint(x + 6, y - 3, client.getPlane()));
				break;
			case 22:
				solutionTiles.add(new WorldPoint(x + 4, y + 1, client.getPlane()));
				break;
			case 23:
				solutionTiles.add(new WorldPoint(x + 6, y - 1, client.getPlane()));
				break;
			case 24:
			case 29:
				solutionTiles.add(new WorldPoint(x + 4, y - 1, client.getPlane()));
				break;
			case 25:
				solutionTiles.add(new WorldPoint(x + 6, y - 2, client.getPlane()));
				break;
			case 27:
				solutionTiles.add(new WorldPoint(x + 6, y + 1, client.getPlane()));
				break;
			case 28:
				solutionTiles.add(new WorldPoint(x + 5, y + 1, client.getPlane()));
				break;
			case 30:
				solutionTiles.add(new WorldPoint(x + 2, y - 1, client.getPlane()));
				break;
			case 31:
				solutionTiles.add(new WorldPoint(x + 4, y + 1, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y + 1, client.getPlane()));
				break;
			case 32:
				solutionTiles.add(new WorldPoint(x + 6, y - 2, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y - 3, client.getPlane()));
				break;
			case 33:
				solutionTiles.add(new WorldPoint(x + 4, y - 2, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y + 1, client.getPlane()));
				break;
			case 34:
				solutionTiles.add(new WorldPoint(x + 6, y - 2, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 4, y - 1, client.getPlane()));
				break;
			case 35:
				solutionTiles.add(new WorldPoint(x + 4, y - 3, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y + 1, client.getPlane()));
				break;
			case 36:
				solutionTiles.add(new WorldPoint(x + 2, y +1, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y +1, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y -1, client.getPlane()));

			case 37:
				solutionTiles.add(new WorldPoint(x + 4, y + 1, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y + 1, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y - 1, client.getPlane()));
				break;
			case 38:
				solutionTiles.add(new WorldPoint(x + 6, y - 2, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 4, y - 2, client.getPlane()));
				break;
			case 39:
				solutionTiles.add(new WorldPoint(x + 6, y - 2, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 4, y - 2, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y, client.getPlane()));
				break;
			case 40:
				solutionTiles.add(new WorldPoint(x + 6, y - 2, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 4, y - 3, client.getPlane()));
				break;
			case 41:
				solutionTiles.add(new WorldPoint(x + 4, y - 3, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y + 1, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y - 1, client.getPlane()));
				break;
			case 42:
				solutionTiles.add(new WorldPoint(x + 6, y + 1, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 4, y - 1, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y - 2, client.getPlane()));
				break;
			case 43:
				solutionTiles.add(new WorldPoint(x + 6, y + 1, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 4, y - 1, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y - 2, client.getPlane()));
				break;
			case 44:
				solutionTiles.add(new WorldPoint(x + 2, y - 4, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 3, y - 4, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 3, y + 1, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y + 1, client.getPlane()));
			case 45:
				solutionTiles.add(new WorldPoint(x + 2, y +1, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 2, y - 2, client.getPlane()));
				solutionTiles.add(new WorldPoint(x + 6, y - 2, client.getPlane()));
				break;
		}
		return solutionTiles;
	}

	private boolean stoodOnTile(List<WorldPoint> tiles, WorldPoint playerLoc)
	{
		for (WorldPoint p : tiles)
		{
			if (p.equals(playerLoc))
			{
				return true;
			}
		}
		return false;
	}

	private boolean stoodOnTile(WorldPoint tile, WorldPoint playerLoc)
	{
		return tile.equals(playerLoc);
	}

	private WorldPoint getNextTile(List<WorldPoint> tiles, List<WorldPoint> excludedTiles)
	{
		for (WorldPoint wp : tiles)
		{
			if (!excludedTiles.contains(wp))
			{
				return wp;
			}
		}
		return null;
	}

	private WorldPoint determineExitTile(List<WorldPoint> potentialTiles, WorldPoint playerLoc, WorldPoint tabletLoc, int offset)
	{
		int tabletX = tabletLoc.getX();
		int adjustedX = tabletX + offset;
		for (WorldPoint wp : potentialTiles)
		{
			if (wp.getX() == adjustedX && wp.getY() == playerLoc.getY())
			{
				return wp;
			}
		}
		return null;
	}


}
