package com.direwolf20.buildinggadgets.client.gui;

import com.cleanroommc.modularui.screen.CustomModularScreen;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import net.minecraft.item.ItemStack;

public class RadialMenuGUI extends CustomModularScreen {
    public RadialMenuGUI(ItemStack tool){

    }

    @Override
    public ModularPanel buildUI(ModularGuiContext context) {
        ModularPanel panel = ModularPanel.defaultPanel("tutorial_panel")
                .widthRel(0.5f)
                .height(18*4+14);

        return panel;
    }
}
