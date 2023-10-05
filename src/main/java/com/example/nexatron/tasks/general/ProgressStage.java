package com.example.nexatron.tasks.general;

import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.nexatron.manager.NexManager;
import com.example.nexatron.model.constants.NexConst;
import com.example.nexatron.model.constants.NexSpecial;
import com.example.nexatron.model.constants.Stage;
import com.example.nexatron.taskformat.Task;
import com.example.nexatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import java.util.ArrayList;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	priority = Integer.MAX_VALUE,
	name = "Updating stage",
	register = true
)
public class ProgressStage extends Task
{
	private final NexManager nexManager;

	@Inject
	public ProgressStage(NexManager nexManager)
	{
		this.nexManager = nexManager;
	}

	public boolean run()
	{
		ArrayList<Integer> regions = new ArrayList<>();
		for (int i : Static.getClient().getMapRegions())
		{
			regions.add(i);
		}
		if (NexConst.LOBBY_AREA.contains(nexManager.getPlayerPoint()))
		{
			nexManager.nex.teleportOut = false;
			nexManager.setStage(Stage.LOBBY);
			return true;
		}
		if (NexConst.KC_AREA.contains(nexManager.getPlayerPoint()))
		{
			if (!nexManager.containsStage(Stage.KC_AREA))
			{
				nexManager.setStage(Stage.KC_AREA);
				nexManager.socket.readyToStart = false;
			}
			nexManager.nex.teleportOut = false;
			return true;
		}
		if (NexConst.BANK_AREA.contains(nexManager.getPlayerPoint()))
		{
			if (!nexManager.containsStage(Stage.BANK))
			{
				nexManager.setStage(Stage.BANK);
				nexManager.socket.readyToStart = false;
			}
			nexManager.nex.bankReset();
			return true;
		}
		if (Reachable.isWalkable(nexManager.nex.centerPoint))
		{
			if ((nexManager.getStage().equals(Stage.BANK)
				|| nexManager.getStage().equals(Stage.NONE)))
			{
				nexManager.setStage(Stage.NEX_START);
			}
			return true;
		}
		else
		{
			if (nexManager.getStage().equals(Stage.NEX_ICE)
				|| nexManager.getStage().equals(Stage.MINION_ICE)
				|| nexManager.getStage().equals(Stage.NEX_ZAROS))
			{
				return false;
			}
			nexManager.setStage(Stage.NONE);
			return false;
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE)
		{
			String message = chatMessage.getMessage().toLowerCase();
			if (message.contains("fill my soul"))
			{
				nexManager.nex.invincibleTick = 4;
				nexManager.nex.dashTick = 0;
				nexManager.nex.initSmokeNexTiles();
				nexManager.socket.readyToStart = false;
				nexManager.setStage(Stage.NEX_SMOKE);
			}
			if (message.contains("fumus, don't fail me!"))
			{
				nexManager.nex.initSmokeMinionTiles();
				nexManager.setStage(Stage.MINION_SMOKE);
			}
			if (message.contains("darken my shadow!"))
			{
				nexManager.nex.attacksUntilSpecial = 0;
				nexManager.nex.invincibleTick = 4;
				nexManager.nex.initShadowNexTiles(false);
				nexManager.setStage(Stage.NEX_SHADOW);
			}
			if (message.contains("umbra, don't fail me!"))
			{
				nexManager.nex.attacksUntilSpecial = 0;
				nexManager.nex.initShadowMinionTiles();
				nexManager.setStage(Stage.MINION_SHADOW);
			}
			if (message.contains("flood my lungs with blood!"))
			{
				nexManager.nex.attacksUntilSpecial = 0;
				nexManager.nex.invincibleTick = 4;
				nexManager.nex.nextSpecial = nexManager.nex.nextSpecial.equals(NexSpecial.SHADOWS) ?
					NexSpecial.SACRIFICE :
					NexSpecial.SIPHON;
				nexManager.setStage(Stage.NEX_BLOOD);
			}
			if (message.contains("cruor, don't fail me!"))
			{
				nexManager.nex.attacksUntilSpecial = 0;
				nexManager.nex.initBloodMinionTiles();
				nexManager.setStage(Stage.MINION_BLOOD);
			}
			if (message.contains("infuse me with the power of ice!"))
			{
				nexManager.nex.nextSpecial = nexManager.nex.nextSpecial.equals(NexSpecial.SIPHON) ?
					NexSpecial.PRISON :
					NexSpecial.SACRIFICE;
				nexManager.nex.invincibleTick = 4;
				nexManager.setStage(Stage.NEX_ICE);
			}
			if (message.contains("glacies, don't fail me!"))
			{
				nexManager.nex.initIceMinionTiles();
				nexManager.setStage(Stage.MINION_ICE);
			}
			if (message.contains("now, the power of zaros!"))
			{
				nexManager.nex.invincibleTick = 4;
				nexManager.setStage(Stage.NEX_ZAROS);
			}
			if (message.contains("my wrath"))
			{
				nexManager.nex.initWrathTiles();
				nexManager.nex.invincibleTick = nexManager.socket.isMaster ? 8 : 12;
				nexManager.setStage(Stage.NEX_DEAD);
			}
		}
	}
}
