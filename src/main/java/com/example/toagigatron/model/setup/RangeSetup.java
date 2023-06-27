package com.example.toagigatron.model.setup;

import com.example.toagigatron.ToaGigatronPlugin;
import com.example.toagigatron.manager.ToaManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.ItemID;

public class RangeSetup
{
	@Inject
	ToaGigatronPlugin plugin;

	public RangeSetup()
	{
	}

	public int helm = 0;
	public int body = 0;

	public int legs = 0;

	public int boots = 0;

	public int weapon = 0;

	public int cape = 0;

	public int gloves = 0;

	public int amulet = 0;

	public int ring = 0;
	public int arrows = 0;
	public final int blowpipe = ItemID.TOXIC_BLOWPIPE;

	public ArrayList<Integer> allGear = new ArrayList<>(
		Arrays.asList(
			weapon,
			helm,
			body,
			legs,
			boots,
			cape,
			gloves,
			arrows,
			ring,
			amulet));


	public boolean hasRangeSetup()
	{
		return ToaManager.isMissingAnyItems(allGear);
	}

	public boolean hasBlowpipe()
	{
		return ToaManager.isMissingAnyItems(new ArrayList<>(List.of(blowpipe)));
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

	public ArrayList<Integer> getAllItemsBp()
	{
		ArrayList<Integer> returnList = new ArrayList<>();
		if (!allGear.contains(blowpipe))
		{
			returnList.add(blowpipe);
		}
		for (int i : allGear)
		{
			if (i != 0 && i != -1)
			{
				returnList.add(i);
			}
		}
		return returnList;
	}

	public void setVariables()
	{
		helm = plugin.config.rangeHelm().itemId;
		body = plugin.config.rangeBody().itemId;
		legs = plugin.config.rangeLegs().itemId;
		boots = plugin.config.rangeBoots().itemId;
		weapon = plugin.config.rangeWeapon().itemId;
		arrows = plugin.config.rangeArrows().itemId;
		cape = plugin.config.rangeCape().itemId;
		gloves = plugin.config.rangeGloves().itemId;
		amulet = plugin.config.rangeAmulet().itemId;
		ring = plugin.config.rangeRing().itemId;
		allGear = new ArrayList<>(
			Arrays.asList(
				weapon,
				helm,
				body,
				legs,
				boots,
				cape,
				gloves,
				arrows,
				ring,
				amulet));
	}
}
