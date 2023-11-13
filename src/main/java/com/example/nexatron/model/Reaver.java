package com.example.nexatron.model;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;

public class Reaver
{

	@Getter
	private final int index;
	@Getter
	private int npcId;
	@Getter
	private String name;
	@Getter
	@Setter
	private int hitpoints;
	@Getter
	@Setter
	private boolean isVenomed;
	@Getter
	@Setter
	private int myDamageCount;
	@Getter
	private final WorldPoint spawnLocation;
	@Getter
	@Setter
	private WorldPoint currentLocation;
	@Getter
	@Setter
	private boolean isAlive;
	@Getter
	private NPC reaver;
	@Getter
	@Setter
	private int timeUntilRespawn;
	@Getter
	private int lastAnimationSeen;
	@Inject
	private Client client;


	public Reaver(int index, WorldPoint spawnLocation)
	{
		//Things we can confidently set now
		this.index = index;
		this.spawnLocation = spawnLocation;

		//All to be set when we add a non null reaver
		this.reaver = null;
		this.name = "";
		this.npcId = -1;
		this.hitpoints = -1;
		this.isVenomed = false;
		this.myDamageCount = 0;
		this.currentLocation = null;
		this.isAlive = false;
		lastAnimationSeen = -1;
		timeUntilRespawn = -1;
	}

	public void updateReaver(NPC reaver)
	{
		if(reaver == null)
		{
			//System.out.println("Reaver is somehow null which should be impossible.");
			return;
		}
		this.reaver = reaver;
		this.name = reaver.getName();
		this.npcId = reaver.getId();
		this.hitpoints = getNPCHP(reaver);
		this.currentLocation = reaver.getWorldLocation();
		this.isAlive = !reaver.isDead() && this.hitpoints > 0;
	}

	public Player getReaverInteracting()
	{
		if(reaver == null || reaver.isDead() || !reaver.isInteracting())
		{
			return null;
		}
		if(reaver.getInteracting() instanceof Player)
		{
			return (Player) reaver.getInteracting();
		}
		return null;
	}

	public void setReaverDead()
	{
		this.hitpoints = 0;
		this.isAlive = false;
	}

	public void setReaverDespawned()
	{
		if(lastAnimationSeen == 9192)
		{
			//System.out.println("Reaver died cause its animation was 9192.");
			this.reaver = null;
			this.hitpoints = -1;
			this.name = null;
			this.npcId = -1;
			this.isVenomed = false;
			this.myDamageCount = -1;
			this.currentLocation = null;
			this.isAlive = false;
			this.timeUntilRespawn = 24; //find out this value
		}
		else
		{
			//System.out.println("Reaver despawned but didn't go through the dying animation, probably went off screen?");
			//System.out.println("Off screen reaver index -> " + this.index);
			//Do we need to change any values other than this if we think it has just wandered off screen? Probably not?
			this.reaver = null;
			this.currentLocation = null;
		}
	}

	public void updateHitpoints(int damageSplat, boolean myDamage, boolean isHealSplat)
	{
		//System.out.println("Inside update hitpoints. Damage: " + damageSplat + ", Is it mine? -> " + myDamage);
		if(isHealSplat)
		{
			setHitpoints(getHitpoints() + damageSplat);
		}
		else
		{
			setHitpoints(getHitpoints() - damageSplat);
			if(myDamage)
			{
				myDamageCount = myDamageCount + damageSplat;
			}
		}
	}

	public void decrementRespawnTimer()
	{
		if(timeUntilRespawn > 0)
		{
			timeUntilRespawn--;
		}
	}

	public void updateLastAnimationSeen(int animation)
	{
		if(reaver == null)
		{
			return;
		}
		lastAnimationSeen = animation;
		if(lastAnimationSeen == 9192)
		{
			setReaverDead();
		}
	}

	public void resetToDefault()
	{
		this.reaver = null;
		this.name = "";
		this.npcId = -1;
		this.hitpoints = -1;
		this.isVenomed = false;
		this.myDamageCount = 0;
		this.currentLocation = null;
		this.isAlive = false;
		lastAnimationSeen = -1;
		timeUntilRespawn = -1;
	}


	public int distanceToKcArea(WorldArea kcArea)
	{
		if(reaver == null || currentLocation == null || !currentLocation.isInScene(client))
		{
			return Integer.MAX_VALUE;
		}
		return reaver.getWorldArea().distanceTo(kcArea);
	}




	public int getNPCHP(NPC npc)
	{
		if(npc.getId() == 11293 && (npc.getHealthRatio() == -1 || npc.getHealthScale() == -1))
		{
			return 125;
		}
		return (int) ((double) npc.getHealthRatio() / (double) npc.getHealthScale()) * 100;
	}

}
