package com.example.toagigatron.model.setup.mage;

import net.runelite.api.ItemID;

public enum MageCape
{
	GUTHIX(ItemID.IMBUED_GUTHIX_CAPE),
	SARA(ItemID.IMBUED_SARADOMIN_CAPE),
	ZAMMORAK(ItemID.IMBUED_ZAMORAK_CAPE),
	NONE(-1);
	public final int itemId;

	MageCape(int itemId)
	{
		this.itemId = itemId;
	}
}
