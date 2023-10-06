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
import com.example.Utility.InventoryUtil;
import com.example.Utility.Movement;
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
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.model.setup.Setup;
import com.google.inject.Singleton;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
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
	private ItemManager itemManager;
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
	private ReflectBreakHandler chinBreakHandler;
	@Inject
	public Random random = new Random();
	private Stage stage = Stage.NONE;

	public boolean allowedToBreak = false;
	public NexatronConfig config;
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

	public void enableRun(boolean enable)
	{
		if (enable)
		{
			if (!Movement.isRunEnabled() && Movement.getRunEnergy() >= 1)
			{
				Movement.toggleRun();
			}
		}
		else
		{
			if (Movement.isRunEnabled())
			{
				Movement.toggleRun();
			}
		}
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

	public void sendIntValue(int amount)
	{
		EthanApiPlugin.getClient().setVarcStrValue(359, Integer.toString(amount));
		EthanApiPlugin.getClient().setVarcIntValue(5, 7);
		EthanApiPlugin.getClient().runScript(681);
	}

	public void sendStringValue(String value)
	{
		EthanApiPlugin.getClient().setVarcStrValue(359, value);
		EthanApiPlugin.getClient().setVarcIntValue(5, 7);
		EthanApiPlugin.getClient().runScript(681);
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
				System.out.println("Missing " + itemManager.getItemComposition(i).getName());
				return false;
			}
		}
		return true;
	}

	public void bank(ArrayList<Widget> items)
	{
		if (!BankUtil.isOpen())
		{
			return;
		}
		int swaps = (int) (3 + (Math.abs(random.nextGaussian() * 1.5)));
		int counter = 0;
		for (Widget item : items)
		{
			if (counter == swaps)
			{
				return;
			}
			print("Banking " + itemManager.getItemComposition(item.getItemId()).getName());
			BankUtil.depositAll(item.getItemId());
			counter++;
		}
	}

	public boolean isAntiPoisoned()
	{
		return Static.getClient().getVarpValue(VarPlayer.POISON) < -35;
	}

	public void swap(ArrayList<Integer> gearList)
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
					return;
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
					return;
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
	}


	public ArrayList<Widget> getJunk()
	{
		ArrayList<Integer> requiredItems = nexBank.requiredItems();
		ArrayList<Widget> unNecessaryItems = (ArrayList<Widget>) Inventory.search().result();
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
		unNecessaryItems.removeIf(n -> requiredItems.contains(n.getItemId()));
		return unNecessaryItems;
	}

	public void withdraw(ArrayList<Integer> items)
	{
		int swaps = (int) (3 + (Math.abs(random.nextGaussian() * 1.5)));
		int counter = 0;
		for (int item : items)
		{
			if (counter == swaps)
			{
				return;
			}
			if (!BankUtil.contains(item))
			{
				continue;
			}
			if (item == ItemID.RUBY_DRAGON_BOLTS_E || item == ItemID.DIAMOND_DRAGON_BOLTS_E)
			{
				print("Withdrawing all" + itemManager.getItemComposition(item).getName());
				BankUtil.withdrawAll(item);
				counter++;
			}
			else
			{
				print("Withdrawing " + itemManager.getItemComposition(item).getName());
				BankUtil.withdrawOne(item);
				counter++;
			}
		}
	}
}
