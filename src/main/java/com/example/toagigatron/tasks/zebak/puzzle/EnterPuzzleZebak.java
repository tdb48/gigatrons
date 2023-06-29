package com.example.toagigatron.tasks.zebak.puzzle;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.Combat;
import com.example.Utility.Dialog;
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
import net.runelite.api.GameObject;

@TaskDescriptor(
	name = "Entering puzzle Zebak",
	priority = 1,
	register = true
)
public class EnterPuzzleZebak extends StagedTask
{
	private final ToaManager toaManager;

	@Inject
	public EnterPuzzleZebak(ToaManager toaManager)
	{
		super(toaManager, Stage.ZEBAK_PUZZLE);
		this.toaManager = toaManager;
	}

	public boolean execute()
	{
		GameObject entrance = ObjectUtil.getNearestGameObject(ToaConstants.ZEBAK_PUZZLE_ENTRANCE);
		GameObject barrier = ObjectUtil.getNearestGameObject((ToaConstants.BARRIER);
		GameObject tree = ObjectUtil.getNearestGameObject((ToaConstants.ZEBAK_TREE_GAME_OBJECT);
		if (barrier == null
			|| tree == null
			|| entrance == null
			|| toaManager.zebak.isPuzzleActive()
//			|| !Reachable.isWalkable(entrance.getWorldLocation().dx(-2))
			|| Static.getClient().getLocalPlayer().getWorldLocation().getX() < barrier.getWorldLocation().getX())
		{
			return false;
		}
		if (Dialog.hasOption("Yes."))
		{
			Dialog.chooseOption("Yes.");
		}
		else
		{
			toaManager.zebak.generateZebakWaterfallRooms(tree);
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(barrier,false,"Pass");
		}
		return true;
	}
}
