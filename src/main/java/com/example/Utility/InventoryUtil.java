package com.example.Utility;

import com.example.EthanApiPlugin.Collections.BankInventory;
import com.example.EthanApiPlugin.Collections.Equipment;
import com.example.EthanApiPlugin.Collections.EquipmentItemWidget;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.Widgets;
import java.util.ArrayList;
import net.runelite.api.GameState;
import net.runelite.api.widgets.Widget;

public class InventoryUtil
{

	public static Widget getFirst(int[] ids)
	{
		for (int id : ids)
		{
			Widget w = Widgets.search().withItemId(id).first().orElse(null);
			if (w != null)
			{
				return w;
			}
		}
		return null;
	}

	public static Widget getFirst(String name)
	{
		if (!Static.getClient().getGameState().equals(GameState.LOGGED_IN))
		{
			return null;
		}
		return Widgets.search().nameContains(name).first().orElse(null);
	}

	public static Widget getFirst(int id)
	{
		return Widgets.search().withItemId(id).first().orElse(null);
	}

	public static boolean contains(int id)
	{
		return Inventory.getItemAmount(id) > 0;
	}

	public static boolean contains(int... id)
	{
		for (int i : id)
		{
			if (Inventory.getItemAmount(i) > 0)
			{
				return true;
			}
		}
		return false;
	}

	public static boolean contains(String name)
	{
		return !Inventory.search().nameContains(name).empty();
	}

	public static boolean isFull()
	{
		return Inventory.getEmptySlots() == 0;
	}

	public static ArrayList<Widget> getAll(String... name)
	{
		ArrayList<Widget> returnList = new ArrayList<>();
		for (String s : name)
		{
			returnList.addAll(Inventory.search().nameContains(s).result());
		}
		return returnList;
	}

	public static ArrayList<Widget> getAll(int... id)
	{
		ArrayList<Widget> returnList = new ArrayList<>();
		for (int i : id)
		{
			returnList.addAll(Inventory.search().withId(i).result());
		}
		return returnList;
	}

	public static ArrayList<Integer> getAllInventoryItems()
	{
		ArrayList<Integer> returnList = new ArrayList<>();
		for (Widget widget : Inventory.search().result())
		{
			returnList.add(widget.getItemId());
		}
		returnList.removeIf(n -> n == 0 || n == -1);
		return returnList;
	}

	public static ArrayList<Integer> getAllEquippedItems()
	{
		ArrayList<Integer> returnList = new ArrayList<>();
		for (EquipmentItemWidget widget : Equipment.search().result())
		{
			returnList.add(widget.getEquipmentItemId());
		}
		returnList.removeIf(n -> n == 0 || n == -1);
		return returnList;
	}

	public static ArrayList<Integer> getAllPlayerItems()
	{
		ArrayList<Integer> returnList = new ArrayList<>(getAllEquippedItems());
		returnList.addAll(getAllInventoryItems());
		returnList.removeIf(n -> n == 0 || n == -1);
		return returnList;
	}

	public static boolean hasItem(ArrayList<Integer> items)
	{
		ArrayList<Integer> inventory = getAllPlayerItems();
		inventory.removeIf(n -> !items.contains(n));
		return inventory.size() != 0;
	}
}
