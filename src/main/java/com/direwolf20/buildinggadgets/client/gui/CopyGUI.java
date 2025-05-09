package com.direwolf20.buildinggadgets.client.gui;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;

/// The CopyGUI is a gui that allows users to manually increment and decrement the x, y, and z values for the copy
/// area. It should have two sets of x, y, and z sliders. There are two modes for the system. Relative and absolute
/// mode.
/// It should also have four buttons.
/// - Okay
/// - Cancel
/// - Clear
/// - CoordsMode
///
/// When Okay is clicked the changes made in the gui should persist to the gadgets nbt data and the gui should be
/// closed.
/// When Cancel is clicked the gui should close without making any changes.
/// When Clear is clicked both the startPos and endPos should be cleared from the tool and the gui should be closed.
/// When CoordsMode is clicked it should toggle the gui between relative mode and absolute mode.
///
/// ## Relative mode
/// The first set is the "start" sliders. They should always start at zero. When the dialog is closed these values
/// should
/// be added to the start position. So if you have 0,3,1 as the slider values, and a start position of 59, 102, and 90
/// the submission of the dialog should result in the start position being changed to 59, 105 (103+3), and 91 (90+1).
///
/// The second set of sliders contains the end position relative to the start position. This means that if the start
/// position is 59, 102, 90, and the end position is 61, 104, 88, then the sliders should start out at 2, 2, -2.
///
/// ## Absolute mode
/// The first set of sliders should contain the actual absolute position of the start pos. Ie 59, 102, 90. The second
/// set
/// of sliders should have the end position.
public class CopyGUI {

    public static ModularScreen createGUI() {
        ModularPanel panel = ModularPanel.defaultPanel(GuiUtils.getPanelName("copy"));
        panel.child(
            IKey.str("Copy")
                .asWidget()
                .top(7)
                .left(7));
        return new ModularScreen(panel);
    }
}
