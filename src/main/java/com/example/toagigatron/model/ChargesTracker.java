package com.example.toagigatron.model;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Dart;
import com.example.toagigatron.model.constants.ToaConstants;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginManager;

public class ChargesTracker
{
	EventBus eventBus;
	Client client;

	PluginManager pluginManager;
	ToaManager toaManager;

	public void register()
	{
		this.eventBus.register(this);
	}

	public void unregister()
	{
		this.eventBus.unregister(this);
	}

	@Inject
	public ChargesTracker(EventBus eventBus, Client client, PluginManager pluginManager, ToaManager toaManager)
	{
		this.eventBus = eventBus;
		this.client = client;
		this.pluginManager = pluginManager;
		this.toaManager = toaManager;
	}

	public Dart dartType = null;
	public int blowpipeDarts = -1;
	public int blowpipeScales = -1;
	public int mageCharges = -1;
	public static final int minimumScales = 1000;
	public static final int minimumDarts = 200;
	public static final int minimumMageCharges = 200;

	public void reset()
	{
		dartType = null;
		blowpipeDarts = -1;
		blowpipeScales = -1;
		mageCharges = -1;
	}

	public boolean shouldRechargeBlowpipe()
	{
		return blowpipeScales < minimumScales;
	}

	public boolean shouldRefillBlowpipe()
	{
		return blowpipeDarts < minimumDarts;
	}

	public boolean shouldRechargeMageWeapon()
	{
		return mageCharges < minimumMageCharges;
	}

	public boolean shouldRechargeAnything()
	{

		if (Inventory.search().idInList(List.of(ToaConstants.BLOWPIPE_EMPTY, ToaConstants.BLOWPIPE_CHARGED)).result().isEmpty()
			|| Inventory.getItemAmount(toaManager.mageSetup.weapon) == 0)
		{
			return false;
		}
		if (shouldRechargeBlowpipe())
		{
			return true;
		}
		if (shouldRefillBlowpipe())
		{
			return true;
		}
		return shouldRechargeMageWeapon();
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned)
	{
		GameObject g = gameObjectSpawned.getGameObject();
		if (g.getId() == ToaConstants.GROUPING_OBELISK)
		{
			toaManager.print("Resetting charges bc obelisk spawned");
			reset();
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM || chatMessage.getType() == ChatMessageType.CONSOLE || chatMessage.getType() == ChatMessageType.ENGINE)
		{
			String message = chatMessage.getMessage().toLowerCase();
			if (message.contains("dart"))
			{
				for (Dart dart : Dart.values())
				{
					if (message.contains(dart.name))
					{
						dartType = dart;
						System.out.println("Found dart type: " + dartType.name);
					}
				}
				// Remove comma's
				String dartsMessage = message.replaceAll(",", "");
				// Only get the part of the message about scales
				if (dartsMessage.split("x ").length <= 1)
				{
					return;
				}
				String dartsPartOfMessage = dartsMessage.split("x ")[1];
				// Remove the color of the text
				String dartsNoColor = dartsPartOfMessage.replaceAll("</col>", "");
				// Remove everything else thats left
				String darts = dartsNoColor.split("\\.")[0];
				System.out.println("Found amount of darts: " + darts);
				blowpipeDarts = Integer.parseInt(darts);
			}

			if (message.contains("scales"))
			{
				// Remove comma's
				String scaleMessage = message.replaceAll(",", "");
				// Only get the part of the message about scales
				if (scaleMessage.split("scales").length <= 1)
				{
					return;
				}
				String scalePartOfMessage = scaleMessage.split("scales")[1];
				// Remove the color of the text
				String scalesNoColor = scalePartOfMessage.replaceAll("<col=007f00>", "");
				// Remove everything else thats left
				String scales = scalesNoColor.split(" ")[1];
				System.out.println("Found amount of scales: " + scales);
				blowpipeScales = Integer.parseInt(scales);
			}
			if (message.contains("charges"))
			{
				// Remove comma's
				String chargesMessage = message.replaceAll(",", "");
				if (chargesMessage.split("has ").length <= 1)
				{
					return;
				}
				String chargesPart = chargesMessage.split("has ")[1];
				String charges = chargesPart.split(" ")[0];
				System.out.println("Found amount of mage charges: " + charges);
				mageCharges = Integer.parseInt(charges);
			}
			if (message.contains("staff is already fully charged"))
			{
				mageCharges = 20000;
			}
		}
	}

}
