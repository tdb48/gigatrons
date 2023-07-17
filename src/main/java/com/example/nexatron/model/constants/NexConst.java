package com.example.nexatron.model.constants;

import com.example.Utility.WorldAreas;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public class NexConst
{
	public static final int TROLLHEIM_TAB = ItemID.TROLLHEIM_TELEPORT;
	public static final int DIA_BOLTS = ItemID.DIAMOND_DRAGON_BOLTS_E;
	public static final int RUBY_BOLTS = ItemID.RUBY_DRAGON_BOLTS_E;
	public static final int FANG_ANIMATION = 9471;
	public static final int ZCB_ANIMATION = 9168;
	public static final ArrayList<Integer> DARTS = new ArrayList<>(Arrays.asList(1043, 1936, 1122));
	public static final Set<Integer> HIGH_PRIO_LOOT = Set.of(
		ItemID.TORVA_FULL_HELM_DAMAGED,
		ItemID.TORVA_PLATEBODY_DAMAGED,
		ItemID.TORVA_PLATELEGS_DAMAGED,
		ItemID.ANCIENT_HILT,
		ItemID.NIHIL_HORN,
		ItemID.ZARYTE_VAMBRACES);
	public static final Set<Integer> LOW_PRIO_LOOT = Set.of(
		246, // Noted wine of zamorak
		2434,
		454,
		452,
		1618,
		574,
		21930,
		26390,
		9245,
		1620,
		560,
		566,
		2,
		451,
		3024,
		2444,
		6685,
		26388,
		26231,
		565);

	public static final Set<Integer> SUPPLY_LOOT = Set.of(
		-1);


	public static final int NEX_MELEE_ANIMATION = -1;
	public static final int NEX_MAGE_ANIMATION = -1;

	public static final int KC_AREA_DOOR = 42933;
	public static final int ICE_PRISON = 42944;
	public static final int DEATH_CHEST = 9168;
	public static final int BANK_DOOR = 42934;
	public static final int ALTAR = 42965;
	public static final int ACTIVE_BARRIER = 42967;
	public static final int INACTIVE_BARRIER = 42968;
	public static final int BANKER = 11289;
	public static final int ANCIENT_KILLCOUNT_VARBIT = 13080;
	public static final WorldArea KC_AREA = WorldAreas.createArea(
		new WorldPoint(2848, 5194, 0),
		new WorldPoint(2900, 5228, 0));
	public static final WorldArea BANK_AREA = WorldAreas.createArea(
		new WorldPoint(2899, 5198, 0),
		new WorldPoint(2909, 5209, 0));
	public static final WorldArea LOBBY_AREA = WorldAreas.createArea(
		new WorldPoint(2850, 5216, 0),
		new WorldPoint(2862, 5231, 0));

	public static final String BREW_MESSAGE = "You drink some of the foul liquid.";


}
