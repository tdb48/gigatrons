package com.example.toagigatron.tasks.wardens.wardensp2;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
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
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.Player;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Warden p2 prayers",
	register = true
)
public class WardensP2PrayerHandler extends StagedTask
{
	@Inject
	public WardensP2PrayerHandler(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P2);
	}

	private Prayer defensive;

	public List<Prayer> getPrayers()
	{
		return this.defensive == null ? List.of(this.getOffensive()) : List.of(this.getOffensive(), this.defensive);
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

	@Subscribe
	public void onAnimationChanged(AnimationChanged e)
	{
		Actor actor = e.getActor();
		if (!(actor instanceof Player))
		{
			if (actor.getAnimation() == 9660)
			{
				this.defensive = Prayer.PROTECT_FROM_MISSILES;
			}
			else if (actor.getAnimation() == 9661)
			{
				this.defensive = Prayer.PROTECT_FROM_MAGIC;
			}


		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage evt)
	{
		if (evt.getType() == ChatMessageType.GAMEMESSAGE)
		{
			String message = evt.getMessage();
			if (message.contains("scimitar"))
			{
				this.defensive = Prayer.PROTECT_FROM_MELEE;
			}
			else if (message.contains("skull"))
			{
				this.defensive = Prayer.PROTECT_FROM_MAGIC;
			}
			else if (message.contains("arrow"))
			{
				this.defensive = Prayer.PROTECT_FROM_MISSILES;
			}

		}
	}
}