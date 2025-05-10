/**
 * Parts of this class were adapted from code written by TTerrag for the Chisel mod:
 * https://github.com/Chisel-Team/Chisel
 * Chisel is Open Source and distributed under GNU GPL v2
 */

package com.direwolf20.buildinggadgets.client.gui;

import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetDestruction;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import com.direwolf20.buildinggadgets.common.network.*;
import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetCopyPaste;

public class CopyPasteGUI extends GadgetGUI {
    private GadgetCopyPaste.ToolMode mode;
    private boolean enableRayTraceFluid;
    private boolean enableAnchored;

    public CopyPasteGUI(ItemStack tool) {
        this(tool, false);
    }

    public CopyPasteGUI(ItemStack tool, boolean temporarilyEnabled) {
        super(tool, temporarilyEnabled);

        this.mode = GadgetCopyPaste.getToolMode(tool);
        this.enableRayTraceFluid = GadgetCopyPaste.shouldRayTraceFluid(tool);
        this.enableAnchored = GadgetCopyPaste.getAnchor(tool) != null;
    }

    @Override
    public ModularPanel buildUI(ModularGuiContext context) {
        ModularPanel panel = ModularPanel.defaultPanel(GuiUtils.getPanelName("copy_paste"));

        var modeButtonFlow = Flow.row()
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

         var buttonFlow = Flow.row()
                 .child(
                         new ButtonWidget<>().overlay(GuiUtils.getUITextureFromResource("setting/undo.png"))
                                 .addTooltipElement(IKey.str("Undo"))
                                 .marginRight(GuiUtils.Spacing)
                                 .size(GuiUtils.ButtonSize, GuiUtils.ButtonSize)
                                 .onMousePressed(button -> {
                                     PacketHandler.INSTANCE.sendToServer(new PacketUndo());
                                     return true;
                                 }))
                 .child(
                         new ToggleButton().overlay(false, GuiUtils.getUITextureFromResource("setting/anchor.png"))
                                 .overlay(true, GuiUtils.getUITextureFromResource("setting/anchor_selected.png"))
                                 .addTooltipElement(IKey.str("Anchor"))
                                 .marginRight(GuiUtils.Spacing)
                                 .size(GuiUtils.ButtonSize, GuiUtils.ButtonSize)
                                 .value(new BoolValue.Dynamic(() -> this.enableAnchored, val -> {
                                     this.enableAnchored = val;
                                     PacketHandler.INSTANCE.sendToServer(new PacketAnchor());
                                 })))
                 .child(
                         new ToggleButton().overlay(false, GuiUtils.getUITextureFromResource("setting/raytrace_fluid.png"))
                                 .overlay(true, GuiUtils.getUITextureFromResource("setting/raytrace_fluid_selected.png"))
                                 .addTooltipElement(IKey.str("Raytrace Fluid"))
                                 .size(GuiUtils.ButtonSize, GuiUtils.ButtonSize)
                                 .value(new BoolValue.Dynamic(() -> this.enableRayTraceFluid, val -> {
                                     this.enableRayTraceFluid = val;
                                     PacketHandler.INSTANCE.sendToServer(new PacketToggleRayTraceFluid());
                                 })))
                 .coverChildrenWidth()
                 .coverChildrenHeight();

        var flow = Flow.column()
            .child(
                IKey.str("Copy Paste")
                    .asWidget()
                    .marginTop(7)
                    .marginBottom(7))
            .child(modeButtonFlow
                .marginTop(7))
            .child(buttonFlow
                .marginTop(7));

        panel.child(flow);

        return panel;
    }
}
