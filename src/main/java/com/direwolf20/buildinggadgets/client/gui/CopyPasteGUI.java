/**
 * Parts of this class were adapted from code written by TTerrag for the Chisel mod:
 * https://github.com/Chisel-Team/Chisel
 * Chisel is Open Source and distributed under GNU GPL v2
 */

package com.direwolf20.buildinggadgets.client.gui;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;

public class CopyPasteGUI extends GadgetGUI {

    public CopyPasteGUI(ItemStack tool) {
        this(tool, false);
    }

    public CopyPasteGUI(ItemStack tool, boolean temporarilyEnabled) {
        super(tool, temporarilyEnabled);
    }

    @Override
    public ModularPanel buildUI(ModularGuiContext context) {
        ModularPanel panel = ModularPanel.defaultPanel(GuiUtils.getPanelName("copy_paste"));
        panel.child(
            IKey.str("Copy Paste")
                .asWidget()
                .top(7)
                .left(7));

        return panel;
    }
}
