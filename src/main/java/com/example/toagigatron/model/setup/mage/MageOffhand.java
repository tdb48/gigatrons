package com.example.toagigatron.model.setup.mage;

import net.runelite.api.ItemID;

public enum MageOffhand
{
	ARCANE(ItemID.ARCANE_SPIRIT_SHIELD),
	WARD(ItemID.ELIDINIS_WARD),
	WARD_UPGRADE(ItemID.ELIDINIS_WARD_F),
	NONE(-1);
	public final int itemId;

	MageOffhand(int itemId)
	{
		this.itemId = itemId;
	}
}
