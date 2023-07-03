package com.example.toagigatron.tasks.kephri.puzzle;

import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Static;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.WallObject;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "Switching puzzles kephri",
	priority = 1,
	blocking = true
)
public class SwitchPuzzleKephri extends StagedTask
{
	private final ToaManager toaManager;

	@Inject
	Client client;

	@Inject
	public SwitchPuzzleKephri(ToaManager toaManager)
	{
		super(toaManager, Stage.KEPHRI_PUZZLE);
		this.toaManager = toaManager;
	}

	public boolean execute()
	{
		WallObject passage = ObjectUtil.getWallObject(ToaConstants.PASSAGE_KEPHRI);
		GameObject platform = ObjectUtil.getObject(ToaConstants.PLATFORM_KEPHRI);
		WorldPoint refPoint = Static.getClient().getLocalPlayer().getWorldLocation();
		if (toaManager.kephri.finalKephriPuzzle == null
			|| toaManager.kephri.finalKephriPuzzle.solved
			|| platform == null
			|| passage == null)
		{
			return false;
		}
		toaManager.print("switch puzzle");

		if (client.getLocalPlayer().getAnimation() == ToaConstants.CRAWLING_IN_TUNNEL_KEPHRI_PUZZLE
			|| client.getLocalPlayer().getAnimation() == ToaConstants.JUMPING_OVER_PLATFORM_KEPHRI_PUZZLE)
		{
			return false;
		}
		if (toaManager.kephri.currentKephriPuzzle.index == 1 && refPoint.getY() < passage.getWorldLocation().getY())
		{
			toaManager.print("Crawling through " + passage.getWorldLocation());
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(passage, false, "Crawl-through");
		}
		else if (toaManager.kephri.currentKephriPuzzle.index == 2 && refPoint.getY() > passage.getWorldLocation().getY() && !toaManager.kephri.currentKephriPuzzle.roomArea.contains(refPoint))
		{
			toaManager.print("Jump-to " + platform.getWorldLocation());
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(platform, false, "Jump-to");
		}
		else
		{
			return false;
		}
		return true;
	}
}
