package com.example.Utility;

import com.example.EthanApiPlugin.Collections.Players;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import net.runelite.api.Actor;
import net.runelite.api.Projectile;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

public class Projectiles
{
	private Projectiles()
	{
	}

	public static List<Projectile> getAll()
	{
		List<Projectile> out = new ArrayList<>();
		Static.getClient().getProjectiles().forEach(out::add);
		return out;
	}

	public static Projectile getProjectile(int id)
	{
		for (Projectile projectile : getAll())
		{
			if (projectile.getId() == id)
			{
				return projectile;
			}
		}
		return null;
	}

}
