package com.direwolf20.buildinggadgets.client.gui;

import net.minecraft.client.gui.GuiButton;

public class GuiUtils {

    public static final float TEXTURE_CONVERSION_FACTOR = 1.0F / 256.0F;

    public static boolean isHovered(GuiButton button, int mouseX, int mouseY) {
        return mouseX >= button.xPosition && mouseX <= button.xPosition + button.width
            && mouseY >= button.yPosition
            && mouseY <= button.yPosition + button.height;
    }
}
