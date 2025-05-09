package com.direwolf20.buildinggadgets.client.gui;

import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import com.cleanroommc.modularui.screen.CustomModularScreen;
import com.direwolf20.buildinggadgets.client.KeyBindings;

public abstract class GadgetGUI extends CustomModularScreen {

    protected boolean temporarilyEnabled = false;

    public GadgetGUI(ItemStack tool) {
        this(tool, false);
    }

    public GadgetGUI(ItemStack tool, boolean temporarilyEnabled) {
        this.temporarilyEnabled = temporarilyEnabled;
    }

    @Override
    public void onFrameUpdate() {
        super.onFrameUpdate();

        if (!this.temporarilyEnabled) {
            return;
        }

        // we are doing this because I was running into some issues where the KeyBinding wasn't getting its actual state
        // updated correctly, and was saying that the key was not down, when it was.
        if (!Keyboard.isKeyDown(KeyBindings.temporarilyEnableMenu.getKeyCode())) {
            this.close();
        }
    }
}
