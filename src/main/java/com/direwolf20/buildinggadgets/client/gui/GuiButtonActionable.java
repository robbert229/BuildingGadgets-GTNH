package com.direwolf20.buildinggadgets.client.gui;

import java.awt.*;
import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.ModSounds;

/**
 * A one stop shop for all your icon screens related needs. We support colors,
 * icons, selected and deselected states, sound and loads more. Come on
 * down!
 */
public class GuiButtonActionable extends GuiButton {

    private Predicate<Boolean> action;
    private boolean selected;
    private boolean isSelectable;

    private Color selectedColor = Color.GREEN;
    private Color deselectedColor = new Color(255, 255, 255);
    private Color activeColor;

    private ResourceLocation selectedTexture;
    private ResourceLocation deselectedTexture;

    private float alpha = 1f;

    public GuiButtonActionable(int x, int y, String texture, String message, boolean isSelectable,
        Predicate<Boolean> action) {
        super(0, x, y, 25, 25, message);
        this.activeColor = deselectedColor;
        this.isSelectable = isSelectable;
        this.action = action;

        this.setSelected(action.test(false));

        // Set the selected and deselected textures.
        String assetLocation = "textures/gui/setting/%s.png";

        this.deselectedTexture = new ResourceLocation(BuildingGadgets.MODID, String.format(assetLocation, texture));
        this.selectedTexture = !isSelectable ? this.deselectedTexture
            : new ResourceLocation(BuildingGadgets.MODID, String.format(assetLocation, texture + "_selected"));
    }

    /**
     * If yo do not need to be able to select / toggle something then use this constructor as
     * you'll hit missing texture issues if you don't have an active (_selected) texture.
     */
    public GuiButtonActionable(int x, int y, String texture, String message, Predicate<Boolean> action) {
        this(x, y, texture, message, false, action);
    }

    public void setFaded(boolean faded) {
        alpha = faded ? .6f : 1f;
    }

    /**
     * This should be used when changing select.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
        this.activeColor = selected ? selectedColor : deselectedColor;
    }

    public boolean isSelected() {
        return selected;
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.func_147674_a(ModSounds.BEEP.getSound(), selected ? .6F : 1F));
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.playPressSound(mc.getSoundHandler());
            return true;
        }

        if (!(mouseX >= this.xPosition && mouseY >= this.yPosition
            && mouseX < this.xPosition + this.width
            && mouseY < this.yPosition + this.height)) return false;

        this.action.test(true);
        if (!this.isSelectable) return false;

        this.setSelected(!this.selected);
        return true;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) return;

        // Enable blending and set blend function using GL11
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        // Disable texture rendering and set color for the rectangle
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(activeColor.getRed() / 255f, activeColor.getGreen() / 255f, activeColor.getBlue() / 255f, 0.15f);

        // Draw the rectangle (similar to blit in newer versions)
        drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, this.width, this.height);

        // Re-enable texture rendering
        GL11.glEnable(GL11.GL_TEXTURE_2D);

        // Set color back to white with alpha for texture rendering
        GL11.glColor4f(1, 1, 1, alpha);

        // Bind the texture (either selected or deselected) and draw it
        mc.getTextureManager()
            .bindTexture(selected ? selectedTexture : deselectedTexture);
        drawTexturedModalRect(this.xPosition, this.yPosition, 0, 0, this.width, this.height);

        // Get the screen resolution
        ScaledResolution scaledResolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);

        // Check if the mouse is hovering over the button and render the display string
        if (mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height) {
            int textX = (mouseX > (scaledResolution.getScaledWidth() / 2)) ? mouseX + 2
                : mouseX - mc.fontRenderer.getStringWidth(this.displayString);
            mc.fontRenderer.drawString(this.displayString, textX, mouseY - 10, activeColor.getRGB());
        }
    }
}
