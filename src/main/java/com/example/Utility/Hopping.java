package com.example.Utility;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.runelite.api.GameState;
import net.runelite.client.util.WorldUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;

public class Hopping
{

	// Finds a random world (if staySameRegion, in same region as current world), without dangerous or special worlds
	public static int getValidWorld(boolean staySameRegion)
	{
		WorldResult result = Static.getWorldService().getWorlds();
		World currentWorld = getCurrentWorld();
		if (result == null || currentWorld == null)
		{
			return -1;
		}
		else
		{
			List<World> worlds = result.getWorlds();
			Collections.shuffle(worlds);
			for (World w : worlds)
			{
				if (Static.getClient().getWorld() == w.getId()
					|| w.getTypes().contains(net.runelite.http.api.worlds.WorldType.HIGH_RISK)
					|| w.getTypes().contains(net.runelite.http.api.worlds.WorldType.DEADMAN)
					|| w.getTypes().contains(net.runelite.http.api.worlds.WorldType.PVP)
					|| w.getTypes().contains(net.runelite.http.api.worlds.WorldType.SKILL_TOTAL)
					|| w.getTypes().contains(net.runelite.http.api.worlds.WorldType.BOUNTY)
					|| w.getTypes().contains(net.runelite.http.api.worlds.WorldType.SEASONAL)
					|| w.getTypes().contains(net.runelite.http.api.worlds.WorldType.PVP_ARENA)
					|| w.getTypes().contains(net.runelite.http.api.worlds.WorldType.QUEST_SPEEDRUNNING)
					|| w.getTypes().contains(net.runelite.http.api.worlds.WorldType.FRESH_START_WORLD)
					|| w.getTypes().contains(net.runelite.http.api.worlds.WorldType.TOURNAMENT)
					|| w.getTypes().contains(net.runelite.http.api.worlds.WorldType.NOSAVE_MODE)
					|| w.getId() == 585
					|| w.getId() == 586
					|| w.getId() == 587
					|| w.getId() == 588
					|| w.getId() == 589
					|| !w.getTypes().contains(net.runelite.http.api.worlds.WorldType.MEMBERS))
				{
					continue;
				}
				if (staySameRegion
					&& !w.getRegion().equals(currentWorld.getRegion()))
				{
					continue;
				}
				return w.getId();
			}
		}
		return -1;
	}

	public static int getCurrentWorldNumber()
	{
		return Static.getClient().getWorld();
	}

	public static net.runelite.http.api.worlds.World getCurrentWorld()
	{
		int worldNumber = getCurrentWorldNumber();
		return Objects.requireNonNull(Static.getWorldService().getWorlds()).findWorld(worldNumber);
	}

	public static void hop(int worldNumber)
	{
		if (Static.getWorldService().getWorlds() != null)
		{
			net.runelite.http.api.worlds.World world = Static.getWorldService().getWorlds().findWorld(worldNumber);
			if (world != null)
			{
				System.out.println("Hopping to world " + worldNumber);
				hop(world);
			}
		}
	}

	public static void hop(net.runelite.http.api.worlds.World world)
	{
		final net.runelite.api.World rsWorld = Static.getClient().createWorld();
		rsWorld.setActivity(world.getActivity());
		rsWorld.setAddress(world.getAddress());
		rsWorld.setId(world.getId());
		rsWorld.setPlayerCount(world.getPlayers());
		rsWorld.setLocation(world.getLocation());
		rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));
		if (Static.getClient().getGameState() == GameState.LOGIN_SCREEN)
		{
			Static.getClient().changeWorld(rsWorld);
			return;
		}
		Static.getClient().openWorldHopper();
		Static.getClient().hopToWorld(rsWorld);
	}
}
