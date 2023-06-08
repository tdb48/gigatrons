package com.example.Utility;

import java.util.List;
import net.runelite.api.NPC;

public class NPCs
{
	public static NPC findFirst(String name)
	{
		return com.example.EthanApiPlugin.Collections.NPCs.search().nameContains(name).result().get(0);
	}

//	public static List<NPC> getAll(int... ids)
//	{
//
//	}
}
