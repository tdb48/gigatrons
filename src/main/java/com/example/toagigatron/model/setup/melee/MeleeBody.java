package com.example.toagigatron.model.setup.melee;

import net.runelite.api.ItemID;

public enum MeleeBody
{
	TORVA(ItemID.TORVA_PLATEBODY),
	BANDOS(ItemID.BANDOS_CHESTPLATE),
	OBSIDIAN(ItemID.OBSIDIAN_PLATEBODY),
	NONE(-1);
	public final int itemId;

	MeleeBody(int itemId)
	{
		this.itemId = itemId;
	}
}
