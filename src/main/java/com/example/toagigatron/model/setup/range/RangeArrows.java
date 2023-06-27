package com.example.toagigatron.model.setup.range;

import net.runelite.api.ItemID;

public enum RangeArrows
{
	ANCIENT(ItemID.ANCIENT_BLESSING),
	HOLY(ItemID.HOLY_BLESSING),
	RADAS_4(ItemID.RADAS_BLESSING_4),
	WAR(ItemID.WAR_BLESSING),
	UNHOLY(ItemID.UNHOLY_BLESSING),
	PEACEFUL(ItemID.PEACEFUL_BLESSING),
	HONOURABLE(ItemID.HONOURABLE_BLESSING),
	DRAGON_ARROWS(ItemID.DRAGON_ARROW),
	NONE(-1);
	public final int itemId;

	RangeArrows(int itemId)
	{
		this.itemId = itemId;
	}
}
