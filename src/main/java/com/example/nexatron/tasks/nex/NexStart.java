package com.example.nexatron.tasks.nex;

import com.example.Utility.Movement;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Nex start",
	priority = 1
)
public class NexStart extends StagedTask
{
	@Inject
	public NexStart(NexManager nexManager)
	{
		super(nexManager, Stage.NEX_START);
	}

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null)
		{
			return false;
		}
		WorldPoint startTile = nexManager.socket.isMaster ?
			nexManager.nex.masterMainTile :
			nexManager.nex.slaveMainTile;

		if (startTile == null
			|| client.getLocalPlayer().getWorldLocation().equals(startTile)
			|| client.getLocalPlayer().getAnimation() != -1)
		{
			return false;
		}

		nexManager.print("Walking to start tile at " + nexManager.worldPointString(startTile));
		Movement.move(startTile);
		incrementActionCount();
		return true;
	}

}
