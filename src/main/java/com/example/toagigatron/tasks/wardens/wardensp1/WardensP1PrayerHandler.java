package com.example.toagigatron.tasks.wardens.wardensp1;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Prayer;
import com.example.Utility.Prayers;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.constants.WeaponMap;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
	name = "Warden p1 prayers"
)
public class WardensP1PrayerHandler extends StagedTask
{
	@Inject
	public WardensP1PrayerHandler(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P1);
	}

	public List<Prayer> getPrayers()
	{
		if (toaManager.wardens12.obelisk == null)
		{
			return List.of();
		}
		return List.of(this.getOffensive());
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

	public boolean execute()
	{
		if (toaManager.wardens12.obelisk == null || Prayers.getPoints() == 0)
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
			if (toaManager.config.prayFlick() && Prayers.hasEnabled(getPrayers()))
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
}