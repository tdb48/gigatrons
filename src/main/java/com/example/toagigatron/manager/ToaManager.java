package com.example.toagigatron.manager;

import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.toagigatron.ReflectBreakHandler;
import com.example.toagigatron.ToaGigatronConfig;
import com.example.toagigatron.ToaGigatronPlugin;
import com.example.toagigatron.model.Overall;
import com.example.toagigatron.model.Zebak;
import com.example.toagigatron.model.bossmodel.ZebakJug;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.setup.MageSetup;
import com.example.toagigatron.model.setup.MeleeSetup;
import com.example.toagigatron.model.setup.RangeSetup;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Prayer;
import net.runelite.api.Projectile;
import net.runelite.api.Scene;
import net.runelite.api.Skill;
import net.runelite.api.Tile;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;

@Singleton
public class ToaManager
{

	@Inject
	public Overall overall;
	@Inject
	ItemManager itemManager;
	@Inject
	public GameTickManager gameTickManager;
	@Inject
	public Random random = new Random();
	@Inject
	private ReflectBreakHandler chinBreakHandler;

	private Stage stage = Stage.NONE;
	private final EventBus eventBus;
	public final Client client;
	public boolean allowedToBreak = false;
	private final ToaGigatronPlugin plugin;
	public ToaGigatronConfig config;
	@Inject
	public MageSetup mageSetup;
	@Inject
	public RangeSetup rangeSetup;
	@Inject
	public MeleeSetup meleeSetup;
	@Inject
	public Zebak zebak;


	@Inject
	public ToaManager(EventBus eventBus, Client client, ToaGigatronConfig config, ToaGigatronPlugin plugin)
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

	public ToaGigatronConfig getConfig()
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

	public LocalPoint findClosestTile(ArrayList<LocalPoint> possibleTiles, LocalPoint targetPoint)
	{
		return possibleTiles.stream().min(Comparator.comparingInt(wp -> wp.distanceTo(targetPoint))).stream().findAny().orElse(null);
	}

	public WorldPoint findClosestTile(ArrayList<WorldPoint> possibleTiles, WorldPoint targetPoint)
	{
		return possibleTiles.stream().min(Comparator.comparingInt(wp -> wp.distanceTo(targetPoint))).stream().findAny().orElse(null);
	}

	public ArrayList<WorldPoint> lpToWp(ArrayList<LocalPoint> lps)
	{
		ArrayList<WorldPoint> returnList = new ArrayList<>();
		for (LocalPoint lp : lps)
		{
			returnList.add(WorldPoint.fromLocal(client, lp));
		}
		return returnList;
	}

	public void initialiseSetups()
	{
		mageSetup.setVariables();
		rangeSetup.setVariables();
		meleeSetup.setVariables();
	}

	public boolean hasGearEquipped(ArrayList<Integer> gearList)
	{
		for (int i : gearList)
		{
			if (Equipment.search().withId(i).first().orElse(null) == null
				&& Inventory.search().withId(i).first().orElse(null) != null)
			{
				return false;
			}
		}
		return true;
	}

	public static boolean isMissingAnyItems(ArrayList<Integer> items)
	{
		ArrayList<Widget> playerItems = (ArrayList<Widget>) Inventory.search().result();
		playerItems.addAll(Equipment.search().result());
		ArrayList<Integer> returnList = itemsToIntegers(playerItems);
		for (int i : items)
		{
			if (!returnList.contains(i))
			{
				return false;
			}
		}
		return true;
	}

	public ZebakJug findClosestNPC(ArrayList<ZebakJug> jugs)
	{
		ZebakJug returnObj = null;
		int distance = Integer.MAX_VALUE;
		LocalPoint playerLoc = client.getLocalPlayer().getLocalLocation();
		for (ZebakJug jug : jugs)
		{
			LocalPoint lp = jug.jugTile;
			if (lp.distanceTo(playerLoc) <= distance)
			{
				returnObj = jug;
				distance = lp.distanceTo(playerLoc);
			}
		}
		return returnObj;
	}

	public static ArrayList<Integer> itemsToIntegers(ArrayList<Widget> items)
	{
		ArrayList<Integer> returnList = new ArrayList<>();
		for (Widget widget : items)
		{
			returnList.add(widget.getItemId());
		}
		return returnList;
	}

	public int getRoomLevel()
	{
		Widget roomLevel = client.getWidget(481, 45);
		if (roomLevel == null || roomLevel.isHidden())
		{
			return -1;
		}
		return Integer.parseInt(roomLevel.getText());
	}



	public int getBossHp()
	{
		return client.getVarbitValue(Varbits.BOSS_HEALTH_CURRENT);
	}

	public int getBossMaxHp()
	{
		return client.getVarbitValue(Varbits.BOSS_HEALTH_MAXIMUM);
	}

}
