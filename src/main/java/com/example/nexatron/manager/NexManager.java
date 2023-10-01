package com.example.nexatron.manager;


import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.BankInventory;
import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.PacketUtils.WidgetInfoExtended;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.BankUtil;
import com.example.Utility.Static;
import com.example.Utility.WidgetUtil;
import com.example.nexatron.NexatronConfig;
import com.example.nexatron.NexatronPlugin;
import com.example.nexatron.ReflectBreakHandler;
import com.example.nexatron.model.NexBank;
import com.example.nexatron.model.ChargesTracker;
import com.example.nexatron.model.KcArea;
import com.example.nexatron.model.Lobby;
import com.example.nexatron.model.Nex;
import com.example.nexatron.model.Overall;
import com.example.nexatron.model.setup.Setup;
import com.example.nexatron.model.Socket;
import com.example.nexatron.model.constants.NexConst;
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
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.EventBus;

@Singleton
public class NexManager
{
	public final Client client;
	private final EventBus eventBus;
	private final NexatronPlugin plugin;
	@Inject
	public GameTickManager gameTickManager;
	public boolean allowedToBreak = false;
	public NexatronConfig config;
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
					// TODO: Swap prayers with equipping weapon
//					if (!stage.equals(com.example.toagigatron.model.constants.Stage.OUTSIDE) && !stage.equals(com.example.toagigatron.model.constants.Stage.OUTSIDE_TOA) && !stage.equals(com.example.toagigatron.model.constants.Stage.GRAND_EXCHANGE))
//					{
//						Prayer p = prayWithId(i);
//						Prayers.toggle(p);
//					}
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
}
