package com.example.Utility;

import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import java.util.Arrays;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.widgets.WidgetInfo;

public class Combat
{
	private static final int VENOM_THRESHOLD = 1000000;
	private static final int SPEC_VARP = 301;
	private static final int SPEC_ENERGY_VARP = 300;
	private static final int SPEC_ORB_ID = WidgetInfoExtended.MINIMAP_SPEC_CLICKBOX.getPackedId();

	public static int getMissingHealth()
	{
		return Skills.getLevel(Skill.HITPOINTS) - Skills.getBoostedLevel(Skill.HITPOINTS);
	}

	public static boolean isPoisoned()
	{
		return Static.getClient().getVarpValue(VarPlayer.POISON) > 0;
	}

	public static boolean isVenomed()
	{
		return Static.getClient().getVarpValue(VarPlayer.POISON) >= VENOM_THRESHOLD;
	}

	public static boolean isSpecEnabled()
	{
		return Static.getClient().getVarpValue(SPEC_VARP) == 1;
	}

	public static int getSpecEnergy()
	{
		return Static.getClient().getVarpValue(SPEC_ENERGY_VARP) / 10;
	}

	public static void toggleSpec()
	{
		if (isSpecEnabled())
		{
			return;
		}
		MousePackets.queueClickPacket();
		WidgetPackets.queueWidgetActionPacket(1, SPEC_ORB_ID, -1, -1);
	}

	public static AttackStyle getAttackStyle() {
		return Combat.AttackStyle.fromIndex(Static.getClient().getVarpValue(43));
	}

	public static enum AttackStyle {
		FIRST(0, WidgetInfo.COMBAT_STYLE_ONE),
		SECOND(1, WidgetInfo.COMBAT_STYLE_TWO),
		THIRD(2, WidgetInfo.COMBAT_STYLE_THREE),
		FOURTH(3, WidgetInfo.COMBAT_STYLE_FOUR),
		SPELLS(4, WidgetInfo.COMBAT_SPELL_BOX),
		SPELLS_DEFENSIVE(4, WidgetInfo.COMBAT_DEFENSIVE_SPELL_BOX),
		UNKNOWN(-1, (WidgetInfo)null);

		private final int index;
		private final WidgetInfo widgetInfo;

		private AttackStyle(int index, WidgetInfo widgetInfo) {
			this.index = index;
			this.widgetInfo = widgetInfo;
		}

		public int getIndex() {
			return this.index;
		}

		public WidgetInfo getWidgetInfo() {
			return this.widgetInfo;
		}

		public static AttackStyle fromIndex(int index) {
			return (AttackStyle) Arrays.stream(values()).filter((x) -> {
				return x.index == index;
			}).findFirst().orElse(UNKNOWN);
		}
	}
}
