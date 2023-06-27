package com.example.toagigatron.model.setup.mage;

import net.runelite.api.ItemID;

public enum MageLegs
{
	ANCESTRAL(ItemID.ANCESTRAL_ROBE_BOTTOM),
	AHRIMS(ItemID.AHRIMS_ROBESKIRT),
	DAGONHAI(ItemID.DAGONHAI_ROBE_BOTTOM),
	MYSTIC(ItemID.MYSTIC_ROBE_BOTTOM),
	NONE(-1);
	public final int itemId;

	MageLegs(int itemId)
	{
		this.itemId = itemId;
	}
}
