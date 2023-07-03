package com.example.toagigatron.tasks.baba.boss;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.NPCUtil;
import com.example.Utility.Walker;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.HashSet;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Baba hit boulder",
	priority = 1
)
public class BabaHitBoulder extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public BabaHitBoulder(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_BOSS);
	}

	//TODO - Boulders are slow because we are finding a boulder that is dead up the top and attempting to hit it. However our pathing is written to
	// accomodate this so if we add a alive() check we start running into boulders. Updating pathing to work properly and adding alive() check
	// would speed up boulders significantly but its not important
	public boolean execute()
	{
		if (toaManager.baba.blockTiles.isEmpty() || toaManager.baba.babaBoss == null)
		{
			return false;
		}

		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		NPC weakBoulder = NPCUtil.findNearestNpcAliveOrDead(ToaConstants.WEAK_BOULDER);
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
			toaManager.print("Safe tile -> " + toaManager.baba.safeTile);
			if (toaManager.baba.safeTile == null)
			{
				toaManager.print("Safe tile is null");
				return false;
			}
			//			Movement.walk(toaManager.baba.safeTile);
			toaManager.print("Walking to " + toaManager.worldPointString(toaManager.baba.safeTile));
			HashSet<WorldPoint> dangerTiles = new HashSet<>(toaManager.baba.bananaTiles);
			dangerTiles.addAll(toaManager.baba.blockTiles);
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