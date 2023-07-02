package com.example.toagigatron.tasks;

import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.NPC;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Refill supplies",
	priority = 1
)
public class RefillSupplies extends StagedTask
{
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public RefillSupplies(ToaManager toaManager)
	{
		super(toaManager, Stage.ZEBAK_BOSS,
			Stage.ZEBAK_PUZZLE,
			Stage.AKKHA_PUZZLE,
			Stage.AKKHA_BOSS,
			Stage.WARDENS_P1,
			Stage.WARDENS_P2,
			Stage.WARDENS_P3);
	}

	public boolean execute()
	{
		if (toaManager.getStage() != Stage.WARDENS_P3)
		{
			if (gameTickManager.isAttackWaiting() && gameTickManager.attackWait <= 1)
			{
//				toaManager.print("Attack waiting and attack cd 1 or 0");
				return false;
			}
		}
		if(toaManager.getStage() == Stage.WARDENS_P3){
			if(client.getSelectedSceneTile() == null){ //we have not clicked to move do not refill
//				toaManager.print("No selected tile so we will not refill supplies right now");
				return false;
			}
		}
		NPC playerInteracting = toaManager.playerInteractingWith();
		if (toaManager.refill())
		{
			if(toaManager.getStage() != Stage.WARDENS_P3){
				//toaManager.reAttack(playerInteracting);
			}
			toaManager.print("Refilling supplies");
			return true;
		}
		return false;
	}
}