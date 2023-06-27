package com.example.toagigatron.model.setup.range;

import net.runelite.api.ItemID;

public enum RangeAmulet
{
	ANGUISH(ItemID.NECKLACE_OF_ANGUISH),
	FURY(ItemID.AMULET_OF_FURY),
	NONE(-1);
	public final int itemId;

	RangeAmulet(int itemId)
	{
		this.itemId = itemId;
	}
}
