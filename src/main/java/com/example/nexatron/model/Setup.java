package com.example.nexatron.model;

import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import java.util.ArrayList;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.ItemID;

public class Setup
{
	@Inject
	NexManager nexManager;

	public int getBolts()
	{
		if (nexManager.getBossHp() <= 200)
		{
			return NexConst.DIA_BOLTS;
		}
		return NexConst.RUBY_BOLTS;
	}

	public ArrayList<Integer> rangeNex()
	{
		return new ArrayList<>(
			Arrays.asList(
				ItemID.ZARYTE_CROSSBOW,
				ItemID.MASORI_BODY_F,
				ItemID.MASORI_CHAPS_F,
				ItemID.AVAS_ASSEMBLER,
				ItemID.NEITIZNOT_FACEGUARD,
				ItemID.NECKLACE_OF_ANGUISH,
				ItemID.TWISTED_BUCKLER,
				ItemID.LIGHTBEARER,
				ItemID.BARROWS_GLOVES,
				getBolts()));
	}

	public ArrayList<Integer> meleeNex()
	{
		return new ArrayList<>(
			Arrays.asList(
				ItemID.OSMUMTENS_FANG,
				ItemID.BANDOS_CHESTPLATE,
				ItemID.MASORI_CHAPS_F,
				ItemID.INFERNAL_CAPE,
				ItemID.NEITIZNOT_FACEGUARD,
				ItemID.AMULET_OF_BLOOD_FURY,
				ItemID.DRAGON_DEFENDER,
				ItemID.LIGHTBEARER,
				ItemID.BARROWS_GLOVES,
				ItemID.PRIMORDIAL_BOOTS));
	}

	public ArrayList<Integer> rangeKC()
	{
		return new ArrayList<>(
			Arrays.asList(
				ItemID.TOXIC_BLOWPIPE,
				ItemID.ARMADYL_CHESTPLATE,
				ItemID.ARMADYL_CHAINSKIRT,
				ItemID.AVAS_ASSEMBLER,
				ItemID.NEITIZNOT_FACEGUARD,
				ItemID.NECKLACE_OF_ANGUISH,
				ItemID.LIGHTBEARER,
				ItemID.BARROWS_GLOVES));
	}
}
