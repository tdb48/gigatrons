package com.example.toagigatron.model.setup.melee;

import net.runelite.api.ItemID;

public enum MeleeGloves
{
	FEROCIOUS(ItemID.FEROCIOUS_GLOVES),
	BARROWS(ItemID.BARROWS_GLOVES),
	COMBAT(ItemID.COMBAT_BRACELET),
	NONE(-1);
	public final int itemId;

	MeleeGloves(int itemId)
	{
		this.itemId = itemId;
	}
}
