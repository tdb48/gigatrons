package com.example.toagigatron.model.setup.melee;

import net.runelite.api.ItemID;

public enum MeleeBoots
{
	PRIMS(ItemID.PRIMORDIAL_BOOTS),
	DBOOTS(ItemID.DRAGON_BOOTS),
	NONE(-1);
	public final int itemId;

	MeleeBoots(int itemId)
	{
		this.itemId = itemId;
	}
}
