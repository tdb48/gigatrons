package com.example.ChinBreakHandler.ui.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;
import net.runelite.client.util.Text;

public class ToggleButton extends JCheckBox {
    private static final ImageIcon ON_SWITCHER;
    private static final ImageIcon OFF_SWITCHER;
    private static final ImageIcon DISABLED_SWITCHER;
    private final Object object;

    public ToggleButton() {
        super(OFF_SWITCHER);
        this.object = null;
        this.setSelectedIcon(ON_SWITCHER);
        this.setDisabledIcon(DISABLED_SWITCHER);
        SwingUtil.removeButtonDecorations(this);
    }

    public ToggleButton(String text) {
        super(text, OFF_SWITCHER, false);
        this.object = null;
        this.setSelectedIcon(ON_SWITCHER);
        this.setDisabledIcon(DISABLED_SWITCHER);
        SwingUtil.removeButtonDecorations(this);
    }

    public ToggleButton(Object object) {
        super(Text.titleCase((Enum)object), OFF_SWITCHER, false);
        this.object = object;
        this.setSelectedIcon(ON_SWITCHER);
        this.setDisabledIcon(DISABLED_SWITCHER);
        SwingUtil.removeButtonDecorations(this);
    }

    public Object getObject() {
        return this.object;
    }

    static {
        BufferedImage onSwitcher = ImageUtil.loadImageResource(ToggleButton.class, "switcher_on.png");
        ON_SWITCHER = new ImageIcon(ImageUtil.recolorImage(onSwitcher, new Color(50, 160, 250)));
        OFF_SWITCHER = new ImageIcon(ImageUtil.flipImage(ImageUtil.luminanceScale(ImageUtil.grayscaleImage(onSwitcher), 0.61F), true, false));
        DISABLED_SWITCHER = new ImageIcon(ImageUtil.flipImage(ImageUtil.luminanceScale(ImageUtil.grayscaleImage(onSwitcher), 0.4F), true, false));
    }
}
