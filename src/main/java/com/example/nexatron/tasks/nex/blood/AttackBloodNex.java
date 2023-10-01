package com.example.nexatron.tasks.nex.blood;


import com.example.Utility.Combat;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Attack Blood Nex",
	priority = 1
)
public class AttackBloodNex extends StagedTask
{
	@Inject
	public AttackBloodNex(NexManager nexManager)
	{
		super(nexManager, Stage.NEX_BLOOD);
	}

	public boolean execute()
	{
		if (nexManager.nex.centerPoint == null
			|| nexManager.nex.nex == null)
		{
			return false;
		}

		ArrayList<Integer> setup = decideSetup();
		if (!nexManager.hasGearEquipped(setup))
		{
			nexManager.print("Equipping gear");
			nexManager.swap(setup);
		}

		return false;
	}

	public ArrayList<Integer> decideSetup()
	{
		if (nexManager.nex.sacrificeActive)
		{
			return nexManager.nex.setup.rangeNex();
		}
		return nexManager.getBossHp() >= 1460 && Combat.getSpecEnergy() >= 75 ?
			nexManager.nex.setup.rangeNex() :
			nexManager.nex.setup.meleeNex();
	}

}
