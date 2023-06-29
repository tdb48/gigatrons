package com.example.toagigatron.tasks.baba.boss;

import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.entities.NPCs;

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

		NPC bossMonkey = NPCs.getNearest(n ->
			n.getId() == ToaConstants.BABA_BOSS_MONKEY
				&& !toaManager.baba.badTiles.contains(n.getWorldLocation())
				&& !toaManager.baba.tilesUnderBoss().contains(n.getWorldLocation())
				&& n.getHealthRatio() != 0);

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
		bossMonkey.interact("Attack");
		return true;
	}
}