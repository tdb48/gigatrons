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
import com.google.inject.Inject;
import java.util.List;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Prayer;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;

@TaskDescriptor(
        name = "Kephri prayers"
)
public class KephriPrayerHandler extends StagedTask
{
    @Inject
    public KephriPrayerHandler(ToaManager toaManager)
    {
        super(toaManager, Stage.KEPHRI_BOSS);
    }

    public List<Prayer> getPrayers()
    {
        NPC scarab = NPCs.getNearest(n -> n.getName().equals("Soldier Scarab") && n.getHealthRatio() != 0);
        if (scarab != null && scarab.distanceTo(Players.getLocal()) <= 4 && Reachable.isInteractable(scarab))
        {
            return List.of(this.getOffensive(), Prayer.PROTECT_FROM_MELEE);
        }
        else
        {
            NPC spittingAgile = NPCs.getNearest("Spitting Scarab", "Agile Scarab");
            return spittingAgile != null ? List.of(this.getOffensive(), Prayer.PROTECT_FROM_MISSILES) : List.of(this.getOffensive());
        }
    }

    public Prayer getOffensive()
    {
        ItemContainer equipped = Static.getClient().getItemContainer(InventoryID.EQUIPMENT);
        if (equipped != null)
        {
            Item weapon = equipped.getItem(3);
            if (weapon != null)
            {
                WeaponMap.WeaponStyle style = WeaponMap.StyleMap.getOrDefault(weapon.getId(), WeaponMap.WeaponStyle.MELEE);
                switch (style.ordinal())
                {
                    case 0:
                        return Prayer.AUGURY;
                    case 1:
                        return Prayer.RIGOUR;
                    case 2:
                        return Prayer.PIETY;
                }
            }

            Widget atk = Widgets.get(Combat.getAttackStyle().getWidgetInfo());
            if (atk != null)
            {
                String[] actions = atk.getActions();
                if (actions != null && actions.length == 1)
                {
                    switch (actions[0])
                    {
                        case "Rapid":
                            return Prayer.RIGOUR;
                        case "Accurate":
                        case "Longrange":
                            return Prayer.AUGURY;
                    }
                }
            }
        }
        return Prayer.PIETY;
    }

    public boolean execute()
    {
        WorldPoint playerPoint = Static.getClient().getLocalPlayer().getWorldLocation();
        NPC resetGhost = NPCs.getNearest(ToaConstants.OSMUMTEN);
        if (toaManager.kephri.kephriRoom == null || !toaManager.kephri.kephriRoom.contains(playerPoint) || resetGhost != null)
        {
            if (Prayers.anyActive())
            {
                Prayers.disableAll();
                return true;
            }
            return false;
        }

        if (!this.getPrayers().isEmpty() && Prayers.getPoints() > 0)
        {
            NPC scarab = NPCs.getNearest("Soldier Scarab");
            NPC spittingAgile = NPCs.getNearest("Spitting Scarab", "Agile Scarab");
            if (scarab == null && Prayers.isEnabled(Prayer.PROTECT_FROM_MELEE))
            {
                Prayers.toggle(Prayer.PROTECT_FROM_MELEE);
            }
            if (spittingAgile == null && Prayers.isEnabled(Prayer.PROTECT_FROM_MISSILES))
            {
                Prayers.toggle(Prayer.PROTECT_FROM_MISSILES);
            }
            for (Prayer prayer : getPrayers())
            {
                if (!Prayers.isEnabled(prayer) && !prayer.equals(Prayer.RIGOUR))
                {
                    Prayers.toggle(prayer);
                }
            }
            return true;
        }
        else if (this.getPrayers().isEmpty() && Prayers.anyActive())
        {
            Prayers.disableAll();
            return true;
        }
        return false;
    }
}