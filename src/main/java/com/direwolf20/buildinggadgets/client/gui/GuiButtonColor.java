package com.direwolf20.buildinggadgets.client.gui;

import java.awt.Color;

import javax.annotation.Nullable;

import com.direwolf20.buildinggadgets.client.proxy.ClientProxy;

import net.minecraft.client.Minecraft;

public class GuiButtonColor extends GuiButtonSound {
    protected Color colorSelected, colorDeselected, colorHovered;

    public GuiButtonColor(int buttonId, int x, int y, int width, int height, String text, String helpTextKey) {
        this(buttonId, x, y, width, height, text, helpTextKey, Color.GREEN, Color.LIGHT_GRAY, null);
    }

    public GuiButtonColor(int buttonId, int x, int y, int width, int height, String text, String helpTextKey,
            Color colorSelected, Color colorDeselected, @Nullable Color colorHovered) {
        super(buttonId, x, y, width, height, text, helpTextKey);
        this.colorSelected = colorSelected;
        this.colorDeselected = colorDeselected;
        this.colorHovered = colorHovered;
    }

    protected void setAlpha(int alpha) {
        colorSelected = ClientProxy.getColor(colorSelected, alpha);
        colorDeselected = ClientProxy.getColor(colorDeselected, alpha);
        colorHovered = ClientProxy.getColor(colorHovered, alpha);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible)
            return;

        var hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, (hovered && colorHovered != null ? colorHovered : (selected ? colorSelected : colorDeselected)).getRGB());
        mouseDragged(mc, mouseX, mouseY);
        if (!displayString.isEmpty()) {
            int textColor = !enabled ? 10526880 : (hovered ? 16777120 : -1);
            mc.fontRenderer.drawString(displayString, xPosition + width / 2 - mc.fontRenderer.getStringWidth(displayString) / 2, yPosition + (height - 8) / 2, textColor);
        }
    }
}