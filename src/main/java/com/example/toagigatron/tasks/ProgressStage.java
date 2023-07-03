package com.example.toagigatron.tasks;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.Utility.Static;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.Task;
import com.example.toagigatron.taskformat.TaskDescriptor;
import com.google.inject.Inject;
import java.util.ArrayList;
import net.runelite.api.NPC;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;

@TaskDescriptor(
	priority = 20,
	name = "Updating stage"
)
public class ProgressStage extends Task
{
	private final ToaManager toaManager;

	@Inject
	public ProgressStage(ToaManager toaManager)
	{
		this.toaManager = toaManager;
	}

	public boolean run()
	{
		ArrayList<Integer> regions = new ArrayList<>();
		for (int i : Static.getClient().getMapRegions())
		{
			regions.add(i);
		}
		if (regions.contains(13454))
		{
			toaManager.setStage(Stage.OUTSIDE);
			return true;
		}
		else if (regions.contains(14160))
		{
			toaManager.setStage(Stage.INSIDE);
			return true;
		}
		else if (regions.contains(13098))
		{
			toaManager.setStage(Stage.OUTSIDE_TOA);
			return true;

		}
		else if (regions.contains(12598))
		{
			toaManager.setStage(Stage.GRAND_EXCHANGE);
			return true;
		}

		else if (regions.contains(15184))
		{
			LocalPoint localPoint = Static.getClient().getLocalPlayer().getLocalLocation();
			int regionID = WorldPoint.fromLocalInstance(Static.getClient(), localPoint).getRegionID();
			// Wardens loot room
			if (regionID == 14672)
			{
				toaManager.setStage(Stage.INSIDE);
				return true;
			}
			else if (regionID == 15696)
			{
				toaManager.setStage(Stage.WARDENS_P3);
				return true;
			}

			NPC warden = NPCs.search().nameContains(ToaConstants.P2_WARDEN_NAME).first().orElse(null);
			if (warden != null
				&& (warden.getId() == ToaConstants.WARDENS_P2_DOWNED
				|| warden.getId() == ToaConstants.WARDENS_P2_ACTIVE_RANGE_MELEE
				|| warden.getId() == ToaConstants.WARDENS_P2_ACTIVE_MAGE_MELEE))
			{
				toaManager.setStage(Stage.WARDENS_P2);
			}
			else
			{
				toaManager.setStage(Stage.WARDENS_P1);
			}
			return true;
		}
		else if (regions.contains(14162))
		{
			toaManager.setStage(Stage.KEPHRI_PUZZLE);
			return true;
		}
		else if (regions.contains(14164))
		{
			toaManager.setStage(Stage.KEPHRI_BOSS);
			return true;
		}
		else if (regions.contains(15698))
		{
			toaManager.setStage(Stage.ZEBAK_PUZZLE);
			return true;
		}
		else if (regions.contains(15700))
		{
			toaManager.setStage(Stage.ZEBAK_BOSS);
			return true;
		}
		else if (regions.contains(15186))
		{
			toaManager.setStage(Stage.BABA_PUZZLE);
			return true;
		}
		else if (regions.contains(14674))
		{
			toaManager.setStage(Stage.AKKHA_PUZZLE);
			return true;
		}
		else if (regions.contains(15188))
		{
			toaManager.setStage(Stage.BABA_BOSS);
			return true;
		}
		else if (regions.contains(14676))
		{
			toaManager.setStage(Stage.AKKHA_BOSS);
			return true;
		}
		else
		{
			toaManager.setStage(Stage.NONE);
			return false;
		}
	}
}
