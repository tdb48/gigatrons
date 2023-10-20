package com.example.Utility;

import java.util.Arrays;
import net.runelite.api.GameState;
import net.runelite.api.VarClientInt;

public class Tabs
{
	public static void open(Tab tab)
	{
		if (Static.getClient() == null || Static.getClient().getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		invoke(() -> Static.getClient().runScript(915, tab.getIndex()));
	}

	public static boolean isOpen(Tab tab)
	{
		return Static.getClient().getVarcIntValue(VarClientInt.INVENTORY_TAB) == Arrays.asList(Tab.values()).indexOf(tab);
	}

	public static void invoke(Runnable runnable)
	{
		if (Static.getClient().isClientThread())
		{
			runnable.run();
		}
		else
		{
			Static.getClientThread().invokeLater(runnable);
		}
	}
}
