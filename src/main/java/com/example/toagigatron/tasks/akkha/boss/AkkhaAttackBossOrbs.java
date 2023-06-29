package com.example.toagigatron.tasks.akkha.boss;

import com.example.Utility.Combat;
import com.example.Utility.NPCUtil;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.Comparator;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Akkha attack boss during orbs",
	priority = 1
)
public class AkkhaAttackBossOrbs extends StagedTask
{
	@Inject
	public AkkhaAttackBossOrbs(ToaManager toaManager)
	{
		super(toaManager, Stage.AKKHA_BOSS);
	}

	public boolean execute()
	{
		if (toaManager.akkha.isNotInBossRoom() || toaManager.akkha.akkhaBoss == null || toaManager.akkha.akkhaBoss.getId() != ToaConstants.FINAL_AKKHA)
		{
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		toaManager.akkha.targetPoint = findBestTile(toaManager.akkha.akkhaOrbTiles, playerPoint);
		if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItems()))
		{
			toaManager.print("Switching to melee gear");
			toaManager.swap(toaManager.meleeSetup.getAllItems());
		}

		if (Combat.getSpecEnergy() >= 25)
		{
			Combat.toggleSpec();
		}
		NPC akkha = NPCUtil.findNearest(ToaConstants.FINAL_AKKHA);

		// If standing on dangerous tile, or not next to akkha, step along next to boss
		if (toaManager.akkha.targetPoint != null && (!playerPoint.equals(toaManager.akkha.targetPoint) || toaManager.akkha.orbTiles.contains(playerPoint)))
		{
			toaManager.print("Found tile at " + toaManager.worldPointStringVerbose(toaManager.akkha.targetPoint));
			toaManager.akkha.finalPhasePath = Movement.getPath(toaManager.akkha.targetPoint, toaManager.akkha.toaCollisionMap);
			if (toaManager.akkha.finalPhasePath.isEmpty())
			{
				toaManager.print("Akkha final path is empty somehow");
			}
			else
			{
				toaManager.print("Akkha path is not empty, size -> " + toaManager.akkha.finalPhasePath.size());
				int count = 1;
				for (WorldPoint wp : toaManager.akkha.finalPhasePath)
				{
					System.out.println("Tile " + count + ": " + toaManager.worldPointString(wp));
					count++;
				}
			}
			//toaManager.print("Before step along old");
			System.out.println("Before step along old");
			toaManager.stepAlongOld(toaManager.akkha.finalPhasePath);
			//toaManager.print("After step along old");
			System.out.println("After step along old");
			return true;
		}

		// Return if already attacking akkha
		if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(akkha))
		{
			toaManager.print("already attacking");
			return false;
		}
		else
		{
			toaManager.print("attacking last akkha");
			akkha.interact("Attack");
		}

		return true;
	}

	public WorldPoint findBestTile(ArrayList<WorldPoint> possibleTiles, WorldPoint playerPoint)
	{
		possibleTiles.removeIf(n -> toaManager.akkha.orbTiles.contains(n) || toaManager.akkha.orbThirdTiles.contains(n));
		return possibleTiles.stream().min(Comparator.comparingInt(wp -> wp.distanceTo(playerPoint))).stream().findFirst().orElse(null);
	}
}