package com.example.nexatron.manager;

import com.example.Utility.Static;
import com.example.nexatron.model.constants.NexConst;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.ChatMessageType;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

@Singleton
public class GameTickManager
{
	private final EventBus eventBus;
	public int attackWait;
	private int tickCount;
	private int tickWait;
	private int potionWait;
	private Projectile p;

	@Inject
	public GameTickManager(EventBus eventBus)
	{
		this.eventBus = eventBus;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		++this.tickCount;
		if (this.isTickWaiting())
		{
			--this.tickWait;
		}

		if (this.isAttackWaiting())
		{
			--this.attackWait;
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

	public int getAttackWait()
	{
		return this.attackWait;
	}

	public boolean isTickWaiting()
	{
		return this.tickWait > 0;
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
	}

	public void drinkPotion()
	{
		this.potionWait = 3;
	}

	public void register()
	{
		this.eventBus.register(this);
		this.setTickCount(0);
		this.p = null;
	}

	public void unregister()
	{
		this.eventBus.unregister(this);
		this.setTickCount(0);
		this.p = null;
	}

	public int getTickCount()
	{
		return this.tickCount;
	}

	public void setTickCount(int tickCount)
	{
		this.tickCount = tickCount;
	}

	public void setTickWait(int tickWait)
	{
		this.tickWait = tickWait;
	}

//	@Subscribe
//	public void onChatMessage(ChatMessage chatMessage)
//	{
//		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM || chatMessage.getType() == ChatMessageType.CONSOLE || chatMessage.getType() == ChatMessageType.ENGINE)
//		{
//			String message = chatMessage.getMessage().toLowerCase();
//			if (message.contains(NexConst.POTION_MESSAGE.toLowerCase()))
//			{
//				System.out.println("Drinking potion!");
//				this.potionWait = 3;
//			}
//		}
//	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged animationChanged)
	{
		if (animationChanged.getActor().equals(Static.getClient().getLocalPlayer()))
		{
			if (animationChanged.getActor().getAnimation() == NexConst.FANG_ANIMATION
				|| animationChanged.getActor().getAnimation() == NexConst.FANG_SLASH_ANIMATION
				|| animationChanged.getActor().getAnimation() == NexConst.ZCB_ANIMATION)
			{
				attack(5);
			}
		}
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved event)
	{
		Projectile projectile = event.getProjectile();
		if (NexConst.DARTS.contains(projectile.getId()))
		{
			if (p == null || !p.equals(projectile))
			{
				attack(2);
				p = projectile;
			}
		}
	}

}
