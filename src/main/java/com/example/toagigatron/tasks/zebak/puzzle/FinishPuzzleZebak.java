package com.example.toagigatron.tasks.zebak.puzzle;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Combat;
import com.example.Utility.Movement;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Reachable;
import com.example.Utility.Static;
import com.example.toagigatron.manager.GameTickManager;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameObject;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;

@TaskDescriptor(
	name = "Exiting puzzle Zebak",
	priority = 5,
	register = true,
	blocking = true
)
public class FinishPuzzleZebak extends StagedTask
{
	private final ToaManager toaManager;

	@Inject
	public FinishPuzzleZebak(ToaManager toaManager)
	{
		super(toaManager, Stage.ZEBAK_PUZZLE);
		this.toaManager = toaManager;
	}

	public boolean execute()
	{
		GameObject exit = ObjectUtil.getNearestGameObject(ToaConstants.ZEBAK_PUZZLE_EXIT);
		GameObject barrier = ObjectUtil.getNearestGameObject(ToaConstants.BARRIER);

		if (toaManager.zebak.isPuzzleActive()
			|| exit == null
			|| barrier == null
			|| Static.getClient().getLocalPlayer().getAnimation() != -1
			|| Static.getClient().getLocalPlayer().getWorldLocation().getX() > barrier.getWorldLocation().getX()
			|| toaManager.zebak.currentZebakPuzzle != null)
		{
			return false;
		}
		toaManager.print("Exiting Zebak");
		MousePackets.queueClickPacket();
		ObjectPackets.queueObjectAction(exit, false, "Quick-Enter");
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
				toaManager.print("Resetting state");
				toaManager.zebak.southWestZebakPuzzle = null;
				toaManager.zebak.northWestZebakPuzzle = null;
				toaManager.zebak.northEastZebakPuzzle = null;
				toaManager.zebak.southEastZebakPuzzle = null;
				toaManager.zebak.currentZebakPuzzle = null;
			}
		}
	}
}
