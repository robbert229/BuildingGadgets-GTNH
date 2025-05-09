/**
 * Parts of this class were adapted from code written by TTerrag for the Chisel mod:
 * https://github.com/Chisel-Team/Chisel
 * Chisel is Open Source and distributed under GNU GPL v2
 */

package com.direwolf20.buildinggadgets.client.gui;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.drawable.text.DynamicKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.value.BoolValue;
import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SliderWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetDestruction;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import com.direwolf20.buildinggadgets.common.network.*;

public class DestructionGUI extends GadgetGUI {

    private static final int ButtonSize = 20;
    private static final int Spacing = 7;

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

    private Flow buildSliders() {
        var panel = new Column().debugName("sliders")
            .coverChildrenHeight();

        panel.child(
            new Column().child(
                new SliderWidget().sliderHeight(12)
                    .sliderWidth(7)
                    .bounds(0, 16)
                    .stopper(1)
                    .widthRel(0.33f)
                    .align(Alignment.Center)
                    .overlay(new DynamicKey(() -> String.format("Up %d", this.up)))
                    .value(new DoubleValue.Dynamic(() -> this.up * 1.0, val -> this.up = (int) Math.round(val))))
                .left(7)
                .right(7)
                .coverChildrenHeight());

        panel.child(
            new Column().child(
                new SliderWidget().sliderHeight(12)
                    .sliderWidth(7)
                    .bounds(0, 16)
                    .stopper(1)
                    .widthRel(0.33f)
                    .align(Alignment.CenterLeft)
                    .overlay(new DynamicKey(() -> String.format("Left %d", this.left)))
                    .value(new DoubleValue.Dynamic(() -> this.left * 1.0, val -> this.left = (int) Math.round(val))))
                .child(
                    new SliderWidget().sliderHeight(12)
                        .sliderWidth(7)
                        .bounds(0, 16)
                        .stopper(1)
                        .widthRel(0.33f)
                        .align(Alignment.Center)
                        .overlay(new DynamicKey(() -> String.format("Depth %d", this.depth)))
                        .value(
                            new DoubleValue.Dynamic(() -> this.depth * 1.0, val -> this.depth = (int) Math.round(val))))
                .child(
                    new SliderWidget().sliderHeight(12)
                        .sliderWidth(7)
                        .bounds(0, 16)
                        .stopper(1)
                        .widthRel(0.33f)
                        .align(Alignment.CenterRight)
                        .overlay(new DynamicKey(() -> String.format("Right %d", this.right)))
                        .value(
                            new DoubleValue.Dynamic(() -> this.right * 1.0, val -> this.right = (int) Math.round(val))))
                .left(7)
                .right(7)
                .top(18 /* height of bar */ + 7)
                .coverChildrenHeight());

        panel.child(
            new Column().child(
                new SliderWidget().sliderHeight(12)
                    .sliderWidth(7)
                    .bounds(0, 16)
                    .stopper(1)
                    .widthRel(0.33f)
                    .align(Alignment.Center)
                    .overlay(new DynamicKey(() -> String.format("Down %d", this.down)))
                    .value(new DoubleValue.Dynamic(() -> this.down * 1.0, val -> this.down = (int) Math.round(val))))
                .left(7)
                .right(7)
                .top(18 * 2 + 7)
                .coverChildrenHeight());

        // panel.child(
        // new Column().child(
        // new ButtonWidget<>().center()
        // .size(18 * 4, 18)
        // .overlay(IKey.str("Ok"))
        // .onMousePressed((IGuiAction.MousePressed) mouseButton -> {
        // if (!this.isWithinBounds()) {
        // Minecraft.getMinecraft().thePlayer.addChatMessage(
        // new ChatComponentText(
        // ChatFormatting.RED + new ChatComponentTranslation("message.gadget.destroysizeerror")
        // .getUnformattedText()));
        // return false;
        // }
        //
        // this.close(true);
        // return true;
        // }))
        // .coverChildrenHeight()
        // .bottom(7)
        // .leftRel(0.3f)
        // .rightRel(0.3f));

        return panel;
    }

    @Override
    public ModularPanel buildUI(ModularGuiContext context) {
        var slidersColumnHeight = 18 * 4;

        ModularPanel root = ModularPanel.defaultPanel(GuiUtils.getPanelName("destruction"))
            .widthRel(0.5f)
            .heightRel(0.5f);

        root.child(ButtonWidget.panelCloseButton());

        var buttonPanel = new Row().debugName("buttons")
            .marginTop(Spacing)
            .marginLeft(Spacing)
            .marginRight(Spacing)
            .coverChildrenWidth()
            .coverChildrenHeight()
            .align(Alignment.TopCenter)
            .child(
                new ButtonWidget<>().overlay(GuiUtils.getUITextureFromResource("setting/undo.png"))
                    .addTooltipElement(IKey.str("Undo"))
                    .marginRight(Spacing)
                    .size(ButtonSize, ButtonSize)
                    .onMousePressed(button -> {
                        PacketHandler.INSTANCE.sendToServer(new PacketUndo());
                        return true;
                    }))
            .child(
                new ToggleButton().overlay(false, GuiUtils.getUITextureFromResource("setting/anchor.png"))
                    .overlay(true, GuiUtils.getUITextureFromResource("setting/anchor_selected.png"))
                    .addTooltipElement(IKey.str("Anchor"))
                    .marginRight(Spacing)
                    .size(ButtonSize, ButtonSize)
                    .value(new BoolValue.Dynamic(() -> this.enableAnchored, val -> {
                        this.enableAnchored = val;
                        PacketHandler.INSTANCE.sendToServer(new PacketAnchor());
                    })))
            .child(
                new ToggleButton().overlay(false, GuiUtils.getUITextureFromResource("setting/connected_area.png"))
                    .overlay(true, GuiUtils.getUITextureFromResource("setting/connected_area_selected.png"))
                    .addTooltipElement(IKey.str("Connected Area"))
                    .marginRight(Spacing)
                    .size(ButtonSize, ButtonSize)
                    .value(new BoolValue.Dynamic(() -> this.enableConnectedArea, val -> {
                        this.enableConnectedArea = val;
                        PacketHandler.INSTANCE.sendToServer(new PacketToggleConnectedArea());
                    })))
            .child(
                new ToggleButton().overlay(false, GuiUtils.getUITextureFromResource("setting/raytrace_fluid.png"))
                    .overlay(true, GuiUtils.getUITextureFromResource("setting/raytrace_fluid_selected.png"))
                    .addTooltipElement(IKey.str("Raytrace Fluid"))
                    .marginRight(Spacing)
                    .size(ButtonSize, ButtonSize)
                    .value(new BoolValue.Dynamic(() -> this.enableRayTraceFluid, val -> {
                        this.enableRayTraceFluid = val;
                        PacketHandler.INSTANCE.sendToServer(new PacketToggleRayTraceFluid());
                    })));

        root.background(new Rectangle().setColor(Color.argb(1f, 1f, 1f, 0.5f)));
        root.child(buttonPanel);

        root.child(buildSliders().align(Alignment.Center));

        root.child(
            new Row().child(
                new ButtonWidget<>().overlay(IKey.str("Okay"))
                    .align(Alignment.BottomCenter)
                    .size(ButtonSize * 4, ButtonSize)
                    .onMousePressed(mouseButton -> {
                        PacketHandler.INSTANCE.sendToServer(new PacketDestructionGUI(left, right, up, down, depth));
                        this.close();
                        return true;
                    }))
                .marginBottom(Spacing));

        return root;
    }
}
