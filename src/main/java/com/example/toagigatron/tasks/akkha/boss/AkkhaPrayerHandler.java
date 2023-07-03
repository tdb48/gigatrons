package com.example.toagigatron.tasks.akkha.boss;

import com.example.Utility.Combat;
import com.example.Utility.NPCUtil;
import com.example.Utility.Prayer;
import com.example.Utility.Prayers;
import com.example.Utility.Projectiles;
import com.example.Utility.Static;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.WeaponMap;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import java.util.List;
import net.runelite.api.Actor;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.NpcChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Akkha prayers",
	register = true
)
public class AkkhaPrayerHandler extends StagedTask
{
	@Inject
	public AkkhaPrayerHandler(ToaManager toaManager)
	{
		super(toaManager, Stage.AKKHA_BOSS);
	}

	private Prayer current;
	private Prayer next;
	private int nextAttack;

	public boolean execute()
	{
		if (!toaManager.akkha.isBossActive() || Prayers.getPoints() == 0)
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
			if (toaManager.config.prayFlick() && Prayers.hasEnabled(getPrayers()))
			{
				for (Prayer prayer : getPrayers())
				{
					Prayers.toggle(prayer);
				}
				for (Prayer prayer : getPrayers())
				{
					Prayers.toggle(prayer);
				}
			}
			else
			{
				for (Prayer prayer : getPrayers())
				{
					if (!Prayers.isEnabled(prayer))
					{
						Prayers.toggle(prayer);
					}
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

	private boolean isAnimating(Actor actor)
	{
		return actor.getAnimation() != -1;
	}

	public List<Prayer> getPrayers()
	{
		NPC akkha = NPCUtil.findNearest("Akkha");
		NPC magicOrb = NPCUtil.findNearest(11804);
		if (akkha == null)
		{
			return List.of(this.getOffensive());
		}
		else if (magicOrb != null)
		{
			return List.of(Prayer.PROTECT_FROM_MAGIC, this.getOffensive());
		}
		else
		{
			if (Static.getClient().getTickCount() >= this.nextAttack)
			{
				this.current = this.next;
			}

			boolean isMageProjectile = Projectiles.getProjectile(2253) != null;
			if (!isMageProjectile && this.next == Prayer.PROTECT_FROM_MELEE && !isAnimating(akkha))
			{
				this.current = this.next;
			}
			if (current == null)
			{
				return List.of(this.getOffensive());
			}
			return List.of(this.getOffensive(), this.current);
		}
	}

	@Subscribe
	public void onNpcChanged(NpcChanged changed)
	{
		NPC npc = changed.getNpc();
		if (npc.getId() == 11790)
		{
			this.next = Prayer.PROTECT_FROM_MELEE;
		}
		else if (npc.getId() == 11791)
		{
			this.next = Prayer.PROTECT_FROM_MISSILES;
		}
		else if (npc.getId() == 11792)
		{
			this.next = Prayer.PROTECT_FROM_MAGIC;
		}

	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged animationChanged)
	{
		Actor npc = animationChanged.getActor();
		if (!(npc instanceof Player))
		{
			if (((NPC) npc).getId() == 11790 || ((NPC) npc).getId() == 11791 || ((NPC) npc).getId() == 11792)
			{
				if (npc.getAnimation() == 9770)
				{
					this.current = Prayer.PROTECT_FROM_MELEE;
				}

				if (npc.getAnimation() == 9772)
				{
					this.current = Prayer.PROTECT_FROM_MISSILES;
				}

				if (npc.getAnimation() == 9774)
				{
					this.current = Prayer.PROTECT_FROM_MAGIC;
				}

				this.nextAttack = Static.getClient().getTickCount() + 4;
			}
		}
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

}
