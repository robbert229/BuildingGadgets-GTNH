package com.direwolf20.buildinggadgets.client.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;

import com.direwolf20.buildinggadgets.BuildingGadgets;

public interface IHoverHelpText {

    boolean isHovered(int mouseX, int mouseY);

    String getHoverHelpText();

    void drawRect(Gui gui, int color);

    public static String get(String key) {
        return I18n.format("help.gui." + BuildingGadgets.MODID + "." + key);
    }

}
