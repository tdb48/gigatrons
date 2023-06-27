package com.example.toagigatron.model.setup.melee;

import net.runelite.api.ItemID;

public enum MeleeOffhand
{
	AVERNIC(ItemID.AVERNIC_DEFENDER),
	DDEF(ItemID.DRAGON_DEFENDER),
	DFS(ItemID.DRAGONFIRE_SHIELD),
	NONE(-1);
	public final int itemId;

	MeleeOffhand(int itemId)
	{
		this.itemId = itemId;
	}
}
