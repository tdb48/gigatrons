package com.example.toagigatron.model.constants;

import net.runelite.api.ItemID;

public enum Dart
{
	ADAMANT(ItemID.ADAMANT_DART, 0, "adamant"),
	RUNE(ItemID.RUNE_DART, 0, "rune"),
	AMETHYST(ItemID.AMETHYST_DART, 1936, "amethyst"),
	DRAGON(ItemID.DRAGON_DART, 1122, "dragon");
	public final int itemId;
	public final int projectileId;
	public final String name;

	Dart(int itemId, int projectileId, String name)
	{
		this.itemId = itemId;
		this.projectileId = projectileId;
		this.name = name;
	}
}
