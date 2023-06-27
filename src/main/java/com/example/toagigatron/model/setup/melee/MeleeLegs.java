package com.example.toagigatron.model.setup.melee;

import net.runelite.api.ItemID;

public enum MeleeLegs
{
	TORVA(ItemID.TORVA_PLATELEGS),
	BANDOS(ItemID.BANDOS_TASSETS),
	OBSIDIAN(ItemID.OBSIDIAN_PLATELEGS),
	NONE(-1);
	public final int itemId;

	MeleeLegs(int itemId)
	{
		this.itemId = itemId;
	}
}
