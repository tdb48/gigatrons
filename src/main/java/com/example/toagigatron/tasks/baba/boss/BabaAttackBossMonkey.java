package com.example.toagigatron.tasks.baba.boss;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Walker;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.HashSet;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

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
		// we should be able to attack these while specials are going on under certain circumstances but this is low-med priority
		// but it would be nice, we take avoidable damage from their thrown rocks

		NPC bossMonkey = NPCs.search().alive().withId(ToaConstants.BABA_BOSS_MONKEY).filter(
			n -> !toaManager.baba.badTiles.contains(n.getWorldLocation())
				&& !toaManager.baba.tilesUnderBoss().contains(n.getWorldLocation())).nearestToPlayer().orElse(null);
		if (bossMonkey == null
			|| toaManager.getBossHp() < 150
			|| !toaManager.baba.blockTiles.isEmpty()
			|| toaManager.baba.rockfallTick != 0
			|| toaManager.baba.ceilingTick != 0
			|| toaManager.baba.shockwaveTick != 0
		)
		{
			return false;
		}
		if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItems()))
		{
			toaManager.swap(toaManager.meleeSetup.getAllItems());
		}
		ArrayList<WorldPoint> tilesNextToBossMonkey = getWpNextToMonkey(bossMonkey);
		if (!tilesNextToBossMonkey.isEmpty()
			&& !isNextToBossMonkey(bossMonkey)
			&& (bossMonkey.getInteracting() == null
			|| bossMonkey.getInteracting() != null
			&& bossMonkey.getInteracting().equals(client.getLocalPlayer())))
		{
			toaManager.print("Stepping next to boss monkey");
			WorldPoint tileNextToMonkey = toaManager.findClosestTile(tilesNextToBossMonkey);
			HashSet<WorldPoint> dangerTiles = new HashSet<>();
			dangerTiles.addAll(toaManager.baba.badTiles);
			dangerTiles.addAll(toaManager.baba.tilesUnderBoss());
			toaManager.baba.attackPath = EthanApiPlugin.pathToGoal(tileNextToMonkey, dangerTiles);
			Walker.stepAlong(toaManager.baba.attackPath);
			return true;
		}
		MousePackets.queueClickPacket();
		NPCPackets.queueNPCAction(bossMonkey, "Attack");
		return true;
	}

	public ArrayList<WorldPoint> getWpNextToMonkey(NPC monkey)
	{
		WorldPoint monkeyLoc = monkey.getWorldLocation();
		ArrayList<WorldPoint> returnList = new ArrayList<>();
		returnList.add(monkeyLoc.dx(-1)); // West
		returnList.add(monkeyLoc.dx(1)); // East
		returnList.add(monkeyLoc.dy(-1)); // North
		returnList.add(monkeyLoc.dy(1)); // South
		returnList.removeAll(toaManager.baba.badTiles);
		returnList.removeAll(toaManager.baba.tilesUnderBoss());
		return returnList;
	}

	public boolean isNextToBossMonkey(NPC monkey)
	{
		ArrayList<WorldPoint> monkeyTiles = getWpNextToMonkey(monkey);
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		return monkeyTiles.contains(playerPoint);
	}
}