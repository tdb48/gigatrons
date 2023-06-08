package com.example.Utility;

import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;

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
}
