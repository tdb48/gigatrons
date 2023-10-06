package com.example.nexatron.model.setup;

import net.runelite.api.ItemID;

public enum RangeCape
{
	RICH_ASSEMBLER(ItemID.MASORI_ASSEMBLER),
	ASSEMBLER(ItemID.AVAS_ASSEMBLER),
	ACCUMULATOR(ItemID.AVAS_ACCUMULATOR);
	public final int itemId;

	RangeCape(int itemId)
	{
		this.itemId = itemId;
	}
}
