package com.example.toagigatron.tasks.akkha.boss;


import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Movement;
import com.example.Utility.Reachable;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Akkha attack boss",
	priority = 1
)
public class AkkhaAttackBoss extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public AkkhaAttackBoss(ToaManager toaManager)
	{
		super(toaManager, Stage.AKKHA_BOSS);
	}

	// Look at quadrants based on shadow akkhas
	// If there is NO shadow akhhas (start), quadrant = NE
	// If theres a shadow akkha missing, that means that quadrant is the quadrant you wanna attack akkha in
	// If the shadows become active, look for the first active shadow based on order (ne, nw, sw, se)
	// Attack the akkha shadow from the SAFE quadrant, MEMORY tile.

	public boolean execute()
	{
		if (toaManager.akkha.isNotInBossRoom()
			|| toaManager.akkha.akkhaBoss == null
			|| toaManager.akkha.activeShadows.size() != 0
			|| toaManager.akkha.akkhaBoss.getId() == ToaConstants.FINAL_AKKHA)
		{
			return false;
		}

//		// toggle spec if wearing melee gear and full spec
//		if (toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItems()) && Combat.getSpecEnergy() >= 75)
//		{
//			Combat.toggleSpec();
//		}

		int akkhaId = toaManager.akkha.akkhaBoss.getId();
		ArrayList<Integer> gear = gameTickManager.attackWait > 1 ? toaManager.mageSetup.getAllItemsTankGear() : toaManager.mageSetup.getAllItems();

		if (akkhaId == ToaConstants.MAGE_AKKHA && !toaManager.hasGearEquipped(gear))
		{
			toaManager.print("Switching to mage");
			toaManager.swap(gear);
		}
		else if (akkhaId == ToaConstants.MELEE_AKKHA && !toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItems()))
		{
			toaManager.print("Switching to melee");
			toaManager.swap(toaManager.meleeSetup.getAllItems());
		}
		else if (akkhaId == ToaConstants.RANGE_AKKHA && !toaManager.hasGearEquipped(toaManager.rangeSetup.getAllItemsBp()))
		{
			toaManager.print("Switching to range");
			toaManager.swap(toaManager.rangeSetup.getAllItemsBp());
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		// If akkha is standing on both the memory tile and center tile, stand on memory tile and akkha will reposition itself hopefully
		if (!toaManager.akkha.orbSpecialActive()
			&& !playerPoint.equals(toaManager.akkha.safeQuadrant.memoryTile)
			&& toaManager.akkha.akkhaBoss.getWorldArea().contains(toaManager.akkha.safeQuadrant.memoryTile)
			&& toaManager.akkha.akkhaBoss.getWorldArea().contains(toaManager.akkha.safeQuadrant.centerTile)
			&& Reachable.isWalkable(toaManager.akkha.safeQuadrant.memoryTile)
			&& !gameTickManager.isTickWaiting())
		{
			toaManager.print("Repositioning to memory tile bc akkha is standing EVERYWHERE");
			Movement.walk(toaManager.akkha.safeQuadrant.memoryTile);
			gameTickManager.setTickWait(3);
			return true;
		}
		else if (!toaManager.akkha.orbSpecialActive()
			&& !playerPoint.equals(toaManager.akkha.safeQuadrant.centerTile)
			&& Reachable.isWalkable(toaManager.akkha.safeQuadrant.centerTile)
			&& !gameTickManager.isTickWaiting())
		{
			toaManager.print("Repositioning to center tile HERE" + toaManager.worldPointString(toaManager.akkha.safeQuadrant.centerTile));
			Movement.walk(toaManager.akkha.safeQuadrant.centerTile);
			gameTickManager.setTickWait(3);
			return true;
		}
		else if (!toaManager.akkha.orbSpecialActive()
			&& !playerPoint.equals(toaManager.akkha.safeQuadrant.memoryTile)
			&& toaManager.akkha.akkhaBoss.getWorldArea().contains(toaManager.akkha.safeQuadrant.centerTile)
			&& Reachable.isWalkable(toaManager.akkha.safeQuadrant.memoryTile)
			&& !gameTickManager.isTickWaiting())
		{
			toaManager.print("Repositioning to memory tile bc akkha is standing on our good tile");
			Movement.walk(toaManager.akkha.safeQuadrant.memoryTile);
			gameTickManager.setTickWait(3);
			return true;
		}
		else if (!toaManager.akkha.orbSpecialActive()
			&& !playerPoint.equals(toaManager.akkha.safeQuadrant.centerTile)
			&& Reachable.isWalkable(toaManager.akkha.safeQuadrant.centerTile)
			&& !gameTickManager.isTickWaiting())
		{
			toaManager.print("Repositioning to center tile THERE" + toaManager.worldPointString(toaManager.akkha.safeQuadrant.centerTile));
			Movement.walk(toaManager.akkha.safeQuadrant.centerTile);
			gameTickManager.setTickWait(3);
			return true;
		}
		else if (toaManager.akkha.orbSpecialActive() && playerPoint.equals(toaManager.akkha.safeQuadrant.memoryTile) && Reachable.isWalkable(toaManager.akkha.safeQuadrant.centerTile) && !gameTickManager.isTickWaiting())
		{
			toaManager.print("Repositioning to center tile EVERYWHERE" + toaManager.worldPointString(toaManager.akkha.safeQuadrant.centerTile));
			Movement.walk(toaManager.akkha.safeQuadrant.centerTile);
			gameTickManager.setTickWait(3);
			return true;
		}
		// Return if in melee gear and akkha is not close enough yet
		else if (toaManager.akkha.activeShadows.size() > 0
			&& !toaManager.isNextToNpc(toaManager.akkha.akkhaBoss))
		{
			toaManager.print("waiting for akkha to get close");
			return false;
		}
		// Return if already attacking akkha
		else if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(toaManager.akkha.akkhaBoss))
		{
			return false;
		}
		else if (Reachable.isWalkable(toaManager.akkha.akkhaBoss.getWorldLocation()))
		{
			toaManager.print("Attacking akkha");
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(toaManager.akkha.akkhaBoss, "Attack");
			return true;
		}

		return true;
	}

}