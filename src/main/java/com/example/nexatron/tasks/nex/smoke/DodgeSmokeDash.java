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
	register = true
)
public class DodgeSmokeDash extends StagedTask
{
	public Lanes activeConstLane = Lanes.NONE;

	@Inject
	public DodgeSmokeDash(NexManager nexManager)
	{
		super(nexManager, Stage.NEX_SMOKE);
	}

	public boolean execute()
	{
		if (nexManager.nex.dashTick > 0)
		{
			nexManager.nex.dashTick--;
			if (nexManager.nex.nex.getOverheadText() != null
				&& nexManager.nex.nex.getOverheadText().toLowerCase().contains("there is.."))
			{
				int orientation = nexManager.nex.nex.getOrientation();
				if (orientation < 250 || orientation > 1780)
				{
					activeConstLane = Lanes.SOUTH;
				}
				else if (orientation > 250 && orientation < 760)
				{
					activeConstLane = Lanes.WEST;
				}
				else if (orientation > 760 && orientation < 1250)
				{
					activeConstLane = Lanes.NORTH;
				}
				else if (orientation > 1250 && orientation < 1780)
				{
					activeConstLane = Lanes.EAST;
				}
			}
			WorldPoint tile;
			if (nexManager.socket.isMaster)
			{
				// If dash is west, move to south (dodge tile), otherwise stay on main tile
				if (activeConstLane == Lanes.WEST)
				{
					tile = nexManager.nex.masterDodgeTile;
				}
				else
				{
					tile = nexManager.nex.masterMainTile;
				}
			}
			else
			{
				if (activeConstLane == Lanes.NORTH)
				{
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
				return true;
			}

			nexManager.print("Walking to start tile at " + nexManager.worldPointString(tile));
			Movement.move(tile);
			return true;
		}
		activeConstLane = Lanes.NONE;
		return false;
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			String message = chatMessage.getMessage().toLowerCase();
			if (message.contains("there is.."))
			{
				nexManager.nex.nexAttackTick = 12;
				nexManager.nex.dashTick = 9;
			}
		}
	}
}
