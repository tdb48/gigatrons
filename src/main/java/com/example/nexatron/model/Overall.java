package com.example.nexatron.model;


import com.example.nexatron.NexatronPlugin;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
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
	public boolean died;
	public Instant botTimer = Instant.now();
	public GameObject kcAreaDoor = null;
	public GameObject deathChest = null;
	public GameObject bankDoor = null;
	public GameObject altar = null;
	public GameObject barrier = null;
	public NPC banker = null;

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
			if (message.contains("killcount"))
			{
				killCount++;
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{

	}

	public void bankReset()
	{
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
	{
		GameObject gameObject = gameObjectSpawned.getGameObject();
		if (gameObject.getId() == NexConst.KC_AREA_DOOR)
		{
			kcAreaDoor = gameObject;
		}
		if (gameObject.getId() == NexConst.DEATH_CHEST)
		{
			deathChest = gameObject;
		}
		if (gameObject.getId() == NexConst.BANK_DOOR)
		{
			bankDoor = gameObject;
		}
		if (gameObject.getId() == NexConst.ALTAR)
		{
			altar = gameObject;
		}
		if (gameObject.getId() == NexConst.ACTIVE_BARRIER)
		{
			nexManager.print("barrier spawned");
			barrier = gameObject;
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned)
	{
		nexManager.print("despawned " + gameObjectDespawned.getGameObject().getId());
		GameObject gameObject = gameObjectDespawned.getGameObject();
		if (gameObject.equals(barrier))
		{
			nexManager.print("setting barrier to null");
			barrier = null;
		}
		if (gameObject.getId() == NexConst.ACTIVE_BARRIER)
		{
			nexManager.print("barrier despawned through ID");
		}
	}

	public void fullReset()
	{
		died = false;
		botTimer = Instant.now();
		killCount = 0;
		deaths = 0;
		kcAreaDoor = null;
		deathChest = null;
		bankDoor = null;
		altar = null;
		barrier = null;
		banker = null;
	}
}

