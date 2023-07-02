package com.example.toagigatron.tasks.kephri.boss;

import com.example.Utility.Movement;
import com.example.Utility.Static;
import com.example.toagigatron.manager.ToaManager;
import com.example.toagigatron.model.constants.Stage;
import com.example.toagigatron.model.constants.ToaConstants;
import com.example.toagigatron.taskformat.StagedTask;
import com.example.toagigatron.taskformat.TaskDescriptor;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;
@TaskDescriptor(
        name = "Kephri Dodge Dung",
        priority = 50,
        blocking = true
)
public class KephriDodgeDung extends StagedTask
{
    @Inject
    public KephriDodgeDung(ToaManager toaManager)
    {
        super(toaManager, Stage.KEPHRI_BOSS);
    }

    public boolean execute()
    {
        if (toaManager.kephri.previousRow == null || toaManager.kephri.currentRow == null || toaManager.kephri.kephriDungRows.isEmpty())
        {
            return false;
        }
        WorldPoint playerPoint = Static.getClient().getLocalPlayer().getWorldLocation();
        WorldPoint dungPoint = toaManager.kephri.previousRow.endPoint;
        if (Static.getClient().getLocalPlayer().getGraphic() == ToaConstants.DUNG_GRAPHIC_START && !playerPoint.equals(toaManager.kephri.currentRow.startPoint))
        {
            toaManager.print("Moving back to start point for dung");
            Movement.walk(toaManager.kephri.currentRow.startPoint);
            return true;
        }
        if(client.getLocalPlayer().getAnimation() == 9799 && client.getLocalPlayer().getPoseAnimation() == 809){
            toaManager.print("Pose anim -> " + client.getLocalPlayer().getPoseAnimation());
            toaManager.print("Moving to: " + toaManager.kephri.currentRow.prePathPoint.toString());
            Movement.walk(toaManager.kephri.currentRow.prePathPoint);
            return true;
        }
//        if (!playerPoint.equals(dungPoint))
//        {
//            return false;
//        }
//        toaManager.print("Moving to: " + toaManager.kephri.currentRow.prePathPoint.toString());
//        Movement.walk(toaManager.kephri.currentRow.prePathPoint);
        return false;
    }
}
