package com.example.nexatron.manager;


import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Static;
import com.example.nexatron.NexatronConfig;
import com.example.nexatron.NexatronPlugin;
import com.example.nexatron.ReflectBreakHandler;
import com.example.nexatron.model.ChargesTracker;
import com.example.nexatron.model.Nex;
import com.example.nexatron.model.Overall;
import com.example.nexatron.model.Setup;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.Stage;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;

@Singleton
public class NexManager
{
	public final Client client;
	private final EventBus eventBus;
	private final NexatronPlugin plugin;
	@Inject
	public Overall overall;
	@Inject
	public GameTickManager gameTickManager;
	@Inject
	public Random random = new Random();
	public boolean allowedToBreak = false;
	public NexatronConfig config;
	@Inject
	public ChargesTracker chargesTracker;
	@Inject
	public Nex nex;
	@Inject
	public Setup setup;
	@Inject
	ItemManager itemManager;
	@Inject
	private ReflectBreakHandler chinBreakHandler;
	private Stage stage = Stage.NONE;
	public boolean shouldReattack;

	@Inject
	public NexManager(EventBus eventBus, Client client, NexatronConfig config, NexatronPlugin plugin)
	{
		this.eventBus = eventBus;
		this.client = client;
		this.config = config;
		this.plugin = plugin;
	}

	public void fullReset()
	{
		stage = Stage.NONE;
		shouldReattack = false;
	}

	public boolean needsBreak()
	{
		return chinBreakHandler.shouldBreak(plugin);
	}

	public boolean onBreak()
	{
		return chinBreakHandler.isBreakActive(plugin);
	}

	public NexatronConfig getConfig()
	{
		return this.config;
	}

	public void register()
	{
		this.eventBus.register(this);
	}

	public void unregister()
	{
		this.eventBus.unregister(this);
	}

	public Stage getStage()
	{
		return this.stage;
	}

	public void setStage(Stage stage)
	{
		this.stage = stage;
	}

	public void print(String msg)
	{
		if (config.debug() && client.isClientThread())
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", msg, "");
		}
	}

	public boolean hasEquipped(int itemId)
	{
		return Equipment.search().withId(itemId).first().orElse(null) != null;
	}

	public String worldPointString(WorldPoint wp)
	{
		return "(X: " + wp.getX() + ", Y: " + wp.getY() + ") ";
	}

	public boolean isDeathChargeOffCD()
	{
		return client.getVarbitValue(Varbits.DEATH_CHARGE_COOLDOWN) == 0;
	}

	public boolean isThrallOffCD()
	{
		return client.getVarbitValue(Varbits.RESURRECT_THRALL) == 0;
	}

	public void castThrall()
	{
		MousePackets.queueClickPacket();
		WidgetPackets.queueWidgetAction(client.getWidget(WidgetInfoExtended.SPELL_RESURRECT_GREATER_GHOST.getPackedId()), "Cast");
	}

	public void castDeathCharge()
	{
		MousePackets.queueClickPacket();
		WidgetPackets.queueWidgetAction(client.getWidget(WidgetInfoExtended.SPELL_DEATH_CHARGE.getPackedId()), "Cast");
	}

	public WorldPoint findClosestTileToPlayer(ArrayList<WorldPoint> possibleTiles)
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		return possibleTiles.stream().min(Comparator.comparingInt(wp -> wp.distanceTo(playerPoint))).stream().findAny().orElse(null);
	}

	public int getBossHp()
	{
		return client.getVarbitValue(Varbits.BOSS_HEALTH_CURRENT);
	}

	public boolean isBoosted(Skill skill)
	{
		return client.getBoostedSkillLevel(skill) > client.getRealSkillLevel(skill);
	}

	public WorldPoint getPlayerPoint()
	{
		return client.getLocalPlayer().getWorldLocation();
	}

	public NPC playerInteractingWith()
	{
		Player p = client.getLocalPlayer();
		if (p.getInteracting() == null)
		{
			return null;
		}
		if (p.getInteracting() instanceof NPC)
		{
			return (NPC) p.getInteracting();
		}
		return null;
	}

	public void reattackInteracting()
	{
		NPC interactingNPC = playerInteractingWith();
		if (interactingNPC != null)
		{
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(interactingNPC, "Attack");
		}
	}

	public int getAncientKc()
	{
		return client.getVarbitValue(NexConst.ANCIENT_KILLCOUNT_VARBIT);
	}

	public boolean isAntiVenomed()
	{
		return Static.getClient().getVarpValue(VarPlayer.POISON) < -36;
	}

}
