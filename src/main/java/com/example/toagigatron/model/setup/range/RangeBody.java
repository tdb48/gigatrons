package com.example.toagigatron.model.setup.range;

import net.runelite.api.ItemID;

public enum RangeBody
{
	MASORI(ItemID.MASORI_BODY_F),
	ARMADYL(ItemID.ARMADYL_CHESTPLATE),
	GUTHIX(ItemID.GUTHIX_DHIDE_BODY),
	NONE(-1);
	public final int itemId;

	RangeBody(int itemId)
	{
		this.itemId = itemId;
	}
}
