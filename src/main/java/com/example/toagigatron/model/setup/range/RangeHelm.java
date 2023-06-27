package com.example.toagigatron.model.setup.range;

import net.runelite.api.ItemID;

public enum RangeHelm
{
	MASORI(ItemID.MASORI_MASK_F),
	ARMADYL(ItemID.ARMADYL_HELMET),
	GUTHIX(ItemID.GUTHIX_COIF),
	NONE(-1);
	public final int itemId;

	RangeHelm(int itemId)
	{
		this.itemId = itemId;
	}
}
