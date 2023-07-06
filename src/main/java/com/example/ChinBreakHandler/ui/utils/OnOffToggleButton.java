package com.example.ChinBreakHandler.ui.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import com.example.ChinBreakHandler.ChinBreakHandlerPlugin;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;

public class OnOffToggleButton extends JToggleButton
{
	private static final ImageIcon ON_SWITCHER;
	private static final ImageIcon OFF_SWITCHER;

	static
	{
		BufferedImage onSwitcher = ImageUtil.loadImageResource(ChinBreakHandlerPlugin.class, "switcher_on.png");
		ON_SWITCHER = new ImageIcon(ImageUtil.recolorImage(onSwitcher, new Color(50, 160, 250)));
		OFF_SWITCHER = new ImageIcon(ImageUtil.flipImage(
			ImageUtil.luminanceScale(
				ImageUtil.grayscaleImage(onSwitcher),
				0.61f
			),
			true,
			false
		));
	}

	public OnOffToggleButton()
	{
		super(OFF_SWITCHER);
		setSelectedIcon(ON_SWITCHER);
		SwingUtil.removeButtonDecorations(this);
		setPreferredSize(new Dimension(25, 0));
		SwingUtil.addModalTooltip(this, "Disable", "Enable");
	}
}