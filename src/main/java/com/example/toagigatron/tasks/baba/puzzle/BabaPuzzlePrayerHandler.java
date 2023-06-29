package com.example.toagigatron.tasks.baba.puzzle;

import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.StagedTask;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.WeaponMap;
import com.example.toagigatron.taskformat.TaskDescriptor;
import net.runelite.api.*;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.widgets.Prayers;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;

import javax.inject.Inject;
import java.util.List;

@TaskDescriptor(
		name = "Baba prayers"
)
public class BabaPuzzlePrayerHandler extends StagedTask
{
	@Inject
	public BabaPuzzlePrayerHandler(ToaManager toaManager)
	{
		super(toaManager, Stage.BABA_PUZZLE);
	}

	private int interactingCount(List<NPC> npcs, List<NPC> thrallNpcs, boolean meleeCheck)
	{
		double count = 0;
		for (NPC n : npcs)
		{
			if (n.getInteracting() != null && n.getInteracting().equals(client.getLocalPlayer()))
			{
				if (meleeCheck)
				{
					if (n.distanceTo(client.getLocalPlayer().getWorldLocation()) <= 1)
					{
						count++;
					}
				}
				else
				{
					if (n.distanceTo(client.getLocalPlayer().getWorldLocation()) <= 5)
					{
						count++;
					}
				}
			}
		}
		if(thrallNpcs != null){
			for (NPC n : thrallNpcs)
			{
				if (n.getInteracting() != null && n.getInteracting().equals(client.getLocalPlayer()))
				{
					if (meleeCheck)
					{
						if (n.distanceTo(client.getLocalPlayer().getWorldLocation()) <= 1)
						{
							count = count + .5;
						}
					}
				}
			}
		}
		return (int) count;
	}

	public List<Prayer> getPrayers()
	{
		Prayer defensive;
		List<NPC> allRangers = NPCs.getAll("Baboon Thrower");
		List<NPC> allMagers = NPCs.getAll("Baboon Mage");
		List<NPC> allMelees = NPCs.getAll("Baboon Brawler");
		List<NPC> allThralls = NPCs.getAll("Baboon Thrall");
		int interactingRanger = interactingCount(allRangers, null, false);
		int interactingMager = interactingCount(allMagers, null, false);
		int interactingMelee = interactingCount(allMelees, allThralls, true);
		boolean isTanky = toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItems());

		if (isTanky)
		{
			if (interactingMager >= interactingRanger)
			{
				defensive = Prayer.PROTECT_FROM_MAGIC;
			}
			else if (interactingRanger > interactingMelee)
			{
				defensive = Prayer.PROTECT_FROM_MISSILES;
			}
			else
			{
				defensive = Prayer.PROTECT_FROM_MELEE;
			}
		}
		else
		{
			if (interactingMager > interactingRanger)
			{
				defensive = Prayer.PROTECT_FROM_MAGIC;
			}
			else if (interactingRanger > interactingMelee)
			{
				defensive = Prayer.PROTECT_FROM_MISSILES;
			}
			else
			{
				defensive = Prayer.PROTECT_FROM_MELEE;
			}
		}
		//if tanky

//		int range = NPCs.getAll(new String[]{"Baboon Thrower"}).size();
//		int mage = NPCs.getAll(new String[]{"Baboon Mage"}).size();
//		Prayer defensive = null;
//		if (mage > range)
//		{
//			defensive = Prayer.PROTECT_FROM_MAGIC;
//		}
//		else if (range > mage)
//		{
//			defensive = Prayer.PROTECT_FROM_MISSILES;
//		}
//		else
//		{
//			NPC nearest = NPCs.getNearest("Baboon Brawler", "Baboon Thrall");
//			if (nearest != null && nearest.distanceTo(Players.getLocal()) < 3)
//			{
//				defensive = Prayer.PROTECT_FROM_MELEE;
//			}
//			else if (mage > 0)
//			{
//				defensive = Prayer.PROTECT_FROM_MAGIC;
//			}
//		}

		return List.of(this.getOffensive(), defensive);
	}

	public Prayer getOffensive()
	{
		if (client.getNpcs().isEmpty())
		{
			return Prayer.THICK_SKIN;
		}
		ItemContainer equipped = Static.getClient().getItemContainer(InventoryID.EQUIPMENT);
		if (equipped != null)
		{
			Item weapon = equipped.getItem(3);
			if (weapon != null)
			{
				WeaponMap.WeaponStyle style = WeaponMap.StyleMap.getOrDefault(weapon.getId(), WeaponMap.WeaponStyle.MELEE);
				switch (style.ordinal())
				{
					case 0:
						return Prayer.AUGURY;
					case 1:
						return Prayer.RIGOUR;
					case 2:
						return Prayer.PIETY;
				}
			}

			Widget atk = Widgets.get(Combat.getAttackStyle().getWidgetInfo());
			if (atk != null)
			{
				String[] actions = atk.getActions();
				if (actions != null && actions.length == 1)
				{
					switch (actions[0])
					{
						case "Rapid":
							return Prayer.RIGOUR;
						case "Accurate":
						case "Longrange":
							return Prayer.AUGURY;
					}
				}
			}
		}
		return Prayer.PIETY;
	}

	public boolean execute()
	{
		if (!toaManager.baba.isPuzzleActive() && Prayers.anyActive())
		{
			toaManager.print("Disabling all prayers");
			Prayers.disableAll();
			return true;
		}
		else if (!toaManager.baba.isPuzzleActive())
		{
			return false;
		}
		if (!this.getPrayers().isEmpty() && Prayers.getPoints() > 0)
		{
			for (Prayer prayer : getPrayers())
			{
				if (!Prayers.isEnabled(prayer))
				{
					Prayers.toggle(prayer);
				}
			}
			return true;
		}
		else if (this.getPrayers().isEmpty() && Prayers.anyActive())
		{
			Prayers.disableAll();
			return true;
		}
		return false;
	}
}