package com.example.toagigatron.tasks.wardens.wardensp2;

import com.example.EthanApiPlugin.Collections.NPCs;
import com.example.Packets.MousePackets;
import com.example.Packets.NPCPackets;
import com.example.Utility.Combat;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import java.util.ArrayList;
import javax.inject.Inject;
import net.runelite.api.NPC;

@TaskDescriptor(
	name = "Attack core p2",
	priority = 10
)
public class AttackCore extends StagedTask
{
	@Inject
	public AttackCore(ToaManager toaManager)
	{
		super(toaManager, Stage.WARDENS_P2);
	}

	public boolean execute()
	{
		if (toaManager.wardens12.warden == null
			|| (toaManager.wardens12.warden.getId() != ToaConstants.WARDENS_P2_DOWNED
			&& toaManager.wardens12.warden.getAnimation() != ToaConstants.WARDEN_P2_DROPPING_CORE_ANIMATION))
		{
			return false;
		}
		//if ratio == 0 put on blowpipe
		if (toaManager.wardens12.warden.getHealthRatio() == 0)
		{
			ArrayList<Integer> gearSet = toaManager.rangeSetup.getAllItemsBp();
			if (!toaManager.hasGearEquipped(gearSet))
			{
				toaManager.print("P2 complete pre-switching into range gear");
				toaManager.swap(gearSet);
			}
			return true;
		}
		if (toaManager.wardens12.p2Completed)
		{
			return false;
		}
		// BGS the core
		ArrayList<Integer> gearSet;
		NPC core = NPCs.search().nameContains("Core").first().orElse(null);
		if (toaManager.wardens12.coreTick == 1 && core != null)
		{
			gearSet = toaManager.meleeSetup.getAllItemsBgs();
			toaManager.print("Equipping bgs and last hitting core");
			toaManager.swap(gearSet);
			MousePackets.queueClickPacket();
			NPCPackets.queueNPCAction(core, "Attack");
			return true;
		}
		gearSet = toaManager.meleeSetup.getAllItemsDds();
		if (!toaManager.hasGearEquipped(gearSet))
		{
			toaManager.print("P2 - Swapping to dds gear");
			toaManager.swap(gearSet);
			return true;
		}
		if (!Combat.isSpecEnabled() && canDDSSpec())
		{
			toaManager.print("P2 - Toggling spec");
			Combat.toggleSpec();
		}
		if (core == null)
		{
			return false;
		}
		if (client.getLocalPlayer().getInteracting() != null && client.getLocalPlayer().getInteracting().equals(core))
		{
			return false;
		}
		toaManager.print("P2 - Attacking core");
		MousePackets.queueClickPacket();
		NPCPackets.queueNPCAction(core, "Attack");
		return true;
	}

	public boolean canDDSSpec()
	{
		int specNeeded = toaManager.isAdrenalineActive() ? 12 : 25;
		return Combat.getSpecEnergy() >= specNeeded;
	}
}