package com.direwolf20.buildinggadgets.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.opengl.GL11;

import static com.direwolf20.buildinggadgets.client.util.AlignmentUtil.*;

public final class RenderUtil {
    
    private RenderUtil() { }

    public static FontRenderer getFontRenderer() {
        return Minecraft.getMinecraft().fontRenderer;
    }

    public static TextureManager getTextureManager() {
        return Minecraft.getMinecraft().getTextureManager();
    }

    public static void renderTextHorizontalLeft(String text, int leftX, int y, int color) {
        getFontRenderer().drawString(text, leftX, y, color);
    }

    public static void renderTextHorizontalRight(String text, int rightX, int y, int color) {
        getFontRenderer().drawString(text, getXForAlignedRight(getFontRenderer().getStringWidth(text), rightX), y, color);
    }

    public static void renderTextHorizontalMiddle(String text, int leftX, int rightX, int y, int color) {
        getFontRenderer().drawString(text, getXForAlignedCenter(getFontRenderer().getStringWidth(text), leftX, rightX), y, color);
    }

    public static void renderTextVerticalTop(String text, int x, int topY, int color) {
        getFontRenderer().drawString(text, x, topY, color);
    }

    public static void renderTextVerticalBottom(String text, int x, int bottomY, int color) {
        getFontRenderer().drawString(text, x, getYForAlignedBottom(getFontRenderer().FONT_HEIGHT, bottomY), color);
    }

    public static void renderTextVerticalCenter(String text, int x, int topY, int bottomY, int color) {
        getFontRenderer().drawString(text, x, getYForAlignedCenter(getFontRenderer().FONT_HEIGHT, topY, bottomY), color);
    }

    public static void drawTexturedModalRect(int x, int y, int width, int height) {
        Tessellator tessellator = Tessellator.instance; // 1.7.10 uses Tessellator.instance
        tessellator.startDrawingQuads(); // Begin drawing quads (like BufferBuilder)
        tessellator.addVertexWithUV(x, y + height, 0, 0, 1);  // Equivalent to pos(x, y, z).tex(u, v)
        tessellator.addVertexWithUV(x + width, y + height, 0, 1, 1);
        tessellator.addVertexWithUV(x + width, y, 0, 1, 0);
        tessellator.addVertexWithUV(x, y, 0, 0, 0);
        tessellator.draw(); // End drawing
    }
}
