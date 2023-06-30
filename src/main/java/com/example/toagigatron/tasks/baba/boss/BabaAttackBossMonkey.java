package com.example.toagigatron.tasks.baba.boss;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Movement;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;

@TaskDescriptor(
	name = "Baba attack monkey",
	priority = 9
)
public class BabaAttackBossMonkey extends StagedTask
{
	@Inject
	public BabaAttackBossMonkey(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_BOSS);
	}

	@Override
	public boolean execute()
	{
		if (toaManager.baba.closeToProccing())
		{
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		if (toaManager.baba.badTiles.contains(playerPoint))
		{
			return false;
		}

//		NPC bossMonkey = NPCs.getNearest(n ->
//			n.getId() == ToaConstants.BABA_BOSS_MONKEY
//				&& !toaManager.baba.badTiles.contains(n.getWorldLocation())
//				&& !toaManager.baba.tilesUnderBoss().contains(n.getWorldLocation())
//				&& n.getHealthRatio() != 0);
		NPC bossMonkey = NPCs.search().alive().nearestToPlayer().filter(n ->
			n.getId() == ToaConstants.BABA_BOSS_MONKEY
				&& !toaManager.baba.badTiles.contains(n.getWorldLocation())
				&& !toaManager.baba.tilesUnderBoss().contains(n.getWorldLocation())).orElse(null);

		if (bossMonkey == null
			|| toaManager.getBossHp() < 150
			|| !toaManager.baba.blockTiles.isEmpty()
			|| toaManager.baba.rockfallTick != 0
			|| toaManager.baba.ceilingTick != 0
			|| toaManager.baba.shockwaveTick != 0)
		{
			return false;
		}
		if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItems()))
		{
			toaManager.swap(toaManager.meleeSetup.getAllItems());
		}
		MousePackets.queueClickPacket();
		NPCPackets.queueNPCAction(bossMonkey, "Attack");
		return true;
	}
}