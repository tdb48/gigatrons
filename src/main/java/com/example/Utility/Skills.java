package com.example.Utility;

public class Skills
{
	public static int getBoostedLevel(net.runelite.api.Skill skill)
	{
		return Static.getClient().getBoostedSkillLevel(skill);
	}

	public static int getLevel(net.runelite.api.Skill skill)
	{
		return Static.getClient().getRealSkillLevel(skill);
	}

	public static int getExperience(net.runelite.api.Skill skill)
	{
		return Static.getClient().getSkillExperience(skill);
	}
}
