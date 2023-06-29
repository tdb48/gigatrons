package com.example.Utility;

import com.example.EthanApiPlugin.Collections.Widgets;
import net.runelite.api.widgets.Widget;

public class InventoryUtil {

    public static Widget getFirst(int[] ids){
        for(int id : ids){
            Widget w = Widgets.search().withItemId(id).first().orElse(null);
            if(w != null){
                return w;
            }
        }
        return null;
    }
}
