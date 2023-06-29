package com.example.toagigatron.tasks.kephri.puzzle;

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
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;
@TaskDescriptor(
	name = "Entering puzzle kephri",
	priority = 1,
	blocking = true
)
public class EnterPuzzleKephri extends StagedTask
{
	private final ToaManager toaManager;

	@Inject
	public EnterPuzzleKephri(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_PUZZLE);
		this.toaManager = toaManager;
	}

	public boolean execute()
	{
		GameObject barrier = ObjectUtil.getNearestGameObject(ToaConstants.BARRIER);
		if(barrier == null){
//			toaManager.print("Returning false in enter kephri as barrier is null");
			return false;
		}
		WorldPoint barrierTile = barrier.getWorldLocation().dx(-1);
		if(!Reachable.isWalkable(barrierTile)){
//			toaManager.print("Returning false as we must be past the door because the barrier tile is not walkable");
			return false;
		}
		WorldPoint playerPoint = Static.getClient().getLocalPlayer().getWorldLocation();
		if(client.getSelectedSceneTile() == null && !playerPoint.equals(barrierTile)){
			Movement.walk(barrierTile);
			toaManager.print("Walking to barrier tile at -> " + toaManager.worldPointString(barrierTile));
			return true;
		}
		toaManager.kephri.generateKephriPuzzleRooms(barrier);
		toaManager.print("Attempting to enter kephri puzzle");
		toaManager.print("Puzzle is -> " + toaManager.kephri.firstKephriPuzzle.roomType.name());
		MousePackets.queueClickPacket();
		ObjectPackets.queueObjectAction(barrier,false,"Quick-Pass");
		return true;
	}
}
