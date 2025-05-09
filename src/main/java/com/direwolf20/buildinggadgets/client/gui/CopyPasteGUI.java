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
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetCopyPaste;
import com.direwolf20.buildinggadgets.common.network.PacketHandler;
import com.direwolf20.buildinggadgets.common.network.PacketToggleMode;

public class CopyPasteGUI extends GadgetGUI {

    private GadgetCopyPaste.ToolMode mode;

    public CopyPasteGUI(ItemStack tool) {
        this(tool, false);
    }

    public CopyPasteGUI(ItemStack tool, boolean temporarilyEnabled) {
        super(tool, temporarilyEnabled);

        this.mode = GadgetCopyPaste.getToolMode(tool);
    }

    @Override
    public ModularPanel buildUI(ModularGuiContext context) {
        ModularPanel panel = ModularPanel.defaultPanel(GuiUtils.getPanelName("copy_paste"));

        var buttonFlow = Flow.row()
            .child(
                new ToggleButton().overlay(false, GuiUtils.getUITextureFromResource("mode/copy.png"))
                    .overlay(true, GuiUtils.getUITextureFromResource("mode/copy.png"))
                    .addTooltipElement(IKey.str("Copy"))
                    .marginRight(7)
                    .value(new BoolValue.Dynamic(() -> this.mode == GadgetCopyPaste.ToolMode.Copy, (val) -> {
                        this.mode = GadgetCopyPaste.ToolMode.Copy;
                        PacketHandler.INSTANCE.sendToServer(new PacketToggleMode(this.mode.ordinal()));
                    })))
            .child(
                new ToggleButton().overlay(false, GuiUtils.getUITextureFromResource("mode/paste.png"))
                    .overlay(true, GuiUtils.getUITextureFromResource("mode/paste.png"))
                    .addTooltipElement(IKey.str("Paste"))
                    .value(new BoolValue.Dynamic(() -> this.mode == GadgetCopyPaste.ToolMode.Paste, (val) -> {
                        this.mode = GadgetCopyPaste.ToolMode.Paste;
                        PacketHandler.INSTANCE.sendToServer(new PacketToggleMode(this.mode.ordinal()));
                    })))
            .coverChildrenHeight()
            .coverChildrenWidth();

        var flow = Flow.column()
            .child(
                IKey.str("Copy Paste")
                    .asWidget()
                    .marginTop(7)
                    .marginBottom(7))
            .child(buttonFlow);

        panel.child(flow);

        return panel;
    }
}
