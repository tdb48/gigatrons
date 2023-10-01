package com.example.nexatron.model.setup;

import net.runelite.api.ItemID;

public enum Helm
{
	MASORI_F(ItemID.MASORI_MASK_F),
	BUDGET_NEIT(ItemID.HELM_OF_NEITIZNOT),
	GUCCI_NEIT(ItemID.NEITIZNOT_FACEGUARD),
	TORVA(ItemID.TORVA_FULL_HELM),
	SLAYER_HELM(ItemID.SLAYER_HELMET);
	public final int itemId;

	Helm(int itemId)
	{
		this.itemId = itemId;
	}
}
