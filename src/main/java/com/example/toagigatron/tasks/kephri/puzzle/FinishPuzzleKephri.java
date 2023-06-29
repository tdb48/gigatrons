package com.example.toagigatron.tasks.kephri.puzzle;

import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.NPCPackets;
import com.example.Packets.ObjectPackets;
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
	name = "Exiting puzzle kephri",
	priority = 1,
	register = true
)
public class FinishPuzzleKephri extends StagedTask
{
	private final ToaManager toaManager;

	@Inject
	public FinishPuzzleKephri(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_PUZZLE);
		this.toaManager = toaManager;
	}

	public boolean execute()
	{
		GameObject exit = ObjectUtil.getNearestGameObject(ToaConstants.EXIT_KEPHRI);
		if (toaManager.kephri.finalKephriPuzzle == null
			|| !toaManager.kephri.finalKephriPuzzle.solved
			|| exit == null
			|| EthanApiPlugin.isMoving())
		{
			return false;
		}
		toaManager.print("Exiting kephri");
		MousePackets.queueClickPacket();
		ObjectPackets.queueObjectAction(exit, false, "Quick-Enter");
		return true;
	}

}
