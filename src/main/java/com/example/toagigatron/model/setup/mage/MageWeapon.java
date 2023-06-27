package com.example.toagigatron.model.setup.mage;

import net.runelite.api.ItemID;

public enum MageWeapon
{
	SHADOW(ItemID.TUMEKENS_SHADOW),
	SANG(ItemID.SANGUINESTI_STAFF),
	SWAMP(ItemID.TRIDENT_OF_THE_SWAMP),
	NONE(-1);
	public final int itemId;

	MageWeapon(int itemId)
	{
		this.itemId = itemId;
	}
}
