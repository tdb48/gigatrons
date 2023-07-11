package com.example.toagigatron.model.constants;

import com.example.Utility.Prayer;
import com.example.toagigatron.model.setup.mage.MageWeapon;
import com.example.toagigatron.model.setup.melee.MeleeWeapon;
import com.example.toagigatron.model.setup.range.RangeWeapon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;

public class ToaConstants
{
	public static final int STEPPING_STONE_ANIMATION = 741;
	public static final int BGS_SPEC_ANIMATION = 7642;
	public static final int YELLOW_UFO = 45751;
	public static final int WARDENS_EXIT = 45138;
	public static final int RED_UFO = 45750;
	public static final String P2_WARDEN_NAME = "Elidinis' Warden";
	public static final int DEATH_CHEST = 46078;
	public static final int ZEBAK_BLOOD_BARRAGE = 377;
	public static final int AKKHA_PUZZLE_MIRROR_SE = 768;
	public static final int AKKHA_PUZZLE_MIRROR_NW = 1792;
	public static final int AKKHA_PUZZLE_MIRROR_SW = 1280;
	public static final int AKKHA_PUZZLE_MIRROR_NE = 256;
	public static final int SANG_DISTANCE = 9;
	public static final int BLOWPIPE_DISTANCE = 5;
	public static final int ZEBAK_JUG_SPLASH = 2193;
	public static final List<Integer> ZEBAK_RANGED_PROJECTILE_IDS = List.of(2187, 2188);
	public static final List<Integer> ZEBAK_POISON_PROJECTILE = List.of(1555, 2194);
	public static final int ZEBAK_ROCK_PROJECTILE = 2172;
	public static final int ZEBAK_JUG_PROJECTILE = 2173;
	public static final List<Integer> ZEBAK_POISON_GAME_OBJECT = List.of(45570, 45571, 45572, 45573, 45574, 45575, 45576);
	public static final int SANG_ATTACK = 1167;
	public static final int DDS_POKE = 376;
	public static final int DDS_SPEC = 1062;

	public static final int ZEBAK_ROAR_ROCK = 43876;
	public static final int ZEBAK_STATIC_JUG = 11735;
	public static final int ZEBAK_ROLLING_JUG = 11736;


	//WIDGETS
	public static final int BANK_CAMEL = 46223;
	public static final String BREW_MESSAGE = "You drink some of the foul liquid.";
	public static final String BABA_KNOCKBACK = "Ba-Ba screams and knocks you back!";
	public static final String SPIRIT_ARRIVED = "A helpful spirit has arrived with some supplies";

	public static final WorldPoint BREAK_TILE = new WorldPoint(3354, 9119, 0);
	public static final int VARBIT_BABA_PUZZLE = 14362;
	public static final int VARBIT_AKKHA_BOSS = 12401;
	public static final int ADRENALINE = Varbits.LIQUID_ADERNALINE_ACTIVE;
	public static final int VARBIT_CLAIMED_SUPPLIES = 14321;
	public static final int WARDEN_P2_DROPPING_CORE_ANIMATION = 9670;
	public static final int SCABARAS = 11662;
	public static final int MAGE_AKKHA = 11790;
	public static final int RANGE_AKKHA = 11792;
	public static final int MELEE_AKKHA = 11791;
	public static final int FINAL_AKKHA = 11795;
	public static final int SHADOW_AKKHA = 11797;
	public static final int SHADOW_AKKHA_EXPLODE = 9777;

	// This is the Akkha ID when you're not in the room
	public static final int INACTIVE_AKKHA = 11789;
	public static final int BABA_BOSS_MONKEY = 11781;
	public static final int BABA_BOSS_MONKEY_ROCKTHROW_ANIMATION = 9745;
	public static final int BABA_BOSS_MONKEY_DEATH_ANIMATION = 9755;
	public static final int INVENTORY_MIRROR = 27296;
	public static final int MINEABLE_WALL_1 = 45464;
	public static final int MINEABLE_WALL_2 = 45462;
	public static final int[] MINEABLE_WALLS = {MINEABLE_WALL_1, MINEABLE_WALL_2};
	public static final int CRONDIS = 11659;
	public static final int HET = 11687;
	public static final int APMEKEN = 11655;
	public static final int OSMUMTEN = 11689;
	public static final int HELPFUL_SPIRIT = 11694;
	public static final int LEAVE_SPIRIT = 11691;
	public static final int AMASCUT = 11696;
	public static final int VOLATILE_EXPLOSION = 9756;
	public static final int WEAK_BOULDER = 11783;
	public static final int STRONG_BOULDER = 11782;
	public static final int AKKHA_PUZZLE_SEAL = 11707;
	public static final int AKKHA_STRONG_PUZZLE_SEAL = 11706;

	public static final int CRAWLING_IN_TUNNEL_KEPHRI_PUZZLE = 2796;
	public static final int JUMPING_OVER_PLATFORM_KEPHRI_PUZZLE = 741;
	public static final int FANG_ATTACK = 9471;
	public static final int FANG_ATTACK_SPEC = 6118;
	public static final int SHADOW_ATTACK = 9493;
	public static final int AKKHA_MOVEABLE_MIRROR = 45455;
	public static final int AKKHA_STATIC_MIRROR = 45456;
	public static final int AKKHA_PICKAXE_STATUE = 45468;
	public static final int AKKHA_SHIELD_STATUE = 45485;
	public static final int AKKHA_PUZZLE_HARD_WALL = 45458;
	public static final int BABA_PUZZLE_POISON = 45493;
	public static final int BABA_BOSS_ROCKFALL = 42838;
	public static final int WARDEN_OBELISK = 42838;
	public static final int BABA_CRATE_HAMMERS = 45497;
	public static final int BABA_CRATE_POTIONS = 45498;
	public static final int BABA_BANANA = 45755;

	public static final int BABA_SARCOPHAGUS_ATTACK_PROJECTILE_ID = 2246;

	public static final int WHITE_CHEST = 29994;
	public static final int PURPLE_CHEST = 46220;
	public static final int PURPLE_CHEST_OPEN = 44934;
	public static final int PURPLE_CHEST_OPEN_LIGHTBEARER = 44935;
	public static final int WHITE_CHEST_LOOTED = 44787;
	public static final int WHITE_CHEST_LOOTED2 = 44788;
	public static final int WHITE_CHEST_LOOTED3 = 44789;
	public static final int ARCANE_SCARAB_FLY_ANIMATION = 9598;
	public static final int BABA_PUZZLE_STATUE = 45496;
	public static final int KEPHRI_BOSS_ENTRY = 45505;
	public static final int KEPHRI_DUNG_GAME_OBJECT = 45504;
	public static final int WARDENS_P1_BOSS_ENTRY = 45579;

	public static final int AKKHA_BOSS_ENTRY = 45866;
	public static final int ZEBAK_BOSS_ENTRY = 45506;
	public static final int ZEBAK_WAVE = 11738;
	public static final String BLOOD_CLOUD = "Blood cloud";
	public static final int BABA_PUZZLE_EXIT = 45500;
	public static final int BABA_BOSS_ENTRY = 45754;
	public static final int KEPHRI_GAME_OBJECT_LIGHT_ENABLED = 45384;
	public static final int KEPHRI_GAME_OBJECT_LIGHT_DISABLED = 45344;
	public static final int REFERENCE_BUSH_ZEBAK = 45408;
	public static final int BARRIER = 45135;
	public static final int EXIT = 45453;
	public static final int AKKHA_PUZZLE_EXIT = 45131;
	public static final int AKKHA_MINED_WALL = 45466;

	public static final int EXIT_KEPHRI = 45337;
	public static final int KEPHRI_ANCIENT_BUTTON = 45338;
	public static final int KEPHRI_ANCIENT_TABLET = 45339;

	public static final int KEPHRI_MEMORY_LIGHT_ACTIVATED = 45341;
	public static final int BANDOS_GODSWORD_SPEC = 7642;
	//45341
	public static final int BARRIER_ENTER_RAID = 46089;
	public static final int GROUPING_OBELISK = 46068;
	public static final int PASSAGE_KEPHRI = 45343;
	public static final int PLATFORM_KEPHRI = 45396;
	public static final int NE_QUADRANT_GAME_OBJECT = 45871;
	public static final int NW_QUADRANT_GAME_OBJECT = 45868;
	public static final int SE_QUADRANT_GAME_OBJECT = 45869;
	public static final int SW_QUADRANT_GAME_OBJECT = 45870;
	public static final int[] blowpipe = new int[]{ItemID.TOXIC_BLOWPIPE, ItemID.TOXIC_BLOWPIPE_EMPTY};
	public static final int BLOWPIPE_EMPTY = ItemID.TOXIC_BLOWPIPE_EMPTY;
	public static final int BLOWPIPE_CHARGED = ItemID.TOXIC_BLOWPIPE;

	public static final int NE_QUADRANT_GRAPHIC_OBJECT = 2257;
	public static final int NW_QUADRANT_GRAPHIC_OBJECT = 2258;
	public static final int SE_QUADRANT_GRAPHIC_OBJECT = 2256;
	public static final int SW_QUADRANT_GRAPHIC_OBJECT = 2259;
	public static final int BABA_SHOCKWAVE_CENTER = 1448;
	public static final int BABA_ROCKFALL_SHADOW = 2250;

	public static final int ACTIVE_DOOR_KEPHRI = 46155;
	public static final int ACTIVE_DOOR_BABA = 46158;
	public static final int ACTIVE_DOOR_ZEBAK = 46161;
	public static final int ACTIVE_DOOR_AKKHA = 46164;
	public static final int ACTIVE_DOOR_WARDENS = 46168;
	public static final int RAID_EXIT = 45128;
	public static final int BABA_BOSS_EXIT = 45844;

	public static final int INACTIVE_DOOR_KEPHRI = 46157;
	public static final int INACTIVE_DOOR_BABA = 46160;
	public static final int INACTIVE_DOOR_ZEBAK = 46163;
	public static final int INACTIVE_DOOR_AKKHA = 46166;
	public static final int INACTIVE_DOOR_WARDENS = 46167;

	public static final int ZEBAK_GROUND_JUG = 27295;
	public static final int ZEBAK_TREE_GAME_OBJECT = 32740;
	public static final int ZEBAK_PUZZLE_ENTRANCE = 45453;
	public static final int ZEBAK_PUZZLE_EXIT = 45397;
	public static final int ACTIVE_ZEBAK_WATERFALL = 45398;
	public static final int INACTIVE_ZEBAK_WATERFALL = 45399;
	public static final int DUNG_GRAPHIC_START = 2146;
	public static final int KEPHRI_BALL = 1447;
	public static final int KEPHRI_DUNG_G_OBJECT = 2145;
	public static final int KEPHRI_KAMIKAZE = 2147;


	public static final int OBELISK_ID_INACTIVE = 11698;
	public static final int OBELISK_ID_ACTIVE = 11699;
	public static final int FALLING_ROCKS_KEPHRI_PILLAR = 317;

	public static final int GROUND_OBJECT_LIGHT_BACKGROUND = 45344;
	public static final int GAME_OBJECT_LIGHT_ENABLED = 45384;
	public static final int ZEBAK_ENRAGE = 11732;
	public static final int WARDENS_SKULL_PROJECTILE = 2225;
	public static final int WARDENS_P2_ACTIVE_RANGE_MELEE = 11753;
	public static final int WARDENS_P2_ACTIVE_MAGE_MELEE = 11754;
	public static final int WARDENS_P2_DOWNED = 11755;
	public static final int WARDENS_P2_WINDMILL = 2236;
	public static final int WARDENS_P2_BEAM = 2235;
	public static final int WARDENS_P2_PRISON = 2210;
	public static final int OSMUMTEN_START_ANIMATION = 5546;
	public static final int WARDEN_P1_BALL_ONE_PROJECTILE_ID = 2238;
	public static final int WARDEN_P1_BALL_TWO_PROJECTILE_ID = 2237;


	// Amethyst dart, dragon dart and blowpipe spec (1043)
	public static final ArrayList<Integer> DARTS = new ArrayList<>(Arrays.asList(1043, 1936, 1122));
	public static final ArrayList<Prayer> OVERHEAD_PRAYERS = new ArrayList<>(Arrays.asList(
		Prayer.PROTECT_FROM_MAGIC,
		Prayer.PROTECT_FROM_MELEE,
		Prayer.PROTECT_FROM_MISSILES));
	public static final ArrayList<Integer> AHRIMS = new ArrayList<>(Arrays.asList(
		ItemID.AHRIMS_ROBETOP,
		ItemID.AHRIMS_ROBETOP_75,
		ItemID.AHRIMS_ROBETOP_50,
		ItemID.AHRIMS_ROBETOP_25,
		ItemID.AHRIMS_ROBESKIRT,
		ItemID.AHRIMS_ROBESKIRT_75,
		ItemID.AHRIMS_ROBESKIRT_50,
		ItemID.AHRIMS_ROBESKIRT_25));


	public static final ArrayList<Prayer> OFFENSIVE_PRAYERS = new ArrayList<>(Arrays.asList(
		Prayer.PIETY,
		Prayer.AUGURY,
		Prayer.RIGOUR));


	//WARDEN P3
	public static final int LIGHTNING_GRAPHICS_OBJECT_ID = 2197;
	public static final int NEW_LIGHTNING_GRAPHICS_OBJECT_ID = 1446;

	public static final int BABA_PHANTOM_ID = 11775;
	public static final int ZEBAK_PHANTOM_ID = 11774;
	public static final ArrayList<Integer> WARDENS_P3_UNWALKABLE_TILES = new ArrayList<>(
		Arrays.asList(45736, 45738, 45734, 45728, 45726));

	public static final int WARDENS_P3_SKULLS_INACTIVE_ID = 11762;
	public static final int WARDENS_P3_ENRAGED_ANIMATION_ID = 9685;
	public static final int WARDENS_P3_EAST_TILE_FLIP_ANIMATION_ID = 9675;
	public static final int WARDENS_P3_WEST_TILE_FLIP_ANIMATION_ID = 9677;
	public static final int WARDENS_P3_MIDDLE_TILE_FLIP_ANIMATION_ID = 9679;
	/**
	 * We need to move the insanity tile position 1 forward in the rotation when this happens
	 * East -> West
	 * West -> Middle
	 * Middle -> East
	 */
	public static final int WARDENS_P3_SKULL_SPAWNING_ANIMATION = 9682;
	/**
	 * When baba does this animation, it begins to drop a boulder on location.
	 * location = player location on the last game tick that occurred.
	 * It takes 5 ticks to hit the ground (i think, maybe 6)
	 */
	public static final int BABA_PHANTOM_ROCKTHROW_ANIMATION_ID = 9743;
	public static final int WARDEN_P2_SKULL_PROJECTILE_ID = 2225;
	public static final List<Integer> WARDEN_P2_IDS = List.of(
		WARDENS_P2_ACTIVE_RANGE_MELEE,
		WARDENS_P2_ACTIVE_MAGE_MELEE,
		WARDENS_P2_DOWNED);
	public static final List<Integer> ZEBAK_PHANTOM_RANGED_PROJECTILE_IDS = List.of(2176, 2181, 2177);
	public static final List<Integer> ZEBAK_PHANTOM_MAGIC_PROJECTILE_IDS = List.of(2178, 2187, 2179);

	public static List<Integer> WEAPONS =
		List.of(
			RangeWeapon.BLOWPIPE.itemId,
			RangeWeapon.TWISTED_BOW.itemId,
			MeleeWeapon.FANG.itemId,
			MeleeWeapon.FANG_KIT.itemId,
			MageWeapon.SANG.itemId,
			MageWeapon.SWAMP.itemId,
			MageWeapon.SHADOW.itemId);


	/////////////////////////////////////////////////////////////////
	//Method for finding safe tiles to stand around warden skull AOE
//	if(plugin.wardenP2Aoe.size() > 0){
//	for(Projectile p : plugin.wardenP2Aoe){
//		if(p.getRemainingCycles() < 1){
//			plugin.wardenP2Aoe.remove(p);
//		} else {
//			int ticksRemaining = p.getRemainingCycles() / 30;
//			for(LocalPoint lp : generateLocalPoints(p)){
//				drawTile(graphics, lp, Color.CYAN, "", stroke);
//			}
//			drawTile(graphics, p.getTarget(), Color.GREEN, String.valueOf(ticksRemaining), stroke);
//		}
//	}
//

	/////////////////////////////////////////////////////////////////////
	//Method for finding current safe and next safe area for insanity warden
//	switch (plugin.tileflip) {
//	case "East":
//		wp = new WorldPoint(wardenLoc.getX() + 1, wardenLoc.getY() + 5, client.getPlane());
//		wp2 = new WorldPoint(wardenLoc.getX() + 3, wardenLoc.getY() + 5, client.getPlane());
//		//label = "L";
//		break;
//	case "West":
//		wp = new WorldPoint(wardenLoc.getX() + 3, wardenLoc.getY() + 5, client.getPlane());
//		wp2 = new WorldPoint(wardenLoc.getX() + 2, wardenLoc.getY() + 5, client.getPlane());
//		//label = "R";
//		break;
//	case "Middle":
//		wp = new WorldPoint(wardenLoc.getX() + 2, wardenLoc.getY() + 5, client.getPlane());
//		wp2 = new WorldPoint(wardenLoc.getX() + 1, wardenLoc.getY() + 5, client.getPlane());
//
//		//label = "M";
//		break;
//}
//                if(wp != null){
//	drawTileAkkha(graphics, wp, Color.GREEN, label, stroke, Color.BLACK, 255, 20);
//}
//                if(wp2 != null){
//	drawTileAkkha(graphics, wp2, Color.RED, label, stroke, Color.BLACK, 255, 20);
//}


}
