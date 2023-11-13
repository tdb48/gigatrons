package com.example.nexatron.model;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.Players;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

/*
 * Tasks:
 * 1. KcPrayer
 * - pray against whatever is attacking us
 * - if we are not in combat, disable prayers
 *
 *
 *
 * 2. KcConsume
 *
 *
 *
 * 3. KcAttack
 *
 *
 *
 * 4. EnterBank
 * - cant enter bank if on kc mode
 * - enter bank when we have enough kc (config?)
 *
 *
 * */
public class KcArea
{
	public GameObject bankDoor = null;
	@Inject
	NexManager nexManager;

	@Inject
	Client client;

	@Inject
	EventBus eventBus;

	@Inject
	GameTickManager gameTickManager;

	public boolean shouldHop = false;

	public void register()
	{
		this.eventBus.register(this);
	}

	public void unregister()
	{
		this.eventBus.unregister(this);
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
//		System.out.println("Should hop -> " + shouldHop);
		nexManager.kcArea.shouldHop = shouldHop();
	}

	public void reset()
	{

	}

	public void fullReset()
	{
		shouldHop = false;
		reset();
		bankDoor = null;
	}

	public boolean shouldHop()
	{
		if (!nexManager.shouldKc())
		{
			return false;
		}
		List<Player> players = Players.search().notLocalPlayer().result();
		for (Player player : players)
		{
			if (player != null
				&& player.isInteracting()
				&& player.getInteracting() != null
				&& player.getInteracting().getName() != null
				&& player.getInteracting().getName().contains("Mage")
				&& canKillMage())
			{
				//COMMENT THIS IF STATEMENT OUT IF BUGS START
//				if (player.getName() != null && nexManager.socket.otherName != null && player.getName().equals(nexManager.socket.otherName))
//				{
//					System.out.println("Someone else hitting mages but its my duo partner so i dont care");
//					return false;
//				}
//				System.out.println("Found someone hitting a mage, we're gonna hop worlds");
//				nexManager.print("Found someone hitting a mage, we're gonna hop worlds");
				return true;
			}
			if (player != null
				&& player.isInteracting()
				&& player.getInteracting() != null
				&& player.getInteracting().getName() != null
				&& player.getInteracting().getName().contains("Reaver")
				&& !canKillMage())
			{
				//COMMENT THIS IF STATEMENT OUT IF BUGS START
//				if (player.getName() != null && nexManager.socket.otherName != null && player.getName().equals(nexManager.socket.otherName))
//				{
//					return false;
//				}
//				System.out.println("Found someone hitting a reaver, we're gonna hop worlds");
//				nexManager.print("Found someone hitting a reaver, we're gonna hop worlds");
				return true;
			}
		}
		if (nexManager.socket.world == nexManager.socket.otherWorld
			&& nexManager.socket.otherWorld != -1
			&& nexManager.socket.isSlave())
		{
//			System.out.println("My world: " +nexManager.socket.world + " | Other world: " + nexManager.socket.otherWorld);
			//If other account is killing mages and I CANT kill mages
			// OR
			//other account is killing reavers and I CAN kill mages
			//return false, we can share a world
			//This actually minimizes the chance of us being crashed i think
			if ((nexManager.socket.otherCanKillMages && !canKillMage())
				|| (!nexManager.socket.otherCanKillMages && canKillMage()))
			{
//				System.out.println("Returning false in socket check thing down here");
//				nexManager.print("Returning false in socket check thing down here");
				return false;
			}
//			System.out.println("Returning TRUE in socket check thing down here");
//			nexManager.print("Returning TRUE in socket check thing down here");
			return true;
		}
		return nexManager.kcArea.shouldHop;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		shouldHop = false;
	}

	public boolean canKillMage()
	{
		return client.getRealSkillLevel(Skill.SLAYER) >= 83;
	}

	public Reaver getReaverTarget()
	{
		Reaver reaver = null;
		int distance = 6;
		WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();
		for (Map.Entry<Integer, Reaver> entry : nexManager.getPlugin().reaverManager.reavers.entrySet())
		{
			Reaver r = entry.getValue();
			int index = entry.getKey();

			// TODO: Maybe add one more check for whether another player is already interacting with the reaver

			//Npc index is not one we are interested in
			if (!nexManager.getPlugin().reaverManager.centralNpcIndexs.contains(index))
			{
				continue;
			}
			if(r.getReaver() == null)
			{
				//System.out.println("Continuing because r.getReaver is null? Index: " + index);
				continue;
			}
			//If reaver is dead, or reaver is already interacting continue
			if (!r.isAlive()
				|| r.getReaverInteracting() != null)
			{
				if(!r.isAlive())
				{
					//System.out.println("Continuing because r.isAlive() is false. Index: " + index);
				}
				else
				{
					//System.out.println("Continuing because r.getInteracting is not null. Index: " + index);
				}

				continue;
			}
			//Reavers current loc is null idk how but continue
			if (r.getCurrentLocation() == null)
			{
				//System.out.println("Continuing because r.getCurrentLocation == null? Index: " + index);
				continue;
			}
			//Reaver is in scene but location is > 6 tiles from central area, continue
			if (r.getCurrentLocation().isInScene(client)
				&& r.getCurrentLocation().distanceTo(nexManager.getPlugin().reaverManager.centralArea) > 6)
			{
				//System.out.println("Continuing because its further than 7 dist apparently. Index: " + index);
				continue;
			}
			//We don't have line of sight to reaver, continue
			if (!r.getCurrentLocation().toWorldArea().hasLineOfSightTo(client, playerLoc)
				&& !playerLoc.toWorldArea().hasLineOfSightTo(client, r.getCurrentLocation()))
			{

				//System.out.println("Continuing because i dont have LOS to reaver and it dont have LOS to me. Index: " + index);
				continue;
			}
			//Checking distance to PLAYER here because in this logic block we are only interested in reavers
			//That can be instantly attacked and require no extra pathing
			int dist = r.getCurrentLocation().distanceTo(playerLoc);
			if (dist < distance) // || reaver == null
			{
				distance = dist;
				reaver = r;
			}
		}
		//Iterate the reavers again with new ruleset (same as above but skipping venomed ones)
		if(reaver == null)
		{
			for (Map.Entry<Integer, Reaver> entry : nexManager.getPlugin().reaverManager.reavers.entrySet())
			{
				Reaver r = entry.getValue();
				int index = entry.getKey();

				//Npc index is not one we are interested in
				if (!nexManager.getPlugin().reaverManager.centralNpcIndexs.contains(index))
				{
					continue;
				}
				if(r.getReaver() == null)
				{
					continue;
				}
				//it's already venomd so we dont care
				if(r.isVenomed())
				{
					continue;
				}
				//Reaver is dead or attacking somebody else, continue
				if (!r.isAlive()
					|| (r.getReaverInteracting() != null && !r.getReaver().getInteracting().equals(client.getLocalPlayer())))
				{
					continue;
				}
				//Reavers current loc is null idk how but continue
				if (r.getCurrentLocation() == null)
				{
					continue;
				}
				if (r.getCurrentLocation().isInScene(client)
					&& r.getCurrentLocation().distanceTo(nexManager.getPlugin().reaverManager.centralArea) > 6)
				{
					continue;
				}
				//We don't have line of sight to reaver, continue
				if (!r.getCurrentLocation().toWorldArea().hasLineOfSightTo(client, playerLoc)
					&& !playerLoc.toWorldArea().hasLineOfSightTo(client, r.getCurrentLocation()))
				{
					continue;
				}
				//Checking distance to PLAYER here because in this logic block we are only interested in reavers
				//That can be instantly attacked and require no extra pathing
				int dist = r.getCurrentLocation().distanceTo(playerLoc);
				if (dist < distance) // || reaver == null
				{
					distance = dist;
					reaver = r;
				}
			}
		}
		//If we get to here it means we havent found a reaver in attack distance that is available to attack and that isnt
		//Already attacking us or someone else
		//Now we look for any reavers that we can attack that are OUTSIDE our range, and bp walk toward it
		if(reaver == null)
		{
			for (Map.Entry<Integer, Reaver> entry : nexManager.getPlugin().reaverManager.reavers.entrySet())
			{
				Reaver r = entry.getValue();
				int index = entry.getKey();

				//Npc index is not one we are interested in
				if (!nexManager.getPlugin().reaverManager.centralNpcIndexs.contains(index))
				{
					continue;
				}
				if(r.getReaver() == null)
				{
					continue;
				}
				//Reaver is dead or attacking somebody, continue
				if (!r.isAlive() || r.getReaverInteracting() != null)
				{
					continue;
				}
				//Reavers current loc is null idk how but continue
				if (r.getCurrentLocation() == null)
				{
					continue;
				}
				if (!r.getCurrentLocation().isInScene(client))
				{
					continue;
				}
				//We don't have line of sight to reaver, continue
				if (!r.getCurrentLocation().toWorldArea().hasLineOfSightTo(client, playerLoc)
					&& !playerLoc.toWorldArea().hasLineOfSightTo(client, r.getCurrentLocation()))
				{
					continue;
				}
				//For testing purposes only, this shouldnt be required due to the logic above
//				if(r.getCurrentLocation().distanceTo(nexManager.getPlugin().reaverManager.centralArea) < 6)
//				{
//					continue;
//				}
				//We are now checking distance to the central area rather than the player tile
				int dist = r.getCurrentLocation().distanceTo(nexManager.getPlugin().reaverManager.centralArea);
				if (dist < distance) // || reaver == null
				{
					distance = dist;
					reaver = r;
				}
			}
		}
//		if(reaver == null)
//		{
//			System.out.println("We are down here in the emergency reaver check where we will take anything");
//			for (Map.Entry<Integer, Reaver> entry : nexManager.getPlugin().reaverManager.reavers.entrySet())
//			{
//				Reaver r = entry.getValue();
//				int index = entry.getKey();
//
//				//Npc index is not one we are interested in
//				if (!nexManager.getPlugin().reaverManager.centralNpcIndexs.contains(index))
//				{
//					continue;
//				}
//				if(r.getReaver() == null)
//				{
//					System.out.println("EMERGENCY Continuing because r.getReaver is null? Index: " + index);
//					continue;
//				}
//				//If reaver is dead, or reaver is already interacting continue
//				if (!r.isAlive())
//				{
//					System.out.println("EMERGENCY Continuing because r.isAlive() is false. Index: " + index);
//					continue;
//				}
//				if(r.getReaver().isInteracting() && !r.getReaver().getInteracting().equals(client.getLocalPlayer()))
//				{
//					System.out.println("EMERGENCY Reaver is interacting with someone other than me.continuing. index: " + index);
//				}
//				//Reavers current loc is null idk how but continue
//				if (r.getCurrentLocation() == null)
//				{
//					System.out.println("EMERGENCY Continuing because r.getCurrentLocation == null? Index: " + index);
//					continue;
//				}
//				//Reaver is in scene but location is > 7 tiles from central area, continue
//				//We dont care bout distance checks at this poin
//				if (r.getCurrentLocation().isInScene(client)
//					&& r.getCurrentLocation().distanceTo(nexManager.getPlugin().reaverManager.centralArea) > 7)
//				{
//					System.out.println("EMERGENCY Continuing because its further than 7 dist apparently. Index: " + index);
//					continue;
//				}
//				//We don't have line of sight to reaver, continue
//				if (!r.getCurrentLocation().toWorldArea().hasLineOfSightTo(client, playerLoc)
//					&& !playerLoc.toWorldArea().hasLineOfSightTo(client, r.getCurrentLocation()))
//				{
//					System.out.println("EMERGENCY Continuing because i dont have LOS to reaver and it dont have LOS to me. Index: " + index);
//					continue;
//				}
//				int dist = r.getCurrentLocation().distanceTo(playerLoc);
//				if (dist < distance) // || reaver == null
//				{
//					distance = dist;
//					reaver = r;
//				}
//			}
//		}
//		//Now we check for any dead reavers so we can prepath toward the respawn tile
//		if(reaver == null)
//		{
//			int timeUntilSpawn = Integer.MAX_VALUE;
//			for (Map.Entry<Integer, Reaver> entry : nexManager.getPlugin().reaverManager.reavers.entrySet())
//			{
//				Reaver r = entry.getValue();
//				int index = entry.getKey();
//
//				//Npc index is not one we are interested in
//				if (!nexManager.getPlugin().reaverManager.centralNpcIndexs.contains(index))
//				{
//					continue;
//				}
//				//Reaver is alive n we don wan this
//				if (r.isAlive())
//				{
//					continue;
//				}
//				int ticksUntilSpawn = r.getTimeUntilRespawn();
//				int dist = r.getSpawnLocation().distanceTo(nexManager.getPlugin().reaverManager.centralArea);
//
//				//This one is spawning faster than the one we currently have stored, replacing.
//				//Alternatively it could be spawning at the same time, but one is closer than the other.
//				if(ticksUntilSpawn < timeUntilSpawn || (ticksUntilSpawn <= timeUntilSpawn && dist < distance))
//				{
//					timeUntilSpawn = ticksUntilSpawn;
//					distance = dist;
//					reaver = r;
//				}
//			}
//		}
		//If we made it this far all we are looking to do is prio the lowest hp ones I guess
//		if(reaver == null)
//		{
//			int hitpoints = Integer.MAX_VALUE;
//			for (Map.Entry<Integer, Reaver> entry : nexManager.getPlugin().reaverManager.reavers.entrySet())
//			{
//				Reaver r = entry.getValue();
//				int index = entry.getKey();
//
//				//Npc index is not one we are interested in
//				if (!nexManager.getPlugin().reaverManager.centralNpcIndexs.contains(index))
//				{
//					continue;
//				}
//				if(r.getReaver() == null)
//				{
//					continue;
//				}
//				//Reaver is dead or attacking somebody else, continue
//				if (!r.isAlive()
//					|| (r.getReaverInteracting() != null && !r.getReaver().getInteracting().equals(client.getLocalPlayer())))
//				{
//					continue;
//				}
//				//Reavers current loc is null idk how but continue
//				if (r.getCurrentLocation() == null)
//				{
//					continue;
//				}
//				if (r.getCurrentLocation().isInScene(client)
//					&& r.getCurrentLocation().distanceTo(nexManager.getPlugin().reaverManager.centralArea) > 6)
//				{
//					continue;
//				}
//				if( r.getHitpoints() < hitpoints)
//				{
//					hitpoints = r.getHitpoints();
//					reaver = r;
//				}
//			}
//		}
		return reaver;
	}

	/*
	 * hopping thoughts:
	 * if theres more than 2 players killing NPCs in our world
	 * just hop, to be safe even though we are only looking for NPCs that other people arent hitting
	 */
	public NPC getTarget()
	{
		NPC target = NPCs.search().interactingWithLocal().first().orElse(null);
		if (target != null)
		{
			return target;
		}
		if (canKillMage())
		{
			target = NPCs.search().nameContains("Mage").alive().notInteracting().nearestToPlayer().orElse(null);
			if (target != null)
			{
				return target;
			}
			//UNCOMMENT THIS IF BAD THINGS START HAPPENING
			//shouldHop = true;
		}
		return NPCs.search().nameContains("Reaver").alive().noOneInteractingWith().first().orElse(null);
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
	{
		GameObject gameObject = gameObjectSpawned.getGameObject();
		if (gameObject.getId() == NexConst.BANK_DOOR)
		{
			bankDoor = gameObject;
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned)
	{
		GameObject gameObject = gameObjectDespawned.getGameObject();
		if (gameObject.getId() == NexConst.BANK_DOOR)
		{
			bankDoor = null;
		}
	}
}
