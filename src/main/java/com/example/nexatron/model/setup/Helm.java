package com.example.nexatron.model.setup;

import net.runelite.api.ItemID;

public enum Helm
{
	BUDGET_NEIT(ItemID.HELM_OF_NEITIZNOT),
	GUCCI_NEIT(ItemID.NEITIZNOT_FACEGUARD),
	SLAYER_HELM(ItemID.SLAYER_HELMET);
	public final int itemId;

	Helm(int itemId)
	{
		this.itemId = itemId;
	}
}
