package com.example.toagigatron.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.NPC;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

@Singleton
public class GameTickManager
{
	private final EventBus eventBus;
	private final Map<NPC, Integer> tornadoes = new HashMap<>();
	private int tickCount;
	private int tickWait;
	private int foodWait;
	private int comboFoodWait;
	private int potionWait;
	public int attackWait;

	@Inject
	public GameTickManager(EventBus eventBus)
	{
		this.eventBus = eventBus;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		++this.tickCount;

		for (NPC lightning : this.tornadoes.keySet())
		{
			int ticks = this.tornadoes.get(lightning) - 1;
			if (ticks == 0)
			{
				this.tornadoes.remove(lightning);
			}
			else
			{
				this.tornadoes.put(lightning, ticks);
			}
		}

		if (this.isTickWaiting())
		{
			--this.tickWait;
		}

		if (this.isFoodWaiting())
		{
			--this.foodWait;
		}

		if (this.isAttackWaiting())
		{
			--this.attackWait;
		}

		if (this.isComboFoodWaiting())
		{
			--this.comboFoodWait;
		}

		if (this.isPotionWaiting())
		{
			--this.potionWait;
		}

	}

	public int getTickWaiting()
	{
		return this.tickWait;
	}

	public boolean isTickWaiting()
	{
		return this.tickWait > 0;
	}

	public boolean isFoodWaiting()
	{
		return this.foodWait > 0;
	}

	public boolean isComboFoodWaiting()
	{
		return this.comboFoodWait > 0;
	}

	public boolean isPotionWaiting()
	{
		return this.potionWait > 0;
	}

	public boolean isAttackWaiting()
	{
		return this.attackWait > 0;
	}

	public void attack(int attackSpeed)
	{
		this.attackWait = attackSpeed;
		this.foodWait = 2;
		this.comboFoodWait = 2;
		this.potionWait = 2;
	}

	public void eat()
	{
		this.attackWait = 4;
		this.foodWait = 3;
	}

	public void drinkPotion()
	{
		this.foodWait = 3;
		this.potionWait = 3;
	}

	public void eatCombo()
	{
		this.drinkPotion();
		this.comboFoodWait = 3;
	}

	public void register()
	{
		this.eventBus.register(this);
		this.setTickCount(0);
	}

	public void unregister()
	{
		this.eventBus.unregister(this);
		this.setTickCount(0);
	}

	public void setLightningSpawn(NPC npc)
	{
		this.tornadoes.put(npc, 21);
	}

	public int getLightningTicks(NPC lightning)
	{
		return (Integer) this.tornadoes.getOrDefault(lightning, 0);
	}

	public void setTickCount(int tickCount)
	{
		this.tickCount = tickCount;
	}

	public int getTickCount()
	{
		return this.tickCount;
	}

	public void setTickWait(int tickWait)
	{
		this.tickWait = tickWait;
	}
}
