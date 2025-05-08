package com.direwolf20.buildinggadgets.client.gui;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;

/**
 * The CopySneakGUI is a gui that allows users to manually increment and decrement the x, y, and z values for the copy
 * area.
 */
public class CopyAreaGUI {
    public static ModularScreen createGUI() {
        ModularPanel panel = ModularPanel.defaultPanel(GuiUtils.getPanelName("copy_sneak"));
        panel.child(IKey.str("Copy Sneak").asWidget()
                .top(7).left(7));
        return new ModularScreen(panel);
    }
}
