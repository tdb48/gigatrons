package com.example.Utility;

import com.example.EthanApiPlugin.Collections.NPCs;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.NPC;

public class NPCUtil
{
	public static NPC findFirst(String name)
	{
		return NPCs.search().nameContains(name).result().get(0);
	}

	public static NPC findNearest(int id)
	{
		return NPCs.search().withId(id).nearestToPlayer().filter(x -> x.getHealthRatio() != 0).orElse(null);
	}

	public static List<NPC> findAll(int... id)
	{
		List<Integer> arrayList = Arrays.stream(id)
			.boxed()
			.collect(Collectors.toList());
		return NPCs.search().idInList(arrayList).filter(x -> x.getHealthRatio() != 0).result();
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
		return NPCs.search().withName(name).nearestToPlayer().filter(x -> x.getHealthRatio() != 0).orElse(null);
	}

	public static NPC findNearestNpcAliveOrDead(int id)
	{
		return NPCs.search().withId(id).nearestToPlayer().orElse(null);
	}

}
