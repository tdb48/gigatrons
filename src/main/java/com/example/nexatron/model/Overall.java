package com.example.nexatron.model;


import com.example.Utility.Static;
import com.example.nexatron.NexatronPlugin;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.Stage;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

public class Overall
{
	public int killCount = 0;
	public int deaths = 0;
	public int failedKills = 0;
	public boolean died;
	public Instant botTimer = Instant.now();

	@Inject
	NexatronPlugin nexatronPlugin;
	@Inject
	NexManager nexManager;
	@Inject
	EventBus eventBus;

	public void register()
	{
		this.eventBus.register(this);
	}

	public void unregister()
	{
		this.eventBus.unregister(this);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM || chatMessage.getType() == ChatMessageType.CONSOLE || chatMessage.getType() == ChatMessageType.ENGINE)
		{
			String message = chatMessage.getMessage().toLowerCase();
			if (message.contains("oh dear, you are dead")
				|| message.contains("you have died"))
			{
				deaths++;
				died = true;
			}
			if (message.contains("kill count"))
			{
				killCount++;
			}
		}
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged animationChanged)
	{
		if (animationChanged.getActor().equals(Static.getClient().getLocalPlayer())
			&& Static.getClient().getLocalPlayer().getAnimation() == NexConst.ALTAR_TELEPORT_ANIM
			&& nexManager.nex.nex != null)
		{
			failedKills++;
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{

	}

	public void bankReset()
	{
	}

	public void fullReset()
	{
		failedKills = 0;
		died = false;
		botTimer = Instant.now();
		killCount = 0;
		deaths = 0;
	}
}

