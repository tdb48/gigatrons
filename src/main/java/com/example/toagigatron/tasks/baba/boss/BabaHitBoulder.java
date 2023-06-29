package com.example.toagigatron.tasks.baba.boss;

import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.pathfinder.Walker;

import javax.inject.Inject;
import java.util.List;

@TaskDescriptor(
	name = "Baba hit boulder",
	priority = 1
)
public class BabaHitBoulder extends StagedTask
{
	@Inject
	public BabaHitBoulder(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_BOSS);
	}

	@Inject
	GameTickManager gameTickManager;

	public boolean execute()
	{
		if (toaManager.baba.blockTiles.isEmpty() || toaManager.baba.babaBoss == null)
		{
			return false;
		}

		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		NPC weakBoulder = NPCs.getNearest(n ->
			n.getId() == ToaConstants.WEAK_BOULDER);
		if (weakBoulder == null
			&& (toaManager.baba.bouldersKilled == 7
			|| toaManager.baba.bouldersKilled == 14))
		{
			return false;
		}
		setCurrentRow(weakBoulder);

		if (weakBoulder == null
			&& toaManager.baba.babaBoss.getInteracting() != null)
		{
			return false;
		}

		if (!toaManager.hasGearEquipped(toaManager.rangeSetup.getAllItemsBp()))
		{
			toaManager.swap(toaManager.rangeSetup.getAllItemsBp());
		}
		WorldPoint prePathTile = getPrePathTile();
		if (prePathTile == null)
		{
			return false;
		}
		// If we are standing on prepath tile, set it to true!
		if (playerPoint.equals(prePathTile))
		{
			toaManager.baba.touchedPrePathTile = true;
		}
		if (!toaManager.baba.touchedPrePathTile)
		{
			toaManager.print("Prepathing");
			toaManager.baba.attackPath = Movement.getPath(List.of(playerPoint), toaManager.baba.prePathTile, toaManager.baba.toaCollisionMap);
			Walker.stepAlong(toaManager.baba.attackPath);
//			Movement.walk(toaManager.baba.prePathTile);
			return true;
		}

		if (weakBoulder != null && !gameTickManager.isAttackWaiting() && weakBoulder.getHealthRatio() == -1)
		{
			weakBoulder.interact("Attack");
			return true;
		}
		else if (weakBoulder != null && !gameTickManager.isAttackWaiting() && toaManager.baba.babaBossRowSafe.contains(playerPoint))
		{
			weakBoulder.interact("Attack");
			return true;
		}
		else if (weakBoulder != null && !toaManager.baba.babaBossRowSafe.contains(playerPoint))
		{
			toaManager.baba.safeTile = toaManager.baba.getSafeTile(weakBoulder.getWorldArea());
			if (toaManager.baba.safeTile == null)
			{
				return false;
			}
			//			Movement.walk(toaManager.baba.safeTile);
			toaManager.print("Walking to " + toaManager.worldPointString(toaManager.baba.safeTile));
			toaManager.baba.attackPath = Movement.getPath(List.of(playerPoint), toaManager.baba.safeTile, toaManager.baba.toaCollisionMap);
			Walker.stepAlong(toaManager.baba.attackPath);
			return true;
		}
		return false;
	}

	public WorldPoint getPrePathTile()
	{
		WorldPoint refPoint = toaManager.baba.babaEntry.getWorldLocation();
		return refPoint.dx(8);
	}

	public void setCurrentRow(NPC weakBoulder)
	{
		if (weakBoulder == null)
		{
			return;
		}
		WorldPoint refPoint = weakBoulder.getWorldLocation();
		if (toaManager.baba.babaBossRowOne.toWorldPointList().contains(refPoint))
		{
			toaManager.baba.babaBossRowSafe = toaManager.baba.babaBossRowOne;
		}
		else if (toaManager.baba.babaBossRowTwo.toWorldPointList().contains(refPoint))
		{
			toaManager.baba.babaBossRowSafe = toaManager.baba.babaBossRowTwo;
		}
		else if (toaManager.baba.babaBossRowThree.toWorldPointList().contains(refPoint))
		{
			toaManager.baba.babaBossRowSafe = toaManager.baba.babaBossRowThree;
		}
		else if (toaManager.baba.babaBossRowFour.toWorldPointList().contains(refPoint))
		{
			toaManager.baba.babaBossRowSafe = toaManager.baba.babaBossRowFour;
		}
		else if (toaManager.baba.babaBossRowFive.toWorldPointList().contains(refPoint))
		{
			toaManager.baba.babaBossRowSafe = toaManager.baba.babaBossRowFive;
		}
//		toaManager.baba.babaBossRowSafe.removeIf(n -> t/**/oaManager.baba.blockTiles.contains(n));
	}
}