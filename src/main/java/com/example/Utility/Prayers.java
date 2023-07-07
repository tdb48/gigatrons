package com.example.Utility;

import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.toagigatron.model.constants.WeaponMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

public class Prayers
{
	public static final ArrayList<Prayer> OVERHEAD_PRAYERS = new ArrayList<>(Arrays.asList(
		Prayer.PROTECT_FROM_MAGIC,
		Prayer.PROTECT_FROM_MELEE,
		Prayer.PROTECT_FROM_MISSILES));

	public static boolean isEnabled(Prayer prayer)
	{
		return Static.getClient().getVarbitValue(prayer.getVarbit()) == 1;
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

	public static boolean hasEnabled(List<Prayer> prayers)
	{
		for (Prayer prayer : prayers)
		{
			if (!isEnabled(prayer))
			{
				return false;
			}
		}
		return true;
	}

	public static void toggle(Prayer prayer)
	{
		int widgetId = prayer.getWidgetInfo().getPackedId();
		MousePackets.queueClickPacket();
		WidgetPackets.queueWidgetActionPacket(1, widgetId, -1, -1);
	}

	public static void disableOverheads()
	{
		for (Prayer p : OVERHEAD_PRAYERS)
		{
			if (Prayers.isEnabled(p))
			{
				Prayers.toggle(p);
			}
		}
	}

	public static int getPoints()
	{
		return Static.getClient().getBoostedSkillLevel(Skill.PRAYER);
	}

	public static void toggleQuickPrayer(boolean enabled)
	{
		int widgetId = WidgetInfo.MINIMAP_QUICK_PRAYER_ORB.getPackedId();
		MousePackets.queueClickPacket();
		WidgetPackets.queueWidgetActionPacket(1, widgetId, -1, -1);
	}

	public static boolean isQuickPrayerEnabled()
	{
		return Static.getClient().getVarbitValue(Varbits.QUICK_PRAYER) == 1;
	}

	public static boolean anyActive()
	{
		return Arrays.stream(Prayer.values()).anyMatch(Prayers::isEnabled);
	}

	public static void disableAll()
	{
		Arrays.stream(Prayer.values()).filter(Prayers::isEnabled).forEach(Prayers::toggle);
	}

}
