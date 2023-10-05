package com.example.nexatron.model;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.BankUtil;
import com.example.Utility.InventoryUtil;
import com.example.Utility.Static;
import com.example.nexatron.manager.NexManager;
import java.util.ArrayList;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.game.ItemManager;

public class Consumable
{
	@Inject
	NexManager nexManager;
	@Inject
	ItemManager itemManager;
	public static final int PREPOT_SCB = ItemID.SUPER_COMBAT_POTION1;
	public static final int PREPOT_STAM = ItemID.STAMINA_POTION1;
	public static final int PREPOT_RANGE = ItemID.RANGING_POTION1;
	public static final int PREPOT_ANGLER = ItemID.ANGLERFISH;
	public static final int PREPOT_ANTI = ItemID.ANTIDOTE1_5958;
	public static ArrayList<Integer> getNecessaryPotions = new ArrayList<>(
		Arrays.asList(
			ItemID.ANGLERFISH,
			ItemID.RANGING_POTION4,
			ItemID.SARADOMIN_BREW4,
			ItemID.SUPER_COMBAT_POTION4,
			ItemID.SUPER_RESTORE1,
			ItemID.SUPER_RESTORE4));
	public static final ArrayList<Integer> RESTORE =
		new ArrayList<>(Arrays.asList(
			ItemID.SUPER_RESTORE1,
			ItemID.SUPER_RESTORE2,
			ItemID.SUPER_RESTORE3,
			ItemID.SUPER_RESTORE4
		));
	public static final ArrayList<Integer> RANGE =
		new ArrayList<>(Arrays.asList(
			ItemID.RANGING_POTION1,
			ItemID.RANGING_POTION2,
			ItemID.RANGING_POTION3,
			ItemID.RANGING_POTION4
		));
	public static final ArrayList<Integer> ANTI =
		new ArrayList<>(Arrays.asList(
			ItemID.ANTIDOTE4_5952,
			ItemID.ANTIDOTE3_5954,
			ItemID.ANTIDOTE2_5956,
			ItemID.ANTIDOTE1_5958,
			ItemID.ANTIVENOM1_12919,
			ItemID.ANTIVENOM2_12917,
			ItemID.ANTIVENOM3_12915,
			ItemID.ANTIVENOM4_12913
		));
	public static final ArrayList<Integer> COMBAT =
		new ArrayList<>(Arrays.asList(
			ItemID.SUPER_COMBAT_POTION1,
			ItemID.SUPER_COMBAT_POTION2,
			ItemID.SUPER_COMBAT_POTION3,
			ItemID.SUPER_COMBAT_POTION4)
		);
	public static final ArrayList<Integer> STAM =
		new ArrayList<>(Arrays.asList(
			ItemID.STAMINA_POTION1,
			ItemID.STAMINA_POTION2,
			ItemID.STAMINA_POTION3,
			ItemID.STAMINA_POTION4
		));
	public static final ArrayList<Integer> BREW =
		new ArrayList<>(Arrays.asList(
			ItemID.SARADOMIN_BREW1,
			ItemID.SARADOMIN_BREW2,
			ItemID.SARADOMIN_BREW3,
			ItemID.SARADOMIN_BREW4)
		);
//	public static ArrayList<Integer> getThrallItems = new ArrayList<>(
//		Arrays.asList(
//			ItemID.BOOK_OF_THE_DEAD,
//			ItemID.RUNE_POUCH));

	public static boolean isDrained(Skill skill)
	{
		return Static.getClient().getBoostedSkillLevel(skill) < Static.getClient().getRealSkillLevel(skill);
	}

	public static boolean isDrainedMore(Skill skill, int amount)
	{
		return Static.getClient().getBoostedSkillLevel(skill) < (Static.getClient().getRealSkillLevel(skill) - amount);
	}


	public static Widget getBrew()
	{
		for (int i : BREW)
		{
			if (Inventory.search().withId(i).first().orElse(null) != null)
			{
				return Inventory.search().withId(i).first().orElse(null);
			}
		}
		return null;
	}

	public static Widget getAnti()
	{
		for (int i : ANTI)
		{
			if (Inventory.search().withId(i).first().orElse(null) != null)
			{
				return Inventory.search().withId(i).first().orElse(null);
			}
		}
		return null;
	}

	public static Widget getRestore()
	{
		for (int i : RESTORE)
		{
			if (Inventory.search().withId(i).first().orElse(null) != null)
			{
				return Inventory.search().withId(i).first().orElse(null);
			}
		}
		return null;
	}

	public static Widget getSCB()
	{
		for (int i : COMBAT)
		{
			if (Inventory.search().withId(i).first().orElse(null) != null)
			{
				return Inventory.search().withId(i).first().orElse(null);
			}
		}
		return null;
	}

	public static Widget getRange()
	{
		for (int i : RANGE)
		{
			if (Inventory.search().withId(i).first().orElse(null) != null)
			{
				return Inventory.search().withId(i).first().orElse(null);
			}
		}
		return null;
	}

	public static Widget getAngler()
	{
		return Inventory.search().withId(ItemID.ANGLERFISH).first().orElse(null);
	}

	public boolean consume(Widget consumable)
	{
		if (consumable == null
			|| consumable.getActions() == null)
		{
			return false;
		}
		String action = "Drink";
		for (String s : consumable.getActions())
		{
			if (s != null
				&& s.equalsIgnoreCase("eat"))
			{
				action = "Eat";
				break;
			}
		}
		MousePackets.queueClickPacket();
		WidgetPackets.queueWidgetAction(consumable, action);
		nexManager.shouldReattack = true;
		return true;
	}

	public boolean prePot(int item)
	{
		if (!InventoryUtil.contains(item))
		{
			if (!BankUtil.contains(item))
			{
				nexManager.print("Missing " + itemManager.getItemComposition(item).getName());
			}
			else
			{
				BankUtil.withdrawOne(item);
			}
		}
		else
		{
			Widget boost = InventoryUtil.getFirst(item);
			int slot = 0;
			ItemContainer invent = Static.getClient().getItemContainer(InventoryID.INVENTORY.getId());
			if (invent != null)
			{
				for (int i = 0; i < 28; i++)
				{
					Item inventoryItem = invent.getItem(i);
					if (inventoryItem != null && inventoryItem.getId() == boost.getItemId())
					{
						slot = i;
						break;
					}
				}
			}
			if (Bank.isOpen() && boost != null)
			{
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetActionPacket(9, WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getId(), boost.getItemId(), slot);
			}
			else
			{
				MousePackets.queueClickPacket();
				WidgetPackets.queueWidgetAction(boost, "Drink");
			}
			nexManager.print("Drinking/eating " + boost.getName());
		}
		return true;
	}

	public boolean consumeBrew()
	{
		return consume(getBrew());
	}

	public boolean consumeRestore()
	{
		return consume(getRestore());
	}

	public boolean consumeSCB()
	{
		return consume(getSCB());
	}

	public boolean consumeRange()
	{
		return consume(getRange());
	}

	public boolean consumeAnglerf()
	{
		return consume(getAngler());
	}
}

