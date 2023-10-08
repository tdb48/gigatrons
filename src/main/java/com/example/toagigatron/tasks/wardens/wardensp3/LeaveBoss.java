package com.example.toagigatron.tasks.wardens.wardensp3;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.query.TileObjectQuery;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Game;
import com.example.Utility.ObjectUtil;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.TileObject;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Leave boss",
	blocking = true,
	register = true
)
public class LeaveBoss extends StagedTask
{

	public GameObject exit;
	@Inject
	public LeaveBoss(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P3);
		exit = null;
	}

	public boolean execute()
	{
		if(exit != null && !exit.getWorldLocation().isInScene(client))
		{
			exit = null;
			return false;
		}
		//GameObject exit = ObjectUtil.getNearestGameObject(ToaConstants.WARDENS_EXIT);
		if (exit != null)
		{
			if (client.getTickCount() % 3 == 0)
			{
				toaManager.print("Entering chest room");
			}
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(exit, false, "Use");
			return true;
		}
		return false;
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		//Should on
		if(event.getGameObject().getId() == ToaConstants.WARDENS_EXIT && toaManager.getStage().equals(Stage.WARDENS_P3))

		{
			exit = event.getGameObject();
		}
	}
	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		if(event.getGameObject().getId() == ToaConstants.WARDENS_EXIT)
		{
			exit = null;
		}
	}
}
