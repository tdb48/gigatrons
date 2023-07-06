package com.example.nexatron.model.constants;

import java.util.HashMap;

public class WeaponMap
{
	public static HashMap<Integer, WeaponStyle> StyleMap = new HashMap<>();

	static
	{
		StyleMap.put(26219, WeaponStyle.MELEE); // Fang
		StyleMap.put(12926, WeaponStyle.RANGE); // BP
		StyleMap.put(22323, WeaponStyle.MAGIC); // Sang
		StyleMap.put(1215, WeaponStyle.MELEE); // Regular DDS
		StyleMap.put(11804, WeaponStyle.MELEE); // BGS (unkitted)
	}

	public WeaponMap()
	{
	}

	public enum WeaponStyle
	{
		MAGIC,
		RANGE,
		MELEE;

		WeaponStyle()
		{
		}
	}
}
