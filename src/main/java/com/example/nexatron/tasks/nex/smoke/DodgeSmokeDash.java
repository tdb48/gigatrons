package com.example.nexatron.tasks.nex.smoke;


import com.example.Utility.Movement;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Lanes;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Dodge Smoke Dash",
	priority = 1,
	register = true,
	blocking = true
)
public class DodgeSmokeDash extends StagedTask
{
	public Lanes activeConstLane = Lanes.NONE;

	@Inject
	public DodgeSmokeDash(NexManager nexManager)
	{
		super(nexManager,
			Stage.NEX_SMOKE,
			Stage.MINION_SMOKE
		);
	}

	public boolean execute()
	{
		if (nexManager.nex.dashTick > 0)
		{
			if (nexManager.getPlayerPoint().distanceTo(nexManager.nex.centerPoint) > 6)
			{
				return false;
			}
			if (nexManager.nex.nex.getOverheadText() != null
				&& nexManager.nex.nex.getOverheadText().toLowerCase().contains("there is..")
				&& nexManager.nex.dashTick < 7)
			{
				activeConstLane = findActiveLane();
			}
			WorldPoint tile;
			if (nexManager.socket.isMaster)
			{
				// If dash is west, move to south (dodge tile), otherwise stay on main tile
				if (activeConstLane.equals(Lanes.WEST))
				{
					nexManager.nex.initSmokeNexTiles();
					tile = nexManager.nex.masterDodgeTile;
				}
				else
				{
					tile = nexManager.nex.masterMainTile;
				}
			}
			else
			{
				if (activeConstLane.equals(Lanes.NORTH))
				{
					nexManager.nex.initSmokeNexTiles();
					tile = nexManager.nex.slaveDodgeTile;
				}
				else
				{
					tile = nexManager.nex.slaveMainTile;
				}
			}
			if (tile == null
				|| client.getLocalPlayer().getWorldLocation().equals(tile))
			{
				return false;
			}
			nexManager.print("Walking to start tile at " + nexManager.worldPointString(tile));
			Movement.move(tile);
			// This is to set it back to the minion tiles, because we used the nex tiles
			if (nexManager.getStage() == Stage.MINION_SMOKE)
			{
				nexManager.nex.initSmokeMinionTiles();
			}
			return true;
		}
		activeConstLane = Lanes.NONE;
		return false;
	}

	public Lanes findActiveLane()
	{
		int orientation = nexManager.nex.nex.getOrientation();
		if (orientation < 250 || orientation > 1780)
		{
			return Lanes.SOUTH;
		}
		else if (orientation > 250 && orientation < 760)
		{
			return Lanes.WEST;
		}
		else if (orientation > 760 && orientation < 1250)
		{
			return Lanes.NORTH;
		}
		else if (orientation > 1250 && orientation < 1780)
		{
			return Lanes.EAST;
		}
		return null;
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			String message = chatMessage.getMessage().toLowerCase();
			if (message.contains("there is.."))
			{
				nexManager.nex.dashTick = nexManager.getStage().equals(Stage.MINION_SMOKE) ? 13 : 9;
				nexManager.nex.nexAttackTick = 12;
			}
		}
	}
}
