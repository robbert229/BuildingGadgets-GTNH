/**
 * Parts of this class were adapted from code written by TTerrag for the Chisel mod:
 * https://github.com/Chisel-Team/Chisel
 * Chisel is Open Source and distributed under GNU GPL v2
 */

package com.direwolf20.buildinggadgets.client.gui;


import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IGuiAction;
import com.cleanroommc.modularui.screen.CustomModularScreen;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.DoubleValue;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.SliderWidget;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetDestruction;
import com.direwolf20.buildinggadgets.common.network.PacketDestructionGUI;
import com.direwolf20.buildinggadgets.common.network.PacketHandler;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class DestructionGUI extends CustomModularScreen {
    private int left;
    private int right;
    private int depth;
    private int up;
    private int down;

    public DestructionGUI(ItemStack tool) {
        this.left = GadgetDestruction.getToolValue(tool, "left");
        this.right = GadgetDestruction.getToolValue(tool, "right");
        this.up = GadgetDestruction.getToolValue(tool, "up");
        this.down = GadgetDestruction.getToolValue(tool, "down");
        this.depth = GadgetDestruction.getToolValue(tool, "depth");
    }

    private boolean isWithinBounds() {
        int x = left + right;
        int y = up + down;

        return x <= 16 && y <= 16;
    }

    @Override
    public ModularPanel buildUI(ModularGuiContext context) {
        ModularPanel panel = ModularPanel.defaultPanel("tutorial_panel")
                .widthRel(0.5f)
                .height(18*4+14);

        panel.child(ButtonWidget.panelCloseButton());

        panel.child(new Column()
                .child(new SliderWidget()
                        .sliderHeight(12)
                        .sliderWidth(7)
                        .bounds(0,16)
                        .stopper(1)
                        .widthRel(0.33f)
                        .align(Alignment.Center)
                        .overlay(IKey.str("Up"))
                        .value(new DoubleValue.Dynamic(() -> this.up * 1.0, val -> this.up = (int)Math.round(val)))
                )
                .left(7)
                .right(7)
                .marginTop(7)
                .coverChildrenHeight()
        );

        panel.child(new Column()
                .child(new SliderWidget()
                        .sliderHeight(12)
                        .sliderWidth(7)
                        .bounds(0,16)
                        .stopper(1)
                        .widthRel(0.33f)
                        .align(Alignment.CenterLeft)
                        .overlay(IKey.str("Left"))
                        .value(new DoubleValue.Dynamic(() -> this.left * 1.0, val -> this.left = (int)Math.round(val)))
                )
                .child(new SliderWidget()
                        .sliderHeight(12)
                        .sliderWidth(7)
                        .bounds(0,16)
                        .stopper(1)
                        .widthRel(0.33f)
                        .align(Alignment.Center)
                        .overlay(IKey.str("Depth"))
                        .value(new DoubleValue.Dynamic(() -> this.depth * 1.0, val -> this.depth = (int)Math.round(val)))
                )
                .child(new SliderWidget()
                        .sliderHeight(12)
                        .sliderWidth(7)
                        .bounds(0,16)
                        .stopper(1)
                        .widthRel(0.33f)
                        .align(Alignment.CenterRight)
                        .overlay(IKey.str("Right"))
                        .value(new DoubleValue.Dynamic(() -> this.right * 1.0, val -> this.right = (int)Math.round(val)))
                )
                .left(7)
                .right(7)
                .top(18 /* height of bar */ + 7)
                .coverChildrenHeight()
        );

        panel.child(new Column()
                .child(new SliderWidget()
                        .sliderHeight(12)
                        .sliderWidth(7)
                        .bounds(0,16)
                        .stopper(1)
                        .widthRel(0.33f)
                        .align(Alignment.Center)
                        .overlay(IKey.str("Down"))
                        .value(new DoubleValue.Dynamic(() -> this.down * 1.0, val -> this.down = (int)Math.round(val)))
                )
                .left(7)
                .right(7)
                .top(18*2 + 7)
                .coverChildrenHeight()
        );

        panel.child(new Column()
                .child(
                        new ButtonWidget<>()
                                .center()
                                .size(18*4, 18)
                                .overlay(IKey.str("Ok"))
                                .onMousePressed((IGuiAction.MousePressed) mouseButton -> {
                                    if (!this.isWithinBounds()) {
                                        Minecraft.getMinecraft().thePlayer.addChatMessage(
                                                new ChatComponentText(
                                                        ChatFormatting.RED
                                                                + new ChatComponentTranslation("message.gadget.destroysizeerror").getUnformattedText()));
                                        return false;
                                    }

                                    PacketHandler.INSTANCE.sendToServer(
                                            new PacketDestructionGUI(
                                                    left,
                                                    right,
                                                    up,
                                                    down,
                                                    depth));
                                    this.close(true);
                                    return true;
                                })
                )
                .coverChildrenHeight()
                .bottom(7)
                .leftRel(0.3f)
                .rightRel(0.3f)
        );

        return panel;
    }
}
