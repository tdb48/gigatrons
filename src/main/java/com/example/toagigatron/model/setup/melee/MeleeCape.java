package com.example.toagigatron.model.setup.melee;

import net.runelite.api.ItemID;

public enum MeleeCape
{
	INFERNAL_CAPE(ItemID.INFERNAL_CAPE),
	FIRE_CAPE(ItemID.FIRE_CAPE),
	NONE(-1);
	public final int itemId;

	MeleeCape(int itemId)
	{
		this.itemId = itemId;
	}
}
