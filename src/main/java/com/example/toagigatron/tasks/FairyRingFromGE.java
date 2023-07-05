package com.example.toagigatron.tasks;


import com.example.EthanApiPlugin.PathFinding.GlobalCollisionMap;
import com.example.Packets.MousePackets;
import com.example.Packets.ObjectPackets;
import com.example.Utility.ObjectUtil;
import com.example.Utility.Walker;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.GameObject;
import net.runelite.api.ObjectComposition;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	name = "FairyRingFromGE",
	priority = 80
)
public class FairyRingFromGE extends StagedTask
{
	// We use the west wall of the GE as reference, if you are west of the west wall, that means you are ready to use the fairy ring
	// otherwise we path to the shortcut to take it
	private final static WorldPoint WEST_GE_WALL = new WorldPoint(3138, 3501, 0);
	private final static WorldPoint TILE_NEXT_TO_TUNNEL = new WorldPoint(3144, 3511, 0);

	private final static int UNDERWALL_TUNNEL = 16530;
	private final static int FAIRY_RING = 29495;
	private final static String FAIRY_RING_OPTION = "Last-destination (AKP)";

	@Inject
	public FairyRingFromGE(ToaManager toaManager)
	{
		super(toaManager, Stage.GRAND_EXCHANGE);
	}

	public boolean execute()
	{
		GameObject tunnel = ObjectUtil.getNearestGameObject(UNDERWALL_TUNNEL);
		GameObject fairyRing = ObjectUtil.getNearestGameObject(FAIRY_RING);
		WorldPoint playerPoint = client.getLocalPlayer().getWorldLocation();
		// If we are west of the wall, use the fairy ring
		boolean hasAction = false;
		if (fairyRing != null && !ObjectUtil.hasAction(fairyRing, FAIRY_RING_OPTION))
		{
			ObjectComposition fairyComp = client.getObjectDefinition(fairyRing.getId());
			ObjectComposition imposterComp = client.getObjectDefinition(fairyComp.getImpostor().getId());
			for (String s : imposterComp.getActions())
			{
				if (s != null && s.toLowerCase().equalsIgnoreCase(FAIRY_RING_OPTION))
				{
					hasAction = true;
					break;
				}
			}
		}

		if (fairyRing != null
			&& tunnel != null
			&& hasAction
			&& playerPoint.getX() <= WEST_GE_WALL.getX())
		{
			toaManager.print("Using fairy ring");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(fairyRing, false, FAIRY_RING_OPTION);
			return true;
		}
		else if (tunnel != null && playerPoint.distanceTo(tunnel.getWorldLocation()) <= 10)
		{
			toaManager.print("Crawling through the tunnel");
			MousePackets.queueClickPacket();
			ObjectPackets.queueObjectAction(tunnel, false, "Climb-into");
			return true;
		}
		else
		{
			ArrayList<WorldPoint> path = (ArrayList<WorldPoint>) GlobalCollisionMap.findPath(TILE_NEXT_TO_TUNNEL);
			toaManager.print("Walking next to tunnel");
			Walker.stepAlongBigSteps(path);
			return true;
		}
	}
}
