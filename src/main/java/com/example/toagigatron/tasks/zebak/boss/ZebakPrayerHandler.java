package com.example.toagigatron.tasks.zebak.boss;


import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.*;
import com.example.Utility.Prayer;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.constants.WeaponMap;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Kephri prayer handler",
	priority = 1,
	register = true
)
public class ZebakPrayerHandler extends StagedTask
{
	private Prayer defensive;

	@Inject
	public ZebakPrayerHandler(ToaManager toaManager)
	{
		super(toaManager, Stage.ZEBAK_BOSS);
	}

	public List<Prayer> getPrayers()
	{
		if (this.defensive == null)
		{
			return List.of(this.getOffensive());
		}
		if (toaManager.zebak.bloodBarrageTick == 1)
		{
			toaManager.print("pray against blood barrage");
			return List.of(this.getOffensive(), Prayer.PROTECT_FROM_MAGIC);
		}
		return List.of(this.getOffensive(), this.defensive);
	}

	public boolean execute()
	{
		if (!toaManager.zebak.isInBossRoom())
		{
			return false;
		}
		if (toaManager.zebak.zebakBoss == null || Prayers.getPoints() == 0)
		{
			if (Prayers.anyActive())
			{
				Prayers.disableAll();
				return true;
			}
			else
			{
				return false;
			}
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

	public Prayer getOffensive()
	{
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

	@Subscribe
	public void onProjectileMoved(ProjectileMoved projectileMoved)
	{
		if (projectileMoved.getProjectile().getId() == 2181)
		{
			this.defensive = Prayer.PROTECT_FROM_MAGIC;
		}
		else if (ToaConstants.ZEBAK_RANGED_PROJECTILE_IDS.contains(projectileMoved.getProjectile().getId()))
		{
			this.defensive = Prayer.PROTECT_FROM_MISSILES;
		}

	}
}
