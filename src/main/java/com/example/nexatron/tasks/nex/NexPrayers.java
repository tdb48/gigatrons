package com.example.nexatron.tasks.nex;


import com.example.Utility.Combat;
import com.example.Utility.Prayer;
import com.example.Utility.Prayers;
import com.example.Utility.Static;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import com.example.nexatron.model.constants.WeaponMap;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Nex prayers",
	priority = 1
)
public class NexPrayers extends StagedTask
{
	public static final int AUGURY_UNLOCKED = 5452;
	@Inject
	GameTickManager gameTickManager;

	@Inject
	public NexPrayers(NexManager nexManager)
	{
		super(nexManager,
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
			else
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
		Prayer offensive = getOffensive();
		if (defensive == null)
		{
			return List.of(Prayers.getOffensive());
		}
		return List.of(offensive, defensive);
	}

	public Prayer getOffensive()
	{
		if (gameTickManager.isAttackWaiting()
			&& (nexManager.getStage().equals(Stage.NEX_BLOOD)
			|| nexManager.getStage().equals(Stage.MINION_BLOOD)
			|| nexManager.getStage().equals(Stage.NEX_ZAROS)))
		{
			return findBestMagePrayer();
		}
		ItemContainer equipped = Static.getClient().getItemContainer(InventoryID.EQUIPMENT);
		if (equipped != null)
		{
			Item weapon = equipped.getItem(3);
			if (weapon != null)
			{
				WeaponMap.WeaponStyle style = WeaponMap.StyleMap.getOrDefault(weapon.getId(), WeaponMap.WeaponStyle.MELEE);
				switch (style.ordinal())
				{
					case 0:
						return Prayer.AUGURY;
					case 1:
						return Prayer.RIGOUR;
					case 2:
						return Prayer.PIETY;
				}
			}

			Widget atk = Static.getClient().getWidget(Combat.getAttackStyle().getWidgetInfo());
			if (atk != null)
			{
				String[] actions = atk.getActions();
				if (actions != null && actions.length == 1)
				{
					switch (actions[0])
					{
						case "Rapid":
							return Prayer.RIGOUR;
						case "Accurate":
						case "Longrange":
							return Prayer.AUGURY;
					}
				}
			}
		}
		return Prayer.PIETY;
	}

	public Prayer getDefensive()
	{
		if (nexManager.getStage().equals(Stage.NEX_ICE)
			|| nexManager.getStage().equals(Stage.MINION_ICE)
			|| nexManager.getStage().equals(Stage.NEX_ZAROS))
		{
			// Protect ranged in ice prison
			if (nexManager.nex.prisonActive
				&& (nexManager.nex.stuckInPrisonTick > 0 && nexManager.nex.stuckInPrisonTick <= 2))
			{
				return Prayer.PROTECT_FROM_MISSILES;
			}
		}

		if ((nexManager.getStage() == Stage.NEX_SMOKE || nexManager.getStage() == Stage.NEX_ZAROS)
			&& nexManager.nex.distanceToNex() < 3
			&& nexManager.nex.nex.isInteracting()
			&& nexManager.nex.nex.getInteracting().equals(client.getLocalPlayer()))
		{
			return Prayer.PROTECT_FROM_MELEE;
		}

		if (nexManager.getStage() == Stage.NEX_SHADOW
			|| nexManager.getStage() == Stage.MINION_SHADOW)
		{
			if (nexManager.nex.umbra != null
				&& nexManager.nex.umbra.isInteracting()
				&& nexManager.nex.umbra.getInteracting().equals(client.getLocalPlayer()))
			{
				if (nexManager.nex.nex.isInteracting()
					&& !nexManager.nex.nex.getInteracting().equals(client.getLocalPlayer()))
				{
					return Prayer.PROTECT_FROM_MAGIC;
				}
//				if (nexManager.nex.umbraAttackTick == 2)
//				{
//					return Prayer.PROTECT_FROM_MAGIC;
//				}
			}
			return Prayer.PROTECT_FROM_MISSILES;
		}
		return Prayer.PROTECT_FROM_MAGIC;
	}

	public Prayer findBestMagePrayer()
	{
		return client.getVarbitValue(AUGURY_UNLOCKED) == 0
			? Prayer.MYSTIC_MIGHT
			: Prayer.AUGURY;
	}
}
