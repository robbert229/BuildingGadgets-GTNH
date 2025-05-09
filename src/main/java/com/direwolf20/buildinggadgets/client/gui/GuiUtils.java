package com.direwolf20.buildinggadgets.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import com.cleanroommc.modularui.drawable.UITexture;
import com.direwolf20.buildinggadgets.BuildingGadgets;

public class GuiUtils {

    public static final float TEXTURE_CONVERSION_FACTOR = 1.0F / 256.0F;

    public static boolean isHovered(GuiButton button, int mouseX, int mouseY) {
        return mouseX >= button.xPosition && mouseX <= button.xPosition + button.width
            && mouseY >= button.yPosition
            && mouseY <= button.yPosition + button.height;
    }

    public static UITexture getUITextureFromResource(String texture) {
        var assetLocation = "textures/gui/%s";
        var resourceLocation = new ResourceLocation(BuildingGadgets.MODID, String.format(assetLocation, texture));
        return UITexture.fullImage(resourceLocation);
    }

    public static String getPanelName(String name) {
        return String.format("%s:%s", BuildingGadgets.MODID, name);
    }
}
