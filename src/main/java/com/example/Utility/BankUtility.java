package com.example.Utility;

import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.BankInventory;
import com.example.EthanApiPlugin.Collections.Widgets;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Objects;
import java.util.function.Supplier;

@Singleton
public class BankUtility {

    @Inject
    private Client client;

    public static int getQuantity(int id){
        if(Bank.search().withId(id).result().size() == 0){
            return 0;
        }
        return Bank.search().withId(id).first().orElseThrow().getItemQuantity();
    }
    public static int getQuantity(String name){
        if(Bank.search().withName(name).result().size() == 0){
            return 0;
        }
        return Bank.search().withName(name).first().orElseThrow().getItemQuantity();
    }

    public static int getInventoryQuantity(int id){
        if(BankInventory.search().withId(id).result().size() == 0){
            return 0;
        }
        return BankInventory.search().withId(id).first().orElseThrow().getItemQuantity();
    }
    public static int getInventoryQuantity(String name){
        if(BankInventory.search().withName(name).result().size() == 0){
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
