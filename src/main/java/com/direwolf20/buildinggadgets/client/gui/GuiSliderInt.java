package com.direwolf20.buildinggadgets.client.gui;

import java.awt.Color;
import java.util.Collection;
import java.util.function.BiConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import com.direwolf20.buildinggadgets.client.proxy.ClientProxy;
import com.google.common.collect.ImmutableSet;

import cpw.mods.fml.client.config.GuiSlider;

public class GuiSliderInt extends GuiSlider {

    private static final ResourceLocation CLICK = new ResourceLocation("random.click");
    private final int colorBackground;
    private final int colorSliderBackground;
    private final int colorSlider;
    private final BiConsumer<GuiSliderInt, Integer> increment;
    private int value;
    private boolean hovered;

    public GuiSliderInt(int xPos, int yPos, int width, int height, String prefix, String suf, double minVal,
        double maxVal, double currentVal, boolean showDec, boolean drawStr, Color color, ISlider par,
        BiConsumer<GuiSliderInt, Integer> increment) {
        super(0, xPos, yPos, width, height, prefix, suf, minVal, maxVal, currentVal, showDec, drawStr, par);
        colorBackground = ClientProxy.getColor(color, 200)
            .getRGB();
        colorSliderBackground = ClientProxy.getColor(color.darker(), 200)
            .getRGB();
        colorSlider = ClientProxy.getColor(
            color.brighter()
                .brighter(),
            200)
            .getRGB();
        this.increment = increment;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        super.mouseReleased(mouseX, mouseY);
        setValue(getValueInt());
    }

    @Override
    public void updateSlider() {
        super.updateSlider();
        int valueInt = getValueInt();
        if (value != valueInt) {
            value = valueInt;
            playSound();
        }
    }

    private void playSound() {
        ClientProxy.playSound(CLICK, 2F);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) {
            return;
        }

        hovered = mouseX >= xPosition && mouseY >= yPosition
            && mouseX < xPosition + width
            && mouseY < yPosition + height;
        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, colorBackground);
        mouseDragged(mc, mouseX, mouseY);
        renderText(mc, this);
    }

    private void renderText(Minecraft mc, GuiButton component) {
        int color = !enabled ? 10526880 : (hovered ? 16777120 : -1);
        String buttonText = component.displayString;
        int strWidth = mc.fontRenderer.getStringWidth(buttonText);
        int ellipsisWidth = mc.fontRenderer.getStringWidth("...");
        if (strWidth > component.width - 6 && strWidth > ellipsisWidth)
            buttonText = mc.fontRenderer.trimStringToWidth(buttonText, component.width - 6 - ellipsisWidth)
                .trim() + "...";

        drawCenteredString(
            mc.fontRenderer,
            buttonText,
            component.xPosition + component.width / 2,
            component.yPosition + (component.height - 8) / 2,
            color);
    }

    // @Override
    // public void playPressSound(SoundHandler soundHandlerIn) {}

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        if (!visible) return;

        if (dragging) {
            sliderValue = (mouseX - (xPosition + 4)) / (float) (width - 8);
            updateSlider();
        }
        drawBorderedRect(xPosition + (int) (sliderValue * (width - 8)), yPosition, 8, height);
    }

    private void drawBorderedRect(int x, int y, int width, int height) {
        drawRect(x, y, x + width, y + height, colorSliderBackground);
        drawRect(++x, ++y, x + width - 2, y + height - 2, colorSlider);
    }

    public Collection<GuiButton> getComponents() {
        return ImmutableSet.of(
            this,
            new GuiButtonIncrement(
                this,
                xPosition - height,
                yPosition,
                height,
                height,
                "-",
                () -> increment.accept(this, -1)),
            new GuiButtonIncrement(
                this,
                xPosition + width,
                yPosition,
                height,
                height,
                "+",
                () -> increment.accept(this, 1)));
    }

    private static class GuiButtonIncrement extends GuiButton {

        private GuiSliderInt parent;
        private ActionPressed action;
        private boolean hovered;

        public GuiButtonIncrement(GuiSliderInt parent, int x, int y, int width, int height, String buttonText,
            Runnable action) {
            super(0, x, y, width, height, buttonText);
            this.parent = parent;
            this.action = new ActionPressed(action);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (!visible) return;

            this.hovered = mouseX >= xPosition && mouseY >= yPosition
                && mouseX < xPosition + width
                && mouseY < yPosition + height;
            drawRect(xPosition, yPosition, xPosition + width, yPosition + height, parent.colorBackground);
            parent.drawBorderedRect(xPosition, yPosition, width, height);
            parent.renderText(mc, this);
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            return action.pressed(super.mousePressed(mc, mouseX, mouseY));
        }

        // @Override
        // public void playPressSound(SoundHandler soundHandlerIn) {}
    }
}
