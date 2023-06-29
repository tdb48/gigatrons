package com.example.toagigatron.tasks.akkha.boss;

import com.example.Utility.Movement;
import com.example.Utility.Reachable;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Akkha dodge memory",
	priority = 1,
	blocking = true
)
public class AkkhaDodgeMemory extends StagedTask
{
	@Inject
	public AkkhaDodgeMemory(ToaManager toaManager)
	{
		super(toaManager, Stage.AKKHA_BOSS);
	}

	public boolean execute()
	{
		if (toaManager.akkha.isNotInBossRoom() || toaManager.akkha.memoryTiles.isEmpty())
		{
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		if (!toaManager.akkha.memoryTiles.get(0).equals(playerPoint) && Reachable.isWalkable(toaManager.akkha.memoryTiles.get(0)))
		{
			toaManager.print("Dodging memory to " + toaManager.akkha.memoryTiles.get(0).toString());
			Movement.walk(toaManager.akkha.memoryTiles.get(0));
			return true;
		}
		return false;
	}
}
