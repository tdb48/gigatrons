package com.example.toagigatron.model.setup;

import com.example.toagigatron.ToaGigatronPlugin;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.setup.mage.MageBody;
import com.example.toagigatron.model.setup.mage.MageLegs;
import java.util.ArrayList;
import java.util.List;
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

	public ArrayList<Integer> allGear()
	{
		ArrayList<Integer> returnList = new ArrayList<>();
		if (plugin.config.mageBody() == MageBody.AHRIMS)
		{
			returnList.add(plugin.toaManager.chargesTracker.ahrimsTop);
		}
		else
		{
			returnList.add(body);
		}
		if (plugin.config.mageLegs() == MageLegs.AHRIMS)
		{
			returnList.add(plugin.toaManager.chargesTracker.ahrimsSkirt);
		}
		else
		{
			returnList.add(legs);
		}
		returnList.add(weapon);
		returnList.add(helm);
		returnList.add(boots);
		returnList.add(cape);
		returnList.add(gloves);
		returnList.add(offhand);
		returnList.add(ring);
		returnList.add(amulet);
		returnList.add(arrows);
		return returnList;
	}

	public ArrayList<Integer> tankGear()
	{
		return new ArrayList<>(
			List.of(
				0));
	}

	public boolean hasMageSetup()
	{
		return ToaManager.isMissingAnyItems(allGear());
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
	}

	public ArrayList<Integer> getAllItems()
	{
		ArrayList<Integer> returnList = new ArrayList<>();
		for (int i : allGear())
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
		for (int i : tankGear())
		{
			if (i != 0 && i != -1)
			{
				returnList.add(i);
			}
		}
		return returnList;
	}
}
	

