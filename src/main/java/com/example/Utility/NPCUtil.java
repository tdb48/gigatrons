package com.example.Utility;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.query.NPCQuery;
import static com.example.EthanApiPlugin.Collections.query.NPCQuery.getNPCComposition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class NPCUtil
{
	static Client client = Static.getClient();

	public static boolean hasAction(NPC npc, String action)
	{
		NPCComposition comp = client.getNpcDefinition(npc.getId());
		if (comp == null || comp.getActions() == null)
		{
			return false;
		}
		for (String s : comp.getActions())
		{
			if (s == null)
			{
				continue;
			}
			if (s.equals(action))
			{
				return true;
			}
		}
		return false;
	}

	public static NPC findFirst(String name)
	{
		return NPCs.search().nameContains(name).result().get(0);
	}

	public static NPC findNearest(int id)
	{
		return NPCs.search().withId(id).alive().nearestToPlayer().orElse(null);
	}

	public static NPC findNearest(int... id)
	{
		List<Integer> arrayList = Arrays.stream(id)
			.boxed()
			.collect(Collectors.toList());
		return NPCs.search().idInList(arrayList).filter(x -> x.getHealthRatio() != 0).nearestToPlayer().orElse(null);
	}

	public static NPC findNearest(String... name)
	{
		List<String> arrayList = Arrays.stream(name)
			.collect(Collectors.toList());
		return NPCs.search().alive().filter(x -> arrayList.contains(x.getName())).nearestToPlayer().orElse(null);
	}

	public static List<NPC> findAll(int... id)
	{
		List<Integer> arrayList = Arrays.stream(id)
			.boxed()
			.collect(Collectors.toList());
		return NPCs.search().alive().idInList(arrayList).result();
	}
	public static List<NPC> findAllDeadOrAlive(int... id)
	{
		List<Integer> arrayList = Arrays.stream(id)
				.boxed()
				.collect(Collectors.toList());
		return NPCs.search().idInList(arrayList).result();
	}

	public static List<NPC> findAll(String... name)
	{
		List<String> nameList = Arrays.stream(name)
			.collect(Collectors.toList());
		List<NPC> returnList = new ArrayList<>();
		for (NPC npc : Static.getClient().getNpcs())
		{
			if (npc.getName() != null
				&& nameList.contains(npc.getName())
				&& npc.getHealthRatio() != 0)
			{
				returnList.add(npc);
			}
		}
		return returnList;
	}

	public static NPC findNearest(String name)
	{
		return NPCs.search().withName(name).alive().nearestToPlayer().orElse(null);
	}

	public static NPC findAt(LocalPoint localPoint, int id)
	{
		WorldPoint worldPoint = WorldPoint.fromLocal(Static.getClient(), localPoint);
		return findAt(worldPoint, id);
	}

	public static NPC findAt(WorldPoint worldPoint, int id)
	{
		for (NPC npc : Static.getClient().getNpcs())
		{
			if (npc.getId() == id && npc.getWorldLocation().equals(worldPoint))
			{
				return npc;
			}
		}
		return null;
	}

	public static NPC findNearestNpcAliveOrDead(int id)
	{
		return NPCs.search().withId(id).nearestToPlayer().orElse(null);
	}

}
