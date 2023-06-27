package com.example.toagigatron.model.setup.range;

import net.runelite.api.ItemID;

public enum RangeGloves
{
	ZARYTE(ItemID.ZARYTE_VAMBRACES),
	BARROWS(ItemID.BARROWS_GLOVES),
	COMBAT(ItemID.COMBAT_BRACELET),
	NONE(-1);
	public final int itemId;

	RangeGloves(int itemId)
	{
		this.itemId = itemId;
	}
}
