package com.example.toagigatron.tasks.zebak.puzzle;

import com.example.EthanApiPlugin.Collections.ETileItem;
import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.EthanApiPlugin.Collections.TileItems;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Packets.TileItemPackets;
import com.example.Packets.WidgetPackets;
import com.example.Utility.Movement;
import com.example.Utility.Static;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Consumables;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.puzzlemodel.ZebakWaterfallRoom;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Solving puzzle Zebak",
	priority = 1,
	register = true
)
public class SolvePuzzleZebak extends StagedTask
{
	private final ToaManager toaManager;
	private boolean isJugFilled;

	@Inject
	public SolvePuzzleZebak(ToaManager toaManager)
	{
		super(toaManager, Stage.ZEBAK_PUZZLE);
		this.toaManager = toaManager;
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM || chatMessage.getType() == ChatMessageType.CONSOLE || chatMessage.getType() == ChatMessageType.ENGINE)
		{
			String message = chatMessage.getMessage().toLowerCase();
			if (message.contains("you fill your container"))
			{
				isJugFilled = true;
			}
			else if (message.contains("you empty"))
			{
				isJugFilled = false;
			}
			else if (message.contains("challenge complete"))
			{
				isJugFilled = false;
			}
		}
	}

	public boolean execute()
	{
		ETileItem jugOnFloor = TileItems.search().withId(ToaConstants.ZEBAK_GROUND_JUG).nearestToPlayer().orElse(null);
		WorldPoint playerPoint = Static.getClient().getLocalPlayer().getWorldLocation();
		NPC tree = NPCs.search().nameContains("Palm of Resourcefulness").filter(n -> n.getName() != null).first().orElse(null);
		ZebakWaterfallRoom currentPuzzle = toaManager.zebak.currentZebakPuzzle;
		if (!toaManager.zebak.isPuzzleActive() || tree == null || playerPoint == null || currentPuzzle == null)
		{
			if (!toaManager.zebak.isPuzzleActive())
			{
				toaManager.print("puzzle active");
				return false;
			}
			if (tree == null)
			{
				toaManager.print("tree null");
				return false;
			}
			if (currentPuzzle == null)
			{
				toaManager.print("puzzle null");
				return false;
			}
			return false;
		}
		if (Inventory.search().withId(ToaConstants.ZEBAK_GROUND_JUG).first().orElse(null) == null && jugOnFloor != null)
		{
			if (Inventory.getEmptySlots() == 0)
			{
				Widget brewToDrop = Consumables.getBrew();
				if (brewToDrop != null)
				{
					toaManager.print("Dropping " + brewToDrop.getName() + " to make space");
					MousePackets.queueClickPacket();
					WidgetPackets.queueWidgetAction(brewToDrop, "Drop");
				}
			}
			else
			{
				toaManager.print("Picking up jug");
				MousePackets.queueClickPacket();
				TileItemPackets.queueTileItemAction(jugOnFloor, false);
				return true;
			}
		}
		Widget inventoryJug = Inventory.search().withId(ToaConstants.ZEBAK_GROUND_JUG).first().orElse(null);

		if (!isJugFilled)
		{
			if (playerPoint.getX() != currentPuzzle.prePathTile.getX())
			{
				toaManager.print("Walking to prepath tile");
				Movement.walk(currentPuzzle.prePathTile);
			}
			else if (inventoryJug != null)
			{
				toaManager.print("Using on waterfall");
				MousePackets.queueClickPacket();
				ObjectPackets.queueWidgetOnTileObject(inventoryJug, currentPuzzle.waterfall);
			}
		}
		if (isJugFilled && inventoryJug != null)
		{
			toaManager.print("Using on tree");
			MousePackets.queueClickPacket();
			NPCPackets.queueWidgetOnNPC(tree, inventoryJug);
		}

		return true;
	}
}
