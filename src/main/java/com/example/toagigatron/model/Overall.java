package com.example.toagigatron.model;


import com.example.EthanApiPlugin.Collections.Bank;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.Utility.BankUtility;
import com.example.toagigatron.ToaGigatronConfig;
import com.example.toagigatron.ToaGigatronPlugin;
import com.example.toagigatron.manager.ToaManager;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import java.time.Instant;

public class Overall
{
    @Inject
    ToaGigatronPlugin toaGigatronPlugin;
    @Inject
    ToaManager toaManager;
    @Inject
    EventBus eventBus;
    public int killCount = 0;
    public int deaths = 0;
    public boolean died;
    public boolean lockedChest;
    public boolean lootClaimed;
    public Instant botTimer = Instant.now();
    public int saltInTicks = 0;

    public int totalResigns = 0;

    public void register()
    {
        this.eventBus.register(this);
    }

    public void unregister()
    {
        this.eventBus.unregister(this);
    }

    //todo - DONE, REPLACED IN GAME TICK METHOD HOW GOOD
//    @Subscribe
//    public void onWidgetHiddenChanged(WidgetHiddenChanged event)
//    {
//        if (event.getWidget().getId() != WidgetInfo.DIALOG_NOTIFICATION_CONTINUE.getId())
//        {
//            return;
//        }
//        Widget w = event.getWidget();
//        if (w != null && w.getText() != null)
//        {
//            String msg = w.getText();
//            if (msg.contains("The chest seems to be empty. If it did have any of your items, but"))
//            {
//                lockedChest = false;
//                died = false;
//            }
//        }
//    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM || chatMessage.getType() == ChatMessageType.CONSOLE || chatMessage.getType() == ChatMessageType.ENGINE)
        {
            String message = chatMessage.getMessage().toLowerCase();
            if (message.contains("oh dear, you are dead")
                    || message.contains("you have died"))
            {

                lockedChest = true;
                deaths++;
                died = true;
                toaGigatronPlugin.resetAllModels();
            }
            if (message.contains("you crush the salts"))
            {
                saltInTicks = 800;
            }
            if (message.contains("your completed tombs of amascut"))
            {
                lootClaimed = false;
                killCount++;
            }
            if (message.contains("payment has been taken"))
            {
                lockedChest = false;
            }
            if (message.contains("payment has been taken"))
            {
                lockedChest = false;
            }
            if (message.contains("there's nothing to take"))
            {
                lockedChest = false;
                died = false;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)
    {
        if (saltInTicks > 0)
        {
            saltInTicks--;
        }
        if (BankUtility.isOpen())
        {
            if (!BankUtility.isMainTabOpen())
            {
                toaManager.print("Opening main tab");
                BankUtility.openMainTab();
            }

            else if (!BankUtility.contains(ItemID.COINS_995) || BankUtility.getFirst(ItemID.COINS_995).getItemQuantity() < 500000)
            {
                toaGigatronPlugin.stopPlugin = true;
                toaManager.print("Not enough coins");
            }
        }
        if(!Widgets.search().withTextContains("The chest seems to be empty. If it did have any of your items, but").empty()){
            lockedChest = false;
            died = false;
        }

    }

    public void reset()
    {
    }

    public void fullReset()
    {
        lootClaimed = false;
        lockedChest = false;
        died = false;
        botTimer = Instant.now();
        killCount = 0;
        deaths = 0;
        totalResigns = 0;
    }
}

