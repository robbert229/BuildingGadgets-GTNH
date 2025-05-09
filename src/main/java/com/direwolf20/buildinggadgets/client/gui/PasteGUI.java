package com.direwolf20.buildinggadgets.client.gui;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;

/// The PasteGUI is the gui that appears when the user shift clicks with the copy gadget in paste mode.
///
/// It should have three sliders: x, y, and z. Additionally, it should have an Okay, and Reset button.
///
/// The sliders are for setting a paste offset. This offset by default is 0, 1, 0. This offset is used to determine
/// relative to the users right click in the world for pasting should the past actually occur. The default value of
/// 0, 1, 0 means that the paste should occur on top of the block that the user is looking at. The gui should
/// immediately make the changes to the data structures when the sliders are changed.
///
/// The reset button when clicked should set the slider values to 0, 1, 0 again.
///
/// The Okay button should close the gui.
public class PasteGUI {

    public static ModularScreen createGUI() {
        ModularPanel panel = ModularPanel.defaultPanel(GuiUtils.getPanelName("paste"));
        panel.child(
            IKey.str("Paste")
                .asWidget()
                .top(7)
                .left(7));
        return new ModularScreen(panel);
    }
}
