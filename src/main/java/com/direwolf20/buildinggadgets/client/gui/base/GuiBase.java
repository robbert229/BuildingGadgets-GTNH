package com.direwolf20.buildinggadgets.client.gui.base;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

public abstract class GuiBase extends GuiScreen {

    public static final char KEY_CLOSE_GUI = Keyboard.KEY_E;

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        super.keyTyped(typedChar, keyCode);
        if (keyCode == KEY_CLOSE_GUI) {
            mc.thePlayer.closeScreen();
        }
    }

}
