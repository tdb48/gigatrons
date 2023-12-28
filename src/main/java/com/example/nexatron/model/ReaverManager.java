package com.example.nexatron.model;

import com.example.EthanApiPlugin.Collections.Equipment;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Hitsplat;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.GameEventManager;

public class ReaverManager
{

	@Inject
	private EventBus eventBus;
	@Inject
	private Client client;
	@Inject
	private GameEventManager gameEventManager;

	public ConcurrentHashMap<Integer, Reaver> reavers = new ConcurrentHashMap<>();

	public WorldArea centralArea = new WorldArea(new WorldPoint(2875, 5208, 0), 8, 8);
	public HashSet<Integer> centralNpcIndexs = new HashSet<>(Set.of(15137,15135,15142,14260,14245,14258,14242,14246,14250));
	public WorldArea southWestArea = new WorldArea(new WorldPoint(2859, 5205, 0), 8, 6);
	public HashSet<Integer> southWestNpcIndexs = new HashSet<>(Set.of(2214,2218,2222,2217,2232,2228,2230));

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{

		Actor a = event.getActor();
		if(!(a instanceof NPC))
		{
			return;
		}
		NPC n = (NPC) a;
		if(n.getId() != 11293)
		{
			return;
		}
		//This checks if the animation is the dying animation and sets the reaver to dead if so.
		int index = n.getIndex();
		reavers.get(index).updateLastAnimationSeen(event.getActor().getAnimation());
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		for(Reaver reaver : reavers.values())
		{
			if(reaver == null)
			{
				continue;
			}
			reaver.decrementRespawnTimer();
			if(reaver.getReaver() == null || !reaver.getReaver().getWorldLocation().isInScene(client))
			{
				continue;
			}
			reaver.setCurrentLocation(reaver.getReaver().getWorldLocation());

		}
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		NPC n = event.getNpc();
		int id = n.getId();
		int index = n.getIndex();
		if (id == 11293)
		{
//			System.out.println("Reaver spawned!");
//			System.out.println("Index: " + index);
//			System.out.println("World Loc: " + n.getWorldLocation());
			reavers.get(index).updateReaver(n);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		NPC n = event.getNpc();
		int id = n.getId();
		int index = n.getIndex();
		if (id == 11293)
		{
			reavers.get(index).setReaverDespawned();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		///This might work or it might not i dont know.
		GameState state = event.getGameState();
		if (state.equals(GameState.HOPPING) || state.equals(GameState.CONNECTION_LOST) || state.equals(GameState.LOGGING_IN))
		{
			if(reavers.size() > 0)
			{
				for(Reaver reaver : reavers.values())
				{
					reaver.resetToDefault();
				}
			}
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		Actor a = event.getActor();
		Hitsplat hitsplat = event.getHitsplat();
		if(!(a instanceof NPC))
		{
			return;
		}
		NPC n = (NPC) a;
		if(n.getId() != 11293)
		{
			return;
		}
		//System.out.println("Hitsplat type: " + hitsplat.getHitsplatType() + " Amount: " + hitsplat.getAmount());
		//If we are here it means this is a reaver.
		int index = n.getIndex();
		int damage = event.getHitsplat().getAmount();
		boolean myDamage = event.getHitsplat().isMine();
		int hitsplatType = event.getHitsplat().getHitsplatType();
		Reaver r = reavers.get(index);
		if(r == null)
		{
			//System.out.println("Reaver class object not found in reavers map in hitsplat event.");
			return;
		}
		if(hitsplatType == 5 || ((hitsplatType == 16 || hitsplatType == 43) && hasBlowpipe() && hasSerp()))
		{
			reavers.get(index).setVenomed(true);
		}
		reavers.get(index).updateHitpoints(damage, myDamage, (hitsplatType == 6));
		//Damage and max damage hit damage and zero damage (from me)
//		if(hitsplatType == 16 || hitsplatType == 43 || hitsplatType == 12)
//		{
//			System.out.println("Id: " + hitsplatType + " Is mine? " + hitsplat.isMine());
//		}
//		//Venom tick
//		else if(hitsplatType == 5)
//		{
//			System.out.println("Id: " + hitsplatType + " Is mine? " + hitsplat.isMine());
//		}
//		//heal
//		else if(hitsplatType == 6)
//		{
//			System.out.println("Id: " + hitsplatType + " Is mine? " + hitsplat.isMine());
//		}
//		//Some other player normal,max or zero hitsplat
//		else if(hitsplatType == 17 || hitsplatType == 13)
//		{
//
//		}
//		else{
//			System.out.println("ELSE HITSPLAT, Id: " + hitsplatType + " Is mine? " + hitsplat.isMine());
//		}
//		System.out.println();
//		System.out.println();
//		System.out.println("Reaver hp before: " + r.getHitpoints());
//		System.out.println("Damage inflicted: " + damage);
//		System.out.println("My damage count on this reaver: " + r.getMyDamageCount());

//		System.out.println("Reaver hp after: " + r.getHitpoints());
//		System.out.println("My updated damage count on this reaver: " + r.getMyDamageCount());

	}

	public NPC getHittableInteractingReaver()
	{
		int distance = Integer.MAX_VALUE;
		NPC returnReaver = null;
		for(Reaver reaver : reavers.values())
		{
			NPC n = reaver.getReaver();
			if(n == null)
			{
				continue;
			}
			if(!n.isInteracting())
			{
				continue;
			}
			if(n.getInteracting() != null && client.getLocalPlayer() != null && n.getInteracting().equals(client.getLocalPlayer()))
			{
				if(n.getWorldArea().distanceTo(client.getLocalPlayer().getWorldLocation()) <= 5)
				{
					//Return this straight away, we can hit it without moving
					return n;
				}
				if (n.getWorldArea().distanceTo(client.getLocalPlayer().getWorldArea()) < distance)
				{
					distance = n.getWorldArea().distanceTo(client.getLocalPlayer().getWorldArea());
					returnReaver = n;
				}

			}
		}
		return returnReaver;
	}

	public boolean hasAtleastOneReaverAgrod()
	{
		for(Reaver reaver : reavers.values())
		{
			NPC n = reaver.getReaver();
			if(n == null)
			{
				continue;
			}
			if(!n.isInteracting())
			{
				continue;
			}
			if(n.getInteracting() != null && client.getLocalPlayer() != null && n.getInteracting().equals(client.getLocalPlayer()))
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasSerp()
	{
		return Equipment.search().idInList(List.of(ItemID.SERPENTINE_HELM, ItemID.MAGMA_HELM, ItemID.TANZANITE_HELM)).first().orElse(null) != null;
	}
	public boolean hasBlowpipe()
	{
		return Equipment.search().withId(ItemID.TOXIC_BLOWPIPE).first().orElse(null) != null;
	}



	public void reset()
	{
		reavers.clear();
	}

	public void register()
	{
		reset();
		initialiseReavers();
		this.eventBus.register(this);
		gameEventManager.simulateGameEvents(this);

	}

	public void unregister()
	{
		reset();
		this.eventBus.unregister(this);
	}

	public void initialiseReavers()
	{
		reavers.put(15137,new Reaver(15137,findSpawnLocation(15137)));
		reavers.put(15135,new Reaver(15135,findSpawnLocation(15135)));
		reavers.put(15142,new Reaver(15142,findSpawnLocation(15142)));
		reavers.put(15134,new Reaver(15134,findSpawnLocation(15134)));
		reavers.put(14262,new Reaver(14262,findSpawnLocation(14262)));
		reavers.put(14256,new Reaver(14256,findSpawnLocation(14256)));
		reavers.put(14260,new Reaver(14260,findSpawnLocation(14260)));
		reavers.put(14258,new Reaver(14258,findSpawnLocation(14258)));
		reavers.put(14245,new Reaver(14245,findSpawnLocation(14245)));
		reavers.put(14246,new Reaver(14246,findSpawnLocation(14246)));
		reavers.put(14242,new Reaver(14242,findSpawnLocation(14242)));
		reavers.put(14250,new Reaver(14250,findSpawnLocation(14250)));
	}

	public WorldPoint findSpawnLocation(int reaverIndex)
	{
		int x = 0;
		int y = 0;
		switch (reaverIndex)
		{
			case 14256:
				x = 2870;
				y = 5222;
				break;
			case 14260:
				x = 2875;
				y = 5214;
				break;
			case 14258:
				x = 2873;
				y = 5208;
				break;
			case 14245:
				x = 2858;
				y = 5208;
				break;
			case 14242:
				x = 2854;
				y = 5206;
				break;
			case 14246:
				x = 2859;
				y = 5200;
				break;
			case 14250:
				x = 2864;
				y = 5203;
				break;
			case 15135:
				x = 2885;
				y = 5208;
				break;
			case 14262:
				x = 2878;
				y = 5201;
				break;
			case 15137:
				x = 2887;
				y = 5215;
				break;
			case 15134:
				x = 2885;
				y = 5197;
				break;
			case 15142:
				x = 2895;
				y = 5207;
			default:
				break;
		}
		return new WorldPoint(x, y, 0);
	}
}
