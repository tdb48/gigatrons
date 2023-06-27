package com.example.toagigatron.model.setup.mage;

import net.runelite.api.ItemID;

public enum MageRing
{
	BRIMSTONE(ItemID.BRIMSTONE_RING),
	BERSERKER_I(ItemID.BERSERKER_RING_I),
	BERSERKER(ItemID.BERSERKER_RING),
	LIGHTBEARER(ItemID.LIGHTBEARER),
	NONE(-1);
	public final int itemId;

	MageRing(int itemId)
	{
		this.itemId = itemId;
	}
}
