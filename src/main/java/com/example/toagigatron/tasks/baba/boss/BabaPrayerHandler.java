package com.example.toagigatron.tasks.baba.boss;

import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.WeaponMap;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.widgets.Prayers;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;

import java.util.List;

@TaskDescriptor(
	name = "Baba prayers"
)
public class BabaPrayerHandler extends StagedTask
{
	@Inject
	public BabaPrayerHandler(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_BOSS);
	}

	public boolean execute()
	{
		NPC baba = NPCs.getNearest("Ba-Ba");
		if (baba == null || Prayers.getPoints() == 0)
		{
			if (Prayers.anyActive())
			{
				Prayers.disableAll();
				return true;
			}
			else
			{
				return false;
			}
		}
		if (!this.getPrayers().isEmpty() && Prayers.getPoints() > 0)
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
		NPC baba = NPCs.getNearest("Ba-Ba");
		return baba != null && baba.getId() == 11780 ? List.of(this.getOffensive()) : List.of(Prayer.PROTECT_FROM_MELEE, this.getOffensive());
	}

	public Prayer getOffensive()
	{
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
						if (toaManager.baba.bouldersKilled == 0 || toaManager.baba.bouldersKilled == 7)
						{
							return Prayer.RIGOUR;
						}
						return Prayer.STEEL_SKIN;
					case 2:
						return Prayer.PIETY;
				}
			}

			Widget atk = Widgets.get(Combat.getAttackStyle().getWidgetInfo());
			if (atk != null)
			{
				String[] actions = atk.getActions();
				if (actions != null && actions.length == 1)
				{
					switch (actions[0])
					{
						case "Rapid":
							return Prayer.STEEL_SKIN;
						case "Accurate":
						case "Longrange":
							return Prayer.AUGURY;
					}
				}
			}
		}
		return Prayer.PIETY;
	}
}
