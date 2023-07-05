package com.example.nexatron.manager;


import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.Utility.Static;
import com.example.Utility.WidgetUtil;
import com.example.nexatron.ReflectBreakHandler;
import com.example.nexatron.NexatronConfig;
import com.example.nexatron.NexatronPlugin;
import com.example.nexatron.model.ChargesTracker;
import com.example.nexatron.model.Nex;
import com.example.nexatron.model.Overall;
import com.example.nexatron.model.constants.Stage;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Scene;
import net.runelite.api.Skill;
import net.runelite.api.Tile;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
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
	ItemManager itemManager;
	@Inject
	private ReflectBreakHandler chinBreakHandler;
	private Stage stage = Stage.NONE;

	@Inject
	public NexManager(EventBus eventBus, Client client, NexatronConfig config, NexatronPlugin plugin)
	{
		this.eventBus = eventBus;
		this.client = client;
		this.config = config;
		this.plugin = plugin;
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


	public WorldPoint findClosestTileToPlayer(ArrayList<WorldPoint> possibleTiles)
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		return possibleTiles.stream().min(Comparator.comparingInt(wp -> wp.distanceTo(playerPoint))).stream().findAny().orElse(null);
	}

	public int getBossHp()
	{
		return client.getVarbitValue(Varbits.BOSS_HEALTH_CURRENT);
	}

	public int getBossMaxHp()
	{
		return client.getVarbitValue(Varbits.BOSS_HEALTH_MAXIMUM);
	}

	public boolean isBoosted(Skill skill)
	{
		return client.getBoostedSkillLevel(skill) > client.getRealSkillLevel(skill);
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

	public boolean isAntiVenomed()
	{
		return Static.getClient().getVarpValue(VarPlayer.POISON) < -36;
	}

}
