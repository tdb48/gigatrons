package com.example.nexatron.model;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.Players;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
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
		System.out.println("Should hop -> " + shouldHop);
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
