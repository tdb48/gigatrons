package com.example.toagigatron.tasks.wardens.wardensp3;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.Utility.Movement;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "P3 Skull skip",
	priority = 1,
	register = true
)
public class WardenP3SkullSkip extends StagedTask
{
	public int amountOfSkulls = -1;

	@Inject
	public WardenP3SkullSkip(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P3);
	}

	public boolean execute()
	{
		if (toaManager.wardens3.enrage
			|| toaManager.wardens3.primarySafeTile == null
			|| toaManager.wardens3.secondarySafeTile == null)
		{
			return false;
		}
		int skullTick = toaManager.wardens3.skullTick;
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		if (skullTick == dodgeTick())
		{
			toaManager.print("Dodging to " + toaManager.worldPointString(toaManager.wardens3.primarySafeTile));
			Movement.walk(toaManager.wardens3.primarySafeTile);
			return true;
		}
		if (skullTick > 0 && skullTick < dodgeTick() && !playerPoint.equals(safeTile()))
		{
			toaManager.print("Setting up skulls to tile " + toaManager.worldPointString(safeTile()));
			Movement.walk(safeTile());
			return true;
		}
		if (skullTick > (dodgeTick() + 10) && toaManager.wardens3.warden != null
			&& toaManager.wardens3.warden.getAnimation() == 9682)
		{
			toaManager.print("else statement dodging baba boulder (?) to " + toaManager.worldPointString(safeTile()));
			Movement.walk(safeTile());
			return true;
		}
		return false;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		ArrayList<NPC> skulls = (ArrayList<NPC>) NPCs.search().nameContains("Energy Siphon").result();
		if (skulls.size() > 0)
		{
			amountOfSkulls = skulls.size();
		}
	}

	public WorldPoint safeTile()
	{
		WorldPoint primaryTile = primarySafeTile();
		WorldPoint secondaryTile = secondarySafeTile();
		if (primaryTile == null || secondaryTile == null)
		{
			return null;
		}
		return toaManager.wardens3.babaBombs.containsKey(primaryTile) ? secondaryTile : primaryTile;
	}

	public WorldPoint primarySafeTile()
	{
		if (toaManager.wardens3.warden == null)
		{
			return null;
		}
		return toaManager.wardens3.wardenRefPoint().dx(6).dy(6);
	}

	public WorldPoint secondarySafeTile()
	{
		if (toaManager.wardens3.warden == null)
		{
			return null;
		}
		return toaManager.wardens3.wardenRefPoint().dx(6).dy(5);
	}

	public int dodgeTick()
	{
		switch (amountOfSkulls)
		{
			// Phase 1 is 4 skulls, tick 9
			case 4:
				return 8;
			// Phase 2 is 5 skulls, tick 9
			case 5:
				return 9;
			case 6:
				return 10;
			case 7:
				return 11;
			default:
				return -1;
		}
	}
}