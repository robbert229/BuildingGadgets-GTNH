/**
 * Parts of this class were adapted from code written by TTerrag for the Chisel mod:
 * https://github.com/Chisel-Team/Chisel
 * Chisel is Open Source and distributed under GNU GPL v2
 */

package com.direwolf20.buildinggadgets.client.gui;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.text.DynamicKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetDestruction;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import com.direwolf20.buildinggadgets.common.network.*;

public class DestructionGUI extends GadgetGUI {



    private int left;
    private int right;
    private int depth;
    private int up;
    private int down;

    private boolean enableConnectedArea;
    private boolean enableRayTraceFluid;
    private boolean enableAnchored;

    public DestructionGUI(ItemStack tool) {
        this(tool, false);
    }

    public DestructionGUI(ItemStack tool, boolean temporarilyEnabled) {
        super(tool, temporarilyEnabled);

        this.left = GadgetDestruction.getToolValue(tool, "left");
        this.right = GadgetDestruction.getToolValue(tool, "right");
        this.up = GadgetDestruction.getToolValue(tool, "up");
        this.down = GadgetDestruction.getToolValue(tool, "down");
        this.depth = GadgetDestruction.getToolValue(tool, "depth");

        this.enableRayTraceFluid = GadgetGeneric.shouldRayTraceFluid(tool);
        this.enableConnectedArea = GadgetGeneric.getConnectedArea(tool);
        this.enableAnchored = GadgetDestruction.getAnchor(tool) != null;
    }

    private static SliderWidget getSlider() {
        return new SliderWidget().sliderHeight(12)
            .sliderWidth(7)
            .bounds(0, 16)
            .stopper(1)
            .width(16 * 4)
            .height(20);
    }

    private Flow getSliders() {
        return Flow.column()
            .debugName("sliders")
            .child(
                getSlider().stopper(1)

                    .overlay(new DynamicKey(() -> String.format("Up %d", this.up)))
                    .value(new DoubleValue.Dynamic(() -> this.up * 1.0, val -> this.up = (int) Math.round(val))))

            .child(
                Flow.row()
                    .child(
                        getSlider().overlay(new DynamicKey(() -> String.format("Left %d", this.left)))
                            .value(
                                new DoubleValue.Dynamic(
                                    () -> this.left * 1.0,
                                    val -> this.left = (int) Math.round(val))))
                    .child(
                        getSlider().overlay(new DynamicKey(() -> String.format("Depth %d", this.depth)))
                            .value(
                                new DoubleValue.Dynamic(
                                    () -> this.depth * 1.0,
                                    val -> this.depth = (int) Math.round(val))))
                    .child(
                        getSlider().overlay(new DynamicKey(() -> String.format("Right %d", this.right)))
                            .value(
                                new DoubleValue.Dynamic(
                                    () -> this.right * 1.0,
                                    val -> this.right = (int) Math.round(val))))
                    .coverChildrenWidth()
                    .coverChildrenHeight())

            .child(
                getSlider().overlay(new DynamicKey(() -> String.format("Down %d", this.down)))
                    .value(new DoubleValue.Dynamic(() -> this.down * 1.0, val -> this.down = (int) Math.round(val))))
            .coverChildrenHeight()
            .coverChildrenWidth();
    }

    private Flow getButtonRow() {
        return Flow.row()
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
                new ToggleButton().overlay(false, GuiUtils.getUITextureFromResource("setting/connected_area.png"))
                    .overlay(true, GuiUtils.getUITextureFromResource("setting/connected_area_selected.png"))
                    .addTooltipElement(IKey.str("Connected Area"))
                    .marginRight(GuiUtils.Spacing)
                    .size(GuiUtils.ButtonSize, GuiUtils.ButtonSize)
                    .value(new BoolValue.Dynamic(() -> this.enableConnectedArea, val -> {
                        this.enableConnectedArea = val;
                        PacketHandler.INSTANCE.sendToServer(new PacketToggleConnectedArea());
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
            .coverChildrenHeight()
            .coverChildrenWidth();
    }

    @Override
    public ModularPanel buildUI(ModularGuiContext context) {
        return ModularPanel.defaultPanel(GuiUtils.getPanelName("destruction"))
            .widthRel(0.5f)
            .coverChildrenHeight()
            .coverChildrenWidth()
            .child(ButtonWidget.panelCloseButton())
            .child(
                Flow.column()
                    .child(getButtonRow().marginTop(7))
                    .child(
                        getSliders().marginLeft(7)
                            .marginRight(7)
                            .marginBottom(7))
                    .child(
                        new ButtonWidget<>().overlay(IKey.str("Okay"))
                            .size(GuiUtils.ButtonSize * 4, GuiUtils.ButtonSize)
                            .marginBottom(7)
                            .onMousePressed(mouseButton -> {
                                PacketHandler.INSTANCE
                                    .sendToServer(new PacketDestructionGUI(left, right, up, down, depth));
                                this.close();
                                return true;
                            }))
                    .coverChildrenHeight()
                    .coverChildrenWidth());
    }
}
