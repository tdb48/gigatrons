package com.example.toagigatron.model;


import com.example.toagigatron.model.constants.Consumables;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.ItemContainer;
import net.runelite.api.TileItem;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ItemDespawned;
import net.runelite.api.events.ItemSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.PluginManager;

public class ConsumableTracker
{
	@Inject
	ItemManager itemManager;

	EventBus eventBus;
	Client client;

	PluginManager pluginManager;

	// TOTAL DOSES
	public int totalRaidBrewDoses = 0;
	public int totalRaidRestoreDoses = 0;
	public int totalSaltDoses = 0;
	public int totalAmbrosiaDoses = 0;
	public int totalAdrenalineDoses = 0;
	public int totalScarabDoses = 0;

	// SUPPLY POUCH DOSES

	public int bagRaidBrewDoses = 0;
	public int bagRaidRestoreDoses = 0;
	public int bagSaltDoses = 0;
	public int bagAmbrosiaDoses = 0;
	public int bagAdrenalineDoses = 0;
	public int bagScarabDoses = 0;

	// INVENTORY DOSES

	public int inventoryRaidBrewDoses = 0;
	public int inventoryRaidRestoreDoses = 0;
	public int inventorySaltDoses = 0;
	public int inventoryAmbrosiaDoses = 0;
	public int inventoryAdrenalineDoses = 0;
	public int inventoryScarabDoses = 0;

	// FLOOR DOSES

	public int floorRaidBrewDoses = 0;
	public int floorRaidRestoreDoses = 0;
	public int floorSaltDoses = 0;
	public int floorAmbrosiaDoses = 0;
	public int floorAdrenalineDoses = 0;
	public int floorScarabDoses = 0;

	TileItem recentlyDespawned = null;

	boolean justDrank = false;

	int previousChange = -1;

	boolean justWithdrew = false;

	public int scarabTicks = 0;

	@Inject
	public ConsumableTracker(EventBus eventBus, Client client, PluginManager pluginManager)
	{
		this.eventBus = eventBus;
		this.client = client;
		this.pluginManager = pluginManager;
	}

	public void resetConsumables()
	{
		totalRaidBrewDoses = 0;
		totalRaidRestoreDoses = 0;
		totalSaltDoses = 0;
		totalAmbrosiaDoses = 0;
		totalAdrenalineDoses = 0;
		totalScarabDoses = 0;
		bagRaidBrewDoses = 0;
		bagRaidRestoreDoses = 0;
		bagSaltDoses = 0;
		bagAmbrosiaDoses = 0;
		bagAdrenalineDoses = 0;
		bagScarabDoses = 0;
		inventoryRaidBrewDoses = 0;
		inventoryRaidRestoreDoses = 0;
		inventorySaltDoses = 0;
		inventoryAmbrosiaDoses = 0;
		inventoryAdrenalineDoses = 0;
		inventoryScarabDoses = 0;
		floorRaidBrewDoses = 0;
		floorRaidRestoreDoses = 0;
		floorSaltDoses = 0;
		floorAmbrosiaDoses = 0;
		floorAdrenalineDoses = 0;
		floorScarabDoses = 0;
		recentlyDespawned = null;
		justDrank = false;
		previousChange = -1;
		justWithdrew = false;
		scarabTicks = 0;
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateTotals();
		if (scarabTicks > 0)
		{
			scarabTicks--;
		}
	}

	@Subscribe
	public void onItemSpawned(ItemSpawned event)
	{
		updateFloorDoses(event.getItem(), true);

	}

	@Subscribe
	public void onItemDespawned(ItemDespawned event)
	{
		recentlyDespawned = event.getItem();
		updateFloorDoses(event.getItem(), false);
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		String msg = chatMessage.getMessage().toLowerCase();
		if (msg.contains("you drink") || msg.contains("you crush"))
		{
			if (msg.contains("tears") || msg.contains("nectar") || msg.contains("ambrosia") || msg.contains("salt") || msg.contains("reducing the energy cost"))
			{
				justDrank = true;
				System.out.println("JUST DRANK!!!");
			}
		}
		if (msg.contains("withdrawing"))
		{
			if (msg.contains("smelling salts (") || msg.contains("liquid adrenaline (") || msg.contains("tears of elidinis (") ||
				msg.contains("ambrosia (") || msg.contains("nectar (") || msg.contains("scarab ("))
			{
				if (msg.contains("smelling salts "))
				{
					System.out.println("Just withdrew a salt");
				}
				if (msg.contains("adrenaline"))
				{
					System.out.println("Just withdrew an adrenaline");
				}
				if (msg.contains("tears"))
				{
					System.out.println("Just withdrew a restore ");
				}
				if (msg.contains("ambrosia"))
				{
					System.out.println("Just withdrew an ambrosia");
				}
				if (msg.contains("nectar"))
				{
					System.out.println("Just withdrew a brew");
				}
				if (msg.contains("scarab"))
				{
					System.out.println("Just withdrew a scarab");
				}
				System.out.println("Setting JUST WITHDREW to true");
				justWithdrew = true;
			}
		}
		if (msg.contains("you crack the crystal in your hand"))
		{
			scarabTicks = 40;
			justDrank = true;
			System.out.println("JUST DRANK A SCARAB OH MY GOD!!!");
		}
//		if(msg.contains("the supplies in your inventory are already full")){
//			//Update inventory count manually
//		}
	}

//	@Subscribe
//	public void onMenuOptionClicked(MenuOptionClicked event){
//		if(event.getMenuOption().contains("Withdraw-1")){
//			System.out.println("Withdrawing one -> " + event.getMenuTarget());
//			justWithdrew = true;
//		} else{
//			justWithdrew = false;
//		}
//
//	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		ItemContainer container = event.getItemContainer();
		int id = event.getContainerId();
//		if(id == 810){ //supply bag
//			System.out.println("JUST OPENED BAG!!");
//			//justWithdrew = true;
//		}
//		int count = 1;
//		System.out.println("Container Id: " + id);
//		for(Item item : container.getItems()){
//			System.out.println("Item " + count + ": " + item.getName());
//		}
	}

//	@Subscribe
//	public void onInventoryChanged(InventoryChanged event)
//	{
//		String name = "";
//		int id = event.getItemId();
//		if(!Consumables.isRaidPotion(id)){
//			return;
//		}
//		if (event.getChangeType().equals(InventoryChanged.ChangeType.ITEM_ADDED))
//		{
//			name = client.getItemComposition(event.getItemId()).getName();
//			//The item disappeared off the ground and appeared in our inventory, logically we likely picked this up
//			if (name != null && recentlyDespawned != null && recentlyDespawned.getName().equals(name))
//			{
//				//Handle adding doses to our inventory
//				updateInventoryDoses(id, true);
//				System.out.println("Updating inventory doses after looting something off the ground");
//			}
//			else
//			{
//				if (justDrank)
//				{
//					justDrank = false;
//					System.out.println("Item added to bag and we drank,updating inventory doses: " + name);
//					updateInventoryDoses(id, true);
//				}
//				else
//				{
//					System.out.println("Item added to bag and we did not just drink or loot an item: " + name);
//					//this must have come from our bag so decrement the count in bag and add it to count in inventory
//					updateBagDoses(id, false);
//					updateInventoryDoses(id, true);
//				}
//			}
//		}
//		if (event.getChangeType().equals(InventoryChanged.ChangeType.ITEM_REMOVED))
//		{
//			previousChange = id;
//			updateInventoryDoses(id, false);
//		}
//	}

	private void updateTotals()
	{
		totalRaidBrewDoses = bagRaidBrewDoses + inventoryRaidBrewDoses + floorRaidBrewDoses;
		totalRaidRestoreDoses = bagRaidRestoreDoses + inventoryRaidRestoreDoses + floorRaidRestoreDoses;
		totalAdrenalineDoses = bagAdrenalineDoses + inventoryAdrenalineDoses + floorAdrenalineDoses;
		totalAmbrosiaDoses = bagAmbrosiaDoses + inventoryAmbrosiaDoses + floorAdrenalineDoses;
		totalSaltDoses = bagSaltDoses + inventorySaltDoses + floorSaltDoses;
		totalScarabDoses = bagScarabDoses + inventoryScarabDoses + floorScarabDoses;
	}

	private void updateInventoryDoses(int id, boolean addition)
	{
		//Not a raid pot we do not care
		if (!Consumables.isRaidPotion(id))
		{
			return;
		}
		String name = itemManager.getItemComposition(id).getName();

		//Scarab
		if (Consumables.SCARAB.contains(id))
		{
			if (addition)
			{
				inventoryScarabDoses += determineDoses(name);
			}
			else if (inventoryScarabDoses != 0)
			{
				inventoryScarabDoses -= determineDoses(name);
			}
		}
		//Tears
		if (Consumables.RAID_RESTORE.contains(id))
		{
			if (addition)
			{
				inventoryRaidRestoreDoses += determineDoses(name);
			}
			else if (inventoryRaidRestoreDoses != 0)
			{
				inventoryRaidRestoreDoses -= determineDoses(name);
			}
		}
		//Nectar
		else if (Consumables.RAID_BREW.contains(id))
		{
			if (addition)
			{
				inventoryRaidBrewDoses += determineDoses(name);
			}
			else if (inventoryRaidBrewDoses != 0)
			{
				inventoryRaidBrewDoses -= determineDoses(name);
			}
		}
		//Salt
		else if (Consumables.SALT.contains(id))
		{
			if (addition)
			{
				inventorySaltDoses += determineDoses(name);
			}
			else if (inventorySaltDoses != 0)
			{
				inventorySaltDoses -= determineDoses(name);
			}
		}
		//Adrenaline
		else if (Consumables.SPEC.contains(id))
		{
			if (addition)
			{
				inventoryAdrenalineDoses += determineDoses(name);
			}
			else if (inventoryAdrenalineDoses != 0)
			{
				inventoryAdrenalineDoses -= determineDoses(name);
			}
		}
		//Ambrosia
		else if (Consumables.AMBROSIA.contains(id))
		{
			if (addition)
			{
				inventoryAmbrosiaDoses += determineDoses(name);
			}
			else if (inventoryAmbrosiaDoses != 0)
			{
				inventoryAmbrosiaDoses -= determineDoses(name);
			}
		}
	}


	private void updateBagDoses(int id, boolean addition)
	{
		//Not a raid pot we do not care
		if (!Consumables.isRaidPotion(id))
		{
			System.out.println("Not a raid potion, returning");
			return;
		}
		String name = itemManager.getItemComposition(id).getName();

		//Scarab
		if (Consumables.SCARAB.contains(id))
		{
			System.out.println("Found a scarab.");
			if (addition)
			{
				bagScarabDoses += determineDoses(name);
			}
			else
			{
				if (justWithdrew)
				{
					System.out.println("Just withdrew, determining scarab doses");
					bagScarabDoses -= determineDoses(name);
					justWithdrew = false;
				}
				else
				{
					System.out.println("Did not withdraw, determining doses");
					bagScarabDoses -= determineDosesResupply(previousChange, id);
				}

			}
		}
		//Tears
		if (Consumables.RAID_RESTORE.contains(id))
		{
			System.out.println("Its a raid restore");
			if (addition)
			{
				bagRaidRestoreDoses += determineDoses(name);
			}
			else
			{
				if (justWithdrew)
				{
					System.out.println("Just withdrew, determining doses");
					bagRaidRestoreDoses -= determineDoses(name);
					justWithdrew = false;
				}
				else
				{
					System.out.println("Did not withdraw, determining doses");
					bagRaidRestoreDoses -= determineDosesResupply(previousChange, id);
				}

			}
		}
		//Nectar
		else if (Consumables.RAID_BREW.contains(id))
		{
			System.out.println("Its a raid brew");
			if (addition)
			{
				bagRaidBrewDoses += determineDoses(name);
			}
			else
			{
				if (justWithdrew)
				{
					System.out.println("Just withdrew, determining doses");
					bagRaidBrewDoses -= determineDoses(name);
					justWithdrew = false;
				}
				else
				{
					System.out.println("Did not withdraw, determining doses");
					bagRaidBrewDoses -= determineDosesResupply(previousChange, id);
				}

			}
		}
		//Salt
		else if (Consumables.SALT.contains(id))
		{
			System.out.println("Its a salt");
			if (addition)
			{
				bagSaltDoses += determineDoses(name);
			}
			else
			{
				if (justWithdrew)
				{
					System.out.println("Just withdrew, determining doses");
					bagSaltDoses -= determineDoses(name);
					justWithdrew = false;
				}
				else
				{
					System.out.println("Did not withdraw, determining doses");
					bagSaltDoses -= determineDosesResupply(previousChange, id);
				}

			}
		}
		//Adrenaline
		else if (Consumables.SPEC.contains(id))
		{
			System.out.println("Its an adrenaline ");
			if (addition)
			{
				bagAdrenalineDoses += determineDoses(name);
			}
			else
			{
				if (justWithdrew)
				{
					System.out.println("Just withdrew, determining doses");
					bagAdrenalineDoses -= determineDoses(name);
					justWithdrew = false;
				}
				else
				{
					System.out.println("Did not withdraw, determining doses");
					bagAdrenalineDoses -= determineDosesResupply(previousChange, id);
				}

			}
		}
		//Ambrosia
		else if (Consumables.AMBROSIA.contains(id))
		{
			System.out.println("Its an ambrosia");
			if (addition)
			{
				bagAmbrosiaDoses += determineDoses(name);
			}
			else
			{
				if (justWithdrew)
				{
					System.out.println("Just withdrew, determining doses");
					bagAmbrosiaDoses -= determineDoses(name);
					justWithdrew = false;
				}
				else
				{
					System.out.println("Did not withdraw, determining doses");
					bagAmbrosiaDoses -= determineDosesResupply(previousChange, id);
				}

			}
		}
	}


	private void updateFloorDoses(TileItem item, boolean addition)
	{
		int id = item.getId();
		//Not a raid pot we do not care
		if (!Consumables.isRaidPotion(id))
		{
			return;
		}
		if (Consumables.SCARAB.contains(id))
		{
			if (addition)
			{
				floorScarabDoses += determineDoses(item);
			}
			else
			{
				floorScarabDoses -= determineDoses(item);
			}
		}
		//Tears
		if (Consumables.RAID_RESTORE.contains(id))
		{
			if (addition)
			{
				floorRaidRestoreDoses += determineDoses(item);
			}
			else
			{
				floorRaidRestoreDoses -= determineDoses(item);
			}
		}
		//Nectar
		else if (Consumables.RAID_BREW.contains(id))
		{
			if (addition)
			{
				floorRaidBrewDoses += determineDoses(item);
			}
			else
			{
				floorRaidBrewDoses -= determineDoses(item);
			}
		}
		//Salt
		else if (Consumables.SALT.contains(id))
		{
			if (addition)
			{
				floorSaltDoses += determineDoses(item);
			}
			else
			{
				floorSaltDoses -= determineDoses(item);
			}
		}
		//Adrenaline
		else if (Consumables.SPEC.contains(id))
		{
			if (addition)
			{
				floorAdrenalineDoses += determineDoses(item);
			}
			else
			{
				floorAdrenalineDoses -= determineDoses(item);
			}
		}
		//Ambrosia
		else if (Consumables.AMBROSIA.contains(id))
		{
			if (addition)
			{
				floorAmbrosiaDoses += determineDoses(item);
			}
			else
			{
				floorAmbrosiaDoses -= determineDoses(item);
			}
		}
	}

	public int determineDosesResupply(int idRemoved, int idAdded)
	{
		if (!Consumables.samePotionType(idRemoved, idAdded))
		{
			return 0;
		}
		//We have the id of the potion removed and the id of the potion added
		//They are both in the same type (both restores, brews etc)
		//Logic dictates that we probably resupplied from bag
		//Update bag to be diff between removed and added
		int dose1 = determineDoses(itemManager.getItemComposition(idRemoved).getName());
		int dose2 = determineDoses(itemManager.getItemComposition(idAdded).getName());
		if (dose1 == 0 || dose2 == 0)
		{
			return 0;
		}
		System.out.println("Item removed: " + itemManager.getItemComposition(idRemoved).getName() + "\n" +
			"Item added: " + itemManager.getItemComposition(idAdded).getName());
		int doseDiff = dose2 - dose1;
		if (doseDiff <= 0)
		{
			System.out.println("Something went wrong in dose calc for items: \n" +
				itemManager.getItemComposition(idRemoved).getName() + "\n" +
				itemManager.getItemComposition(idAdded).getName());
		}
		return dose2 - dose1;
	}

//	public void manualInventoryCheck(){
//		int newBrewCount = 0;
//		int newRestoreCount = 0;
//		int newScarabCount = 0;
//		int newAmbrosiaCount = 0;
//		int newAdrenalineCount = 0;
//		int newSaltCount = 0;
//		for(Item item : Inventory.getAll()){
//			String name = item.getName().toLowerCase();
//			if(name.equals("null")){
//				continue;
//			}
//			if(!Consumables.isRaidPotion(item.getId())){
//				continue;
//			}
//			if(name.contains("smelling salts")){
//				newSaltCount += determineDoses(name);
//			}
//			if(name.contains("adrenaline")){
//				newAdrenalineCount += determineDoses(name);
//			}
//			if(name.contains("tears")){
//				newRestoreCount += determineDoses(name);
//			}
//			if(name.contains("ambrosia")){
//				newAmbrosiaCount += determineDoses(name);
//			}
//			if(name.contains("nectar")){
//				newBrewCount += determineDoses(name);
//			}
//			if(name.contains("scarab")){
//				newScarabCount += determineDoses(name);
//			}
//		}
//		inventoryRaidBrewDoses = newBrewCount;
//		inventoryRaidRestoreDoses = newRestoreCount;
//		inventoryScarabDoses = newScarabCount;
//		inventoryAmbrosiaDoses = newAmbrosiaCount;
//		inventoryAdrenalineDoses = newAdrenalineCount;
//		inventorySaltDoses = newSaltCount;
//	}

	public int determineDoses(String name)
	{
		int dose = 0;
		if (name.contains("(4)"))
		{
			dose = 4;
		}
		else if (name.contains("(3)"))
		{
			dose = 3;
		}
		else if (name.contains("(2)"))
		{
			dose = 2;
		}
		else if (name.contains("(1)"))
		{
			dose = 1;
		}
		return dose;
	}

	public int determineDoses(TileItem item)
	{
		String name = itemManager.getItemComposition(item.getId()).getName();
		int dose = 0;
		if (name.contains("(4)"))
		{
			dose = 4;
		}
		else if (name.contains("(3)"))
		{
			dose = 3;
		}
		else if (name.contains("(2)"))
		{
			dose = 2;
		}
		else if (name.contains("(1)"))
		{
			dose = 1;
		}
		return dose;
	}

	public void register()
	{
		this.eventBus.register(this);
	}

	public void unregister()
	{
		this.eventBus.unregister(this);
	}

}
