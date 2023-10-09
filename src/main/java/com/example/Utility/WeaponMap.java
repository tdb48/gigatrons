package com.example.Utility;

import java.util.HashMap;
import net.runelite.api.ItemID;

public class WeaponMap
{
	public static HashMap<Integer, WeaponStyle> StyleMap = new HashMap<>();

	static
	{
		StyleMap.put(ItemID.ZARYTE_CROSSBOW, WeaponStyle.RANGE);
		StyleMap.put(ItemID.ARMADYL_CROSSBOW, WeaponStyle.RANGE);
		StyleMap.put(ItemID.DRAGON_CROSSBOW, WeaponStyle.RANGE);
		StyleMap.put(ItemID.RUNE_CROSSBOW, WeaponStyle.RANGE);
		StyleMap.put(ItemID.DRAGON_CLAWS, WeaponStyle.MELEE);
		StyleMap.put(ItemID.VOIDWAKER, WeaponStyle.RANGE);
		StyleMap.put(ItemID.OSMUMTENS_FANG, WeaponStyle.MELEE);
		StyleMap.put(ItemID.OSMUMTENS_FANG_OR, WeaponStyle.MELEE);
		StyleMap.put(ItemID.TOXIC_BLOWPIPE, WeaponStyle.RANGE);
		StyleMap.put(ItemID.SANGUINESTI_STAFF, WeaponStyle.MAGIC);
		StyleMap.put(ItemID.DRAGON_DAGGER, WeaponStyle.MELEE);
		StyleMap.put(ItemID.BANDOS_GODSWORD, WeaponStyle.MELEE);
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
