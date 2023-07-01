package com.example.toagigatron.tasks.baba.boss;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
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

		//TODO - Path next to the monkey instead of attacking it so we stop running over bananas

		NPC bossMonkey = NPCs.search().alive().withId(ToaConstants.BABA_BOSS_MONKEY).filter(
				n -> !toaManager.baba.badTiles.contains(n.getWorldLocation())
				&& !toaManager.baba.tilesUnderBoss().contains(n.getWorldLocation())).nearestToPlayer().orElse(null);

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