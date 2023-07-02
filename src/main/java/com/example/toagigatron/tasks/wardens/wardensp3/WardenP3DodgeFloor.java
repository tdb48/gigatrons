package com.example.toagigatron.tasks.wardens.wardensp3;

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
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Dodge floor p3",
	priority = 20
)
public class WardenP3DodgeFloor extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public WardenP3DodgeFloor(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P3);
	}

	public int runTick = 2;

	public boolean execute()
	{
		if (toaManager.wardens3.enrage
			|| toaManager.wardens3.warden == null
			|| toaManager.wardens3.warden.getId() != ToaConstants.WARDENS_P3_SKULLS_INACTIVE_ID)
		{
			return false;
		}
		WorldPoint primaryTile = toaManager.wardens3.primarySafeTile;
		WorldPoint nextPrimaryTile = toaManager.wardens3.nextPrimarySafeTile;
		if (toaManager.wardens3.babaBombs.containsKey(primaryTile))
		{
			primaryTile = toaManager.wardens3.secondarySafeTile;
		}
		if (toaManager.wardens3.babaBombs.size() > 0 && toaManager.wardens3.babaBombs.containsKey(nextPrimaryTile))
		{
			nextPrimaryTile = toaManager.wardens3.nextSecondarySafeTile;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		int tileTick = toaManager.wardens3.tileFlipTickCounter;
		if (gameTickManager.isAttackWaiting() && (tileTick == 0 || tileTick == 3) && playerPoint.equals(primaryTile))
		{
			if (toaManager.wardens3.stayOnGreen)
			{
				return false;
			}
			toaManager.print("Walking to NP EARLY, tick(" + tileTick + ")");
			Movement.walk(nextPrimaryTile);
			return true;
		}
		else if (gameTickManager.isAttackWaiting()
			&& tileTick == 1)
		{
			if (playerPoint.equals(nextPrimaryTile))
			{
				return false;
			}
//			if(nextPrimaryTile == null)
//			{
//				toaManager.print("Next Primary tile null somehow.");
//				return false;
//			}
			toaManager.print("Walking to NP, tick(" + tileTick + ")");
//			toaManager.print("Walking");
			Movement.walk(nextPrimaryTile);
			return true;
		}
		// Should only happen at start?>
		else if (toaManager.wardens3.warden.getHealthRatio() == -1)
		{
			if (playerPoint.equals(primaryTile))
			{
				return false;
			}
			toaManager.print("AT START: Walking to P, tick(" + tileTick + ")");
//			toaManager.print("Walking");
			Movement.walk(primaryTile);
			return true;
		}
		else if (gameTickManager.isAttackWaiting()
			&& !playerPoint.equals(primaryTile)
			&& !playerPoint.equals(nextPrimaryTile)
			&& (toaManager.wardens3.babaBombs.size() == 0 || !toaManager.wardens3.babaBombs.containsKey(primaryTile)))
		{
			toaManager.print("Walking to P, tick(" + tileTick + ")");
//			toaManager.print("Walking");
			Movement.walk(primaryTile);
			return true;
		}
		else if (tileTick == 2
			&& !playerPoint.equals(primaryTile)
			&& !playerPoint.equals(nextPrimaryTile)
				&& (toaManager.wardens3.babaBombs.size() == 0 || !toaManager.wardens3.babaBombs.containsKey(primaryTile)))
		{
			toaManager.print("Walking to P, tick(" + tileTick + ")");
//			toaManager.print("Walking");
			Movement.walk(primaryTile);
			return true;
		}
		return false;

	}

	public WorldPoint safeTile()
	{
		WorldPoint primaryTile;
		WorldPoint secondaryTile;
		if (!gameTickManager.isAttackWaiting() && toaManager.wardens3.tileFlipTickCounter == 2)
		{
			primaryTile = client.getLocalPlayer().getWorldLocation();
			secondaryTile = client.getLocalPlayer().getWorldLocation().dy(1);
		}
		else if (toaManager.wardens3.tileFlipTickCounter == 1
			&& gameTickManager.isAttackWaiting()
			&& toaManager.wardens3.nextPrimarySafeTile != null
			&& toaManager.wardens3.nextSecondarySafeTile != null)
		{
			runTick = 1;
//			toaManager.print("Setting to next tile");
			primaryTile = toaManager.wardens3.nextPrimarySafeTile;
			secondaryTile = toaManager.wardens3.nextSecondarySafeTile;
		}
		else
		{
			runTick = 2;
			primaryTile = toaManager.wardens3.primarySafeTile;
			secondaryTile = toaManager.wardens3.secondarySafeTile;
		}

		if (primaryTile == null || secondaryTile == null)
		{
			return null;
		}
		for (Map.Entry<WorldPoint, Integer> entry : toaManager.wardens3.babaBombs.entrySet())
		{
			WorldPoint wp = entry.getKey();
			if (wp.equals(primaryTile) && entry.getValue() < 2)
			{
				return secondaryTile;
			}
		}
		return primaryTile;
	}
}