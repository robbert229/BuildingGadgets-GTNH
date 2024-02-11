/**
 * Parts of this class were adapted from code written by TTerrag for the Chisel mod: https://github.com/Chisel-Team/Chisel
 * Chisel is Open Source and distributed under GNU GPL v2
 */

package com.direwolf20.buildinggadgets.client.gui;

import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetDestruction;
import com.direwolf20.buildinggadgets.common.network.PacketDestructionGUI;
import com.direwolf20.buildinggadgets.common.network.PacketHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DestructionGUI extends GuiScreen {

    private GuiDestructionSlider left;
    private GuiDestructionSlider right;
    private GuiDestructionSlider up;
    private GuiDestructionSlider down;
    private GuiDestructionSlider depth;

    private ItemStack destructionTool;

    public DestructionGUI(ItemStack tool) {
        super();
        this.destructionTool = tool;
    }

    @Override
    public void initGui() {
        super.initGui();

        int x = width / 2;
        int y = height / 2;

        this.addButton(new GuiButton(1, (x - 30) + 32, y + 50, 60, 20, I18n.format("singles.buildinggadgets.confirm")));
        this.addButton(new GuiButton(2, (x - 30) - 32, y + 50, 60, 20, I18n.format("singles.buildinggadgets.cancel")));

        List<GuiDestructionSlider> sliders = new ArrayList<>();

        sliders.add(depth   = new GuiDestructionSlider(x - (GuiDestructionSlider.width / 2), y - (GuiDestructionSlider.height / 2), "Depth", GadgetDestruction.getToolValue(destructionTool, "depth")));
        sliders.add(left    = new GuiDestructionSlider(x - (GuiDestructionSlider.width * 2) - 5, y - (GuiDestructionSlider.height / 2), "Left", GadgetDestruction.getToolValue(destructionTool, "left")));
        sliders.add(right   = new GuiDestructionSlider(x + (GuiDestructionSlider.width + 5), y - (GuiDestructionSlider.height / 2), "Right", GadgetDestruction.getToolValue(destructionTool, "right")));
        sliders.add(up      = new GuiDestructionSlider(x - (GuiDestructionSlider.width / 2), y - 35, "Up", GadgetDestruction.getToolValue(destructionTool, "up")));
        sliders.add(down    = new GuiDestructionSlider(x - (GuiDestructionSlider.width / 2), y + 20, "Down", GadgetDestruction.getToolValue(destructionTool, "down")));

        sliders.forEach( gui -> gui.getComponents().forEach(this::addButton));
    }

    private boolean isWithinBounds() {
        int x = left.getValueInt() + right.getValueInt();
        int y = up.getValueInt() + down.getValueInt();

        return x <= 16 && y <= 16;
    }

    @Override
    protected void actionPerformed(GuiButton b) {
        if (b.id == 1) {
            if (isWithinBounds()) {
                PacketHandler.INSTANCE.sendToServer(new PacketDestructionGUI(left.getValueInt(), right.getValueInt(), up.getValueInt(), down.getValueInt(), depth.getValueInt()));
                this.mc.displayGuiScreen(null);
            }
            else
                Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentString(TextFormatting.RED + new TextComponentTranslation("message.gadget.destroysizeerror").getUnformattedComponentText()), true);
        }
        else if (b.id == 2)
            this.mc.displayGuiScreen(null);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    // This is only done to reduce code dupe in this class.
    private static class GuiDestructionSlider extends GuiSliderInt {
        public static final int width = 70;
        public static final int height = 14;

        private static final int min = 0;
        private static final int max = 16;

        GuiDestructionSlider(int x, int y, String prefix, int current) {
            super(
                    x, y, width, height, String.format("%s ", prefix), "", min, max, current, false, true, Color.DARK_GRAY, null,
                    (slider, amount) -> {
                        slider.setValue(MathHelper.clamp(slider.getValueInt() + amount, min, max));
                        slider.updateSlider();
                    }
            );
        }
    }
}
