package com.example.Utility;

import net.runelite.api.widgets.Widget;

public class WidgetUtil
{

	public static boolean isVisible(Widget widget)
	{
		return widget != null && !widget.isHidden() && !widget.isSelfHidden();
	}

	public static boolean hasAction(Widget widget, String action)
	{
		if (widget.getActions() == null)
		{
			return false;
		}
		for (String widgetAction : widget.getActions())
		{
			if (widgetAction != null && widgetAction.equalsIgnoreCase(action.toLowerCase()))
			{
				return true;
			}
		}
		return false;
	}
}
