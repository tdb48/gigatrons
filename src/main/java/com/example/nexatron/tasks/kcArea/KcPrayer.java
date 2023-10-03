package com.example.nexatron.tasks.kcArea;


import com.example.Utility.Prayer;
import com.example.Utility.Prayers;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.NPC;

@TaskDescriptor(
	name = "KC Area Prayer",
	priority = 1,
	register = true
)
public class KcPrayer extends StagedTask
{
	@Inject
	public KcPrayer(NexManager nexManager)
	{
		super(nexManager, Stage.KC_AREA);
	}

	public boolean execute()
	{
		if (!nexManager.config.kcMode())
		{
			return false;
		}
		if (Prayers.getPoints() == 0)
		{
			return false;
		}
		if (!this.getPrayers().isEmpty())
		{
			if (nexManager.config.prayFlick() && Prayers.hasEnabled(getPrayers()))
			{
				for (Prayer prayer : getPrayers())
				{
					Prayers.toggle(prayer);
				}
				for (Prayer prayer : getPrayers())
				{
					Prayers.toggle(prayer);
				}
			}
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
		NPC interactingNpc = nexManager.kcArea.getTarget();
		if (interactingNpc != null && interactingNpc.getName() != null)
		{
			String name = interactingNpc.getName();
			if (name.contains("Reaver") || name.contains("Mage"))
			{
				return Prayer.PROTECT_FROM_MAGIC;
			}
			if (name.contains("Ranger"))
			{
				return Prayer.PROTECT_FROM_MISSILES;
			}
			if (name.contains("Warrior"))
			{
				return Prayer.PROTECT_FROM_MELEE;
			}
		}
		return null;
	}

}
