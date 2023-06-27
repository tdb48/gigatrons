package com.example.toagigatron.model.setup.melee;

import net.runelite.api.ItemID;

public enum MeleeWeapon
{
	FANG_KIT(ItemID.OSMUMTENS_FANG_OR),
	FANG(ItemID.OSMUMTENS_FANG),
	NONE(-1);
	public final int itemId;

	MeleeWeapon(int itemId)
	{
		this.itemId = itemId;
	}
}
