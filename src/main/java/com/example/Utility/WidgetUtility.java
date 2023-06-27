package com.example.Utility;

import net.runelite.api.widgets.Widget;

public class WidgetUtility {

    public static boolean isVisible(Widget widget){
        return widget != null && !widget.isHidden() && !widget.isSelfHidden();
    }
}
