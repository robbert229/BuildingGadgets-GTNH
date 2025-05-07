package com.direwolf20.buildinggadgets.client.gui;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.drawable.UITexture;
import com.cleanroommc.modularui.screen.CustomModularScreen;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ToggleButton;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.direwolf20.buildinggadgets.BuildingGadgets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GeneralMenuGUI extends CustomModularScreen {
    public GeneralMenuGUI(ItemStack tool){

    }

    private static UITexture getUITextureFromResource(String texture) {
        var assetLocation = "textures/gui/setting/%s.png";
        var resourceLocation = new ResourceLocation(BuildingGadgets.MODID, String.format(assetLocation, texture));
        return UITexture.fullImage(resourceLocation);
    }

    private static final int ButtonSize = 18*2;
    private static final int PanelMargin = 7 * 2;
    private static final int ButtonMargin = 7;

    @Override
    public ModularPanel buildUI(ModularGuiContext context) {
        ModularPanel panel = ModularPanel.defaultPanel("general_menu")
                .width(ButtonSize * 3 + PanelMargin + 14 + 14)
                .height(ButtonSize * 2 + PanelMargin + ButtonMargin);

        panel.child(ButtonWidget.panelCloseButton());

        panel.child(new Column()
                .child(new ToggleButton()
                        .overlay(
                                getUITextureFromResource("destroy_overlay")
                        )
                        .hoverOverlay(
                                getUITextureFromResource("destroy_overlay"),
                                IKey.str("Overlay")
                        )
                        .size(ButtonSize, ButtonSize)
                        .left(0)
                        .top(0)
                )
                .child(new ToggleButton()
                        .overlay(
                                getUITextureFromResource("connected_area")
                        )
                        .hoverOverlay(
                                getUITextureFromResource("connected_area"),
                                IKey.str("Connected Area")
                        )
                        .size(ButtonSize, ButtonSize)
                        .left(ButtonSize + ButtonMargin)
                        .top(0)
                )
                .child(new ToggleButton()
                        .overlay(
                                getUITextureFromResource("raytrace_fluid")
                        )
                        .hoverOverlay(
                                getUITextureFromResource("raytrace_fluid"),
                                IKey.str("Raytrace Fluid")
                        )
                        .size(ButtonSize, ButtonSize)
                        .left((ButtonSize + ButtonMargin) * 2)
                        .top(0)
                )
                .top(ButtonMargin)
                .left(ButtonMargin)
                .right(ButtonMargin)
                .coverChildrenHeight()
        );

        panel.child(new Column()
                .child(new ToggleButton()
                        .overlay(
                                getUITextureFromResource("anchor")
                        )
                        .hoverOverlay(
                                getUITextureFromResource("anchor"),
                                IKey.str("Anchor")
                        )
                        .size(ButtonSize, ButtonSize)
                        .left(0)
                        .top(0)
                )
                .child(new ToggleButton()
                        .overlay(
                                getUITextureFromResource("undo")
                        )
                        .hoverOverlay(
                                getUITextureFromResource("undo"),
                                IKey.str("Undo")
                        )
                        .size(ButtonSize, ButtonSize)
                        .left(ButtonSize + ButtonMargin)
                        .top(0)
                )
                .bottom(ButtonMargin)
                .left(ButtonMargin)
                .right(ButtonMargin)
                .coverChildrenHeight()
        );

        return panel;
    }
}
