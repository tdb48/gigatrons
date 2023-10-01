package com.example.nexatron.model.setup;

import net.runelite.api.ItemID;

public enum MeleeCape
{
	INFERNAL_CAPE(ItemID.INFERNAL_CAPE),
	FIRE_CAPE(ItemID.FIRE_CAPE);
	public final int itemId;

	MeleeCape(int itemId)
	{
		this.itemId = itemId;
	}
}
