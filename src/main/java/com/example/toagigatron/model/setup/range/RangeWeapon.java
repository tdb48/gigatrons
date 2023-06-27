package com.example.toagigatron.model.setup.range;

import net.runelite.api.ItemID;

public enum RangeWeapon
{
	TWISTED_BOW(ItemID.TWISTED_BOW),
	BLOWPIPE(ItemID.TOXIC_BLOWPIPE),
	NONE(-1);
	public final int itemId;

	RangeWeapon(int itemId)
	{
		this.itemId = itemId;
	}
}
