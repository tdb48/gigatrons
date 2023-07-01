package com.example.toagigatron.manager;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.BankInventory;
import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.EquipmentItemWidget;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.BankUtil;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.Utility.Prayer;
import com.example.Utility.Prayers;
import com.example.Utility.Static;
import com.example.Utility.Tiles;
import com.example.Utility.WidgetUtil;
import com.example.toagigatron.ReflectBreakHandler;
import com.example.toagigatron.ToaGigatronConfig;
import com.example.toagigatron.ToaGigatronPlugin;
import com.example.toagigatron.model.Akkha;
import com.example.toagigatron.model.Baba;
import com.example.toagigatron.model.ChargesTracker;
import com.example.toagigatron.model.ConsumableTracker;
import com.example.toagigatron.model.Inside;
import com.example.toagigatron.model.Kephri;
import com.example.toagigatron.model.Outside;
import com.example.toagigatron.model.Overall;
import com.example.toagigatron.model.Wardens12;
import com.example.toagigatron.model.Wardens3;
import com.example.toagigatron.model.Zebak;
import com.example.toagigatron.model.bossmodel.ZebakJug;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.constants.WeaponMap;
import com.example.toagigatron.model.setup.MageSetup;
import com.example.toagigatron.model.setup.MeleeSetup;
import com.example.toagigatron.model.setup.RangeSetup;
import com.google.inject.Singleton;

import java.util.*;
import java.util.stream.Collectors;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;

@Singleton
public class ToaManager
{
	@Inject
	public ConsumableTracker consumableTracker;
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
	public Kephri kephri;
	@Inject
	public Baba baba;
	@Inject
	public Akkha akkha;
	public Wardens12 wardens12;
	@Inject
	public ChargesTracker chargesTracker;
	@Inject
	public Wardens3 wardens3;
	@Inject
	public Inside inside;
	@Inject
	public Outside outside;
	public int necessarySanfew = 1;
	public int necessaryAnti = 0;
	public int necessaryScb = 1;
	public int necessaryStam = 0;

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

	public boolean hasEquipped(int itemId)
	{
		return Equipment.search().withId(itemId).first().orElse(null) != null;
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

	public String worldPointString(WorldPoint wp)
	{
		return "(X: " + wp.getX() + ", Y: " + wp.getY() + ") ";
	}

	public String worldPointStringVerbose(WorldPoint wp)
	{
		return "(X: " + wp.getX() + ", Y: " + wp.getY() + ", Z: " + wp.getPlane() + ") ";
	}

	public boolean isSaltBrewTick()
	{
		return getSaltTick() % 25 == 0;
	}

	public int getSaltTick()
	{
		return overall.saltInTicks;
	}

	public void reAttack(NPC npc)
	{
		if (npc != null)
		{
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(npc, "Attack");
			print("Re-Attacking " + npc.getName());
		}
		else
		{
			print("NPC IS NULL IN RE-ATTACK");
		}
	}

	public boolean refill()
	{
		Widget bag = Inventory.search().withId(Consumables.SUPPLY_BAG).first().orElse(null);
		if (bag == null)
		{
			return false;
		}
		if (consumableTracker.inventoryRaidBrewDoses % 4 != 0
			&& consumableTracker.bagRaidBrewDoses > 0
			&& consumableTracker.inventoryRaidBrewDoses > 0)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(bag, "Resupply");
			return true;
		}
		if (consumableTracker.inventoryRaidRestoreDoses % 4 != 0
			&& consumableTracker.bagRaidRestoreDoses > 0
			&& consumableTracker.inventoryRaidRestoreDoses > 0)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(bag, "Resupply");
			return true;
		}
		if (consumableTracker.inventorySaltDoses == 1
			&& consumableTracker.bagSaltDoses > 0)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(bag, "Resupply");
			return true;
		}
		if (consumableTracker.inventoryAdrenalineDoses == 1 && consumableTracker.bagAdrenalineDoses > 0)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(bag, "Resupply");
			return true;
		}
		if (consumableTracker.inventoryAmbrosiaDoses == 1 && consumableTracker.bagAmbrosiaDoses > 0)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(bag, "Resupply");
			return true;
		}
		if (consumableTracker.inventoryScarabDoses == 1 && consumableTracker.bagScarabDoses > 0)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(bag, "Resupply");
			return true;
		}
		return false;
	}

//	@Subscribe
//	public void onProjectileSpawned(ProjectileSpawned projectileSpawned)
//	{
//		Projectile projectile = projectileSpawned.getProjectile();
//		if (ToaConstants.DARTS.contains(projectile.getId()))
//		{
//			gameTickManager.attack(2);
//		}
//	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged animationChanged)
	{
		if (animationChanged.getActor().equals(client.getLocalPlayer()))
		{
			if (client.getLocalPlayer().getAnimation() == ToaConstants.BANDOS_GODSWORD_SPEC)
			{
				gameTickManager.attack(6);
			}
			if (animationChanged.getActor().getAnimation() == ToaConstants.FANG_ATTACK
				|| animationChanged.getActor().getAnimation() == ToaConstants.FANG_ATTACK_SPEC
				|| animationChanged.getActor().getAnimation() == ToaConstants.SHADOW_ATTACK)
			{
				gameTickManager.attack(5);
			}
			if (client.getLocalPlayer().getAnimation() == ToaConstants.SANG_ATTACK
				|| client.getLocalPlayer().getAnimation() == ToaConstants.DDS_POKE
				|| client.getLocalPlayer().getAnimation() == ToaConstants.DDS_SPEC)
			{
				gameTickManager.attack(4);
			}
		}
	}

	public ArrayList<Widget> getAllUnnecessaryItems()
	{
		ArrayList<Widget> unNecessaryItems = (ArrayList<Widget>) Inventory.search().result();
		if (!isPrePotted())
		{
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumables.PREPOT_SCB);
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumables.PREPOT_ANGLER);
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumables.PREPOT_SATURATED_HEART);
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumables.PREPOT_RANGE);
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumables.PREPOT_STAM);
			unNecessaryItems.removeIf(n -> n.getItemId() == Consumables.PREPOT_ANTI);
		}
		else
		{
			unNecessaryItems.removeIf(n -> Consumables.getNecessaryPotions.contains(n.getItemId()));
		}
		unNecessaryItems.removeIf(n -> getAllNecessaryItems().contains(n.getItemId()));
		return unNecessaryItems;
	}

	public void disableOverheadsIfEnabled()
	{
		for (Prayer p : ToaConstants.OVERHEAD_PRAYERS)
		{
			if (Prayers.isEnabled(p))
			{
				Prayers.toggle(p);
			}
		}
	}

	public void disableOffensiveIfEnabled()
	{
		for (Prayer p : ToaConstants.OFFENSIVE_PRAYERS)
		{
			if (Prayers.isEnabled(p))
			{
				Prayers.toggle(p);
			}
		}
	}

	public List<WorldPoint> findClosestPotentialTiles(
		WorldPoint currTile, WorldPoint targetTile, List<WorldPoint> tiles,
		List<Integer> gameObjAvoidIds, List<Integer> groundObjectAvoidIds,
		int distance)
	{
		List<WorldPoint> potentialTiles = new ArrayList<>();
		if (currTile.distanceTo(targetTile) <= 2)
		{
			potentialTiles.add(targetTile);
			return potentialTiles;
		}
		int closestDistance = Integer.MAX_VALUE;
		for (WorldPoint wp : tiles)
		{
			if (wp.distanceTo(currTile) == distance)
			{
				Tile tile = Tiles.getAt(wp);
				//Check if undesirable game object on tile
				if (tile.getGameObjects() != null)
				{
					for (GameObject obj : tile.getGameObjects())
					{
						if (obj != null)
						{
							if (gameObjAvoidIds != null && gameObjAvoidIds.contains(obj.getId()))
							{
								break;
							}
						}

					}
				}
				//Check if undesirable ground object on tile
				if (tile.getGroundObject() != null)
				{
					if (groundObjectAvoidIds != null && groundObjectAvoidIds.contains(tile.getGroundObject().getId()))
					{
						continue;
					}
				}
				if (wp.distanceTo(targetTile) < closestDistance)
				{
					closestDistance = wp.distanceTo(targetTile);
				}
			}
		}
		for (WorldPoint wp : tiles)
		{
			if (wp.distanceTo(currTile) == distance)
			{
				Tile tile = Tiles.getAt(wp);
				if (wp.distanceTo(targetTile) == closestDistance)
				{
					//Check if undesirable game object on tile
					if (tile.getGameObjects() != null)
					{
						for (GameObject obj : tile.getGameObjects())
						{
							if (obj != null)
							{
								if (gameObjAvoidIds != null && gameObjAvoidIds.contains(obj.getId()))
								{
									break;
								}
							}
						}
					}
					//Check if undesirable ground object on tile
					if (tile.getGroundObject() != null)
					{
						if (groundObjectAvoidIds != null && groundObjectAvoidIds.contains(tile.getGroundObject().getId()))
						{
							continue;
						}
					}
					potentialTiles.add(wp);
				}
			}
		}
		return potentialTiles;
	}

	public WorldPoint findClosestTile(ArrayList<WorldPoint> possibleTiles)
	{
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		return possibleTiles.stream().min(Comparator.comparingInt(wp -> wp.distanceTo(playerPoint))).stream().findAny().orElse(null);
	}

	public WorldPoint findFurthestTile(ArrayList<WorldPoint> possibleTiles, WorldPoint targetTile)
	{
		return possibleTiles.stream().max(Comparator.comparingInt(wp -> wp.distanceTo(targetTile))).stream().findAny().orElse(null);
	}

	public LocalPoint findClosestTile(ArrayList<LocalPoint> possibleTiles, LocalPoint targetPoint)
	{
		return possibleTiles.stream().min(Comparator.comparingInt(wp -> wp.distanceTo(targetPoint))).stream().findAny().orElse(null);
	}

	public WorldPoint findClosestTile(ArrayList<WorldPoint> possibleTiles, WorldPoint targetPoint)
	{
		return possibleTiles.stream().min(Comparator.comparingInt(wp -> wp.distanceTo(targetPoint))).stream().findAny().orElse(null);
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

	public static ArrayList<Integer> equipmentItemsToIntegers(ArrayList<EquipmentItemWidget> items)
	{
		ArrayList<Integer> returnList = new ArrayList<>();
		for (EquipmentItemWidget widget : items)
		{
			//System.out.println("printing item id : "  +widget.getEquipmentItemId());
			returnList.add(widget.getEquipmentItemId());
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

	public boolean isNextToNpc(NPC npc)
	{
		if (npc == null)
		{
			return false;
		}
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		WorldArea npcArea = npc.getWorldArea();
		for (WorldPoint wp : npcArea.toWorldPointList())
		{
			if (wp.distanceTo(playerPoint) == 1 && !npcArea.contains(playerPoint))
			{
				return true;
			}
		}
		return false;
	}

	public ArrayList<Integer> withdrawNecessaryItems()
	{
		ArrayList<Integer> returnList = getAllNecessaryItems();
		ArrayList<Integer> equipment = equipmentItemsToIntegers((ArrayList<EquipmentItemWidget>) Equipment.search().result());
		ArrayList<Integer> inventory = itemsToIntegers((ArrayList<Widget>) Inventory.search().result());
		returnList.removeIf(n -> equipment.contains(n) || inventory.contains(n));
		return returnList;
	}

	public void withdrawFromBag(int i)
	{
		withdrawFromBag((ArrayList<Integer>) List.of(i));
	}

	public void withdrawFromBag(ArrayList<Integer> list)
	{
		int bag = Consumables.SUPPLY_BAG;
		Widget bagWidget = client.getWidget(778, 0);

		// If bag is not open, open it
		if (bagWidget == null || bagWidget.isHidden())
		{
			Widget bagItem = Inventory.search().withId(bag).first().orElse(null);
			if (bagItem != null)
			{
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(bagItem, "Open");
			}
			return;
		}
		Widget closeButton = client.getWidget(778, 2);
		Widget bagItems = client.getWidget(778, 5);
		if (bagItems == null || bagWidget.isHidden())
		{
			return;
		}
		if (closeButton == null || closeButton.isHidden())
		{
			return;
		}
		for (Widget w : bagItems.getDynamicChildren())
		{
			if (list.contains(w.getItemId()))
			{
				list.remove(list.indexOf(w.getItemId()));
				print("Withdrawing " + w.getName());
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(w, "Withdraw-1");
				if (list.isEmpty())
				{
					MousePackets.queueClickPacket();
					WidgetPackets.queueWidgetAction(closeButton, "Close");
				}
				return;
			}
		}
		if (list.isEmpty())
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(closeButton, "Close");
		}
	}

	public void withdraw(ArrayList<Integer> items)
	{
		int swaps = (int) (3 + (Math.abs(random.nextGaussian() * 1.5)));
		int counter = 0;

		//System.out.println("Items size -> " + items.size());
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
			if (item == rangeSetup.arrows && item == ItemID.DRAGON_ARROW)
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

	public void openAndCloseBag()
	{
		int bag = Consumables.SUPPLY_BAG;
		Widget bagWidget = client.getWidget(778, 0);

		if (bagWidget == null || bagWidget.isHidden())
		{
			Widget bagItem = Inventory.search().withId(bag).first().orElse(null);
			if (bagItem != null)
			{
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(bagItem, "Open");
			}
			return;
		}
		Widget closeButton = client.getWidget(778, 2);
		Widget bagItems = client.getWidget(778, 5);
		if (bagItems == null || bagWidget.isHidden())
		{
			return;
		}
		if (closeButton == null || closeButton.isHidden())
		{
			return;
		}
		int brewCount = 0;
		int resCount = 0;
		int ambCount = 0;
		int saltCount = 0;
		int adrCount = 0;
		for (Widget w : bagItems.getDynamicChildren())
		{
			int id = w.getItemId();
			if (!Consumables.isRaidPotion(id))
			{
				continue;
			}
			String name = itemManager.getItemComposition(id).getName();
			if (Consumables.RAID_BREW.contains(id))
			{
				brewCount += consumableTracker.determineDoses(name);
				//brewCount++;
			}
			else if (Consumables.RAID_RESTORE.contains(id))
			{
				resCount += consumableTracker.determineDoses(name);
				//resCount++;
			}
			else if (Consumables.AMBROSIA.contains(id))
			{
				ambCount += consumableTracker.determineDoses(name);
				//ambCount++;
			}
			else if (Consumables.SALT.contains(id))
			{
				saltCount += consumableTracker.determineDoses(name);
				//saltCount++;
			}
			else if (Consumables.SPEC.contains(id))
			{
				adrCount += consumableTracker.determineDoses(name);
				//adrCount++;
			}
		}
		print("Brew count -> " + brewCount);
		print("Restore count -> " + resCount);
		print("Ambrosia count -> " + ambCount);
		print("Adrenaline count -> " + adrCount);
		print("Salt count -> " + saltCount);

		consumableTracker.bagRaidBrewDoses = brewCount;
		consumableTracker.bagRaidRestoreDoses = resCount;
		consumableTracker.bagAmbrosiaDoses = ambCount;
		consumableTracker.bagAdrenalineDoses = adrCount;
		consumableTracker.bagSaltDoses = saltCount;
		MousePackets.queueClickPacket();
		WidgetPackets.queueWidgetAction(closeButton, "Close");
		wardens12.bagOpened = true;
	}

	public void bank(ArrayList<Widget> items)
	{
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

	public void swap(ArrayList<Integer> gearList)
	{
		int swaps = (int) (3 + (Math.abs(random.nextGaussian() * 1.5)));
		int counter = 0;
		// Equip weapon first
		for (int i : ToaConstants.WEAPONS)
		{
			if (gearList.contains(i))
			{
				Widget item = Inventory.search().withId(i).first().orElse(null);
				if (item != null)
				{
					if(!stage.equals(Stage.OUTSIDE) && !stage.equals(Stage.OUTSIDE_TOA) && !stage.equals(Stage.GRAND_EXCHANGE)){
						Prayer p = prayWithId(i);
						Prayers.toggle(p);
					}


					int slot = 0;
					ItemContainer invent = client.getItemContainer(InventoryID.INVENTORY.getId());
					if(invent != null){
						for(int j = 0 ; j < 28; j++){
							Item inventoryItem = invent.getItem(j);
							if(inventoryItem == null){
								continue;
							}
							//System.out.println("Item id -> " + item.getItemId());
							if(inventoryItem.getId() == item.getItemId()){
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
		int cape = rangeSetup.cape;
		if (gearList.contains(cape))
		{
			Widget item = Inventory.search().withId(cape).first().orElse(null);
			if (item != null)
			{
				int slot = 0;
				ItemContainer invent = client.getItemContainer(InventoryID.INVENTORY.getId());
				if(invent != null){
					for(int j = 0 ; j < 28; j++){
						Item inventoryItem = invent.getItem(j);
						if(inventoryItem == null){
							//System.out.println("Inventory item is null somehow?");
							continue;
						}
						//System.out.println("Item id -> " + item.getItemId());
						if(inventoryItem.getId() == item.getItemId()){
							//System.out.println("Item found at slot -> " + j);
							slot = j;
							break;
						}
					}
				}
				if (WidgetUtil.hasAction(item, "Wear"))
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
					gearList.remove((Integer) cape);
				}
			}
		}

		int[] gear = gearList.stream().mapToInt(i -> i).toArray();
		List<Integer> gearAsList = Arrays.stream(gear).boxed().collect(Collectors.toList());
		if(BankUtil.isOpen()){
			for (Widget item : BankInventory.search().idInList(gearAsList).result())
			{
				if (counter == swaps)
				{
					return;
				}
				int slot = 0;
				ItemContainer invent = client.getItemContainer(InventoryID.INVENTORY.getId());
				if(invent != null){
					for(int j = 0 ; j < 28; j++){
						Item inventoryItem = invent.getItem(j);
						if(inventoryItem == null){
							//System.out.println("Inventory item is null somehow?");
							continue;
						}
						//System.out.println("Item id -> " + item.getItemId());
						if(inventoryItem.getId() == item.getItemId()){
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
		} else {
			for (Widget item : Inventory.search().idInList(gearAsList).result())
			{
				if (counter == swaps)
				{
					return;
				}
				int slot = 0;
				ItemContainer invent = client.getItemContainer(InventoryID.INVENTORY.getId());
				if(invent != null){
					for(int j = 0 ; j < 28; j++){
						Item inventoryItem = invent.getItem(j);
						if(inventoryItem == null){
							//System.out.println("Inventory item is null somehow?");
							continue;
						}
						//System.out.println("Item id -> " + item.getItemId());
						if(inventoryItem.getId() == item.getItemId()){
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

	public boolean isAdrenalineActive()
	{
		return client.getVarbitValue(ToaConstants.ADRENALINE) != 0;
	}

	public boolean isBoosted(Skill skill)
	{
		return client.getBoostedSkillLevel(skill) > client.getRealSkillLevel(skill);
	}

	public boolean hasItem(ArrayList<Integer> items)
	{
		ArrayList<Widget> inventory = (ArrayList<Widget>) Inventory.search().result();
		inventory.addAll(Equipment.search().result());
		inventory.removeIf(n -> !items.contains(n.getItemId()));
		return inventory.size() != 0;
	}

	public boolean hasAllItems(ArrayList<Integer> items)
	{
		items.removeIf(n -> n == 0 || n == -1);
		ArrayList<Widget> playerItems = (ArrayList<Widget>) Inventory.search().result();
		ArrayList<Integer> returnList = itemsToIntegers(playerItems);
		for(EquipmentItemWidget w : Equipment.search().result()){
			returnList.add(w.getEquipmentItemId());
		}

		for (int i : items)
		{
			if (!returnList.contains(i))
			{
				System.out.println("missing ID " + i + ", name: " + itemManager.getItemComposition(i).getName());
				return false;
			}
		}
		return true;
	}


	public boolean readyToEnterRaid()
	{
		return !overall.died && isPrePotted() && hasAllItems(getAllNecessaryItems()) && hasRequiredSupplies();
	}

	public boolean isDiagonalOf(WorldPoint wp, WorldPoint wp2)
	{
		int xDifference = Math.abs(wp.getX() - wp2.getX());
		int yDifference = Math.abs(wp.getY() - wp2.getY());
		return yDifference == xDifference;
	}

	public boolean hasRequiredSupplies()
	{
		if (Bank.isOpen())
		{
			return BankInventory.search().withId(Consumables.FULL_DOSE_ANTI).result().size() == necessaryAnti
				&& BankInventory.search().withId(Consumables.FULL_DOSE_SANFEW).result().size() == necessarySanfew
				&& BankInventory.search().withId(Consumables.FULL_DOSE_BREW).result().size() == necessaryBrew()
				&& BankInventory.search().withId(Consumables.FULL_DOSE_RESTORE).result().size() == necessaryRestore()
				&& BankInventory.search().withId(Consumables.FULL_DOSE_SCB).result().size() == necessaryScb
				&& BankInventory.search().withId(Consumables.FULL_DOSE_STAM).result().size() == necessaryStam;
		}
		return Inventory.search().withId(Consumables.FULL_DOSE_ANTI).result().size() == necessaryAnti
			&& Inventory.search().withId(Consumables.FULL_DOSE_SANFEW).result().size() == necessarySanfew
			&& Inventory.search().withId(Consumables.FULL_DOSE_BREW).result().size() == necessaryBrew()
			&& Inventory.search().withId(Consumables.FULL_DOSE_RESTORE).result().size() == necessaryRestore()
			&& Inventory.search().withId(Consumables.FULL_DOSE_SCB).result().size() == necessaryScb
			&& Inventory.search().withId(Consumables.FULL_DOSE_STAM).result().size() == necessaryStam;
	}

	public boolean hasTooManySupplies()
	{
		if (Bank.isOpen())
		{
			return BankInventory.search().withId(Consumables.FULL_DOSE_ANTI).result().size() > necessaryAnti
				&& BankInventory.search().withId(Consumables.FULL_DOSE_SANFEW).result().size() > necessarySanfew
				&& BankInventory.search().withId(Consumables.FULL_DOSE_BREW).result().size() > necessaryBrew()
				&& BankInventory.search().withId(Consumables.FULL_DOSE_RESTORE).result().size() > necessaryRestore()
				&& BankInventory.search().withId(Consumables.FULL_DOSE_SCB).result().size() > necessaryScb
				&& BankInventory.search().withId(Consumables.FULL_DOSE_STAM).result().size() > necessaryStam;
		}
		return Inventory.search().withId(Consumables.FULL_DOSE_ANTI).result().size() > necessaryAnti
			&& Inventory.search().withId(Consumables.FULL_DOSE_SANFEW).result().size() > necessarySanfew
			&& Inventory.search().withId(Consumables.FULL_DOSE_BREW).result().size() > necessaryBrew()
			&& Inventory.search().withId(Consumables.FULL_DOSE_RESTORE).result().size() > necessaryRestore()
			&& Inventory.search().withId(Consumables.FULL_DOSE_SCB).result().size() > necessaryScb
			&& Inventory.search().withId(Consumables.FULL_DOSE_STAM).result().size() > necessaryStam;
	}


	public ArrayList<Integer> getAllNecessaryItems()
	{
		ArrayList<Integer> returnList = new ArrayList<>(meleeSetup.getAllItems());
		returnList.add(meleeSetup.dds);
		returnList.add(meleeSetup.bgs);
		returnList.addAll(rangeSetup.getAllItems());
		returnList.addAll(mageSetup.getAllItems());
		// Incase range setup has a tbow or bowfa over the blowpipe
		if (!returnList.contains(rangeSetup.blowpipe))
		{
			returnList.add(rangeSetup.blowpipe);
		}
		returnList.removeIf(n -> n == 0 || n == -1);
		return returnList;
	}

	public boolean isSaltActive()
	{
		return getSaltTick() != 0;
	}


	public boolean containsObjectBaba(GameObject[] objects)
	{
		for (GameObject object : objects)
		{
			if (object != null && object.getId() == ToaConstants.BABA_PUZZLE_POISON)
			{
				return true;
			}
		}
		return false;
	}

	public boolean findTileGameObject(Client client, List<Integer> itemIDs, WorldPoint wp)
	{
		List<Tile> tilesList = new ArrayList<>();
		Scene scene = client.getScene();
		Tile[][][] tiles = scene.getTiles();
		int z = client.getPlane();
		for (int x = 0; x < 104; ++x)
		{
			for (int y = 0; y < 104; ++y)
			{
				Tile tile = tiles[z][x][y];
				if (tile == null)
				{
					continue;
				}
				tilesList.add(tile);
			}
		}
		for (Tile tile : tilesList)
		{
			if (tile.getWorldLocation().equals(wp))
			{
				if (tile.getGameObjects() != null)
				{
					for (GameObject object : tile.getGameObjects())
					{
						if (object != null && itemIDs.contains(object.getId()))
						{
							return true;
						}
					}

				}
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

	public boolean isAntiVenomed()
	{
		return Static.getClient().getVarpValue(VarPlayer.POISON) < -36;
	}

	public int requiredAnti()
	{
		return necessaryAnti - Inventory.search().withId(Consumables.FULL_DOSE_ANTI).result().size();
	}

	public int requiredScb()
	{
		return necessaryScb - Inventory.search().withId(Consumables.FULL_DOSE_SCB).result().size();
	}

	public int requiredStam()
	{
		return necessaryStam - Inventory.search().withId(Consumables.FULL_DOSE_STAM).result().size();
	}

	public int requiredRestore()
	{
		return necessaryRestore() - Inventory.search().withId(Consumables.FULL_DOSE_RESTORE).result().size();
	}

	public int requiredSanfew()
	{
		return necessarySanfew - Inventory.search().withId(Consumables.FULL_DOSE_SANFEW).result().size();
	}

	public int requiredBrew()
	{
		return necessaryBrew() - Inventory.search().withId(Consumables.FULL_DOSE_BREW).result().size();
	}

	public int necessaryBrew()
	{
		return config.brewCount();
	}

	public int necessaryRestore()
	{
		ArrayList<Integer> gear = getAllNecessaryItems();
		gear.removeIf(n -> meleeSetup.getAllItems().contains(n));
		int restore = 28 - gear.size();
		restore -= necessaryAnti;
		restore -= necessaryStam;
		restore -= necessaryScb;
		restore -= necessarySanfew;
		restore -= necessaryBrew();
		return restore;
	}

	public boolean isPrePotted()
	{
		return isBoosted(Skill.STRENGTH)
			&& isBoosted(Skill.RANGED)
			&& (client.getVarbitValue(Varbits.IMBUED_HEART_COOLDOWN) > 0 || isBoosted(Skill.MAGIC))
			&& isBoosted(Skill.HITPOINTS)
			&& Movement.isStaminaBoosted();
	}

	public Prayer prayWithId(int weaponId)
	{
		ItemContainer equipped = Static.getClient().getItemContainer(InventoryID.EQUIPMENT);
		if (equipped != null)
		{
			WeaponMap.WeaponStyle style = WeaponMap.StyleMap.getOrDefault(weaponId, WeaponMap.WeaponStyle.MELEE);
			switch (style.ordinal())
			{
				case 0:
					return Prayer.AUGURY;
				case 1:
					return Prayer.RIGOUR;
				case 2:
					return Prayer.PIETY;
			}

			Widget atk = Static.getClient().getWidget(Combat.getAttackStyle().getWidgetInfo());
			if (atk != null)
			{
				String[] actions = atk.getActions();
				if (actions != null && actions.length == 1)
				{
					switch (actions[0])
					{
						case "Rapid":
							return Prayer.RIGOUR;
						case "Accurate":
						case "Longrange":
							return Prayer.AUGURY;
					}
				}
			}
		}
		return Prayer.PIETY;
	}


}
