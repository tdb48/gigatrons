package com.example.nexatron.tasks.nex;


import com.example.Utility.Prayer;
import com.example.Utility.Prayers;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.List;
import javax.inject.Inject;

@TaskDescriptor(
	name = "Nex prayers",
	priority = 1
)
public class NexPrayers extends StagedTask
{
	@Inject
	public NexPrayers(NexManager nexManager)
	{
		super(nexManager,
			Stage.NEX_DEAD,
			Stage.MINION_SMOKE,
			Stage.NEX_SMOKE,
			Stage.MINION_SHADOW,
			Stage.NEX_SHADOW,
			Stage.MINION_BLOOD,
			Stage.NEX_BLOOD,
			Stage.MINION_ICE,
			Stage.NEX_ICE);
	}

	public boolean execute()
	{
		if (Prayers.getPoints() == 0)
		{
			return false;
		}
		if (!this.getPrayers().isEmpty())
		{
			for (Prayer prayer : getPrayers())
			{
				if (!Prayers.isEnabled(prayer))
				{
					Prayers.toggle(prayer);
				}
			}
			return true;
		}
		else if (this.getPrayers().isEmpty() && Prayers.anyActive())
		{
			Prayers.disableAll();
			return true;
		}
		return false;
	}

	public List<Prayer> getPrayers()
	{
		if (nexManager.nex.nex == null)
		{
			return List.of();
		}
		Prayer defensive = getDefensive();
		Prayer offensive = Prayers.getOffensive();
		if (defensive == null)
		{
			return List.of(Prayers.getOffensive());
		}
		return List.of(offensive, defensive);
	}

	public Prayer getDefensive()
	{
		// TODO: add pray mage logic for minion shadow
		if (nexManager.getStage() == Stage.NEX_SMOKE
			&& nexManager.nex.nex.isInteracting()
			&& nexManager.nex.nex.getInteracting().equals(client.getLocalPlayer()))
		{
			return Prayer.PROTECT_FROM_MELEE;
		}
		if (nexManager.getStage() == Stage.NEX_SHADOW
			|| nexManager.getStage() == Stage.MINION_SHADOW)
		{
			// TODO: flick against umbra depending on ticks while setting it up!
			if (nexManager.nex.umbra != null
				&& nexManager.nex.umbra.isInteracting()
				&& nexManager.nex.umbra.getInteracting().equals(client.getLocalPlayer()))
			{
				if (nexManager.nex.nex.isInteracting()
					&& !nexManager.nex.nex.getInteracting().equals(client.getLocalPlayer()))
				{
					return Prayer.PROTECT_FROM_MAGIC;
				}
				if (nexManager.nex.umbraAttackTick == 2)
				{
					return Prayer.PROTECT_FROM_MAGIC;
				}
			}
			return Prayer.PROTECT_FROM_MISSILES;
		}
		return Prayer.PROTECT_FROM_MAGIC;
	}
}
