package com.example.nexatron.model;

import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

public class Nex
{
	//	public GameObject icePrisonSpike = null;
	public NPC nex = null;
	public NPC fumus = null;
	public NPC umbra = null;
	public NPC glacies = null;
	public NPC cruor = null;
	public GameObject altar = null;
	public WorldPoint centerPoint = null;
	public int nexAttackTick = 0;
	public int umbraAttackTick = 0;
	public boolean teleportOut = false;
	public WorldPoint masterMainTile = null;
	public WorldPoint masterDodgeTile = null;
	public WorldPoint masterStepUnderTile = null;

	public WorldPoint slaveMainTile = null;
	public WorldPoint slaveDodgeTile = null;
	public WorldPoint slaveStepUnderTile = null;
	public int invincibleTick = 0;
	@Inject
	NexManager nexManager;

	@Inject
	Client client;

	@Inject
	EventBus eventBus;

	@Inject
	public Setup setup;

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
		if (nexAttackTick > 0)
		{
			nexAttackTick--;
		}
		if (invincibleTick > 0)
		{
			invincibleTick--;
		}
	}

	public void bankReset()
	{
		nex = null;
		fumus = null;
		umbra = null;
		glacies = null;
		cruor = null;
		nexAttackTick = 0;
		umbraAttackTick = 0;
		teleportOut = false;
	}

	public void fullReset()
	{
		centerPoint = null;
//		icePrisonSpike = null;
		altar = null;
		nex = null;
		fumus = null;
		umbra = null;
		glacies = null;
		cruor = null;
		nexAttackTick = 0;
		umbraAttackTick = 0;
		teleportOut = false;
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged animationChanged)
	{
		Actor npc = animationChanged.getActor();
		if (!(npc instanceof NPC))
		{
			return;
		}
		if (npc.getAnimation() == NexConst.NEX_SHADOW_ANIMATION
			|| npc.getAnimation() == NexConst.NEX_MAGE_ANIMATION
			|| npc.getAnimation() == NexConst.NEX_MELEE_ANIMATION)
		{
			nexAttackTick = 5;
		}
	}


	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
	{
		GameObject gameObject = gameObjectSpawned.getGameObject();
		if (gameObject.getId() == NexConst.ALTAR)
		{
			altar = gameObject;
			centerPoint = altar.getWorldLocation().dx(-15);
			initSmokeTiles();
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned)
	{
		GameObject gameObject = gameObjectDespawned.getGameObject();
		if (gameObject.getId() == NexConst.ALTAR)
		{
			altar = null;
			centerPoint = null;
			deinitSmokeTiles();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();
		if (npc.getName() != null
			&& npc.getName().toLowerCase().contains("nex"))
		{
			nexManager.print("Nex spawned");
			nex = npc;
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();
		if (npc.getName() != null
			&& npc.getName().toLowerCase().contains("nex"))
		{
			nexManager.print("Nex despawned");
			nex = null;
		}
	}

	public void initSmokeTiles()
	{
		if (centerPoint == null)
		{
			return;
		}
		masterMainTile = centerPoint.dx(-2);
		masterDodgeTile = centerPoint.dy(-2);
		masterStepUnderTile = centerPoint.dx(-1);
		slaveMainTile = centerPoint.dy(2);
		slaveDodgeTile = centerPoint.dx(2);
		slaveStepUnderTile = centerPoint.dy(1);
	}

	public void deinitSmokeTiles()
	{
		masterMainTile = null;
		masterDodgeTile = null;
		masterStepUnderTile = null;
		slaveMainTile = null;
		slaveDodgeTile = null;
		slaveStepUnderTile = null;
	}

	public boolean isTargetUnderNex()
	{
		if (nex == null
			|| !nex.isInteracting())
		{
			return false;
		}
		WorldPoint nexTarget = nex.getInteracting().getWorldLocation();
		return nex.getWorldArea().toWorldPointList().contains(nexTarget);
	}
}
