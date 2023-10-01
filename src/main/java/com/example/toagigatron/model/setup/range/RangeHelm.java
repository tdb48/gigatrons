package com.example.toagigatron.model.setup.range;

import net.runelite.api.ItemID;

public enum RangeHelm
{
	MASORI_RICH(ItemID.MASORI_MASK_F),
	MASORI_POOR(ItemID.MASORI_MASK),
	ARMADYL(ItemID.ARMADYL_HELMET),
	GUTHIX(ItemID.GUTHIX_COIF),
	NONE(-1);
	public final int itemId;

	RangeHelm(int itemId)
	{
		this.itemId = itemId;
	}
}

