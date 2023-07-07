package com.example.nexatron.model;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.nexatron.manager.GameTickManager;
import com.example.nexatron.manager.NexManager;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
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
		bankDoor = null;
	}

	public NPC getTarget()
	{
		NPC target = NPCs.search().interactingWithLocal().first().orElse(null);
		if (target != null)
		{
			return target;
		}
		if (client.getBoostedSkillLevel(Skill.SLAYER) >= 83)
		{
			target = NPCs.search().nameContains("Mage").alive().notInteracting().first().orElse(null);
			if (target != null)
			{
				return target;
			}
		}
		return NPCs.search().nameContains("Reaver").alive().notInteracting().first().orElse(null);
	}
}
