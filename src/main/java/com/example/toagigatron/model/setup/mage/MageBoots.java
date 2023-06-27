package com.example.toagigatron.model.setup.mage;

import net.runelite.api.ItemID;

public enum MageBoots
{
	PRIMS(ItemID.PRIMORDIAL_BOOTS),
	ETERNALS(ItemID.ETERNAL_BOOTS),
	NONE(-1);
	public final int itemId;

	MageBoots(int itemId)
	{
		this.itemId = itemId;
	}
}
