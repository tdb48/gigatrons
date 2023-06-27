package com.example.toagigatron.model.setup.mage;

import net.runelite.api.ItemID;

public enum MageBody
{
	ANCESTRAL(ItemID.ANCESTRAL_ROBE_TOP),
	AHRIMS(ItemID.AHRIMS_ROBETOP),
	DAGONHAI(ItemID.DAGONHAI_ROBE_TOP),
	MYSTIC(ItemID.MYSTIC_ROBE_TOP),
	NONE(-1);
	public final int itemId;

	MageBody(int itemId)
	{
		this.itemId = itemId;
	}
}
