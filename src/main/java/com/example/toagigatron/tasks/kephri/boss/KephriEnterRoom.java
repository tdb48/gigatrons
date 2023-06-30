package com.example.toagigatron.tasks.kephri.boss;


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
        name = "Kephri enter",
        priority = 10
)
public class KephriEnterRoom extends StagedTask
{
    @Inject
    public KephriEnterRoom(ToaManager toaManager)
    {
        super(toaManager, Stage.KEPHRI_BOSS);
    }

    public boolean execute()
    {
        if (toaManager.kephri.kephriRoom == null || toaManager.kephri.kephriRoom.contains(Static.getClient().getLocalPlayer().getWorldLocation()))
        {
            return false;
        }
        if (!toaManager.hasGearEquipped(toaManager.meleeSetup.getAllItems()))
        {
            toaManager.swap(toaManager.meleeSetup.getAllItems());
            return true;
        }
        GameObject entry = new GameObjectQuery().idEquals(ToaConstants.KEPHRI_BOSS_ENTRY).result(client).first();
        if (entry != null && Reachable.isInteractable(entry))
        {
            toaManager.print("Entering boss fight");
            entry.interact("Quick-Use");
            return true;
        }

        return false;
    }
}