package com.example.toagigatron.tasks.kephri.puzzle;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Movement;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.puzzlemodel.KephriPuzzleRoom;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GroundObject;
import net.runelite.api.Point;
import net.runelite.api.Tile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Solve Light Puzzle",
	priority = 1
)
public class SolveLightPuzzle extends StagedTask
{
	private final ToaManager toaManager;

	@Inject
	private Client client;
	private static final Point[] SCENE_COORD_STARTS = {
		new Point(36, 56),
		new Point(36, 44),
		new Point(53, 56),
		new Point(53, 44),
	};

	private static final int[] LIGHTS_PUZZLE_XOR_ARRAY = {
		0B01110101,
		0B10111010,
		0B11001101,
		0B11001110,
		0B01110011,
		0B10110011,
		0B01011101,
		0B10101110,
	};

	@Inject
	public SolveLightPuzzle(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_PUZZLE);
		this.toaManager = toaManager;
	}

	public boolean execute()
	{
		solve();
		if (toaManager.kephri.currentKephriPuzzle == null || !toaManager.kephri.currentKephriPuzzle.roomType.equals(KephriPuzzleRoom.RoomType.LIGHT))
		{
			return false;
		}
		WorldPoint centerTile = WorldAreas.getCenter(toaManager.kephri.currentKephriPuzzle.roomArea);
		WorldPoint playerPoint = Static.getClient().getLocalPlayer().getWorldLocation();
		LocalPoint dest = client.getLocalDestinationLocation();
		LocalPoint playerLocalPoint = Static.getClient().getLocalPlayer().getLocalLocation();
		if (toaManager.kephri.flips.isEmpty() && !toaManager.kephri.currentKephriPuzzle.solved)
		{
			GameObject anyLight = ObjectUtil.getNearestGameObject(ToaConstants.KEPHRI_GAME_OBJECT_LIGHT_ENABLED);
			if (anyLight != null)
			{
				Movement.walk(anyLight.getWorldLocation());
				return true;
			}
		}
		if (dest != null && toaManager.kephri.flips.contains(dest) && !playerLocalPoint.equals(dest))
		{
			//toaManager.print("Returning false as im moving toward a red tile.");
			return false;
		}
		else if (!toaManager.kephri.currentKephriPuzzle.roomArea.contains(playerPoint) || playerPoint.equals(centerTile))
		{
			//toaManager.print("Stepping on solve");
			Movement.walk(WorldPoint.fromLocal(Static.getClient(), toaManager.kephri.flips.get(0)));
		}
		else
		{
			//toaManager.print("Stepping back");
			Movement.walk(centerTile);
		}
		return true;
	}

	private void solve()
	{
		toaManager.kephri.solved = true;

		Tile[][] sceneTiles = client.getScene().getTiles()[client.getPlane()];
		Point tl = findStartTile(sceneTiles);
		if (tl == null)
		{
			return;
		}

		toaManager.kephri.tileStates = readTileStates(sceneTiles, tl);
		toaManager.kephri.flips = findSolution(tl);
	}

	private int readTileStates(Tile[][] sceneTiles, Point topLeft)
	{
		int tileStates = 0;
		for (int i = 0; i < 8; i++)
		{
			// middle of puzzle has no light
			// skip middle tile
			int tileIx = i > 3 ? i + 1 : i;
			int x = tileIx % 3;
			int y = tileIx / 3;
			Tile lightTile = sceneTiles[topLeft.getX() + (x * 2)][topLeft.getY() - (y * 2)];

			boolean active = Arrays.stream(lightTile.getGameObjects())
				.filter(Objects::nonNull)
				.mapToInt(GameObject::getId)
				.anyMatch(id -> id == ToaConstants.GAME_OBJECT_LIGHT_ENABLED);

			//log.debug("Read light ({}, {}) as active={}", x, y, active);
			if (active)
			{
				tileStates |= 1 << i;
			}
		}

		return tileStates;
	}

	private ArrayList<LocalPoint> findSolution(Point topLeft)
	{
		int xor = 0;
		for (int i = 0; i < 8; i++)
		{
			// invert the state for xor (consider lights out as a 1)
			int mask = 1 << i;
			if ((toaManager.kephri.tileStates & mask) != mask)
			{
				xor ^= LIGHTS_PUZZLE_XOR_ARRAY[i];
			}
		}

		// convert to scene points
		ArrayList<LocalPoint> points = new ArrayList<>();
		for (int i = 0; i < 8; i++)
		{
			int mask = 1 << i;
			if ((xor & mask) == mask)
			{
				// skip middle tile
				int tileIx = i > 3 ? i + 1 : i;
				int x = tileIx % 3;
				int y = tileIx / 3;
				points.add(LocalPoint.fromScene(topLeft.getX() + (x * 2), topLeft.getY() - (y * 2)));
			}
		}

		return points;
	}

	private Point findStartTile(Tile[][] sceneTiles)
	{
		for (Point sceneCoordStart : SCENE_COORD_STARTS)
		{
			Tile startTile = sceneTiles[sceneCoordStart.getX()][sceneCoordStart.getY()];
			if (startTile != null)
			{
				GroundObject groundObject = startTile.getGroundObject();
				if (groundObject != null && groundObject.getId() == ToaConstants.GROUND_OBJECT_LIGHT_BACKGROUND)
				{
					return sceneCoordStart;
				}
			}
		}

		return null;
	}

}
