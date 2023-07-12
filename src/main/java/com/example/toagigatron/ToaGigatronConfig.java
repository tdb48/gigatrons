package com.example.toagigatron;

import com.example.toagigatron.model.setup.mage.MageAmulet;
import com.example.toagigatron.model.setup.mage.MageArrows;
import com.example.toagigatron.model.setup.mage.MageBody;
import com.example.toagigatron.model.setup.mage.MageBoots;
import com.example.toagigatron.model.setup.mage.MageCape;
import com.example.toagigatron.model.setup.mage.MageGloves;
import com.example.toagigatron.model.setup.mage.MageHelm;
import com.example.toagigatron.model.setup.mage.MageLegs;
import com.example.toagigatron.model.setup.mage.MageOffhand;
import com.example.toagigatron.model.setup.mage.MageRing;
import com.example.toagigatron.model.setup.mage.MageWeapon;
import com.example.toagigatron.model.setup.melee.MeleeAmulet;
import com.example.toagigatron.model.setup.melee.MeleeArrows;
import com.example.toagigatron.model.setup.melee.MeleeBody;
import com.example.toagigatron.model.setup.melee.MeleeBoots;
import com.example.toagigatron.model.setup.melee.MeleeCape;
import com.example.toagigatron.model.setup.melee.MeleeGloves;
import com.example.toagigatron.model.setup.melee.MeleeHelm;
import com.example.toagigatron.model.setup.melee.MeleeLegs;
import com.example.toagigatron.model.setup.melee.MeleeOffhand;
import com.example.toagigatron.model.setup.melee.MeleeRing;
import com.example.toagigatron.model.setup.melee.MeleeWeapon;
import com.example.toagigatron.model.setup.range.RangeAmulet;
import com.example.toagigatron.model.setup.range.RangeArrows;
import com.example.toagigatron.model.setup.range.RangeBody;
import com.example.toagigatron.model.setup.range.RangeBoots;
import com.example.toagigatron.model.setup.range.RangeCape;
import com.example.toagigatron.model.setup.range.RangeGloves;
import com.example.toagigatron.model.setup.range.RangeHelm;
import com.example.toagigatron.model.setup.range.RangeLegs;
import com.example.toagigatron.model.setup.range.RangeRing;
import com.example.toagigatron.model.setup.range.RangeWeapon;
import java.awt.Button;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("ToaGigatronConfig")
public interface ToaGigatronConfig extends Config
{

	@ConfigSection(
		name = "General",
		description = "General",
		position = 0
	)
	String GENERAL_SETTINGS = "General";
	@ConfigSection(
		name = "Range setup",
		description = "Settings for setup",
		position = 10,
		closedByDefault = true
	)
	String RANGE_SETUP = "Range setup";
	@ConfigSection(
		name = "Melee setup",
		description = "Settings for setup",
		position = 20,
		closedByDefault = true
	)
	String MELEE_SETUP = "Melee setup";
	@ConfigSection(
		name = "Mage setup",
		description = "Settings for setup",
		position = 30,
		closedByDefault = true
	)
	String MAGE_SETUP = "Mage setup";

	@ConfigItem(
		position = 10,
		keyName = "rangeHelm",
		name = "Helm",
		description = "",
		section = "Range setup"
	)
	default RangeHelm rangeHelm()
	{
		return RangeHelm.NONE;
	}

	@ConfigItem(
		position = 11,
		keyName = "rangeBody",
		name = "Body",
		description = "",
		section = "Range setup"
	)
	default RangeBody rangeBody()
	{
		return RangeBody.ARMADYL;
	}

	@ConfigItem(
		position = 12,
		keyName = "rangeLegs",
		name = "Legs",
		description = "",
		section = "Range setup"
	)
	default RangeLegs rangeLegs()
	{
		return RangeLegs.ARMADYL;
	}

	@ConfigItem(
		position = 13,
		keyName = "rangeBoots",
		name = "Boots",
		description = "",
		section = "Range setup"
	)
	default RangeBoots rangeBoots()
	{
		return RangeBoots.PRIMS;
	}

	@ConfigItem(
		position = 14,
		keyName = "rangeAmulet",
		name = "Amulet",
		description = "",
		section = "Range setup"
	)
	default RangeAmulet rangeAmulet()
	{
		return RangeAmulet.ANGUISH;
	}

	@ConfigItem(
		position = 15,
		keyName = "rangeArrows",
		name = "Arrows",
		description = "",
		section = "Range setup"
	)
	default RangeArrows rangeArrows()
	{
		return RangeArrows.NONE;
	}

	@ConfigItem(
		position = 10,
		keyName = "rangeCape",
		name = "Cape",
		description = "",
		section = "Range setup"
	)
	default RangeCape rangeCape()
	{
		return RangeCape.ASSEMBLER;
	}

	@ConfigItem(
		position = 17,
		keyName = "rangeRing",
		name = "Ring",
		description = "",
		section = "Range setup"
	)
	default RangeRing rangeRing()
	{
		return RangeRing.BRIMSTONE;
	}

	@ConfigItem(
		position = 18,
		keyName = "rangeWeapon",
		name = "Weapon",
		description = "",
		section = "Range setup"
	)
	default RangeWeapon rangeWeapon()
	{
		return RangeWeapon.BLOWPIPE;
	}

	@ConfigItem(
		position = 19,
		keyName = "rangeGloves",
		name = "Gloves",
		description = "",
		section = "Range setup"
	)
	default RangeGloves rangeGloves()
	{
		return RangeGloves.BARROWS;
	}

	@ConfigItem(
		position = 20,
		keyName = "mageArrows",
		name = "Arrows",
		description = "",
		section = "Mage setup"
	)
	default MageArrows mageArrows()
	{
		return MageArrows.NONE;
	}

	@ConfigItem(
		position = 20,
		keyName = "mageHelm",
		name = "Helm",
		description = "",
		section = "Mage setup"
	)
	default MageHelm mageHelm()
	{
		return MageHelm.NONE;
	}

	@ConfigItem(
		position = 21,
		keyName = "mageBody",
		name = "Body",
		description = "",
		section = "Mage setup"
	)
	default MageBody mageBody()
	{
		return MageBody.ANCESTRAL;
	}

	@ConfigItem(
		position = 22,
		keyName = "mageLegs",
		name = "Legs",
		description = "",
		section = "Mage setup"
	)
	default MageLegs mageLegs()
	{
		return MageLegs.ANCESTRAL;
	}

	@ConfigItem(
		position = 23,
		keyName = "mageBoots",
		name = "Boots",
		description = "",
		section = "Mage setup"
	)
	default MageBoots mageBoots()
	{
		return MageBoots.PRIMS;
	}

	@ConfigItem(
		position = 24,
		keyName = "mageAmulet",
		name = "Amulet",
		description = "",
		section = "Mage setup"
	)
	default MageAmulet mageAmulet()
	{
		return MageAmulet.OCCULT;
	}

	@ConfigItem(
		position = 25,
		keyName = "mageCape",
		name = "Cape",
		description = "",
		section = "Mage setup"
	)
	default MageCape mageCape()
	{
		return MageCape.SARA;
	}

	@ConfigItem(
		position = 26,
		keyName = "mageRing",
		name = "Ring",
		description = "",
		section = "Mage setup"
	)
	default MageRing mageRing()
	{
		return MageRing.BRIMSTONE;
	}

	@ConfigItem(
		position = 27,
		keyName = "mageWeapon",
		name = "Weapon",
		description = "",
		section = "Mage setup"
	)
	default MageWeapon mageWeapon()
	{
		return MageWeapon.SANG;
	}

	@ConfigItem(
		position = 28,
		keyName = "mageGloves",
		name = "Gloves",
		description = "",
		section = "Mage setup"
	)
	default MageGloves mageGloves()
	{
		return MageGloves.TORM;
	}

	@ConfigItem(
		position = 29,
		keyName = "mageOffhand",
		name = "Offhand",
		description = "",
		section = "Mage setup"
	)
	default MageOffhand mageOffhand()
	{
		return MageOffhand.NONE;
	}

	@ConfigItem(
		position = 30,
		keyName = "meleeArrows",
		name = "Arrows",
		description = "",
		section = "Melee setup"
	)
	default MeleeArrows meleeArrows()
	{
		return MeleeArrows.NONE;
	}

	@ConfigItem(
		position = 30,
		keyName = "meleeHelm",
		name = "Helm",
		description = "",
		section = "Melee setup"
	)
	default MeleeHelm meleeHelm()
	{
		return MeleeHelm.FACEGUARD;
	}

	@ConfigItem(
		position = 31,
		keyName = "meleeBody",
		name = "Body",
		description = "",
		section = "Melee setup"
	)
	default MeleeBody meleeBody()
	{
		return MeleeBody.BANDOS;
	}

	@ConfigItem(
		position = 32,
		keyName = "meleeLegs",
		name = "Legs",
		description = "",
		section = "Melee setup"
	)
	default MeleeLegs meleeLegs()
	{
		return MeleeLegs.BANDOS;
	}

	@ConfigItem(
		position = 33,
		keyName = "meleeBoots",
		name = "Boots",
		description = "",
		section = "Melee setup"
	)
	default MeleeBoots meleeBoots()
	{
		return MeleeBoots.PRIMS;
	}

	@ConfigItem(
		position = 34,
		keyName = "meleeAmulet",
		name = "Amulet",
		description = "",
		section = "Melee setup"
	)
	default MeleeAmulet meleeAmulet()
	{
		return MeleeAmulet.TORTURE;
	}

	@ConfigItem(
		position = 30,
		keyName = "meleeCape",
		name = "Cape",
		description = "",
		section = "Melee setup"
	)
	default MeleeCape meleeCape()
	{
		return MeleeCape.INFERNAL_CAPE;
	}

	@ConfigItem(
		position = 36,
		keyName = "meleeRing",
		name = "Ring",
		description = "",
		section = "Melee setup"
	)
	default MeleeRing meleeRing()
	{
		return MeleeRing.BRIMSTONE;
	}

	@ConfigItem(
		position = 37,
		keyName = "meleeWeapon",
		name = "Weapon",
		description = "",
		section = "Melee setup"
	)
	default MeleeWeapon meleeWeapon()
	{
		return MeleeWeapon.FANG;
	}

	@ConfigItem(
		position = 38,
		keyName = "meleeGloves",
		name = "Gloves",
		description = "",
		section = "Melee setup"
	)
	default MeleeGloves meleeGloves()
	{
		return MeleeGloves.BARROWS;
	}

	@ConfigItem(
		position = 30,
		keyName = "meleeOffhand",
		name = "Offhand",
		description = "",
		section = "Melee setup"
	)
	default MeleeOffhand meleeOffhand()
	{
		return MeleeOffhand.AVERNIC;
	}


//	@ConfigItem(
//		position = 1,
//		keyName = "useThralls",
//		name = "Use Thralls",
//		description = "Whether to use thralls or not",
//		section = "General"
//	)
//	default boolean useThralls()
//	{
//		return false;
//	}


	@ConfigItem(
		position = 1,
		keyName = "prayFlick",
		name = "Prayer flick",
		description = "Whether to 1 tick flick or not",
		section = "General"
	)
	default boolean prayFlick()
	{
		return false;
	}

	@ConfigItem(
		position = 2,
		keyName = "brewCount",
		name = "Brews #",
		description = "",
		section = "General"
	)
	default int brewCount()
	{
		return 5;
	}

	@ConfigItem(
		position = 9999,
		keyName = "debug",
		name = "Debug",
		description = "Posts debug message to chat if having problems"
	)
	default boolean debug()
	{
		return true;
	}

	@ConfigItem(
		position = 900,
		keyName = "showInfobox",
		name = "Show Infobox",
		description = "Show infobox"
	)
	default boolean showInfobox()
	{
		return true;
	}

	@ConfigItem(
		position = 901,
		keyName = "showOverlay",
		name = "Show Overlay",
		description = "Show overlay"
	)
	default boolean showOverlay()
	{
		return true;
	}

	@ConfigItem(
		position = 900,
		keyName = "showConsumablesInfobox",
		name = "Consumables Infobox",
		description = "Show consumables overlay"
	)
	default boolean showConsumablesInfobox()
	{
		return true;
	}
}
