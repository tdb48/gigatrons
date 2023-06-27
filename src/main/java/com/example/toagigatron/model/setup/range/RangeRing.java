package com.example.toagigatron.model.setup.range;

import net.runelite.api.ItemID;

public enum RangeRing
{
	BRIMSTONE(ItemID.BRIMSTONE_RING),
	BERSERKER_I(ItemID.BERSERKER_RING_I),
	BERSERKER(ItemID.BERSERKER_RING),
	LIGHTBEARER(ItemID.LIGHTBEARER),
	NONE(-1);
	public final int itemId;

	RangeRing(int itemId)
	{
		this.itemId = itemId;
	}
}
