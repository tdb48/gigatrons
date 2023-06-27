package com.example.toagigatron.model.setup.mage;

import net.runelite.api.ItemID;

public enum MageAmulet
{
	OCCULT(ItemID.OCCULT_NECKLACE),
	FURY(ItemID.AMULET_OF_FURY),
	NONE(-1);
	public final int itemId;

	MageAmulet(int itemId)
	{
		this.itemId = itemId;
	}
}
