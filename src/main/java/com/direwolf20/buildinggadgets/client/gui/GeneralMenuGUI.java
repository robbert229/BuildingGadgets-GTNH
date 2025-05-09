package com.direwolf20.buildinggadgets.client.gui;

import net.minecraft.item.ItemStack;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.CustomModularScreen;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Column;

public class GeneralMenuGUI extends CustomModularScreen {

    public GeneralMenuGUI(ItemStack tool) {

    }

    private static final int ButtonSize = 20;
    private static final int PanelMargin = 7 * 2;
    private static final int ButtonMargin = 7;

    @Override
    public ModularPanel buildUI(ModularGuiContext context) {
        ModularPanel panel = ModularPanel.defaultPanel("general_menu")
            .width(ButtonSize * 3 + PanelMargin + 14 + 14)
            .height(ButtonSize * 2 + PanelMargin + ButtonMargin);

        panel.child(ButtonWidget.panelCloseButton());

        panel.child(
            new Column().child(
                new ToggleButton().overlay(GuiUtils.getUITextureFromResource("setting/destroy_overlay.png"))
                    .hoverOverlay(GuiUtils.getUITextureFromResource("setting/destroy_overlay.png"), IKey.str("Overlay"))
                    .size(ButtonSize, ButtonSize)
                    .left(0)
                    .top(0))
                .child(
                    new ToggleButton().overlay(GuiUtils.getUITextureFromResource("setting/connected_area.png"))
                        .hoverOverlay(
                            GuiUtils.getUITextureFromResource("setting/connected_area.png"),
                            IKey.str("Connected Area"))
                        .size(ButtonSize, ButtonSize)
                        .left(ButtonSize + ButtonMargin)
                        .top(0))
                .child(
                    new ToggleButton().overlay(GuiUtils.getUITextureFromResource("setting/raytrace_fluid.png"))
                        .hoverOverlay(
                            GuiUtils.getUITextureFromResource("setting/raytrace_fluid.png"),
                            IKey.str("Raytrace Fluid"))
                        .size(ButtonSize, ButtonSize)
                        .left((ButtonSize + ButtonMargin) * 2)
                        .top(0))
                .top(ButtonMargin)
                .left(ButtonMargin)
                .right(ButtonMargin)
                .coverChildrenHeight());

        panel.child(
            new Column().child(
                new ToggleButton().overlay(GuiUtils.getUITextureFromResource("setting/anchor.png"))
                    .hoverOverlay(GuiUtils.getUITextureFromResource("setting/anchor.png"), IKey.str("Anchor"))
                    .size(ButtonSize, ButtonSize)
                    .left(0)
                    .top(0))
                .child(
                    new ToggleButton().overlay(GuiUtils.getUITextureFromResource("setting/undo.png"))
                        .hoverOverlay(GuiUtils.getUITextureFromResource("setting/undo.png"), IKey.str("Undo"))
                        .size(ButtonSize, ButtonSize)
                        .left(ButtonSize + ButtonMargin)
                        .top(0))
                .bottom(ButtonMargin)
                .left(ButtonMargin)
                .right(ButtonMargin)
                .coverChildrenHeight());

        return panel;
    }
}
