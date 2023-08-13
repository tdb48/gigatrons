package com.example.nexatron.model;

import com.example.Utility.WorldAreas;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.Stage;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
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
	public int dashTick = 0;
	public boolean shouldTripleBrew = false;
	public int brewSipsNeeded = 0;
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
		invincibleTick = 0;
		dashTick = 0;
		shouldTripleBrew = false;
		brewSipsNeeded = 0;
	}

	public void fullReset()
	{
		bankReset();
		centerPoint = null;
		altar = null;
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
		shouldTripleBrew = shouldTripleBrew();
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM || chatMessage.getType() == ChatMessageType.CONSOLE || chatMessage.getType() == ChatMessageType.ENGINE)
		{
			String message = chatMessage.getMessage().toLowerCase();
			if (message.contains(NexConst.BREW_MESSAGE.toLowerCase()))
			{
				brewSipsNeeded--;
			}
		}
	}

	public int MISSING_HEALTH = 40;
	public final int MAX_BREW_HP = 115;
	public final int BREW_HEAL = 16;

	public boolean shouldTripleBrew()
	{
		if (getMissingHealth() > MISSING_HEALTH)
		{
			brewSipsNeeded = brewSipsToFull();
			nexManager.print("Setting brew sips to " + brewSipsNeeded);
			return true;
		}
		if (brewSipsNeeded == 0)
		{
			return false;
		}
		return shouldTripleBrew;
	}

	public int brewSipsToFull()
	{
		return getMissingHealth() / BREW_HEAL;
	}

	public int getMissingHealth()
	{
		return MAX_BREW_HP - client.getBoostedSkillLevel(Skill.HITPOINTS);
	}

	public boolean onRangedPhase()
	{
		Stage stage = nexManager.getStage();
		return stage == Stage.MINION_ICE
			|| stage == Stage.MINION_BLOOD
			|| stage == Stage.MINION_SHADOW
			|| stage == Stage.NEX_SHADOW
			|| stage == Stage.MINION_SMOKE;
	}

	public boolean onMeleePhase()
	{
		Stage stage = nexManager.getStage();
		return stage == Stage.NEX_ICE
			|| stage == Stage.NEX_BLOOD
			|| stage == Stage.NEX_SMOKE
			|| stage == Stage.NEX_ZAROS;
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
			initSmokeNexTiles();
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
			deinitTiles();
		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		NPC npc = npcSpawned.getNpc();
		if (npc.getName() == null)
		{
			return;
		}
		String npcName = npc.getName().toLowerCase();
		if (npcName.contains("nex"))
		{
			nex = npc;
		}
		if (npcName.contains("fumus"))
		{
			fumus = npc;
		}
		if (npcName.contains("umbra"))
		{
			umbra = npc;
		}
		if (npcName.contains("cruor"))
		{
			cruor = npc;
		}
		if (npcName.contains("glacies"))
		{
			glacies = npc;
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		NPC npc = npcDespawned.getNpc();
		if (npc.getName() == null)
		{
			return;
		}
		String npcName = npc.getName().toLowerCase();
		if (npcName.contains("nex"))
		{
			nex = null;
		}
		if (npcName.contains("fumus"))
		{
			fumus = null;
		}
		if (npcName.contains("umbra"))
		{
			umbra = null;
		}
		if (npcName.contains("cruor"))
		{
			cruor = null;
		}
		if (npcName.contains("glacies"))
		{
			glacies = null;
		}
	}

	public WorldPoint getUnderNex()
	{
		if (nex == null)
		{
			return null;
		}
		if (isUnderNex(client.getLocalPlayer()))
		{
			return WorldAreas.getCenter(nex.getWorldArea());
		}
		return nexManager.findClosestTileToPlayer((ArrayList<WorldPoint>) nex.getWorldArea().toWorldPointList());
	}

	public WorldPoint getStepUnderTile()
	{
		return nexManager.socket.isMaster ?
			nexManager.nex.masterStepUnderTile :
			nexManager.nex.slaveStepUnderTile;
	}

	public WorldPoint getMainTile()
	{
		return nexManager.socket.isMaster ?
			nexManager.nex.masterMainTile :
			nexManager.nex.slaveMainTile;
	}

	public WorldPoint getDodgeTile()
	{
		return nexManager.socket.isMaster ?
			nexManager.nex.masterDodgeTile :
			nexManager.nex.slaveDodgeTile;
	}

	public boolean isNexChasing()
	{
		if (nex == null
			|| !nex.isInteracting())
		{
			return false;
		}
		Player nexTarget = (Player) nex.getInteracting();
		if (nex.getWorldArea().toWorldPointList().contains(nexTarget.getWorldLocation())
			&& nex.isInteracting())
		{
			return true;
		}
		return nex.getPoseAnimation() == NexConst.NEX_CHASE_POSE_ANIMATION;
	}

	public boolean isNexChasingUs()
	{
		if (nex == null)
		{
			return false;
		}
		return isNexChasing()
			&& nex.isInteracting()
			&& nex.getInteracting().equals(client.getLocalPlayer());
	}

	public void initSmokeNexTiles()
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

	public void initSmokeMinionTiles()
	{
		if (centerPoint == null)
		{
			return;
		}
		masterMainTile = centerPoint.dx(-11).dy(3);
		slaveMainTile = centerPoint.dx(-3).dy(11);
		// This tile is for stepping out north out of nex range
		slaveDodgeTile = centerPoint.dx(-5).dy(12);
	}

	public boolean outOfNexRange()
	{
		return distanceToNex() >= 11;
	}

	public boolean canStepOut()
	{
		if (isNexChasing()
			&& !isNexChasingUs())
		{
			return true;
		}
		return nex.isInteracting()
			&& !nex.getInteracting().equals(client.getLocalPlayer())
			&& (nexAttackTick == 3 || nexAttackTick == 4);
	}

	public void deinitTiles()
	{
		masterMainTile = null;
		masterDodgeTile = null;
		masterStepUnderTile = null;
		slaveMainTile = null;
		slaveDodgeTile = null;
		slaveStepUnderTile = null;
	}

	public int distanceToNex()
	{
		if (nex == null
			|| dashTick > 0)
		{
			return 999;
		}
		return nexManager.getPlayerPoint().distanceTo(nex.getWorldArea());
	}

	public int getNPCHP(NPC npc)
	{
		return (int) ((double) npc.getHealthRatio() / (double) npc.getHealthScale() * 100);
	}

	public boolean isUnderNex(Player player)
	{
		if (nex == null
			|| player == null
			|| !nex.isInteracting())
		{
			return false;
		}
		return nex.getWorldArea().toWorldPointList().contains(player.getWorldLocation());
	}

}
