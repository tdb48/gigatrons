package com.example.nexatron.model.setup;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.nexatron.NexatronConfig;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.Stage;
import java.util.ArrayList;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;

public class Setup
{
	public static final ArrayList<Integer> RANGE_CAPE =
		new ArrayList<>(Arrays.asList(
			ItemID.MASORI_ASSEMBLER,
			ItemID.AVAS_ASSEMBLER,
			ItemID.AVAS_ACCUMULATOR
		));
	public static final ArrayList<Integer> MELEE_CAPE =
		new ArrayList<>(Arrays.asList(
			ItemID.INFERNAL_CAPE,
			ItemID.INFERNAL_CAPE_L,
			ItemID.FIRE_CAPE
		));
	public static final ArrayList<Integer> MELEE_OFFHAND =
		new ArrayList<>(Arrays.asList(
			ItemID.AVERNIC_DEFENDER,
			ItemID.DRAGON_DEFENDER
		));
	public static final ArrayList<Integer> HELM =
		new ArrayList<>(Arrays.asList(
			ItemID.NEITIZNOT_FACEGUARD,
			ItemID.HELM_OF_NEITIZNOT,
			ItemID.BERSERKER_HELM
		));
	@Inject
	NexManager nexManager;
	@Inject
	NexatronConfig config;

	public void reset()
	{
//		rangeCape = -1;
//		meleeCape = -1;
//		helm = -1;
//		meleeOffhand = -1;
	}

	public int findBestSlot(ArrayList<Integer> potentialItems)
	{
		for (int i : potentialItems)
		{
			if (Bank.search().withId(i).first().orElse(null) != null)
			{
				return i;
			}
			if (Inventory.search().withId(i).first().orElse(null) != null)
			{
				return i;
			}
			if (Equipment.search().withId(i).first().orElse(null) != null)
			{
				return i;
			}
		}
		return -2;
	}


	public int getBolts()
	{
		NPC activeMinion = nexManager.nex.getActiveMinion();
		if (activeMinion != null
			&& activeMinion.getHealthRatio() != -1
			&& nexManager.nex.getNPCHP(activeMinion) <= 40)
		{
			return NexConst.DIA_BOLTS;
		}
		if (nexManager.getStage().equals(Stage.NEX_SHADOW) && nexManager.nex.hpUntilProc() <= 20)
		{
			return NexConst.DIA_BOLTS;
		}
		if (nexManager.getBossHp() <= 220)
		{
			return NexConst.DIA_BOLTS;
		}
		return NexConst.RUBY_BOLTS;
	}

	public ArrayList<Integer> rangeKc()
	{
		int cape = config.rangeCape().itemId;
		int rangeHelm = ItemID.ANCIENT_COIF;
		if (config.autoDecide())
		{
			cape = nexManager.nex.rangeCape;
		}
		if (!nexManager.kcArea.canKillMage())
		{
			rangeHelm = ItemID.SERPENTINE_HELM;
		}
		return new ArrayList<>(
			Arrays.asList(
				ItemID.TOXIC_BLOWPIPE,
				ItemID.ANCIENT_DHIDE_BODY,
				ItemID.ANCIENT_CHAPS,
				cape,
				rangeHelm,
				ItemID.ANCIENT_DHIDE_BOOTS,
				ItemID.NECKLACE_OF_ANGUISH,
				ItemID.LIGHTBEARER,
				ItemID.ANCIENT_BLESSING,
				ItemID.ANCIENT_BRACERS));
	}

	public ArrayList<Integer> rangeNex()
	{
		int cape = config.rangeCape().itemId;
		int rangeHelm = config.helm().itemId;
		if (config.autoDecide())
		{
			cape = nexManager.nex.rangeCape;
			rangeHelm = nexManager.nex.helm;
		}
		return new ArrayList<>(
			Arrays.asList(
				ItemID.ZARYTE_CROSSBOW,
				ItemID.MASORI_BODY_F,
				ItemID.MASORI_CHAPS_F,
				cape,
				rangeHelm,
				ItemID.NECKLACE_OF_ANGUISH,
				ItemID.TWISTED_BUCKLER,
				ItemID.LIGHTBEARER,
				ItemID.BARROWS_GLOVES,
				ItemID.PRIMORDIAL_BOOTS,
				getBolts()));
	}

	public ArrayList<Integer> meleeNex()
	{
		int cape = config.meleeCape().itemId;
		int helm = config.helm().itemId;
		int offhand = config.meleeOffhand().itemId;
		if (config.autoDecide())
		{
			cape = nexManager.nex.meleeCape;
			helm = nexManager.nex.helm;
			offhand = nexManager.nex.meleeOffhand;
		}
		return new ArrayList<>(
			Arrays.asList(
				ItemID.OSMUMTENS_FANG,
				ItemID.MASORI_BODY_F,
				ItemID.MASORI_CHAPS_F,
				cape,
				helm,
				ItemID.AMULET_OF_BLOOD_FURY,
				offhand,
				ItemID.LIGHTBEARER,
				ItemID.BARROWS_GLOVES,
				ItemID.PRIMORDIAL_BOOTS,
				getBolts()));
	}

	public ArrayList<Integer> defensiveNex()
	{
		int cape = config.meleeCape().itemId;
		int helm = config.helm().itemId;
		if (config.autoDecide())
		{
			cape = nexManager.nex.meleeCape;
			helm = nexManager.nex.helm;
		}
		return new ArrayList<>(
			Arrays.asList(
				ItemID.ZARYTE_CROSSBOW,
				ItemID.MASORI_BODY_F,
				ItemID.MASORI_CHAPS_F,
				cape,
				helm,
				ItemID.AMULET_OF_BLOOD_FURY,
				ItemID.TWISTED_BUCKLER,
				ItemID.LIGHTBEARER,
				ItemID.BARROWS_GLOVES));
	}
}
