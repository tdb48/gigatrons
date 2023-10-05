package com.example.nexatron.model;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.Players;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Utility.Combat;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.Utility.WorldAreas;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.NexSpecial;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.model.setup.Setup;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
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
	public NPC nex = null;
	public NPC fumus = null;
	public NPC umbra = null;
	public NPC glacies = null;
	public NPC cruor = null;
	public GameObject altar = null;
	public GameObject bankDoor = null;
	public WorldPoint centerPoint = null;
	public int nexAttackTick = 0;
	public int nexZarosAttacks = 0;
	public int attacksUntilSpecial = 0;
	public int shadowTick = 0;
	public int containTick = 0;
	public int minionAttackTick = 0;
	public boolean teleportOut = false;
	public WorldPoint masterMainTile = null;
	public WorldPoint masterDodgeTile = null;
	public WorldPoint masterStepUnderTile = null;
	public WorldPoint slaveMainTile = null;
	public WorldPoint slaveDodgeTile = null;
	public WorldPoint slaveStepUnderTile = null;
	public HashMap<NPC, Integer> reavers = new HashMap<>();
	public ArrayList<WorldPoint> sacrificeTiles = new ArrayList<>();
	public NexSpecial nextSpecial = null;
	public int invincibleTick = 0;
	public int dashTick = 0;
	public boolean shouldTripleBrew = false;
	public int brewSipsNeeded = 0;
	public boolean sacrificeActive = false;
	public boolean prisonActive = false;
	public int stuckInPrisonTick = 0;
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
		attacksUntilSpecial = 0;
		nextSpecial = null;
		reavers = new HashMap<>();
		nex = null;
		fumus = null;
		umbra = null;
		glacies = null;
		cruor = null;
		nexAttackTick = 0;
		shadowTick = 0;
		containTick = 0;
		minionAttackTick = 0;
		teleportOut = false;
		invincibleTick = 0;
		dashTick = 0;
		shouldTripleBrew = false;
		brewSipsNeeded = 0;
		prisonActive = false;
		stuckInPrisonTick = 0;
		sacrificeActive = false;
		sacrificeTiles.clear();
		nexZarosAttacks = 0;
	}

	public void fullReset()
	{
		bankReset();
		centerPoint = null;
		altar = null;
		bankDoor = null;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (nex != null)
		{
			setReaverHp();
		}
		if (nexAttackTick > 0)
		{
			nexAttackTick--;
		}
		if (minionAttackTick > 0)
		{
			minionAttackTick--;
		}
		if (invincibleTick > 0)
		{
			invincibleTick--;
		}
		if (shadowTick > 0)
		{
			shadowTick--;
		}
		if (containTick > 0)
		{
			containTick--;
		}
		if (stuckInPrisonTick > 0)
		{
			stuckInPrisonTick--;
		}
		if (dashTick > 0)
		{
			dashTick--;
		}
		if (nexManager.getStage().equals(Stage.MINION_SHADOW))
		{
			initShadowMinionTiles();
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
			if (message.contains(NexConst.SHADOW_DARKNESS_SPECIAL_MSG.toLowerCase()))
			{
				attacksUntilSpecial = 5;
				nextSpecial = NexSpecial.SHADOWS;
			}
			if (message.contains(NexConst.SHADOW_POOL_SPECIAL_MSG.toLowerCase()))
			{
				attacksUntilSpecial = 5;
				nextSpecial = NexSpecial.EMBRACE;
			}
			if (message.contains(NexConst.BLOOD_SACRIFICE_SPECIAL_MSG.toLowerCase()))
			{
				attacksUntilSpecial = 5;
				nextSpecial = NexSpecial.SIPHON;
			}
			if (message.contains(NexConst.BLOOD_SIPHON_SPECIAL_MSG.toLowerCase()))
			{
				attacksUntilSpecial = 5;
				nextSpecial = NexSpecial.SACRIFICE;
			}
			if (message.contains(NexConst.BLOOD_SACRIFICE_ACTIVE_MSG.toLowerCase()))
			{
				sacrificeActive = true;
			}
			if (message.contains(NexConst.BLOOD_SACRIFICE_INACTIVE_MSG.toLowerCase())
				|| message.contains(NexConst.BLOOD_SACRIFICE_INACTIVE_MSG2.toLowerCase()))
			{
				sacrificeActive = false;
				sacrificeTiles.clear();
			}
			if (message.contains(NexConst.ICE_CONTAIN_SPECIAL_MSG.toLowerCase()))
			{
				attacksUntilSpecial = 5;
				nextSpecial = NexSpecial.PRISON;
				containTick = 15;
			}
			if (message.contains(NexConst.ICE_PRISON_SPECIAL_MSG.toLowerCase()))
			{
				attacksUntilSpecial = 5;
				nextSpecial = NexSpecial.CONTAIN;
			}
			if (message.contains(NexConst.PRISON_IMPRISONED.toLowerCase()))
			{
				stuckInPrisonTick = 7;
			}
			if (message.contains(NexConst.PRISON_FREED.toLowerCase()))
			{
				stuckInPrisonTick = 0;
			}
			if (message.contains(NexConst.ZAROS_START.toLowerCase()))
			{
				nexZarosAttacks = 4;
			}
		}
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
			if (attacksUntilSpecial > 0)
			{
				attacksUntilSpecial--;
			}
			if (nexManager.getStage().equals(Stage.NEX_ZAROS))
			{
				if (nexZarosAttacks >= 12)
				{
					nexZarosAttacks = 0;
				}
				nexZarosAttacks++;
			}
		}
		if (animationChanged.getActor().equals(client.getLocalPlayer()) && client.getLocalPlayer().getAnimation() == NexConst.ALTAR_TELEPORT_ANIM)
		{

		}
		if (npc.getAnimation() == NexConst.NEX_NEW_PHASE_ANIMATION)
		{
			nexZarosAttacks = 4;
		}
		if (npc.getAnimation() == NexConst.UMBRA_ATTACK_ANIMATION)
		{
			minionAttackTick = 6;
		}
		if (npc.getAnimation() == NexConst.NEX_DASHBACK_ANIMATION)
		{
			if (nexManager.getStage().equals(Stage.NEX_SHADOW))
			{
				initShadowNexTiles(true);
			}
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
		if (gameObject.getId() == NexConst.BANK_DOOR)
		{
			bankDoor = gameObject;
		}
		if (gameObject.getId() == NexConst.SHADOW)
		{
			shadowTick = 5;
		}
		if (gameObject.getId() == NexConst.ICE_PRISON)
		{
			prisonActive = true;
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
		if (gameObject.getId() == NexConst.BANK_DOOR)
		{
			bankDoor = null;
		}
		if (gameObject.getId() == NexConst.ICE_PRISON)
		{
			// Reset attack tick because fanging the prison will add 5 ticks
			// because of the animation, but in reality it doesn't cost any ticks
			nexManager.gameTickManager.attack(0);
			prisonActive = false;
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
			nexManager.socket.readyToStart = false;
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
		if (npcName.contains("reaver"))
		{
			reavers.put(npc, 100);
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
		if (npcName.contains("reaver"))
		{
			reavers.remove(npc);
		}
	}
//
//	@Subscribe
//	public void onHitsplatApplied(HitsplatApplied hitsplatApplied)
//	{
//		if (hitsplatApplied.getActor().getName() != null
//			&& hitsplatApplied.getActor().getName().toLowerCase().contains("reaver"))
//		{
//			NPC reaver = (NPC) hitsplatApplied.getActor();
//			int hp = nexManager.nex.getNPCHP(reaver);
//			if (hp == 0)
//			{
//				reavers.remove(reaver);
//			}
//			else
//			{
//				reavers.put(reaver, hp);
//			}
//		}
//	}

//	public WorldPoint getClosestBloodStepUnder()
//	{
//		if (nex == null)
//		{
//			return null;
//		}
//		WorldPoint nexCenter = WorldAreas.getCenter(nex.getWorldArea());
//		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
//		ArrayList<String> directions = (ArrayList<String>) Arrays.asList("west", "north", "east", "south");
//		for (WorldPoint worldPoint : nex.getWorldArea().toWorldPointList())
//		{
//			int x = worldPoint.getX();
//			int y = worldPoint.getY();
//			if (y > playerPoint.getY())
//			{
//				directions.remove("north");
//			}
//			if (y < playerPoint.getY())
//			{
//				directions.remove("south");
//			}
//			if (x < playerPoint.getX())
//			{
//				directions.remove("west");
//			}
//			if (x > playerPoint.getX())
//			{
//				directions.remove("east");
//			}
//		}
//		String direction = directions.isEmpty() ?
//			"south" :
//			directions.get(0);
//		if (direction.equals("south"))
//		{
//
//		}
//	}

	public WorldPoint getBloodIceStepUnderNEW()
	{
		if (nex == null)
		{
			return null;
		}
		WorldPoint nexCenter = WorldAreas.getCenter(nex.getWorldArea());
		ArrayList<WorldPoint> stepUnderTiles = new ArrayList<>();
		if (Reachable.isWalkable(nexCenter.dx(-2)))
		{
			stepUnderTiles.add(nexCenter.dx(-1)); // West
		}
		if (Reachable.isWalkable(nexCenter.dx(2)))
		{
			stepUnderTiles.add(nexCenter.dx(1)); // East
		}
		if (Reachable.isWalkable(nexCenter.dy(-2)))
		{
			stepUnderTiles.add(nexCenter.dy(-1)); // South
		}
		if (Reachable.isWalkable(nexCenter.dy(2)))
		{
			stepUnderTiles.add(nexCenter.dy(1)); // North
		}
		WorldPoint closestStepUnder = nexManager.findClosestTileToPlayer(stepUnderTiles);
		Player otherPlayer = Players.search().withName(nexManager.socket.otherName).first().orElse(null);
		if (otherPlayer == null)
		{
			nexManager.print("Missing other player!");
			return closestStepUnder;
		}
		WorldPoint otherPlayerTile = otherPlayer.getWorldLocation();
		WorldPoint closestStepUnderOtherPlayer = nexManager.findClosestTileToWorldPoint(stepUnderTiles, otherPlayerTile);
		if (closestStepUnder != null
			&& closestStepUnder.equals(closestStepUnderOtherPlayer))
		{
			stepUnderTiles.remove(closestStepUnder);
		}
		return nexManager.findClosestTileToPlayer(stepUnderTiles);
	}

	public WorldPoint getSacrificeTile()
	{
		if (nex == null)
		{
			return null;
		}
		WorldPoint southWest = WorldAreas.getCenter(nex.getWorldArea()).dx(-10).dy(-10);
		WorldPoint northEast = WorldAreas.getCenter(nex.getWorldArea()).dx(10).dy(10);
		sacrificeTiles = (ArrayList<WorldPoint>) WorldAreas.createArea(southWest, northEast).toWorldPointList();
		sacrificeTiles.removeIf(n -> n.distanceTo(nex.getWorldArea()) != 8);
		int shortestDistance = 10;
		WorldPoint shortestPathPoint = null;
		for (WorldPoint worldPoint : sacrificeTiles)
		{
			ArrayList<WorldPoint> path = EthanApiPlugin.pathToGoal(worldPoint, new HashSet<>());
			if (path == null)
			{
				continue;
			}
			if (path.size() < shortestDistance)
			{
				shortestDistance = path.size();
				shortestPathPoint = worldPoint;
			}
		}
		return shortestPathPoint;
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

	public final int MAX_BREW_HP = 115;
	public final int BREW_HEAL = 16;

	public boolean shouldTripleBrew()
	{
		if (nexManager.containsStage(Stage.BANK, Stage.KC_AREA, Stage.NONE, Stage.LOBBY))
		{
			return false;
		}
		int missingHealth = onRangedPhase() ? 40 : 50;
		if (nexManager.getStage().equals(Stage.NEX_ZAROS)
			|| nexManager.getStage().equals(Stage.NEX_ICE))
		{
			missingHealth = 60;
		}
		if (getMissingHealth() > missingHealth)
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
		int hpThreshold = MAX_BREW_HP;
		if (nexManager.getStage().equals(Stage.NEX_SHADOW)
			|| nexManager.getStage().equals(Stage.MINION_SMOKE))
		{
			hpThreshold = 85;
		}
//		return MAX_BREW_HP - client.getBoostedSkillLevel(Skill.HITPOINTS);
		return hpThreshold - client.getBoostedSkillLevel(Skill.HITPOINTS);
	}

	public int wpDistanceToMinion(WorldPoint wp)
	{
		NPC activeMinion = getActiveMinion();
		if (activeMinion == null)
		{
			return Integer.MAX_VALUE;
		}
		return wp.distanceTo(activeMinion.getWorldLocation());
	}

	public int distanceToActiveMinion()
	{
		return wpDistanceToMinion(nexManager.getPlayerPoint());
	}

	public boolean onRangedPhase()
	{
		return Equipment.search().nameContains("crossbow").first().orElse(null) != null;
//		Stage stage = nexManager.getStage();
//		return stage == Stage.MINION_ICE
//			|| stage == Stage.MINION_BLOOD
//			|| stage == Stage.MINION_SHADOW
//			|| stage == Stage.NEX_SHADOW
//			|| stage == Stage.MINION_SMOKE;
	}

	public boolean onMeleePhase()
	{
		return Equipment.search().nameContains("fang").first().orElse(null) != null;
//		Stage stage = nexManager.getStage();
//		return stage == Stage.NEX_ICE
//			|| stage == Stage.NEX_BLOOD
//			|| stage == Stage.NEX_SMOKE
//			|| stage == Stage.NEX_ZAROS;
	}

	public WorldPoint getMainTile()
	{
		return nexManager.socket.isMaster ?
			nexManager.nex.masterMainTile :
			nexManager.nex.slaveMainTile;
	}

	public boolean canPrayAltar()
	{
		return client.getVarbitValue(NexConst.ALTAR_VARBIT) == 0;
	}

	public boolean shouldPrayAltar()
	{
		if (nextSpecial.equals(NexSpecial.PRISON) && attacksUntilSpecial <= 1)
		{
			return false;
		}
		NPC interactingNPC = NPCs.search().interactingWithLocal().first().orElse(null);
		return altar != null
			&& Combat.getSpecEnergy() <= 55
			&& canPrayAltar()
			&& distanceToAltar() <= 10
			&& interactingNPC == null;
	}

	public int distanceToAltar()
	{
		if (altar == null)
		{
			return Integer.MAX_VALUE;
		}
		return Objects.requireNonNull(ObjectUtil.getWorldArea(altar)).distanceTo(nexManager.getPlayerPoint());
	}

	public void setReaverHp()
	{
		List<NPC> reavers = NPCs.search().nameContains("eaver").result();
		for (NPC reaver : reavers)
		{
			if (reaver.getHealthRatio() == 0)
			{
				nexManager.nex.reavers.remove(reaver);
			}
			else if (reaver.getHealthRatio() != -1)
			{
				nexManager.nex.reavers.put(reaver, nexManager.nex.getNPCHP(reaver));
			}
		}
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

	public int hpUntilProc()
	{
		Stage stage = nexManager.getStage();
		if (stage.equals(Stage.NEX_SMOKE))
		{
			return nexManager.getBossHp() - NexConst.NEX_SMOKE_PROC;
		}
		if (stage.equals(Stage.NEX_SHADOW))
		{
			return nexManager.getBossHp() - NexConst.NEX_SHADOW_PROC;
		}
		if (stage.equals(Stage.NEX_BLOOD))
		{
			return nexManager.getBossHp() - NexConst.NEX_BLOOD_PROC;
		}
		if (stage.equals(Stage.NEX_ICE))
		{
			return nexManager.getBossHp() - NexConst.NEX_ICE_PROC;
		}
		return -1;
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

	public int distanceToTile(WorldPoint worldPoint)
	{
		ArrayList<WorldPoint> path = EthanApiPlugin.pathToGoal(worldPoint, new HashSet<>());
		return path.size();
	}

	public WorldPoint nearestContainWp(int distance)
	{
		WorldPoint playerPoint = nexManager.getPlayerPoint();
		ArrayList<WorldPoint> possibleTiles = (ArrayList<WorldPoint>) WorldAreas.createArea(playerPoint.dx(-4).dy(-4), playerPoint.dx(5).dy(5)).toWorldPointList();
		possibleTiles.removeIf(n -> !Reachable.isWalkable(n));
		possibleTiles.removeIf(n -> n.distanceTo(nexManager.nex.nex.getWorldArea()) <= distance);
		if (nexManager.socket.isMaster)
		{
			Player otherPlayer = nexManager.socket.getOtherPlayer();
			if (otherPlayer != null)
			{
				possibleTiles.remove(otherPlayer.getWorldLocation());
			}
		}
		return nexManager.findClosestTileToPlayer(possibleTiles);
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

	public NPC getActiveMinion()
	{
		if (nexManager.getStage().equals(Stage.MINION_SMOKE))
		{
			return nexManager.nex.fumus;
		}
		if (nexManager.getStage().equals(Stage.MINION_SHADOW))
		{
			return nexManager.nex.umbra;
		}
		if (nexManager.getStage().equals(Stage.MINION_BLOOD))
		{
			return nexManager.nex.cruor;
		}
		if (nexManager.getStage().equals(Stage.MINION_ICE))
		{
			return nexManager.nex.glacies;
		}
		return null;
	}

	public void initShadowNexTiles(boolean hasTeleported)
	{
		if (centerPoint == null)
		{
			return;
		}
		slaveMainTile = centerPoint.dx(1).dy(11);
		slaveDodgeTile = centerPoint.dy(11);
		if (hasTeleported)
		{
			masterMainTile = centerPoint.dx(-1).dy(11);
			masterDodgeTile = centerPoint.dy(11);
		}
		else
		{
			masterMainTile = centerPoint.dx(-11).dy(1);
			masterDodgeTile = centerPoint.dx(-11);
		}
	}

	public void initShadowMinionTiles()
	{
		if (centerPoint == null)
		{
			return;
		}
		if (shadowTick > 0)
		{
			masterMainTile = centerPoint.dx(3).dy(10);
			slaveMainTile = centerPoint.dx(3).dy(10);
			masterDodgeTile = centerPoint.dx(1).dy(10);
		}
		else
		{
			masterMainTile = centerPoint.dx(3).dy(11);
			slaveMainTile = centerPoint.dx(3).dy(11);
			masterDodgeTile = centerPoint.dx(1).dy(11);
		}
		slaveDodgeTile = centerPoint.dx(7).dy(12);
		slaveStepUnderTile = centerPoint.dx(5).dy(12);
	}

	// Reference screenshot for the blood minion tile layout
	public void initBloodMinionTiles()
	{
		if (centerPoint == null
			|| nexManager.nex.cruor == null)
		{
			return;
		}
		WorldPoint refPoint = nexManager.nex.cruor.getWorldLocation();
		slaveMainTile = refPoint.dx(-1);
		masterMainTile = refPoint.dx(-2).dy(2);
	}

	public GameObject findNearestPrisonSpike()
	{
		Player otherPlayer = nexManager.socket.getOtherPlayer();
		WorldPoint playerPoint = nexManager.nex.stuckInPrisonTick == 0 && otherPlayer != null
			? otherPlayer.getWorldLocation()
			: nexManager.getPlayerPoint();
		ArrayList<WorldPoint> adjecantTiles = new ArrayList<>();
		if (Reachable.isWalkable(playerPoint.dx(-2)))
		{
			adjecantTiles.add(playerPoint.dx(-1));
		}
		if (Reachable.isWalkable(playerPoint.dx(2)))
		{
			adjecantTiles.add(playerPoint.dx(1));
		}
		if (Reachable.isWalkable(playerPoint.dy(-2)))
		{
			adjecantTiles.add(playerPoint.dy(-1));
		}
		if (Reachable.isWalkable(playerPoint.dy(2)))
		{
			adjecantTiles.add(playerPoint.dy(1));
		}
		List<TileObject> allSpikes = TileObjects.search().withId(NexConst.ICE_PRISON).result();
		List<GameObject> bestSpikes = new ArrayList<>();
		for (TileObject spike : allSpikes)
		{
			if (adjecantTiles.contains(spike.getWorldLocation()))
			{
				bestSpikes.add((GameObject) spike);
			}
		}
		if (!bestSpikes.isEmpty())
		{
			return bestSpikes.stream().min(Comparator.comparingInt(o -> client.getLocalPlayer().getWorldLocation().distanceTo(o.getWorldLocation()))).orElse(null);
		}
		return (GameObject) TileObjects.search().withId(NexConst.ICE_PRISON).nearestToPlayer().orElse(null);
	}

	public void initIceMinionTiles()
	{
		if (centerPoint == null
			|| nexManager.nex.glacies == null)
		{
			return;
		}
		WorldPoint refPoint = nexManager.nex.glacies.getWorldLocation();
		slaveMainTile = refPoint.dx(1);
		masterMainTile = refPoint.dx(1).dy(2);
	}

	public void initWrathTiles()
	{
		if (centerPoint == null)
		{
			return;
		}
		WorldPoint refPoint = WorldAreas.getCenter(nex.getWorldArea());
		// Create area around nex
		ArrayList<WorldPoint> possibleTiles = (ArrayList<WorldPoint>) WorldAreas.createArea(refPoint.dx(-5).dy(-5), refPoint.dx(6).dy(6)).toWorldPointList();
		possibleTiles.removeIf(n -> !Reachable.isWalkable(n));
		slaveMainTile = possibleTiles.stream().min(Comparator.comparingInt(o -> altar.getWorldLocation().distanceTo(o))).orElse(null);
		masterMainTile = slaveMainTile;
	}


	public boolean isInteractingWithUs(NPC npc)
	{
		if (npc == null)
		{
			return false;
		}
		return npc.isInteracting()
			&& npc.getInteracting().equals(client.getLocalPlayer());
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
