package com.example.toagigatron.tasks.kephri.puzzle;

import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Movement;
import com.example.Utility.NPCUtil;
import com.example.Utility.WorldAreas;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.puzzlemodel.KephriPuzzleRoom;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.GraphicsObject;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.NpcChanged;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Solve Pillar Puzzle",
	priority = 1,
	register = true
)
public class SolvePillarPuzzle extends StagedTask
{

	//falling rock id thing == 317 (graphics object)
	//5 ticks after spawning you need to move
	//hit pillars determining pattern, move if its been 5 ticks since graphics object spawned and you are on a bad tile


	@Inject
	public SolvePillarPuzzle(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_PUZZLE);

	}

	@Override
	public boolean execute()
	{
//		 || !toaManager.kephri.enteredKephriBarrier
		if (toaManager.kephri.currentKephriPuzzle == null || !toaManager.kephri.currentKephriPuzzle.roomType.equals(KephriPuzzleRoom.RoomType.PILLAR))
		{
			return false;
		}
		if (toaManager.kephri.activeObelisks == 6)
		{
			return false;
		}
		toaManager.print("solve pillar");

		if (client.getLocalPlayer().getAnimation() == -1)
		{
			toaManager.kephri.attemptedPillars.clear();
		}
		if (!toaManager.hasGearEquipped(toaManager.rangeSetup.getAllItemsBp()))
		{
			toaManager.swap(toaManager.rangeSetup.getAllItemsBp());
			return true;
		}
		else
		{
			LocalPoint playerLoc = client.getLocalPlayer().getLocalLocation();
			for (GraphicsObject obj : client.getGraphicsObjects())
			{
				if (obj.getId() == ToaConstants.FALLING_ROCKS_KEPHRI_PILLAR)
				{
					LocalPoint lp = obj.getLocation();
					if (lp.equals(playerLoc))
					{
						//toaManager.print("On a danger tile");
						WorldPoint newTile = findSafeTile();
						if (newTile != null)
						{
							toaManager.print("New tile -> " + newTile);
							Movement.walk(newTile);
						}
						else
						{
							toaManager.print("New tile is null");
						}
						return false;
					}
				}
			}
			//if theres pillars in the pillar order list, attempt to hit them
			//npc query get the npc at the localpoint and see if it has a hit option
			//if no attack option on pillar, move to next one
			//if none of them have att options, move on to hitting other pillars
			for (LocalPoint lp : toaManager.kephri.obeliskOrder)
			{
				NPC pillar = NPCUtil.findAt(lp, ToaConstants.OBELISK_ID_INACTIVE);
				if (pillar != null)
				{
					MousePackets.queueClickPacket();
					NPCPackets.queueNPCAction(pillar, "Hit");
					return false;
				}
			}

			for (NPC n : client.getNpcs())
			{
				if (n.getId() == ToaConstants.OBELISK_ID_INACTIVE)
				{
					//toaManager.print("found inactive obelisk");
//				if(!toaManager.kephri.obeliskOrder.contains(n.getLocalLocation())){
//					toaManager.print("DIDNT FIND CONTAISN N LGETOCATION POIINT KEPHRI OBELISK RODER");
//					for (int i = 0; i < toaManager.kephri.obeliskOrder.size(); i++)
//					{
//						toaManager.print(i + ", " + WorldPoint.fromLocal(Static.getClient(),toaManager.kephri.obeliskOrder.get(i)));
//					}
//				}
//				if(!toaManager.kephri.attemptedPillars.contains(n)){
//					toaManager.print("STATEMENT 2 ATTEMPTED PILLARS");
//				}
					//toaManager.print("obelisk order contains pillar -> " + toaManager.kephri.obeliskOrder.contains(n.getLocalLocation()));
					//toaManager.print("attempted pillars contains pillar -> " + toaManager.kephri.attemptedPillars.contains(n));
					//toaManager.print("reachable? " + Reachable.isInteractable(n));
					if (!toaManager.kephri.obeliskOrder.contains(n.getLocalLocation()) && !toaManager.kephri.attemptedPillars.contains(n)
//						&& Reachable.isInteractable(n)
					)
					{
						toaManager.print("obelisk not in current order and not been attempted yet");
						MousePackets.queueClickPacket();
						NPCPackets.queueNPCAction(n, "Hit");
						NPC hitPillar = NPCUtil.findAt(n.getLocalLocation(), ToaConstants.OBELISK_ID_INACTIVE);
						if (hitPillar != null)
						{
							toaManager.print("Pillar i hit was wrong, adding to attempted list");
							toaManager.kephri.attemptedPillars.add(n);
						}
						return false;
					}
				}
			}
		}


		return false;
	}

	private WorldPoint findSafeTile()
	{
		ArrayList<WorldPoint> potentialTiles = (ArrayList<WorldPoint>) toaManager.kephri.currentKephriPuzzle.roomArea.toWorldPointList();
		ArrayList<WorldPoint> safeTiles = new ArrayList<>();
		ArrayList<WorldPoint> fallingRockTiles = new ArrayList<>();
		WorldPoint center = WorldAreas.getCenter(toaManager.kephri.currentKephriPuzzle.roomArea);
		for (GraphicsObject obj : client.getGraphicsObjects())
		{
			if (obj.getId() == ToaConstants.FALLING_ROCKS_KEPHRI_PILLAR)
			{
				if (!obj.finished())
				{
					fallingRockTiles.add(WorldPoint.fromLocal(client, obj.getLocation()));
				}
			}
		}
		for (WorldPoint wp : potentialTiles)
		{
			if (!fallingRockTiles.contains(wp))
			{
				safeTiles.add(wp);
			}
		}
		return toaManager.findClosestTile(safeTiles, center);
	}


	@Subscribe
	public void onNpcChanged(NpcChanged event)
	{
		if (event.getNpc().getId() == ToaConstants.OBELISK_ID_ACTIVE)
		{
			LocalPoint obeliskTile = event.getNpc().getLocalLocation();
			if (!toaManager.kephri.obeliskOrder.contains(obeliskTile))
			{
				toaManager.kephri.obeliskOrder.add(obeliskTile);
				toaManager.kephri.attemptedPillars.clear();
			}

			toaManager.kephri.activeObelisks++;
		}
		else if (event.getNpc().getId() == ToaConstants.OBELISK_ID_INACTIVE)
		{
			toaManager.kephri.activeObelisks = 0;
		}
	}


}