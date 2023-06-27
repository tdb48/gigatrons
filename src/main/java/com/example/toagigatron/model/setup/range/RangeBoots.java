package com.example.toagigatron.model.setup.range;

import net.runelite.api.ItemID;

public enum RangeBoots
{
	PRIMS(ItemID.PRIMORDIAL_BOOTS),
	NONE(-1);
	public final int itemId;

	RangeBoots(int itemId)
	{
		this.itemId = itemId;
	}
}
