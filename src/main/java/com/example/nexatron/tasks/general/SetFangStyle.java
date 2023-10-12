package com.example.nexatron.tasks.general;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.Utility.Combat;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import javax.inject.Inject;

@TaskDescriptor(
	name = "SetFangStyle",
	priority = -Integer.MAX_VALUE
)
public class SetFangStyle extends StagedTask
{
	@Inject
	NexManager nexManager;

	@Inject
	public SetFangStyle(NexManager nexManager)
	{
		super(nexManager,
			Stage.NEX_START,
			Stage.NEX_DEAD,
			Stage.MINION_SMOKE,
			Stage.NEX_SMOKE,
			Stage.MINION_SHADOW,
			Stage.NEX_SHADOW,
			Stage.MINION_BLOOD,
			Stage.NEX_BLOOD,
			Stage.MINION_ICE,
			Stage.NEX_ICE,
			Stage.NEX_ZAROS);
	}

	public boolean execute()
	{
		if (Equipment.search().nameContains("fang").first().orElse(null) == null)
		{
			return false;
		}
		Combat.AttackStyle attackStyle = nexManager.getStage().equals(Stage.MINION_BLOOD)
			? Combat.AttackStyle.THIRD
			: Combat.AttackStyle.SECOND;
		if (!Combat.getAttackStyle().equals(attackStyle))
		{
			nexManager.print("Switching fang style ");
			Combat.toggleStyle(attackStyle);
			incrementActionCount();
			return true;
		}
		return false;
	}
}
