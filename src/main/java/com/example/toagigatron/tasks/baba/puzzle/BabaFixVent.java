package com.example.toagigatron.tasks.baba.puzzle;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Walker;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;

@TaskDescriptor(
	name = "Baba fix vent",
	priority = 1,
	blocking = true
)
public class BabaFixVent extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;
	@Inject
	public BabaFixVent(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE);
	}

	public boolean execute()
	{
		if (toaManager.baba.targetVent == null || !toaManager.baba.isPuzzleActive() || !toaManager.baba.explosionTiles.isEmpty())
		{
			return false;
		}
		//DO SOMETHING WITH THIS
//		NPC shaman = NPCs.getNearest(n -> (
//				n.getHealthRatio() != 0 && (n.getName().equals("Baboon Shaman"))));
//		if(toaManager.baba.puzzleSpecialTickTimer > 7 && (shaman != null ||
//				(client.getLocalPlayer().isInteracting() &&
//				(client.getLocalPlayer().getInteracting().getName().equalsIgnoreCase("baboon shaman") || client.getLocalPlayer().getInteracting().getName().equalsIgnoreCase("baboon thrower"))))){
//			toaManager.print("Returning false in fix vent because im interacting with a shaman or a ranger and there is " +
//					toaManager.baba.puzzleSpecialTickTimer + " ticks until vents demolish me.");
//			return false;
//		}
		if(toaManager.baba.puzzleSpecialTickTimer > 10
				&& client.getLocalPlayer().isInteracting()
				&& client.getLocalPlayer().getInteracting().getName() != null
				&& client.getLocalPlayer().getInteracting().getName().equalsIgnoreCase("baboon shaman")){
			toaManager.print("Returning false in fix vent cause im attacking a shaman and have ticks to spare");
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		if (toaManager.baba.targetVent.getWorldLocation().equals(playerPoint) && InventoryUtil.contains("Neutralising potion"))
		{
			toaManager.print("Fixing vent");
			Widget potion = Inventory.search().nameContains("Neutralising potion").first().orElse(null);
			if(potion != null){
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(potion, "Pour");
				gameTickManager.setTickWait(1);
			}

		}
		else
		{
			HashSet<WorldPoint> dangerTiles = new HashSet<>();
			dangerTiles.addAll(toaManager.baba.explosionTiles);
			dangerTiles.addAll(toaManager.baba.poisonTiles);
			toaManager.baba.specialPath = EthanApiPlugin.pathToGoal(toaManager.baba.targetVent.getWorldLocation(), dangerTiles);
			Walker.stepAlong(toaManager.baba.specialPath);

//			toaManager.baba.specialPath = Movement.getPath(toaManager.baba.targetVent.getWorldLocation(), toaManager.baba.toaCollisionMap);
//			toaManager.stepAlong(toaManager.baba.specialPath);
//			toaManager.print("Walking to vent tile at " + toaManager.baba.targetVent.getWorldLocation());
		}
		return true;
	}
}
