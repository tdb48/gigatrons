package com.example.nexatron.model.setup;

import net.runelite.api.ItemID;

public enum Helm
{
	SERP(ItemID.SERPENTINE_HELM),
	MAGMA_SERP(ItemID.MAGMA_HELM),
	BERSERKER(ItemID.BERSERKER_HELM),
	NEIT(ItemID.HELM_OF_NEITIZNOT),
	FACEGUARD(ItemID.NEITIZNOT_FACEGUARD);
	public final int itemId;

	Helm(int itemId)
	{
		this.itemId = itemId;
	}
}
