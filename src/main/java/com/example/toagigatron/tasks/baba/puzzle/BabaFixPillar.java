package com.example.toagigatron.tasks.baba.puzzle;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Walker;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.coords.WorldPoint;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;

@TaskDescriptor(
	name = "Baba fix pillar",
	priority = 1,
	blocking = true
)
public class BabaFixPillar extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public BabaFixPillar(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE);
	}

	public boolean execute()
	{
		if (toaManager.baba.targetPillar == null || toaManager.baba.targetPillarTiles.isEmpty() || !toaManager.baba.isPuzzleActive() || !toaManager.baba.explosionTiles.isEmpty())
		{
			return false;
		}

		//DO SOMETHING WITH THIS
//		NPC shaman = NPCs.getNearest(n -> (
//				n.getHealthRatio() != 0 && (n.getName().equals("Baboon Shaman"))));
//		if(toaManager.baba.puzzleSpecialTickTimer > 7 && (shaman != null ||
//				(client.getLocalPlayer().isInteracting() &&
//						(client.getLocalPlayer().getInteracting().getName().equalsIgnoreCase("baboon shaman") || client.getLocalPlayer().getInteracting().getName().equalsIgnoreCase("baboon thrower"))))){
//			toaManager.print("Returning false in fix pillar because im interacting with a shaman or a ranger and there is " +
//					toaManager.baba.puzzleSpecialTickTimer + " ticks until pillars demolish me.");
//			return false;
//		}
		if(toaManager.baba.puzzleSpecialTickTimer > 10
				&& client.getLocalPlayer().isInteracting()
				&& client.getLocalPlayer().getInteracting().getName() != null
				&& client.getLocalPlayer().getInteracting().getName().equalsIgnoreCase("baboon shaman")){
			toaManager.print("Returning false in fix pillar cause im attacking a shaman and have ticks to spare");
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		if (toaManager.baba.targetPillarTiles.contains(playerPoint) && Inventory.getItemAmount("Hammer") > 0)
		{
			toaManager.print("Repairing pillar");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(toaManager.baba.targetPillar, false, "Repair");
			gameTickManager.setTickWait(1);
		}
		else
		{
			HashSet<WorldPoint> dangerTiles = new HashSet<>();
			dangerTiles.addAll(toaManager.baba.explosionTiles);
			dangerTiles.addAll(toaManager.baba.poisonTiles);
			toaManager.baba.specialPath = EthanApiPlugin.pathToGoal(toaManager.baba.targetPillarTiles.get(1), dangerTiles);
			Walker.stepAlong(toaManager.baba.specialPath);
		}
		return true;
	}
}
