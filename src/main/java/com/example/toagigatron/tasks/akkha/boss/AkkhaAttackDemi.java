package com.example.toagigatron.tasks.akkha.boss;

import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.Utility.Reachable;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Akkha attack demi",
	priority = 1
)
public class AkkhaAttackDemi extends StagedTask
{
	@Inject
	public AkkhaAttackDemi(ToaManager toaManager)
	{
		super(toaManager, Stage.AKKHA_BOSS);
	}

	// attack demi from safe quadrant
	// attack demi if activeshadow size is more than 0
	// attack with shadow if inventory has shadow, otherwise attack with blowpipe
	public boolean execute()
	{
		if (toaManager.akkha.isNotInBossRoom() || toaManager.akkha.activeShadows.size() == 0 || toaManager.akkha.memoryTick == 1)
		{
			return false;
		}
		ArrayList<Integer> demiGear = toaManager.rangeSetup.getAllItemsBp();

		if (!toaManager.hasGearEquipped(demiGear))
		{
			toaManager.print("Switching gear");
			toaManager.swap(demiGear);
		}

		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		// If there's no memory going on AND player is not standing on the right tile, move
		if (!toaManager.akkha.orbSpecialActive()
			&& toaManager.akkha.memoryTiles.isEmpty()
			&& !playerPoint.equals(toaManager.akkha.nextQuadrant.memoryTile)
			&& Reachable.isWalkable(toaManager.akkha.nextQuadrant.memoryTile))
		{
			toaManager.print("Moving to memory tile because we arent on it and theres no orb special, no memory tiles, and its reachable.");
			toaManager.print("Memory tile -> " + toaManager.akkha.nextQuadrant.memoryTile);
			Movement.walk(toaManager.akkha.nextQuadrant.memoryTile);
			return true;
		}

		// toggle spec if wearing melee gear and full spec
		if (toaManager.hasGearEquipped(demiGear) && Combat.getMissingHealth() > 20 && Combat.getSpecEnergy() >= 50)
		{
			Combat.toggleSpecVoid();
		}

		NPC targetShadow = toaManager.akkha.findNpcInArea(toaManager.akkha.activeShadows, toaManager.akkha.nextQuadrant.area);
		if (targetShadow == null || client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(targetShadow))
		{
			return false;
		}
		MousePackets.queueClickPacket();
		NPCPackets.queueNPCAction(targetShadow, "Attack");
		return true;
	}
}