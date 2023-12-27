package com.example.nexatron.model.setup;

import net.runelite.api.ItemID;

public enum Helm
{
	BERSERKER(ItemID.BERSERKER_HELM),
	NEIT(ItemID.HELM_OF_NEITIZNOT),
	FACEGUARD(ItemID.NEITIZNOT_FACEGUARD);
	public final int itemId;

	Helm(int itemId)
	{
		this.itemId = itemId;
	}
}
