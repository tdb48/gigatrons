package com.example.Utility;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.TileItems;
import java.util.ArrayList;
import net.runelite.api.TileItem;

public class TileItemUtil
{

	public static ArrayList<TileItem> getAll(String... name)
	{
		ArrayList<TileItem> returnList = new ArrayList<>();
		for (String s : name)
		{
			for (ETileItem eTileItem : TileItems.search().withName(s).result())
			{
				returnList.add(eTileItem.getTileItem());
			}
		}
		return returnList;
	}

	public static ArrayList<ETileItem> getAllETileItems(int[] ids)
	{
		ArrayList<ETileItem> returnList = new ArrayList<>();
		for (int id : ids)
		{
			returnList.addAll(TileItems.search().withId(id).result());
		}
		return returnList;
	}

	public static ArrayList<ETileItem> getAllETileItems(String... name)
	{
		ArrayList<ETileItem> returnList = new ArrayList<>();
		for (String s : name)
		{
			returnList.addAll(TileItems.search().withName(s).result());
		}
		return returnList;
	}
}
