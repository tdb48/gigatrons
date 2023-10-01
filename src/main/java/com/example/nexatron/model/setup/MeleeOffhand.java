package com.example.nexatron.model.setup;

import net.runelite.api.ItemID;

public enum MeleeOffhand
{
	AVERNIC(ItemID.AVERNIC_DEFENDER),
	DDEF(ItemID.DRAGON_DEFENDER);
	public final int itemId;

	MeleeOffhand(int itemId)
	{
		this.itemId = itemId;
	}
}
