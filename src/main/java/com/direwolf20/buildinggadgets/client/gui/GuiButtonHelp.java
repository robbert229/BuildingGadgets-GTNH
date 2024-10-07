package com.direwolf20.buildinggadgets.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import org.lwjgl.opengl.GL11;

public class GuiButtonHelp extends GuiButtonSelect {
    public GuiButtonHelp(int buttonId, int x, int y) {
        super(buttonId, x, y, 12, 12, "?", "");
    }

    public String getHoverText() {
        return IHoverHelpText.get("button." + (selected ? "help.exit" : "help.enter"));
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) {
            return;
        }

        GL11.glColor4f(1, 1, 1, 1);

        boolean hovered = mouseX >= this.xPosition &&
                mouseY >= this.yPosition &&
                mouseX < this.xPosition + width &&
                mouseY < this.yPosition + height;

        float x = this.xPosition + 5.5F;
        int y = this.yPosition + 6;

        double radius = 6;
        int red, green, blue;

        if (selected) {
            red = blue = 0;
            green = 200;
        } else {
            red = green = blue = 120;
        }

        Tessellator tessellator = Tessellator.instance;
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        tessellator.startDrawing(GL11.GL_TRIANGLE_FAN);
        tessellator.setColorRGBA(red, green, blue, 255);
        tessellator.addVertex(x, y, 0);

        double s = 30;

        for (int k = 0; k <= s; k++) {
            double angle = (Math.PI * 2 * k / s) + Math.toRadians(180);

            tessellator.addVertex(
                    x + Math.sin(angle) * radius,
                    y + Math.cos(angle) * radius,
                    0
            );
        }
        tessellator.draw();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);

        mouseDragged(mc, mouseX, mouseY);

        int colorText = -1;

        if (packedFGColour != 0) {
            colorText = packedFGColour;
        } else if (!enabled) {
            colorText = 10526880;
        } else if (hovered) {
            colorText = 16777120;
        }

        mc.fontRenderer.drawString(
                displayString,
                this.xPosition + width / 2 - mc.fontRenderer.getStringWidth(displayString) / 2,
                this.yPosition + (height - 8) / 2,
                colorText
        );
    }

}