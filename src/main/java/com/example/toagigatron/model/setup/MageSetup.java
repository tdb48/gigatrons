package com.example.toagigatron.model.setup;

import com.example.toagigatron.ToaGigatronPlugin;
import com.example.toagigatron.manager.ToaManager;
import java.util.ArrayList;
import java.util.Arrays;
import javax.inject.Inject;

public class MageSetup
{

	@Inject
	ToaGigatronPlugin plugin;
	public MageSetup()
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

	public ArrayList<Integer> tankGear = new ArrayList<>(
		Arrays.asList(
			0));

	public boolean hasMageSetup()
	{
		return ToaManager.isMissingAnyItems(allGear);
	}

	public void setVariables()
	{
		arrows = plugin.config.mageArrows().itemId;
		helm = plugin.config.mageHelm().itemId;
		body = plugin.config.mageBody().itemId;
		legs = plugin.config.mageLegs().itemId;
		boots = plugin.config.mageBoots().itemId;
		weapon = plugin.config.mageWeapon().itemId;
		offhand = plugin.config.mageOffhand().itemId;
		cape = plugin.config.mageCape().itemId;
		gloves = plugin.config.mageGloves().itemId;
		amulet = plugin.config.mageAmulet().itemId;
		ring = plugin.config.mageRing().itemId;
		allGear = new ArrayList<>(
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
		tankGear = new ArrayList<>(
			Arrays.asList(
				weapon,
				helm,
				plugin.config.meleeBody().itemId,
				plugin.config.meleeLegs().itemId,
				boots,
				cape,
				gloves,
				offhand,
				ring,
				amulet,
				arrows));
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

	public ArrayList<Integer> getAllItemsTankGear()
	{
		ArrayList<Integer> returnList = new ArrayList<>();
		for (int i : tankGear)
		{
			if (i != 0 && i != -1)
			{
				returnList.add(i);
			}
		}
		return returnList;
	}
}
	

