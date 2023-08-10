package com.example.nexatron.model.constants;

import com.example.Utility.WorldAreas;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public class NexConst
{
	public static List<Integer> WEAPONS =
		List.of(
			ItemID.OSMUMTENS_FANG,
			ItemID.OSMUMTENS_FANG_OR,
			ItemID.ZARYTE_CROSSBOW,
			ItemID.ARMADYL_CROSSBOW,
			ItemID.TOXIC_BLOWPIPE);

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

	public static final int NEX_MELEE_ANIMATION = 9180;
	public static final int NEX_MAGE_ANIMATION = 9188;
	public static final int NEX_SHADOW_ANIMATION = 9189;

	public static final int KC_AREA_DOOR = 42933;
	public static final int ICE_PRISON = 42944;
	public static final int DEATH_CHEST = 9168;
	public static final int BANK_DOOR = 42934;
	public static final int ALTAR = 42965;
	public static final int ACTIVE_BARRIER = 42967;
	public static final int INACTIVE_BARRIER = 42968;
	public static final int BANKER = 11289;
	public static final int ANCIENT_KILLCOUNT_VARBIT = 13080;
	public static final WorldPoint ENTER_BANK_TILE = new WorldPoint(2898, 5203, 0);
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
	public final String SMOKE_SPAWN_MSG = "Fill my soul with smoke!";
	public final String COUGH_SPECIAL_MSG = "Let the virus flow through you!";
	public final String DASH_SPECIAL_MSG = "There is...";
	public final String FUMUS_SPAWN_MSG = "Fumus, don't fail me!";

	//Shadow
	public final String SHADOW_SPAWN_MSG = "Darken my shadow!";
	public final String SHADOW_DARKNESS_SPECIAL_MSG = "Embrace darkness!";
	public final String SHADOW_POOL_SPECIAL_MSG = "Fear the shadow!";
	public final String UMBRA_SPAWN_MSG = "Umbra, don't fail me!";

	//Blood
	public final String BLOOD_SPAWN_MSG = "Flood my lungs with blood!";
	public final String BLOOD_SIPHON_SPECIAL_MSG = "A siphon will solve this!";
	public final String BLOOD_SACRIFICE_SPECIAL_MSG = "I demand a blood sacrifice!";
	public final String CRUOR_SPAWN_MSG = "Cruor, don't fail me!";

	//Ice
	public final String ICE_SPAWN_MSG = "Infuse me with the power of ice!";
	public final String ICE_PRISON_SPECIAL_MSG = "Die now, in a prison of ice!";
	public final String ICE_CONTAIN_SPECIAL_MSG = "Contain this!";
	public final String GLACIES_SPAWN_MSG = "Glacies, don't fail me!";

	//Zaros
	public final String ZAROS_SPAWN_MSG = "NOW, THE POWER OF ZAROS!";

}
