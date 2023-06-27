package com.example.toagigatron.model.setup;

import com.example.toagigatron.ToaGigatronPlugin;
import com.example.toagigatron.manager.ToaManager;
import java.util.ArrayList;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.ItemID;

public class MeleeSetup
{
	@Inject
	ToaGigatronPlugin plugin;

	public MeleeSetup()
	{
	}

	public int arrows = 0;

	public int helm = 0;
	public int body = 0;

	public int legs = 0;

	public int boots = 0;

	public int weapon = 0;

	public int offhand = 0;

	public int cape = 0;

	public int gloves = 0;

	public int amulet = 0;

	public int ring = 0;
	public final int dds = ItemID.DRAGON_DAGGER;

	public final int bgs = ItemID.BANDOS_GODSWORD;
	public final int fourTickWeapon = ItemID.ABYSSAL_TENTACLE;

	public ArrayList<Integer> allGearFourTick = new ArrayList<>(
		Arrays.asList(
			fourTickWeapon,
			helm,
			body,
			legs,
			boots,
			cape,
			gloves,
			offhand,
			ring,
			amulet,
			arrows));

	public ArrayList<Integer> allGear = new ArrayList<>(
		Arrays.asList(
			weapon,
			helm,
			body,
			legs,
			boots,
			cape,
			gloves,
			offhand,
			ring,
			amulet,
			arrows));

	public ArrayList<Integer> allGearBgs = new ArrayList<>(Arrays.asList(
		bgs,
		helm,
		body,
		legs,
		boots,
		cape,
		gloves,
		ring,
		amulet,
		arrows));

	public ArrayList<Integer> allGearDds = new ArrayList<>(Arrays.asList(
		dds,
		helm,
		body,
		legs,
		boots,
		offhand,
		cape,
		gloves,
		ring,
		amulet,
		arrows));

	public ArrayList<Integer> meleeWeaponOffhand = new ArrayList<>(Arrays.asList(
		weapon,
		offhand));

	public void setVariables()
	{
		arrows = plugin.config.meleeArrows().itemId;
		helm = plugin.config.meleeHelm().itemId;
		body = plugin.config.meleeBody().itemId;
		legs = plugin.config.meleeLegs().itemId;
		boots = plugin.config.meleeBoots().itemId;
		weapon = plugin.config.meleeWeapon().itemId;
		offhand = plugin.config.meleeOffhand().itemId;
		cape = plugin.config.meleeCape().itemId;
		gloves = plugin.config.meleeGloves().itemId;
		amulet = plugin.config.meleeAmulet().itemId;
		ring = plugin.config.meleeRing().itemId;
		allGear = new ArrayList<>(Arrays.asList(
			weapon,
			helm,
			body,
			legs,
			boots,
			cape,
			gloves,
			offhand,
			ring,
			amulet,
			arrows));

		allGearBgs = new ArrayList<>(Arrays.asList(
			bgs,
			helm,
			body,
			legs,
			boots,
			cape,
			gloves,
			ring,
			amulet,
			arrows));
		allGearDds = new ArrayList<>(Arrays.asList(
			dds,
			helm,
			body,
			legs,
			boots,
			offhand,
			cape,
			gloves,
			ring,
			amulet,
			arrows));
		allGearFourTick = new ArrayList<>(
			Arrays.asList(
				fourTickWeapon,
				helm,
				body,
				legs,
				boots,
				cape,
				gloves,
				offhand,
				ring,
				amulet,
				arrows));
	}

	public boolean hasMeleeSetup()
	{
		return ToaManager.isMissingAnyItems(allGear);
	}

	public ArrayList<Integer> getAllItems()
	{
		ArrayList<Integer> returnList = new ArrayList<>();
		for (int i : allGear)
		{
			if (i != 0 && i != -1)
			{
				returnList.add(i);
			}
		}
		return returnList;
	}

	public ArrayList<Integer> getAllItemsBgs()
	{
		ArrayList<Integer> returnList = new ArrayList<>();
		for (int i : allGearBgs)
		{
			if (i != 0 && i != -1)
			{
				returnList.add(i);
			}
		}
		return returnList;
	}

	public ArrayList<Integer> getAllItemsDds()
	{
		ArrayList<Integer> returnList = new ArrayList<>();
		for (int i : allGearDds)
		{
			if (i != 0 && i != -1)
			{
				returnList.add(i);
			}
		}
		return returnList;
	}

	public boolean hasBgs()
	{
		return ToaManager.isMissingAnyItems(allGearBgs);
	}

	public boolean hasDds()
	{
		return ToaManager.isMissingAnyItems(allGearDds);
	}

}
