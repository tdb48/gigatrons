package com.example.toagigatron.tasks.kephri.puzzle;

import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Movement;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.model.puzzlemodel.KephriTilePuzzle;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.TileObject;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Solve final puzzle",
	priority = 1,
	register = true
)
public class SolveFinalPuzzle extends StagedTask
{
	private final ToaManager toaManager;

	@Inject
	public SolveFinalPuzzle(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_PUZZLE);
		this.toaManager = toaManager;
		toaManager.kephri.kephriTilePuzzles.add(KephriTilePuzzle.STAR);
		toaManager.kephri.kephriTilePuzzles.add(KephriTilePuzzle.BOOT);
		toaManager.kephri.kephriTilePuzzles.add(KephriTilePuzzle.DIAMOND);
		toaManager.kephri.kephriTilePuzzles.add(KephriTilePuzzle.WIGGLE);
		toaManager.kephri.kephriTilePuzzles.add(KephriTilePuzzle.KNIVES);
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned e)
	{
		int gameId = e.getGameObject().getId();
		if (gameId != 45389 && gameId != 45391 && gameId != 45387 && gameId != 45394 && gameId != 45395)
		{
			return;
		}
		for (KephriTilePuzzle kephriTilePuzzle : toaManager.kephri.kephriTilePuzzles)
		{
			if (kephriTilePuzzle.light == gameId)
			{
				toaManager.kephri.solvedTiles.add(e.getGameObject().getWorldLocation());
			}
		}
	}

	public boolean execute()
	{
		if (toaManager.kephri.currentKephriPuzzle != null && toaManager.kephri.currentKephriPuzzle.index != 2)
		{
			return false;
		}
		TileObject flippedTile = null;
		TileObject targetTile = null;
		toaManager.print("final puzzle");
		for (KephriTilePuzzle kephriTilePuzzle : toaManager.kephri.kephriTilePuzzles)
		{
			TileObject tempFlippedTile = ObjectUtil.getNearestTileObject(kephriTilePuzzle.flipped);
			// IF there is a flipped tile AND there is no light on it
			if (tempFlippedTile != null && !toaManager.kephri.solvedTiles.contains(tempFlippedTile.getWorldLocation()))
			{
//				System.out.println("Found flipped tile! at " + tempFlippedTile.getWorldLocation());
				flippedTile = tempFlippedTile;
				break;
			}
		}

		for (KephriTilePuzzle kephriTilePuzzle : toaManager.kephri.kephriTilePuzzles)
		{
			// If there is a flipped tile without light and the id matches of an unflipped tile, that is the target tile
			TileObject potentialTargetTile = ObjectUtil.getNearestTileObject(kephriTilePuzzle.unflipped);
			if (flippedTile != null && potentialTargetTile != null && flippedTile.getId() == kephriTilePuzzle.flipped)
			{
				targetTile = potentialTargetTile;
//				System.out.println("Found a used tile to flip at " + targetTile.getWorldLocation().toString());
				break;
			}
			else if (potentialTargetTile != null)
			{
				targetTile = potentialTargetTile;
//				System.out.println("Found a NEW tile to flip at " + targetTile.getWorldLocation().toString());
			}
			if (targetTile != null)
			{
				break;
			}
		}

		if (targetTile != null)
		{
			toaManager.print("Flipping unflipped tile at " + targetTile.getWorldLocation().toString());
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(targetTile, false, "Activate");
		}
		else
		{
			return false;
		}
		return true;
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType() == ChatMessageType.GAMEMESSAGE || chatMessage.getType() == ChatMessageType.SPAM || chatMessage.getType() == ChatMessageType.CONSOLE || chatMessage.getType() == ChatMessageType.ENGINE)
		{
			String message = chatMessage.getMessage().toLowerCase();
			if (message.contains("challenge complete"))
			{
				toaManager.kephri.solvedTiles = new ArrayList<>();
			}
		}
	}
}
