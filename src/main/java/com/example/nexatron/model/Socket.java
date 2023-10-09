package com.example.nexatron.model;

import com.example.EthanApiPlugin.Collections.Players;
import com.example.Utility.Reachable;
import com.example.nexatron.manager.NexManager;
import com.example.socket.org.json.JSONObject;
import com.example.socket.packet.SocketBroadcastPacket;
import com.example.socket.packet.SocketReceivePacket;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.Varbits;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

public class Socket
{
	// Local variables
	public boolean isMaster;
	public int world = 0;
	public boolean needToKc;
	public boolean needsToBreak;
	public boolean stopPlugin;
	public boolean teleportOut;
	public boolean readyToStart;

	// Other account/Socket variables
	public String otherName = "";
	public boolean otherHardDiary;
	public int otherNeedToKc;
	public boolean otherReadyToStart;
	public boolean otherIsInside;
	public int otherWorld;
	@Inject
	NexManager nexManager;
	@Inject
	EventBus eventBus;
	@Inject
	Client client;

	public void register()
	{
		this.eventBus.register(this);
	}

	public void unregister()
	{
		this.eventBus.unregister(this);
	}

	public void reset()
	{
		isMaster = false;
		world = 0;
		otherName = "";
		otherHardDiary = false;
		needsToBreak = false;
		teleportOut = false;
		readyToStart = false;
		otherReadyToStart = false;
		otherIsInside = false;
	}

	@Subscribe(priority = -1)
	public void onGameTick(GameTick gameTick)
	{
		isMaster = decideMaster();
		sendSocketPacket();
		world = nexManager.hopping.getCurrentWorldNumber();
	}

	public boolean inRightWorld()
	{
		return true;
	}

	public void sendSocketPacket()
	{
		JSONObject payload = new JSONObject();
		payload.put("overall-packet", "This is but a test.");
		payload.put("name", client.getLocalPlayer().getName());
		payload.put("hard", client.getVarbitValue(Varbits.COMBAT_ACHIEVEMENT_TIER_HARD) == 2);
		payload.put("world", world);
		payload.put("readyToStart", readyToStart);
		payload.put("isInside", nexManager.nex.nex != null || isCenterReachable());
		payload.put("teleport", teleportOut);
		eventBus.post(new SocketBroadcastPacket(payload));
	}

	public boolean isCenterReachable()
	{
		return Reachable.isWalkable(nexManager.nex.centerPoint);
	}

	@Subscribe
	public void onSocketReceivePacket(SocketReceivePacket event)
	{
		JSONObject payload = event.getPayload();
		if (!payload.has("overall-packet"))
		{
			System.out.println("Returning false, the packet does not contain our desired key, this is not for us.");
			return;
		}
		if (payload.getString("name").equals(client.getLocalPlayer().getName()))
		{
			return;
		}
		otherIsInside = payload.getBoolean("isInside");
		otherReadyToStart = payload.getBoolean("readyToStart");
		otherName = payload.getString("name");
		otherHardDiary = payload.getBoolean("hard");
		otherWorld = payload.getInt("world");
	}

	public boolean needToKc()
	{
		return nexManager.getAncientKc() < 100;
	}

	public boolean isSlave()
	{
		return !isMaster;
	}

	public boolean decideMaster()
	{
		// We have hard diary and the other account does not, which means we are the master
		if (client.getVarbitValue(Varbits.COMBAT_ACHIEVEMENT_TIER_HARD) == 2
			&& !otherHardDiary)
		{
			return true;
		}

		// If both accounts have hard diary completed
		// compare them alphabetically, the name that comes first is the master
		if (client.getVarbitValue(Varbits.COMBAT_ACHIEVEMENT_TIER_HARD) == 2
			&& otherHardDiary)
		{
			String ourName = client.getLocalPlayer().getName();
			return ourName != null
				&& ourName.compareTo(otherName) < 0;
		}

		// "Else" case, where we are not the master
		// because we don't have hard diary completed
		return false;
	}

	public Player getOtherPlayer()
	{
		return Players.search().withName(nexManager.socket.otherName).first().orElse(null);
	}

}
