package com.example.nexatron.manager;


import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.BankInventory;
import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.BankUtil;
import com.example.Utility.Hopping;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Movement;
import com.example.Utility.Prayers;
import com.example.Utility.Static;
import com.example.Utility.WidgetUtil;
import com.example.nexatron.NexatronConfig;
import com.example.nexatron.NexatronPlugin;
import com.example.nexatron.ReflectBreakHandler;
import com.example.nexatron.model.ChargesTracker;
import com.example.nexatron.model.Consumable;
import com.example.nexatron.model.KcArea;
import com.example.nexatron.model.Lobby;
import com.example.nexatron.model.Nex;
import com.example.nexatron.model.NexBank;
import com.example.nexatron.model.Overall;
import com.example.nexatron.model.Socket;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.OptionalMode;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.model.setup.Setup;
import com.google.inject.Singleton;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.Actor;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Hitsplat;
import net.runelite.api.HitsplatID;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Player;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.annotations.HitsplatType;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;

@Singleton
public class NexManager
{
	public final Client client;
	private final EventBus eventBus;
	@Getter
	private final NexatronPlugin plugin;
	@Inject
	public GameTickManager gameTickManager;
	@Inject
	public ChargesTracker chargesTracker;
	@Inject
	public Nex nex;
	@Inject
	public Overall overall;
	@Inject
	public NexBank nexBank;
	@Inject
	public KcArea kcArea;
	@Inject
	public Socket socket;
	@Inject
	public Lobby lobby;
	@Inject
	public Setup setup;
	@Inject
	public Random random = new Random();
	public boolean allowedToBreak = false;
	public NexatronConfig config;
	public boolean shouldReattack;
	@Inject
	public Hopping hopping;
	@Inject
	private ItemManager itemManager;
	@Inject
	private ReflectBreakHandler chinBreakHandler;
	private Stage stage = Stage.NONE;
	public ArrayList<Integer> gearSetup = new ArrayList<>();
	public ArrayList<Integer> switchesLeft = new ArrayList<>();
	public int totalClientTicks = 0;
	public int clientTick = 0;

	public int totalDamageTaken = 0;
	public int phaseDamageTaken = 0;

	public NPC reaverTest = null;

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
		clientTick = 0;
		totalClientTicks = 0;
		stage = Stage.NONE;
		shouldReattack = false;
		switchesLeft = new ArrayList<>();
		gearSetup = new ArrayList<>();
		totalDamageTaken = 0;
		phaseDamageTaken = 0;
		reaverTest = null;
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
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", clientTick + ": " + msg, "");
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		Actor a = event.getActor();
		if(!(a instanceof Player))
		{
			return;
		}
		Player p = (Player) a;
		if(!p.equals(client.getLocalPlayer()))
		{
			return;
		}
		Hitsplat splat = event.getHitsplat();
		int splatId = splat.getHitsplatType();
		int damageAmount = splat.getAmount();
		//Damage taken
		if(splatId == 16)
		{
			totalDamageTaken += damageAmount;
			phaseDamageTaken += damageAmount;
		}
//		System.out.println("Is mine? " + splat.isMine());
//		System.out.println("Amount: " + splat.getAmount());
//		System.out.println("Hitsplat Id -> " + splatId);
	}


	@Subscribe
	public void onClientTick(ClientTick event)
	{
		totalClientTicks++;
		clientTick++;
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		clientTick = 0;
	}

	public boolean hasGearEquipped(ArrayList<Integer> gearList)
	{
		for (int i : gearList)
		{
			// If not equipped, but it's in our inventory, return false
			if (Equipment.search().withId(i).first().orElse(null) == null
				&& Inventory.search().withId(i).first().orElse(null) != null)
			{
				return false;
			}
		}
		return true;
	}

	public boolean hasEquipped(int itemId)
	{
		return Equipment.search().withId(itemId).first().orElse(null) != null
			|| Inventory.search().withId(itemId).first().orElse(null) == null;
	}

	public String worldPointString(WorldPoint wp)
	{
		return "(X: " + wp.getX() + ", Y: " + wp.getY() + ") ";
	}

	public boolean isDeathChargeOffCD()
	{
		return client.getVarbitValue(Varbits.DEATH_CHARGE_COOLDOWN) == 0;
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

	public WorldPoint findClosestTileToWorldPoint(ArrayList<WorldPoint> possibleTiles, WorldPoint worldPoint)
	{
		return possibleTiles.stream().min(Comparator.comparingInt(wp -> wp.distanceTo(worldPoint))).stream().findAny().orElse(null);
	}

	public boolean shouldKc()
	{
		if (config.kcMode().equals(OptionalMode.Yes))
		{
			return true;
		}
		if (config.kcMode().equals(OptionalMode.No))
		{
			return false;
		}
		return socket.needKc || socket.otherNeedKc;
	}

	public boolean isDDd()
	{
		Player otherPlayer = socket.getOtherPlayer();
		if (otherPlayer == null)
		{
			return false;
		}
		return otherPlayer.getWorldLocation().equals(getPlayerPoint());
	}

	public NPC findClosestNPC(ArrayList<NPC> npcs)
	{
		if (npcs.isEmpty())
		{
			return null;
		}
		int distance = Integer.MAX_VALUE;
		WorldPoint playerLoc = getPlayerPoint();
		NPC returnNPC = npcs.get(0);
		for (NPC npc : npcs)
		{
			WorldPoint wp = npc.getWorldLocation();
			if (wp.distanceTo(playerLoc) <= distance)
			{
				returnNPC = npc;
				distance = wp.distanceTo(playerLoc);
			}
		}
		return returnNPC;
	}

	public WorldPoint findActualClosest(ArrayList<WorldPoint> worldPoints)
	{
		int shortestDistance = Integer.MAX_VALUE;
		WorldPoint shortestPathPoint = null;
		for (WorldPoint worldPoint : worldPoints)
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

	public int enableRun(boolean enable)
	{
		if (enable)
		{
			if (!Movement.isRunEnabled() && Movement.getRunEnergy() >= 1)
			{
				Movement.toggleRun();
				return 1;
			}
		}
		else
		{
			if (Movement.isRunEnabled())
			{
				Movement.toggleRun();
				return 1;
			}
		}
		return 0;
	}

	public boolean containsStage(Stage... stages)
	{
		for (Stage stage : stages)
		{
			if (getStage().equals(stage))
			{
				return true;
			}
		}
		return false;
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

	public boolean isPrePotted()
	{
		return isBoosted(Skill.STRENGTH)
			&& isBoosted(Skill.RANGED)
			&& isBoosted(Skill.HITPOINTS)
			&& isAntiPoisoned()
			&& Movement.isStaminaBoosted();
	}

	public boolean useThralls()
	{
		if (config.thralls().equals(OptionalMode.Yes))
		{
			return true;
		}
		return config.thralls().equals(OptionalMode.Auto)
			&& client.getVarbitValue(NexConst.SPELLBOOK_VARB) == 3
			&& Quest.A_KINGDOM_DIVIDED.getState(client).equals(QuestState.FINISHED);
	}

	public int getOverheadIcon(NPC n)
	{
		NPCComposition comp = n.getComposition();
		for (Field f : n.getComposition().getClass().getDeclaredFields())
		{
			if (f.getType().toString().contains("[S"))
			{
				try
				{
					f.setAccessible(true);
					short[] temp = (short[]) f.get(comp);
					if (temp != null)
					{
						for (short s : temp)
						{
							//System.out.println("Old Overhead value -> " + s);
							return s;
						}
					}
				}
				catch (IllegalAccessException e)
				{
					//intentionally ignored
				}
			}
		}
		return -1;
	}

	public int getAncientKc()
	{
		return client.getVarbitValue(NexConst.ANCIENT_KILLCOUNT_VARBIT);
	}

	public boolean hasAllItems(ArrayList<Integer> items)
	{
		items.removeIf(n -> n == 0 || n == -1);
		ArrayList<Integer> playerItems = InventoryUtil.getAllPlayerItems();
		for (int i : items)
		{
			if (!playerItems.contains(i))
			{
				return false;
			}
		}
		return true;
	}

	public int bank(ArrayList<Widget> items)
	{
		if (!BankUtil.isOpen())
		{
			return 0;
		}
		int swaps = (int) (3 + (Math.abs(random.nextGaussian() * 1.5)));
		int counter = 0;
		for (Widget item : items)
		{
			if (counter == swaps)
			{
				return counter;
			}
			print("Banking " + itemManager.getItemComposition(item.getItemId()).getName());
			BankUtil.depositAll(item.getItemId());
			counter++;
			return counter;
		}
		return counter;
	}

	public boolean isAntiPoisoned()
	{
		return Static.getClient().getVarpValue(VarPlayer.POISON) < -35;
	}

	public int swap(List<Integer> gearList)
	{
		int swaps = (int) (3 + (Math.abs(random.nextGaussian() * 1.5)));
		int counter = 0;
		// Equip weapon first
		for (int i : NexConst.WEAPONS)
		{
			if (gearList.contains(i))
			{
				Widget item = Inventory.search().withId(i).first().orElse(null);
				if (item != null)
				{
					int slot = 0;
					ItemContainer invent = client.getItemContainer(InventoryID.INVENTORY.getId());
					if (invent != null)
					{
						for (int j = 0; j < 28; j++)
						{
							Item inventoryItem = invent.getItem(j);
							if (inventoryItem == null)
							{
								continue;
							}
							//System.out.println("Item id -> " + item.getItemId());
							if (inventoryItem.getId() == item.getItemId())
							{
								//System.out.println("Item found at slot -> " + j);
								slot = j;
								break;
							}
						}
					}

					if (WidgetUtil.hasAction(item, "Wield"))
					{
						if (Bank.isOpen())
						{
							MousePackets.queueClickPacket();
							WidgetPackets.queueWidgetActionPacket(9, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId(), item.getItemId(), slot);
						}
						else
						{
							MousePackets.queueClickPacket();
							WidgetPackets.queueWidgetAction(item, "Wield");
						}
						counter++;
						gearList.remove((Integer) i);
					}
					else if (WidgetUtil.hasAction(item, "Wear"))
					{
						if (Bank.isOpen())
						{
							MousePackets.queueClickPacket();
							WidgetPackets.queueWidgetActionPacket(9, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId(), item.getItemId(), slot);
						}
						else
						{
							MousePackets.queueClickPacket();
							WidgetPackets.queueWidgetAction(item, "Wear");
						}
						counter++;
						gearList.remove((Integer) i);
					}
				}
			}
		}

		int[] gear = gearList.stream().mapToInt(i -> i).toArray();
		List<Integer> gearAsList = Arrays.stream(gear).boxed().collect(Collectors.toList());
		if (BankUtil.isOpen())
		{
			for (Widget item : BankInventory.search().idInList(gearAsList).result())
			{
				if (counter == swaps)
				{
					return counter;
				}
				int slot = 0;
				ItemContainer invent = client.getItemContainer(InventoryID.INVENTORY.getId());
				if (invent != null)
				{
					for (int j = 0; j < 28; j++)
					{
						Item inventoryItem = invent.getItem(j);
						if (inventoryItem == null)
						{
							//System.out.println("Inventory item is null somehow?");
							continue;
						}
						//System.out.println("Item id -> " + item.getItemId());
						if (inventoryItem.getId() == item.getItemId())
						{
							//System.out.println("Item found at slot -> " + j);
							slot = j;
							break;
						}
					}
				}
				if (Bank.isOpen())
				{
					MousePackets.queueClickPacket();
					WidgetPackets.queueWidgetActionPacket(9, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId(), item.getItemId(), slot);
					counter++;
				}
				else
				{
					if (WidgetUtil.hasAction(item, "Wield"))
					{
						MousePackets.queueClickPacket();
						WidgetPackets.queueWidgetAction(item, "Wield");
						counter++;
					}
					else if (WidgetUtil.hasAction(item, "Wear"))
					{
						MousePackets.queueClickPacket();
						WidgetPackets.queueWidgetAction(item, "Wear");
						counter++;
					}
				}
			}
		}
		else
		{
			for (Widget item : Inventory.search().idInList(gearAsList).result())
			{
				if (counter == swaps)
				{
					return counter;
				}
				int slot = 0;
				ItemContainer invent = client.getItemContainer(InventoryID.INVENTORY.getId());
				if (invent != null)
				{
					for (int j = 0; j < 28; j++)
					{
						Item inventoryItem = invent.getItem(j);
						if (inventoryItem == null)
						{
							//System.out.println("Inventory item is null somehow?");
							continue;
						}
						//System.out.println("Item id -> " + item.getItemId());
						if (inventoryItem.getId() == item.getItemId())
						{
							//System.out.println("Item found at slot -> " + j);
							slot = j;
							break;
						}
					}
				}
				if (Bank.isOpen())
				{
					MousePackets.queueClickPacket();
					WidgetPackets.queueWidgetActionPacket(9, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getPackedId(), item.getItemId(), slot);
					counter++;
				}
				else
				{
					if (WidgetUtil.hasAction(item, "Wield"))
					{
						MousePackets.queueClickPacket();
						WidgetPackets.queueWidgetAction(item, "Wield");
						counter++;
					}
					else if (WidgetUtil.hasAction(item, "Wear"))
					{
						MousePackets.queueClickPacket();
						WidgetPackets.queueWidgetAction(item, "Wear");
						counter++;
					}
				}
			}
		}
		return counter;
	}

	public boolean shouldFlick()
	{
		if (config.prayFlick().equals(OptionalMode.Yes))
		{
			return true;
		}
		if (config.prayFlick().equals(OptionalMode.No))
		{
			return false;
		}
		// Auto
		int restoreDoses = Consumable.restoreDoseCount();
		if (restoreDoses <= 0)
		{
			return true;
		}
		if (!switchesLeft.isEmpty())
		{
			return false;
		}
		if (containsStage(Stage.NEX_ZAROS, Stage.MINION_ICE)
			&& restoreDoses <= 6)
		{
			return true;
		}
		if (EthanApiPlugin.isMoving())
		{
			return false;
		}
		if (nex.isUnderNex(client.getLocalPlayer()))
		{
			return false;
		}
		if (nex.brewSipsNeeded > 0
			&& Prayers.getMissingPoints() < 30)
		{
			return false;
		}
		return switchesLeft.isEmpty();
	}

	public boolean targetIsNex(NPC target)
	{
		return Objects.requireNonNull(target.getName()).toLowerCase().contains("nex");
	}

	public NPC bloodNexDecideTarget()
	{
		if (nex.nex == null)
		{
			return null;
		}
		if (!nex.reavers.isEmpty())
		{
			// If we are the master, we only want to hit reavers until they are about half hp,
			// slave hits anything above 10%
			int threshHold = socket.isMaster ? 50 : 20;
			ArrayList<NPC> targets = new ArrayList<>();
			for (NPC reaver : nex.reavers.keySet())
			{
				if (nex.reavers.get(reaver) >= threshHold)
				{
					targets.add(reaver);
				}
			}
			if (!targets.isEmpty())
			{
				return findClosestNPC(targets);
			}
		}
		return nex.nex;
	}

	public ArrayList<Widget> getJunk()
	{
		ArrayList<Integer> requiredItems = nexBank.requiredItems();
		ArrayList<Widget> unNecessaryItems = (ArrayList<Widget>) Inventory.search().result();
		unNecessaryItems.removeIf(n -> requiredItems.contains(n.getItemId()));
		if (shouldKc())
		{
			unNecessaryItems.removeIf(n -> Consumable.getNecessaryKcPotions.contains(n.getItemId()));
			return unNecessaryItems;
		}
		if (Consumable.isDrained(Skill.HITPOINTS))
		{
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumable.PREPOT_ANGLER);
		}
		if (Consumable.isDrainedMore(Skill.PRAYER, 5))
		{
			unNecessaryItems.removeIf(n -> n.getItemId() == ItemID.SUPER_RESTORE1);

		}
		if (!isPrePotted())
		{
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumable.PREPOT_ANGLER);
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumable.PREPOT_SCB);
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumable.PREPOT_RANGE);
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumable.PREPOT_STAM);
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumable.PREPOT_ANTI);
		}
		else
		{
			unNecessaryItems.removeIf(n -> Consumable.getNecessaryPotions.contains(n.getItemId()));
		}
		return unNecessaryItems;
	}

	public int withdraw(ArrayList<Integer> items)
	{
		int swaps = (int) (3 + (Math.abs(random.nextGaussian() * 1.5)));
		int counter = 0;
		for (int item : items)
		{
			if (counter == swaps)
			{
				break;
			}
			if (!BankUtil.contains(item))
			{
				continue;
			}
			if (item == ItemID.RUBY_DRAGON_BOLTS_E || item == ItemID.DIAMOND_DRAGON_BOLTS_E)
			{
				print("Withdrawing all" + itemManager.getItemComposition(item).getName());
				BankUtil.withdrawAll(item);
			}
			else
			{
				print("Withdrawing " + itemManager.getItemComposition(item).getName());
				BankUtil.withdrawOne(item);
			}
			counter++;
			return 1;
		}
		return counter;
	}
}
