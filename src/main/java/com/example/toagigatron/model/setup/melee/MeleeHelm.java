package com.example.toagigatron.model.setup.melee;

import net.runelite.api.ItemID;

public enum MeleeHelm
{
	TORVA(ItemID.TORVA_FULL_HELM),
	FACEGUARD(24271),
	NEZZY(ItemID.HELM_OF_NEITIZNOT),
	BERSERKER(ItemID.BERSERKER_HELM),
	NONE(-1);
	public final int itemId;

	MeleeHelm(int itemId)
	{
		this.itemId = itemId;
	}
}
