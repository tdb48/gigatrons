package com.example.ChinBreakHandler.config;

import java.awt.event.KeyEvent;

public class ModifierlessKeybind extends Keybind
{
    public ModifierlessKeybind(int keyCode, int modifiers)
    {
        super(keyCode, modifiers, true);
    }

    /**
     * Constructs a keybind with that matches the passed KeyEvent
     */
    public ModifierlessKeybind(KeyEvent e)
    {
        this(e.getExtendedKeyCode(), e.getModifiersEx());

        assert matches(e);
    }

    /**
     * If the KeyEvent is from a KeyPressed event this returns if the
     * Event is this hotkey being pressed. If the KeyEvent is a
     * KeyReleased event this returns if the event is this hotkey being
     * released
     */
    @Override
    public boolean matches(KeyEvent e)
    {
        return matches(e, true);
    }
}
