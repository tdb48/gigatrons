package com.example.nexatron.model.setup;

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
	@Inject
	NexManager nexManager;

	@Inject
	NexatronConfig config;

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
//				ItemID.AVAS_ASSEMBLER,
				config.rangeCape().itemId,
				config.helm().itemId,
//				ItemID.HELM_OF_NEITIZNOT,
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
//				ItemID.INFERNAL_CAPE,
				config.meleeCape().itemId,
//				ItemID.HELM_OF_NEITIZNOT,
				config.helm().itemId,
				ItemID.AMULET_OF_BLOOD_FURY,
//				ItemID.DRAGON_DEFENDER,
				config.meleeOffhand().itemId,
				ItemID.LIGHTBEARER,
				ItemID.BARROWS_GLOVES,
				ItemID.PRIMORDIAL_BOOTS));
	}
}
