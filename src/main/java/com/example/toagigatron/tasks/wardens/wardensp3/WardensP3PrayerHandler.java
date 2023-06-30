package com.example.toagigatron.tasks.wardens.wardensp3;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.Utility.NPCUtil;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Prayer;
import com.example.Utility.Prayers;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.constants.WeaponMap;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Warden p3 prayers",
	register = true
)
public class WardensP3PrayerHandler extends StagedTask
{
	@Inject
	public WardensP3PrayerHandler(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P3);
	}

	private Prayer defensive;

	public List<Prayer> getPrayers()
	{
		NPC zebakPhantom = NPCUtil.findNearest(ToaConstants.ZEBAK_PHANTOM_ID);
		if (zebakPhantom == null)
		{
			this.defensive = null;
			Prayers.disableOverheads();
		}
		return this.defensive == null ? List.of(this.getOffensive()) : List.of(this.getOffensive(), this.defensive);
	}

	public Prayer getOffensive()
	{
		ArrayList<NPC> skulls = (ArrayList<NPC>) NPCUtil.findAll("Energy Siphon");
		if (skulls.size() > 0)
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

			Widget atk = client.getWidget(Combat.getAttackStyle().getWidgetInfo());
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

//	@Subscribe
//	public void onProjectileSpawned(ProjectileSpawned spawned)
//	{
//		Projectile projectile = spawned.getProjectile();
//		if (projectile.getId() == 2181)
//		{
//			this.defensive = Prayer.PROTECT_FROM_MAGIC;
//		}
//		else if (ToaConstants.ZEBAK_RANGED_PROJECTILE_IDS.contains(spawned.getProjectile().getId()))
//		{
//			this.defensive = Prayer.PROTECT_FROM_MISSILES;
//		}
//
//	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned evt)
	{
		NPC npc = evt.getNpc();
		if (npc.getId() == 11744)
		{
			this.defensive = npc.getGraphic() == 2186 ? Prayer.PROTECT_FROM_MAGIC : Prayer.PROTECT_FROM_MISSILES;
		}
	}
}