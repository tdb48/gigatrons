package com.example.nexatron.tasks.general;

import com.example.Utility.Combat;
import com.example.Utility.Prayer;
import com.example.Utility.Prayers;
import com.example.Utility.Static;
import com.example.nexatron.NexatronPlugin;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.model.constants.WeaponMap;
import com.example.nexatron.taskformat.StagedTask;
import com.example.nexatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Pray",
	priority = Integer.MAX_VALUE,
	register = true
)
public class Pray extends StagedTask
{
	public static final int AUGURY_UNLOCKED = 5452;
	public ArrayList<Prayer> prayers = new ArrayList<>();
	@Inject
	GameTickManager gameTickManager;

	@Inject
	NexatronPlugin plugin;

	@Inject
	public Pray(NexManager nexManager)
	{
		super(nexManager,
			Stage.MINION_SMOKE,
			Stage.NEX_SMOKE,
			Stage.MINION_SHADOW,
			Stage.NEX_SHADOW,
			Stage.MINION_BLOOD,
			Stage.NEX_BLOOD,
			Stage.MINION_ICE,
			Stage.NEX_ICE,
			Stage.NEX_ZAROS,
			Stage.KC_AREA);
	}

	// Returning true means we have prayers left to do
	public boolean execute()
	{
		if (prayers.isEmpty() || Prayers.getPoints() == 0)
		{
			return false;
		}
		nexManager.print("Toggling " + prayers.get(0).getVarbit() +", on tick " + nexManager.totalClientTicks);
		Prayers.toggle(prayers.get(0));
		prayers.remove(0);
		return !prayers.isEmpty();
	}

	@Subscribe(priority = 10)
	public void onGameTick(GameTick gameTick)
	{
		List<Prayer> requiredPrayers = getPrayers();
		prayers = filterPrayers(requiredPrayers);
	}

	public ArrayList<Prayer> filterPrayers(List<Prayer> prayers)
	{
		ArrayList<Prayer> toPray = new ArrayList<>();
		if (nexManager.config.prayFlick()
			&& plugin.getManager().actionCounter < 8
			&& Prayers.hasEnabled(prayers))
		{
			toPray.addAll(prayers);
			toPray.addAll(prayers);
			return toPray;
		}
		for (Prayer prayer : prayers)
		{
			if (!Prayers.isEnabled(prayer))
			{
				toPray.add(prayer);
			}
		}
		return toPray;
	}


	public List<Prayer> getPrayers()
	{
//		if (nexManager.nex.nex == null)
//		{
//			return List.of();
//		}
		Prayer protectionPrayer = getDefensive();
		if (nexManager.containsStage(Stage.KC_AREA))
		{
			protectionPrayer = getKcDefensive();
		}
		Prayer offensive = getOffensive();
		if (protectionPrayer == null)
		{
			return List.of(Prayers.getOffensive());
		}
		return List.of(offensive, protectionPrayer);
	}

	public Prayer getOffensive()
	{
		if (gameTickManager.attackWait > 1
			&& nexManager.containsStage(Stage.NEX_BLOOD, Stage.MINION_BLOOD, Stage.NEX_ZAROS))
		{
			return findBestMagePrayer();
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

			Widget atk = Static.getClient().getWidget(Combat.getAttackStyle().getWidgetInfo());
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

	public Prayer getDefensive()
	{
		if (nexManager.getStage().equals(Stage.NEX_ICE)
			|| nexManager.getStage().equals(Stage.MINION_ICE)
			|| nexManager.getStage().equals(Stage.NEX_ZAROS))
		{
			// Protect ranged in ice prison
			if (nexManager.nex.prisonActive
				&& (nexManager.nex.stuckInPrisonTick > 0 && nexManager.nex.stuckInPrisonTick <= 3))
			{
				return Prayer.PROTECT_FROM_MISSILES;
			}
		}

		if ((nexManager.getStage() == Stage.NEX_SMOKE || nexManager.getStage() == Stage.NEX_ZAROS)
			&& nexManager.nex.distanceToNex() < 3
			&& nexManager.nex.nex.isInteracting()
			&& nexManager.nex.nex.getInteracting().equals(client.getLocalPlayer()))
		{
			return Prayer.PROTECT_FROM_MELEE;
		}

		if (nexManager.getStage() == Stage.NEX_SHADOW
			|| nexManager.getStage() == Stage.MINION_SHADOW)
		{
			if (nexManager.nex.umbra != null
				&& nexManager.nex.umbra.isInteracting()
				&& nexManager.nex.umbra.getInteracting().equals(client.getLocalPlayer()))
			{
				if (nexManager.nex.nex.isInteracting()
					&& !nexManager.nex.nex.getInteracting().equals(client.getLocalPlayer()))
				{
					return Prayer.PROTECT_FROM_MAGIC;
				}
//				if (nexManager.nex.umbraAttackTick == 2)
//				{
//					return Prayer.PROTECT_FROM_MAGIC;
//				}
			}
			return Prayer.PROTECT_FROM_MISSILES;
		}
		return Prayer.PROTECT_FROM_MAGIC;
	}

	public Prayer findBestMagePrayer()
	{
		return client.getVarbitValue(AUGURY_UNLOCKED) == 0
			? Prayer.MYSTIC_MIGHT
			: Prayer.AUGURY;
	}


	public Prayer getKcDefensive()
	{
		return Prayer.PROTECT_FROM_MAGIC;
	}

}
