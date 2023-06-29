package com.example.toagigatron.tasks.baba.puzzle;

import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.items.Inventory;

import javax.inject.Inject;
import java.util.ArrayList;

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
				&& client.getLocalPlayer().getInteracting().getName().equalsIgnoreCase("baboon shaman")){
			toaManager.print("Returning false in fix pillar cause im attacking a shaman and have ticks to spare");
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		if (toaManager.baba.targetPillarTiles.contains(playerPoint) && Inventory.contains("Hammer"))
		{
			toaManager.print("Repairing pillar");
			toaManager.baba.targetPillar.interact("Repair");
			gameTickManager.setTickWait(1);
		}
		else
		{
			toaManager.baba.specialPath = com.example.toagigatron.model.pathing.Movement.getAvoidancePath(toaManager.baba.targetPillarTiles.get(1), toaManager.baba.toaCollisionMap, toaManager.baba.getTrueBabaRoom(), toaManager.baba.poisonTiles, new ArrayList<>(),false);
			toaManager.stepAlong(toaManager.baba.specialPath);

			//toaManager.baba.specialPath = Movement.getPath(toaManager.baba.targetPillarTiles.get(0), toaManager.baba.toaCollisionMap);
			//toaManager.stepAlong(toaManager.baba.specialPath);
			//toaManager.print("Walking to pillar tile at " + toaManager.baba.targetPillarTiles.get(0));
		}
		return true;
	}
}
