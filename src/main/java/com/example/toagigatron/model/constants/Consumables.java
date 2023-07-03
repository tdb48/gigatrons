package com.example.toagigatron.model.constants;

import com.example.EthanApiPlugin.Collections.Inventory;
import java.util.ArrayList;
import java.util.Arrays;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;

public class Consumables
{
	public static final int PREPOT_SCB = ItemID.SUPER_COMBAT_POTION1;
	public static final int PREPOT_STAM = ItemID.STAMINA_POTION1;
	public static final int PREPOT_RANGE = ItemID.RANGING_POTION1;
	public static final int PREPOT_HEART = ItemID.IMBUED_HEART;
	public static final int PREPOT_SATURATED_HEART = ItemID.SATURATED_HEART;
	public static final int PREPOT_ANGLER = ItemID.ANGLERFISH;
	public static final int PREPOT_ANTI = ItemID.ANTIVENOM1_12919;
	public static final int FULL_DOSE_SCB = ItemID.SUPER_COMBAT_POTION4;
	public static final int FULL_DOSE_STAM = ItemID.STAMINA_POTION4;
	public static final int FULL_DOSE_ANTI = ItemID.ANTIVENOM4_12913;
	public static final int FULL_DOSE_BREW = ItemID.SARADOMIN_BREW4;
	public static final int FULL_DOSE_RESTORE = ItemID.SUPER_RESTORE4;
	public static final int FULL_DOSE_SANFEW = ItemID.SANFEW_SERUM4;
	public static final int SUPPLY_BAG = ItemID.SUPPLIES;
	public static final ArrayList<Integer> RESTORE =
		new ArrayList<>(Arrays.asList(
			ItemID.TEARS_OF_ELIDINIS_1,
			ItemID.TEARS_OF_ELIDINIS_2,
			ItemID.TEARS_OF_ELIDINIS_3,
			ItemID.TEARS_OF_ELIDINIS_4,
			ItemID.SUPER_RESTORE1,
			ItemID.SUPER_RESTORE2,
			ItemID.SUPER_RESTORE3,
			ItemID.SUPER_RESTORE4,
			ItemID.SANFEW_SERUM1,
			ItemID.SANFEW_SERUM2,
			ItemID.SANFEW_SERUM3,
			ItemID.SANFEW_SERUM4
		));
	public static final ArrayList<Integer> RAID_RESTORE =
		new ArrayList<>(Arrays.asList(
			ItemID.TEARS_OF_ELIDINIS_1,
			ItemID.TEARS_OF_ELIDINIS_2,
			ItemID.TEARS_OF_ELIDINIS_3,
			ItemID.TEARS_OF_ELIDINIS_4
		));
	public static final ArrayList<Integer> RANGE =
		new ArrayList<>(Arrays.asList(
			ItemID.RANGING_POTION1,
			ItemID.RANGING_POTION2,
			ItemID.RANGING_POTION3,
			ItemID.RANGING_POTION4
		));
	public static final ArrayList<Integer> SANFEW =
		new ArrayList<>(Arrays.asList(
			ItemID.SANFEW_SERUM1,
			ItemID.SANFEW_SERUM2,
			ItemID.SANFEW_SERUM3,
			ItemID.SANFEW_SERUM4
		));
	public static final ArrayList<Integer> ANTI =
		new ArrayList<>(Arrays.asList(
			ItemID.ANTIDOTE4_5952,
			ItemID.ANTIDOTE3_5954,
			ItemID.ANTIDOTE2_5956,
			ItemID.ANTIDOTE1_5958,
			ItemID.ANTIVENOM1_12919,
			ItemID.ANTIVENOM2_12917,
			ItemID.ANTIVENOM3_12915,
			ItemID.ANTIVENOM4_12913
		));
	public static final ArrayList<Integer> COMBAT =
		new ArrayList<>(Arrays.asList(
			ItemID.SUPER_COMBAT_POTION1,
			ItemID.SUPER_COMBAT_POTION2,
			ItemID.SUPER_COMBAT_POTION3,
			ItemID.SUPER_COMBAT_POTION4)
		);
	public static final ArrayList<Integer> STAM =
		new ArrayList<>(Arrays.asList(
			ItemID.STAMINA_POTION1,
			ItemID.STAMINA_POTION2,
			ItemID.STAMINA_POTION3,
			ItemID.STAMINA_POTION4
		));
	public static final ArrayList<Integer> SALT =
		new ArrayList<>(Arrays.asList(
			ItemID.SMELLING_SALTS_1,
			ItemID.SMELLING_SALTS_2
		));
	public static final ArrayList<Integer> SPEC =
		new ArrayList<>(Arrays.asList(
			ItemID.LIQUID_ADRENALINE_1,
			ItemID.LIQUID_ADRENALINE_2
		));
	public static final ArrayList<Integer> RAID_BREW =
		new ArrayList<>(Arrays.asList(
			ItemID.NECTAR_1,
			ItemID.NECTAR_2,
			ItemID.NECTAR_3,
			ItemID.NECTAR_4)
		);
	public static final ArrayList<Integer> SCARAB =
		new ArrayList<>(Arrays.asList(
			ItemID.BLESSED_CRYSTAL_SCARAB_1,
			ItemID.BLESSED_CRYSTAL_SCARAB_2)
		);
	public static final ArrayList<Integer> BREW =
		new ArrayList<>(Arrays.asList(
			ItemID.NECTAR_1,
			ItemID.NECTAR_2,
			ItemID.NECTAR_3,
			ItemID.NECTAR_4,
			ItemID.SARADOMIN_BREW1,
			ItemID.SARADOMIN_BREW2,
			ItemID.SARADOMIN_BREW3,
			ItemID.SARADOMIN_BREW4)
		);
	public static final ArrayList<Integer> AMBROSIA =
		new ArrayList<>(Arrays.asList(
			ItemID.AMBROSIA_1,
			ItemID.AMBROSIA_2
		));
	public static ArrayList<Integer> getNecessaryPotions = new ArrayList<>(
		Arrays.asList(
			ItemID.ANTIVENOM4_12913,
			ItemID.SARADOMIN_BREW4,
			ItemID.SUPER_COMBAT_POTION4,
			ItemID.SANFEW_SERUM4,
			ItemID.SUPER_RESTORE4,
			ItemID.STAMINA_POTION4));
	public static ArrayList<Integer> getThrallItems = new ArrayList<>(
		Arrays.asList(
			ItemID.BOOK_OF_THE_DEAD,
			ItemID.DIVINE_RUNE_POUCH));

	public static Widget getBrew()
	{
		for (int i : BREW)
		{
			if (Inventory.search().withId(i).first().orElse(null) != null)
			{
				return Inventory.search().withId(i).first().orElse(null);
			}
		}
		return null;
	}

	public static Widget getScarab()
	{
		for (int i : SCARAB)
		{
			if (Inventory.search().withId(i).first().orElse(null) != null)
			{
				return Inventory.search().withId(i).first().orElse(null);
			}
		}
		return null;
	}

	public static Widget getSalt()
	{
		for (int i : SALT)
		{
			if (Inventory.search().withId(i).first().orElse(null) != null)
			{
				return Inventory.search().withId(i).first().orElse(null);
			}
		}
		return null;
	}

	public static Widget getAmbrosia()
	{
		for (int i : AMBROSIA)
		{
			if (Inventory.search().withId(i).first().orElse(null) != null)
			{
				return Inventory.search().withId(i).first().orElse(null);
			}
		}
		return null;
	}

	public static Widget getRestore()
	{
		for (int i : RESTORE)
		{
			if (Inventory.search().withId(i).first().orElse(null) != null)
			{
				return Inventory.search().withId(i).first().orElse(null);
			}
		}
		return null;
	}

	public static Widget getSanfew()
	{
		for (int i : SANFEW)
		{
			if (Inventory.search().withId(i).first().orElse(null) != null)
			{
				return Inventory.search().withId(i).first().orElse(null);
			}
		}
		return null;
	}

	public static Widget getAdrenaline()
	{
		for (int i : SPEC)
		{
			if (Inventory.search().withId(i).first().orElse(null) != null)
			{
				return Inventory.search().withId(i).first().orElse(null);
			}
		}
		return null;
	}

	public static boolean isRaidPotion(int id)
	{
		return AMBROSIA.contains(id) || RAID_BREW.contains(id) || RAID_RESTORE.contains(id) || SALT.contains(id) || SPEC.contains(id) || SCARAB.contains(id);
	}

	public static boolean samePotionType(int idOne, int idTwo)
	{
		return (AMBROSIA.contains(idOne) && AMBROSIA.contains(idTwo)) ||
			(RAID_BREW.contains(idOne) && RAID_BREW.contains(idTwo)) ||
			(RAID_RESTORE.contains(idOne) && RAID_RESTORE.contains(idTwo)) ||
			(SALT.contains(idOne) && SALT.contains(idTwo)) ||
			(SPEC.contains(idOne) && SPEC.contains(idTwo)) ||
			(SCARAB.contains(idOne) && SCARAB.contains(idTwo));
	}
}

