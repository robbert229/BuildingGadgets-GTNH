package com.direwolf20.buildinggadgets.client.util;

import net.minecraft.client.gui.GuiTextField;

public class MouseUtil {

    // Helper method to check if the mouse is over a text field
    private static boolean isMouseOverTextField(GuiTextField textField, int mouseX, int mouseY) {
        return mouseX >= textField.xPosition && mouseX < textField.xPosition + textField.width
            && mouseY >= textField.yPosition
            && mouseY < textField.yPosition + textField.height;
    }

    public static boolean mouseClicked(GuiTextField textField, int mouseX, int mouseY, int mouseButton) {
        var hovered = isMouseOverTextField(textField, mouseX, mouseY);
        var clicked = mouseButton == 0 || mouseButton == 1;
        return hovered && clicked;
    }
}
