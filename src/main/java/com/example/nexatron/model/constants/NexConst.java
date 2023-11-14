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
	public static final int ALTAR_TELEPORT_ANIM = 714;
	public static final int TROLLHEIM_TAB = ItemID.TROLLHEIM_TELEPORT;
	public static final int DIA_BOLTS = ItemID.DIAMOND_DRAGON_BOLTS_E;
	public static final int RUBY_BOLTS = ItemID.RUBY_DRAGON_BOLTS_E;
	public static final int FANG_ANIMATION = 9471;
	public static final int FANG_SLASH_ANIMATION = 390;
	public static final int FANG_SPEC_ANIMATION = 6118;
	public static final int ZCB_ANIMATION = 9168;
	public static final int TROLLHEIM_ROCKS = 3803;
	public static final int TROLLHEIM_TO_GWD_CRAWL = 26382;
	public static final int GWD_ENTRANCE = 26419;
	public static final int NEX_DOOR = 42841;

	public static final ArrayList<Integer> DARTS = new ArrayList<>(Arrays.asList(1043, 1936, 1122));
	public static final Set<Integer> KC_LOOT = Set.of(
		ItemID.SUPER_RESTORE3,
		ItemID.DRAGON_BOOTS,
		ItemID.BLOOD_ESSENCE,
		ItemID.NIHIL_SHARD,
		ItemID.PRAYER_POTION2,
		ItemID.ANCIENT_CEREMONIAL_BOOTS,
		ItemID.ANCIENT_CEREMONIAL_GLOVES,
		ItemID.ANCIENT_CEREMONIAL_LEGS,
		ItemID.ANCIENT_CEREMONIAL_MASK,
		ItemID.ANCIENT_CEREMONIAL_TOP,
		ItemID.DRAGON_SPEAR,
		ItemID.SHIELD_LEFT_HALF,
		ItemID.BLOOD_RUNE);
	public static final Set<Integer> HIGH_PRIO_LOOT = Set.of(
		ItemID.TORVA_FULL_HELM_DAMAGED,
		ItemID.TORVA_PLATEBODY_DAMAGED,
		ItemID.TORVA_PLATELEGS_DAMAGED,
		ItemID.ANCIENT_HILT,
		ItemID.NIHIL_HORN,
		ItemID.ZARYTE_VAMBRACES);
	public static final Set<Integer> LOW_PRIO_LOOT = Set.of(
		246, // noted wine of zamorak
		2434, // ppot 4
		454, // noted coal
		452, // noted runite ore
		1618, // noted uncut diamond
		574, // noted air orb
		21930, // dragon bolts (unf)
		26390, // blood essence
		9245, // onyx bolts (e)
		1620, // noted uncut ruby
		560, // death rune
		566, // soul rune
		2, // cannonball
		451, // runite ore?
		3024, // super restore (4)
		6685, // saradomin brew (4)
		26388, // ecu key shards
		26231, // nihil shards
		995, // coins
		ItemID.COINS,
		565); // blood runes
	public static final Set<Integer> SUPPLY_LOOT = Set.of(
		-1);
	public static final int NEX_MELEE_ANIMATION = 9180;
	public static final int NEX_MAGE_ANIMATION = 9188;
	public static final int NEX_SHADOW_ANIMATION = 9189;
	public static final int NEX_CHASE_POSE_ANIMATION = 9175;
	public static final int NEX_DASHBACK_ANIMATION = 9187;
	public static final int NEX_NEW_PHASE_ANIMATION = 9179;
	public static final int KC_AREA_DOOR = 42933;
	public static final int ICE_PRISON = 42944;
	public static final int DEATH_CHEST = 9168;
	public static final int BANK_DOOR = 42934;
	public static final int ALTAR = 42965;
	public static final int UMBRA_ATTACK_ANIMATION = 1979;
	public static final int ACTIVE_BARRIER = 42967;
	public static final int INACTIVE_BARRIER = 42968;
	public static final int BANKER = 11289;
	public static final int SHADOW = 42942;
	public static final int MUSHROOM_GRAPIHC_1 = 2013;
	public static final int MUSHROOM_GRAPIHC_2 = 2014;
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
	public static final WorldArea TROLLHEIM_TELEPORT_AREA = WorldAreas.createArea(
		new WorldPoint(2886, 3667, 0),
		new WorldPoint(2896, 3684, 0));
	public static final WorldArea TROLLHEIM = WorldAreas.createArea(
		new WorldPoint(2866, 3665, 0),
		new WorldPoint(2924, 3717, 0));
	public static final WorldArea GWD_OUTSIDE = WorldAreas.createArea(
		new WorldPoint(2896, 3719, 0),
		new WorldPoint(2926, 3750, 0));
	public static final WorldArea GWD = WorldAreas.createArea(
		new WorldPoint(2869, 5275, 2),
		new WorldPoint(2900, 5314, 2));
	public static final int NEX_SMOKE_PROC = 2720;
	public static final int NEX_SHADOW_PROC = 2040;
	public static final int NEX_BLOOD_PROC = 1360;
	public static final int NEX_ICE_PROC = 680;
	public static final int SACRIFICE_GRAPIHC = -1;
	public static final int SPELLBOOK_VARB = 4070;
	public static final String BREW_MESSAGE = "You drink some of the foul liquid.";
	public static final String POTION_MESSAGE = "you drink some";
	public static final int ALTAR_VARBIT = 4099;
	public static final String SMOKE_SPAWN_MSG = "Fill my soul with smoke!";
	public static final String COUGH_SPECIAL_MSG = "Let the virus flow through you!";
	public static final String DASH_SPECIAL_MSG = "There is...";
	public static final String FUMUS_SPAWN_MSG = "Fumus, don't fail me!";
	//Shadow
	public static final String SHADOW_SPAWN_MSG = "Darken my shadow!";
	public static final String SHADOW_DARKNESS_SPECIAL_MSG = "Embrace darkness!";
	public static final String SHADOW_POOL_SPECIAL_MSG = "Fear the shadow!";
	public static final String UMBRA_SPAWN_MSG = "Umbra, don't fail me!";
	//Blood
	public static final String BLOOD_SPAWN_MSG = "Flood my lungs with blood!";
	public static final String BLOOD_SIPHON_SPECIAL_MSG = "A siphon will solve this!";
	public static final String BLOOD_SACRIFICE_SPECIAL_MSG = "I demand a blood sacrifice!";
	public static final String BLOOD_SACRIFICE_ACTIVE_MSG = "nex has marked you for a blood sacrifice";
	public static final String BLOOD_SACRIFICE_INACTIVE_MSG = "you managed to escape from nex";
	public static final String BLOOD_SACRIFICE_INACTIVE_MSG2 = "you failed to escape from nex";
	public static final String CRUOR_SPAWN_MSG = "Cruor, don't fail me!";
	//Ice
	public static final String ICE_SPAWN_MSG = "Infuse me with the power of ice!";
	public static final String ICE_PRISON_SPECIAL_MSG = "Die now, in a prison of ice!";
	public static final String ICE_CONTAIN_SPECIAL_MSG = "Contain this!";
	public static final String GLACIES_SPAWN_MSG = "Glacies, don't fail me!";
	public static final String PRISON_IMPRISONED = "you've been trapped in an ice prison";
	public static final String PRISON_FREED = "you've been freed from the ice prison";
	public static final String ZAROS_START = "the power of zaros";
	//Zaros
	public static final String ZAROS_SPAWN_MSG = "NOW, THE POWER OF ZAROS!";
	public static List<Integer> WEAPONS =
		List.of(
			ItemID.OSMUMTENS_FANG,
			ItemID.OSMUMTENS_FANG_OR,
			ItemID.ZARYTE_CROSSBOW,
			ItemID.ARMADYL_CROSSBOW,
			ItemID.TOXIC_BLOWPIPE);

}
