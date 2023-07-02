package com.example.toagigatron.tasks.wardens.wardensp1;

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
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Attack obelisk",
	priority = 5
)
public class SolveOrbUfo extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public SolveOrbUfo(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P1);
	}

	public boolean execute()
	{
		if (toaManager.wardens12.warden != null && toaManager.wardens12.warden.getHealthRatio() == 0)
		{
			return false;
		}
		GameObject yellowUFO = ObjectUtil.getNearestGameObject(ToaConstants.YELLOW_UFO);
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		// Step to a tile thats safe if theres red ufos out
		if (yellowUFO != null)
		{
			WorldPoint redTile = toaManager.wardens12.dodgeUFO();
			if (redTile == null)
			{
				toaManager.print("redTilee is somehow null");
				return false;
			}
			if (playerPoint.equals(redTile))
			{
				return false;
			}
			toaManager.print("Moving to safe tile from red ufo");
			Movement.walk(redTile);
			return true;
		}
		if (!gameTickManager.isAttackWaiting())
		{
			return false;
		}
		// Tank orbs if needed
		if (toaManager.wardens12.orbsTanked < 7)
		{
			WorldPoint blockTile = toaManager.wardens12.blockTile();
			if (blockTile == null)
			{
				toaManager.print("Block tile is somehow null");
				return false;
			}
			if (playerPoint.equals(blockTile))
			{
				return false;
			}
			toaManager.print("Moving to block orb");
			Movement.walk(blockTile);
			return true;
		}
		// Else move to standard tile
		else
		{
			WorldPoint defaultTile = toaManager.wardens12.defaultTile();
			if (defaultTile == null)
			{
				toaManager.print("Default tile is somehow null");
				return false;
			}
			if (playerPoint.equals(defaultTile))
			{
				return false;
			}
			toaManager.print("Moving to default tile");
			Movement.walk(defaultTile);
			return true;
		}
	}
}
