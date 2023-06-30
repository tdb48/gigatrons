package com.example.toagigatron.tasks.baba.boss;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Movement;
import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.Utility.Walker;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.HashSet;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

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
		NPC weakBoulder = NPCUtil.findNearest(ToaConstants.WEAK_BOULDER);
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
			HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.baba.badTiles);
			toaManager.baba.attackPath = EthanApiPlugin.pathToGoal(toaManager.baba.prePathTile, dangerTiles);
			Walker.stepAlong(toaManager.baba.attackPath);
			return true;
		}

		if (weakBoulder != null && !gameTickManager.isAttackWaiting() && weakBoulder.getHealthRatio() == -1)
		{
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(weakBoulder, "Attack");
			return true;
		}
		else if (weakBoulder != null && !gameTickManager.isAttackWaiting() && toaManager.baba.babaBossRowSafe.contains(playerPoint))
		{
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(weakBoulder, "Attack");
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
			HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.baba.badTiles);
			toaManager.baba.attackPath = EthanApiPlugin.pathToGoal(toaManager.baba.safeTile, dangerTiles);
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