package com.example.toagigatron.model.setup.range;

import net.runelite.api.ItemID;

public enum RangeLegs
{
	MASORI(ItemID.MASORI_CHAPS_F),
	ARMADYL(ItemID.ARMADYL_CHAINSKIRT),
	GUTHIX(ItemID.GUTHIX_CHAPS),
	NONE(-1);
	public final int itemId;

	RangeLegs(int itemId)
	{
		this.itemId = itemId;
	}
}
