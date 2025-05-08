package com.direwolf20.buildinggadgets.client.gui;

import java.awt.Color;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiButtonIcon extends GuiButtonColor {

    private Icon iconSelected, iconDeselected;
    private ActionPressed action;
    private float alpha = 1F;

    public GuiButtonIcon(int x, int y, int width, int height, String helpTextKey, Color colorSelected,
        Color colorDeselected, @Nullable Color colorHovered, ResourceLocation textureSelected,
        @Nullable Runnable action) {
        super(0, x, y, width, height, "", helpTextKey, colorSelected, colorDeselected, colorHovered);
        iconDeselected = new Icon(textureSelected);
        this.action = new ActionPressed(action);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return action.pressed(super.mousePressed(mc, mouseX, mouseY));
    }

    protected void setFaded(boolean faded, int alphaFaded) {
        alpha = faded ? alphaFaded / 255F : 1F;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) {
            return;
        }

        super.drawButton(mc, mouseX, mouseY);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        TextureManager textureManager = Minecraft.getMinecraft()
            .getTextureManager();
        if (iconSelected == null) {
            ResourceLocation texture = iconDeselected.getModifiedTexture("selected");
            iconSelected = iconDeselected.isTextureMissing(textureManager, texture) ? iconDeselected
                : new Icon(texture);
        }
        Icon icon = selected ? iconSelected : iconDeselected;
        if (selected) {
            GL11.glColor4f(
                colorSelected.getRed() / 255F,
                colorSelected.getGreen() / 255F,
                colorSelected.getBlue() / 255F,
                alpha);
        } else {
            GL11.glColor4f(1F, 1F, 1F, alpha);
        }

        icon.bindTextureColored(textureManager);
        drawTexturedModalRect(xPosition, yPosition, width, height);

        GL11.glColor4f(1F, 1F, 1F, alpha);
        if (icon.bindTexture(textureManager)) {
            drawTexturedModalRect(xPosition, yPosition, width, height);
        }
    }

    private void drawTexturedModalRect(int x, int y, int width, int height) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x + 0, y + height, zLevel, 0, 1);
        tessellator.addVertexWithUV(x + width, y + height, zLevel, 1, 1);
        tessellator.addVertexWithUV(x + width, y + 0, zLevel, 1, 0);
        tessellator.addVertexWithUV(x + 0, y + 0, zLevel, 0, 0);
        tessellator.draw(); // Finish drawing
    }

    private static class Icon {

        private ResourceLocation texture, textureColored;

        public Icon(ResourceLocation texture) {
            this.texture = texture;
        }

        public ResourceLocation getModifiedTexture(String suffix) {
            return new ResourceLocation(
                texture.toString()
                    .replace(".png", String.format("_%s.png", suffix)));
        }

        public boolean isTextureMissing(TextureManager textureManager, ResourceLocation texture) {
            textureManager.bindTexture(texture);
            return textureManager.getTexture(texture) == TextureUtil.missingTexture;
        }

        public void bindTextureColored(TextureManager textureManager) {
            if (textureColored == null) {
                textureColored = getModifiedTexture("colored");
                if (isTextureMissing(textureManager, textureColored)) {
                    textureColored = texture;
                    texture = null;
                }
            }
            textureManager.bindTexture(textureColored);
        }

        public boolean bindTexture(TextureManager textureManager) {
            if (texture == null) return false;

            textureManager.bindTexture(texture);
            return true;
        }
    }
}
