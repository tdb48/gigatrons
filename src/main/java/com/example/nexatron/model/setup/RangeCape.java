package com.example.nexatron.model.setup;

import net.runelite.api.ItemID;

public enum RangeCape
{
	ASSEMBLER(ItemID.AVAS_ASSEMBLER),
	ASSEMBLER_ORN(ItemID.MASORI_ASSEMBLER),
	ACCUMULATOR(ItemID.AVAS_ACCUMULATOR),
	INFERNAL_CAPE(ItemID.INFERNAL_CAPE);
	public final int itemId;

	RangeCape(int itemId)
	{
		this.itemId = itemId;
	}
}
