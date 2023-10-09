package com.example.nexatron.model;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
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

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		shouldHop = false;
	}

	public boolean canKillMage()
	{
		return client.getBoostedSkillLevel(Skill.SLAYER) >= 83;
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
			target = NPCs.search().nameContains("Mage").alive().noOneInteractingWith().nearestToPlayer().orElse(null);
			if (target != null)
			{
				return target;
			}
			nexManager.print("Can't find any mages that no one is hitting, let's hop");
			shouldHop = true;
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
