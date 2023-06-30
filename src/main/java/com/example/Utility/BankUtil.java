package com.example.Utility;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.BankInventory;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.Packets.MousePackets;
import com.example.Packets.WidgetPackets;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import org.jocl.CL;

import javax.inject.Singleton;
import java.util.List;
import java.util.Objects;

@Singleton
public class BankUtil
{

	private static final Client client = Static.getClient();

	public static boolean contains(int itemID)
	{
		return Widgets.search().withItemId(itemID).result().size() > 0;
	}

	public static Widget getFirst(int itemID)
	{
		return Widgets.search().withItemId(itemID).first().orElse(null);
	}

	public static boolean isOpen()
	{
		return WidgetUtil.isVisible(client.getWidget(WidgetInfo.BANK_ITEM_CONTAINER));
	}

	public static boolean isMainTabOpen()
	{
		return isTabOpen(0);
	}

	public static boolean isTabOpen(int index)
	{
		return client.getVarbitValue(4150) == index;
	}

	public static void openTab(int index)
	{
		if (index >= 0 && index <= getTabs().size())
		{
			Widget tabContainer = client.getWidget(WidgetInfo.BANK_TAB_CONTAINER);
			if (WidgetUtil.isVisible(tabContainer) && !isTabOpen(index))
			{
				Widget tab = tabContainer.getChild(10 + index);
				if (WidgetUtil.isVisible(tab))
				{
					MousePackets.queueClickPacket();
					if (index == 10)
					{
						WidgetPackets.queueWidgetAction(tab, "View all items");
					}
					else
					{
						WidgetPackets.queueWidgetAction(tab, "View tab");
					}
				}
			}
		}
	}

	public static void openMainTab()
	{
		openTab(0);
	}

	public static List<Widget> getTabs()
	{
		return Widgets.search().withAction("Collapse tab").withParentId(WidgetInfo.BANK_TAB_CONTAINER.getId()).result();
	}

	public static void withdrawAll(int id)
	{
		if (!BankUtil.isOpen())
		{
			return;
		}
		Widget bankItem = Bank.search().withId(id).first().orElse(null);
		if (bankItem != null)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(bankItem, "Withdraw-All");
		}
	}

	public static void withdrawOne(int id)
	{
		if (!BankUtil.isOpen())
		{
			return;
		}
		Widget bankItem = Bank.search().withId(id).first().orElse(null);
		if (bankItem != null)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(bankItem, "Withdraw-1");
		}
	}

	public static void depositInventory(){
		Widget widget = client.getWidget(WidgetInfo.BANK_DEPOSIT_INVENTORY);
		if(widget != null){
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(widget, "Deposit Inventory");
		}
	}

	public static void depositEquipment() {
		Widget widget = client.getWidget(WidgetInfo.BANK_DEPOSIT_EQUIPMENT);
		if (widget != null) {
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(widget, "Deposit worn items");
		}

	}

	public static void depositAll(int id)
	{
		if (!BankUtil.isOpen())
		{
			return;
		}
		Widget bankItem = BankInventory.search().withId(id).first().orElse(null);
		if (bankItem != null)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(bankItem, "Deposit-All");
		}
	}

	public static void depositOne(int id)
	{
		if (!BankUtil.isOpen())
		{
			return;
		}
		Widget bankItem = BankInventory.search().withId(id).first().orElse(null);
		if (bankItem != null)
		{
			MousePackets.queueClickPacket();
			WidgetPackets.queueWidgetAction(bankItem, "Deposit-1");
		}
	}

	public static int getQuantity(int id)
	{
		if (Bank.search().withId(id).result().size() == 0)
		{
			return 0;
		}
		return Bank.search().withId(id).first().orElseThrow().getItemQuantity();
	}

	public static int getQuantity(String name)
	{
		if (Bank.search().withName(name).result().size() == 0)
		{
			return 0;
		}
		return Bank.search().withName(name).first().orElseThrow().getItemQuantity();
	}

	public static int getInventoryQuantity(int id)
	{
		if (BankInventory.search().withId(id).result().size() == 0)
		{
			return 0;
		}
		return BankInventory.search().withId(id).first().orElseThrow().getItemQuantity();
	}

	public static int getInventoryQuantity(String name)
	{
		if (BankInventory.search().withName(name).result().size() == 0)
		{
			return 0;
		}
		return BankInventory.search().withName(name).first().orElseThrow().getItemQuantity();
	}

	public Widget close()
	{
		Widget exitBank = Objects.requireNonNull(client.getWidget(786434)).getChild(11);
		if (exitBank != null && !exitBank.isHidden() && !exitBank.isSelfHidden())
		{
			return null;
		}
		return exitBank;
	}

}
