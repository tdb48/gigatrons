package com.example.toagigatron.model.setup.mage;

import net.runelite.api.ItemID;

public enum MageGloves
{
	TORM(ItemID.TORMENTED_BRACELET),
	BARROWS(ItemID.BARROWS_GLOVES),
	COMBAT(ItemID.COMBAT_BRACELET),
	NONE(-1);
	public final int itemId;

	MageGloves(int itemId)
	{
		this.itemId = itemId;
	}
}
