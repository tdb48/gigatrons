package com.example.toagigatron.model.setup.mage;

import net.runelite.api.ItemID;

public enum MageHelm
{
	ANCESTRAL(ItemID.ANCESTRAL_HAT),
	DAGONHAI(ItemID.DAGONHAI_HAT),
	MYSTIC(ItemID.MYSTIC_HAT),
	NONE(-1);
	public final int itemId;

	MageHelm(int itemId)
	{
		this.itemId = itemId;
	}
}
