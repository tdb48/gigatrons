package com.example.toagigatron.model.setup.melee;

import net.runelite.api.ItemID;

public enum MeleeAmulet
{
	TORTURE(ItemID.AMULET_OF_TORTURE),
	BLOOD_FURY(ItemID.AMULET_OF_BLOOD_FURY),
	FURY(ItemID.AMULET_OF_FURY),
	NONE(-1);
	public final int itemId;

	MeleeAmulet(int itemId)
	{
		this.itemId = itemId;
	}
}
