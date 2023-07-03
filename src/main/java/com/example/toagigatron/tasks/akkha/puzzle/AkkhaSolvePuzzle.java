package com.example.toagigatron.tasks.akkha.puzzle;


import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Movement;
import com.example.Utility.NPCUtil;
import com.example.Utility.Reachable;
import com.example.Utility.Walker;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.puzzlemodel.AkkhaPuzzleSolution;
import com.example.toagigatron.model.puzzlemodel.Mirror;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.TileObject;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import org.apache.commons.lang3.ArrayUtils;

@TaskDescriptor(
	name = "Akkha solve puzzle",
	priority = 1,
	register = true
)
public class AkkhaSolvePuzzle extends StagedTask
{
	@Inject
	public AkkhaSolvePuzzle(ToaManager toaManager)
	{
		super(toaManager, Stage.AKKHA_PUZZLE);
	}

	private static final String CHALLENGE_START_MESSAGE = "Challenge started: Path of Het.";
	private static final String CHALLENGE_COMPLETE_MESSAGE = "Challenge complete: Path of Het.";
	private static final int BEAM_FIRE_RATE_TICKS = 9;
	private static final Set<Integer> BEAM_GRAPHICS_OBJECT_IDS = ImmutableSet.of(
		2114, // horizontal
		2064, // vertical
		2120 // crash (into an object)
	);
	private int nextFireTick = -1;

	public boolean execute()
	{
		if (!toaManager.akkha.isPuzzleActive() || toaManager.akkha.puzzle == null || toaManager.akkha.puzzle.solution == null)
		{
			return false;
		}
		TileObject exit = TileObjects.search().withId(ToaConstants.AKKHA_PUZZLE_EXIT).nearestToPlayer().orElse(null);
		if (exit == null || Reachable.isWalkable(exit.getWorldLocation().dx(1)))
		{
			return false;
		}
		AkkhaPuzzleSolution solution = toaManager.akkha.puzzle.solution;
		NPC strongSeal = NPCUtil.findNearest(ToaConstants.AKKHA_STRONG_PUZZLE_SEAL);
		if (strongSeal != null && solution.isSolved() && getProgress() >= 0 && getProgress() <= 0.2)
		{
			if (client.getLocalPlayer().getInteracting() == null || !client.getLocalPlayer().getInteracting().equals(strongSeal))
			{
				toaManager.print("Clicking strong seal");
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(strongSeal, "Destroy");
			}
			return true;
		}
		NPC weakSeal = NPCUtil.findNearest(ToaConstants.AKKHA_PUZZLE_SEAL);
		if (weakSeal != null)
		{
			if (client.getLocalPlayer().getInteracting() == null || !client.getLocalPlayer().getInteracting().equals(weakSeal))
			{
				toaManager.print("Clicking weak seal");
				MousePackets.queueClickPacket();
				NPCPackets.queueNPCAction(weakSeal, "Destroy");
			}
			return true;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		if (solution.isSolved() && !playerPoint.equals(solution.mineTile))
		{
			if (isStandingOnMirror())
			{
				toaManager.print("Standing on mirror, clicking off");
				clickOnTile(solution.mineTile);
			}
			else
			{
				toaManager.print("Pathing to mine tile");
				HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.akkha.orbTiles);
				toaManager.akkha.finalPhasePath = EthanApiPlugin.pathToGoal(solution.mineTile, dangerTiles);
				Walker.stepAlong(toaManager.akkha.finalPhasePath);
			}
			return true;
		}

		if (!solution.areWallsSolved())
		{
			TileObject wallToMine = TileObjects.search().idInList(List.of(ToaConstants.MINEABLE_WALL_1, ToaConstants.MINEABLE_WALL_2)).atLocation(solution.wallsToMine.get(0)).first().orElse(null);
			if (wallToMine == null)
			{
				if (solution.wallsToMine.size() > 1)
				{
					wallToMine = TileObjects.search().idInList(List.of(ToaConstants.MINEABLE_WALL_1, ToaConstants.MINEABLE_WALL_2)).atLocation(solution.wallsToMine.get(1)).first().orElse(null);
					if (wallToMine == null)
					{
						return false;
					}
					toaManager.print("Mining wall at " + toaManager.worldPointString(wallToMine.getWorldLocation()));
					MousePackets.queueClickPacket();
					ObjectPackets.queueObjectAction(wallToMine, false, "Break");
					return true;
				}
				else
				{
					return false;
				}
			}
			toaManager.print("Mining wall at " + toaManager.worldPointString(wallToMine.getWorldLocation()));
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(wallToMine, false, "Break");
			return true;
		}

		ArrayList<GameObject> pickupMirrors = solution.getWrongMirrors();
		GameObject closestMirror = toaManager.findClosestGameObject(pickupMirrors);
		ArrayList<WorldPoint> placeMirrors = solution.getPlaceMirrors();
		TileObject mirrorOnPlayer = TileObjects.search().withId(ToaConstants.AKKHA_MOVEABLE_MIRROR).atLocation(playerPoint).first().orElse(null);
		if (isStandingOnMirror() && mirrorOnPlayer != null)
		{
			toaManager.print("Standing on mirror, clicking off - not done");
			WorldPoint walkPoint = getPointNextToMirror(solution.mineTile);
			if (walkPoint == null)
			{
				toaManager.print("Tile to walk off mirror is null");
				return false;
			}
			clickOnTile(walkPoint);
			return true;
		}

		if (!solution.getRotateMirrors().isEmpty() && solution.getRotateMirrors().get(0) != null)
		{
			Mirror rotateMirror = solution.getRotateMirrors().get(0);
			GameObject targetMirror = (GameObject) TileObjects.search().withId(ToaConstants.AKKHA_MOVEABLE_MIRROR).atLocation(rotateMirror.worldPoint).first().orElse(null);
			if (targetMirror == null)
			{
				toaManager.print("ERROR 2");
				return false;
			}
			toaManager.print("Rotate direction -> " + turnString(targetMirror.getOrientation(), rotateMirror.orientation));
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(targetMirror, false, turnString(targetMirror.getOrientation(), rotateMirror.orientation));
			return true;
		}

		if (!solution.areMirrorsSolved() && InventoryUtil.contains("Mirror"))
		{
			// Place mirrors
			if (!placeMirrors.isEmpty() && (closestMirror == null || (placeMirrors.get(0).distanceTo(playerPoint) <= closestMirror.getWorldLocation().distanceTo(playerPoint))))
			{
				if (playerPoint.equals(placeMirrors.get(0)))
				{
					toaManager.print("Placing at " + toaManager.worldPointString(placeMirrors.get(0)));
					Widget mirror = InventoryUtil.getFirst("Mirror");
					if (mirror != null)
					{
						MousePackets.queueClickPacket();
						WidgetPackets.queueWidgetAction(mirror, "Place");
					}
					return true;
				}
				else
				{
					toaManager.print("Walking2 to place at " + toaManager.worldPointString(placeMirrors.get(0)));
					HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.akkha.orbTiles);
					toaManager.akkha.finalPhasePath = EthanApiPlugin.pathToGoal(placeMirrors.get(0), dangerTiles);
					Walker.stepAlong(toaManager.akkha.finalPhasePath);
					return true;
				}
			}
		}

		int inventMirrors = Inventory.getItemAmount(ToaConstants.INVENTORY_MIRROR);

		if (inventMirrors < solution.mirrorsNeeded())
		{
			// Dropping
			if (InventoryUtil.isFull())
			{
				int[] potentialPotions = Consumables.BREW.stream().mapToInt(i -> i).toArray();
				int[] potentialPotions2 = Consumables.RESTORE.stream().mapToInt(i -> i).toArray();
				int[] listPotions = ArrayUtils.addAll(potentialPotions, potentialPotions2);
				ArrayList<Widget> dropPotions = InventoryUtil.getAll(listPotions);
				if (!dropPotions.isEmpty())
				{
					Widget w = dropPotions.get(0);
					MousePackets.queueClickPacket();
					WidgetPackets.queueWidgetAction(w, "Drop");
				}
			}

			// Picking up
			if (closestMirror != null)
			{
				toaManager.print("Picking up mirror");
				MousePackets.queueClickPacket();
				ObjectPackets.queueObjectAction(closestMirror, false, "Pick-up");
				return true;
			}
		}
		if (!solution.areMirrorsSolved())
		{
			// Place mirrors
			if (!placeMirrors.isEmpty())
			{
				if (playerPoint.equals(placeMirrors.get(0)))
				{
					toaManager.print("Placing at " + toaManager.worldPointString(placeMirrors.get(0)));
					Widget w = InventoryUtil.getFirst("Mirror");
					if (w != null)
					{
						MousePackets.queueClickPacket();
						WidgetPackets.queueWidgetAction(w, "Place");
					}
					return true;
				}
				else
				{

					toaManager.print("Walking to place at " + toaManager.worldPointString(placeMirrors.get(0)));
					HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.akkha.orbTiles);
					toaManager.akkha.finalPhasePath = EthanApiPlugin.pathToGoal(placeMirrors.get(0), dangerTiles);
					Walker.stepAlong(toaManager.akkha.finalPhasePath);
					return true;
				}
			}
		}
		return false;
	}

	public String turnString(int startingOrientation, int goalOrientation)
	{
		int orientationDelta = Math.abs(goalOrientation - startingOrientation);
		String returnStr = "";
		//2 turns it sodesnt matter which way we turn
		if (orientationDelta == 1024)
		{
			returnStr = "Rotate-clockwise";
		}
		else if (orientationDelta == 512)
		{
			if (goalOrientation > startingOrientation)
			{
				returnStr = "Rotate-clockwise";
			}
			else
			{
				returnStr = "Rotate-anticlockwise";
			}
		}
		else
		{
			if (goalOrientation > startingOrientation)
			{
				returnStr = "Rotate-anticlockwise";
			}
			else
			{
				returnStr = "Rotate-clockwise";
			}
		}
		return returnStr;
	}

	public boolean isStandingOnMirror()
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		TileObject targetMirror = TileObjects.search().withId(ToaConstants.AKKHA_MOVEABLE_MIRROR).atLocation(playerPoint).first().orElse(null);
		return targetMirror != null;
	}

	public WorldPoint getPointNextToPlayer(WorldPoint mineTile)
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		WorldArea area = WorldAreas.createArea(playerPoint.dx(-1).dy(-1), playerPoint.dx(2).dy(2));
		ArrayList<WorldPoint> freeTiles = new ArrayList<>();
		for (WorldPoint wp : area.toWorldPointList())
		{
			if (Reachable.isWalkable(wp))
			{
				freeTiles.add(wp);
			}
		}
		return toaManager.findClosestTile(freeTiles, mineTile);
	}

	public WorldPoint getPointNextToMirror(WorldPoint mineTile)
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		WorldArea area = WorldAreas.createArea(playerPoint.dx(-1).dy(-1), playerPoint.dx(2).dy(2));
		ArrayList<WorldPoint> freeTiles = new ArrayList<>();
		for (WorldPoint wp : area.toWorldPointList())
		{
			if (Reachable.isWalkable(wp) && !toaManager.isDiagonalOf(playerPoint, wp))
			{
				freeTiles.add(wp);
			}
		}
		return toaManager.findClosestTile(freeTiles, mineTile);
	}

	public void clickOnTile(WorldPoint walkPoint)
	{
		int sceneX = walkPoint.getX() - client.getBaseX();
		int sceneY = walkPoint.getY() - client.getBaseY();
		Point canv = Perspective.localToCanvas(client, LocalPoint.fromScene(sceneX, sceneY), client.getPlane());
		int x = canv != null ? canv.getX() : -1;
		int y = canv != null ? canv.getY() : -1;
		Movement.walk(walkPoint);
//		client.interact(0, MenuAction.WALK.getId(), sceneX, sceneY, x, y);
	}

	@Subscribe
	public void onChatMessage(ChatMessage e)
	{
		if (e.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		if (e.getMessage().equals(CHALLENGE_START_MESSAGE))
		{
			this.nextFireTick = client.getTickCount() + BEAM_FIRE_RATE_TICKS + 1;
		}
		else if (e.getMessage().equals(CHALLENGE_COMPLETE_MESSAGE))
		{
			this.nextFireTick = -1;
		}
	}

	@Subscribe
	public void onGraphicsObjectCreated(GraphicsObjectCreated e)
	{
		if (BEAM_GRAPHICS_OBJECT_IDS.contains(e.getGraphicsObject().getId()))
		{
			this.nextFireTick = client.getTickCount() + BEAM_FIRE_RATE_TICKS;
		}
	}

	@Subscribe
	public void onNpcChanged(NpcChanged e)
	{
		if (e.getOld().getId() == NpcID.HETS_SEAL_WEAKENED && e.getNpc().getId() == NpcID.HETS_SEAL_PROTECTED)
		{
			this.nextFireTick = client.getTickCount() + BEAM_FIRE_RATE_TICKS + 1;
		}
		else if (e.getOld().getId() == NpcID.HETS_SEAL_PROTECTED && e.getNpc().getId() == NpcID.HETS_SEAL_WEAKENED)
		{
			this.nextFireTick = -1;
		}
	}

	public double getProgress()
	{
		return (double) (this.nextFireTick - client.getTickCount()) / BEAM_FIRE_RATE_TICKS;
	}
}
