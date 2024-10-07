package com.direwolf20.buildinggadgets.client.gui.materiallist;

import com.direwolf20.buildinggadgets.client.util.AlignmentUtil;
import com.direwolf20.buildinggadgets.client.util.RenderUtil;
import com.direwolf20.buildinggadgets.common.tools.MathTool;
import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.client.GuiScrollingList;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static com.direwolf20.buildinggadgets.client.util.AlignmentUtil.SLOT_SIZE;
import static com.direwolf20.buildinggadgets.client.util.RenderUtil.getFontRenderer;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

class ScrollingMaterialList extends GuiScrollingList {
    static final int MARGIN = 2;
    static final int ENTRY_HEIGHT = Math.max(SLOT_SIZE + MARGIN * 2, getFontRenderer().FONT_HEIGHT * 2 + MARGIN * 3);
    static final int TOP = 24;
    static final int BOTTOM = 32;
    static final int LINE_SIDE_MARGIN = 8;

    private final MaterialListGUI parent;

    public ScrollingMaterialList(MaterialListGUI parent, int width, int height) {
        super(Minecraft.getMinecraft(),
                parent.getWindowWidth(),
                height,
                parent.getWindowTopY() + TOP,
                parent.getWindowBottomY() - BOTTOM,
                parent.getWindowLeftX(),
                ENTRY_HEIGHT);
        this.parent = parent;
    }

    @Override
    protected int getSize() {
        return parent.getMaterials().size();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {
        //
    }

    @Override
    protected boolean isSelected(int slotIndex) {
        // No need to select entries because there is no use for it yet
        return false;
    }

    @Override
    protected void drawBackground() {
    }

    @Override
    protected void drawSlot(int index, int rightIn, int top, int entryHeight, Tessellator tess) {
        ItemStack item = parent.getMaterials().get(index);
        // We don't want our content to be exactly aligned with the border
        int right = rightIn - 2;
        int bottom = top + entryHeight;
        int slotX = left + MARGIN;
        int slotY = top + MARGIN;

        drawIcon(item, slotX, slotY);
        drawTextOverlay(index, right, top, item, bottom, slotX);
        drawHoveringText(item, slotX, slotY);
    }

    private void drawTextOverlay(int index, int right, int top, ItemStack item, int bottom, int slotX) {
        String itemName = item.getDisplayName();
        int itemNameX = slotX + SLOT_SIZE + MARGIN;
        // -1 because the bottom x coordinate is exclusive
        RenderUtil.renderTextVerticalCenter(itemName, itemNameX, top, bottom - 1, Color.WHITE.getRGB());

        int required = item.stackSize;
        int available = MathTool.clamp(parent.getAvailable().getInt(index), 0, required);
        boolean fulfilled = available == required;
        int color = fulfilled ? Color.GREEN.getRGB() : Color.RED.getRGB();
        String amount = I18n.format("gui.buildinggadgets.materialList.text.statusTemplate", available, required);
        RenderUtil.renderTextHorizontalRight(amount, right, AlignmentUtil.getYForAlignedCenter(getFontRenderer().FONT_HEIGHT, top, bottom), color);

        int widthItemName = Minecraft.getMinecraft().fontRenderer.getStringWidth(itemName);
        int widthAmount = Minecraft.getMinecraft().fontRenderer.getStringWidth(amount);
        drawGuidingLine(index, right, top, bottom, itemNameX, widthItemName, widthAmount);
    }

    private void drawGuidingLine(int index, int right, int top, int bottom, int itemNameX, int widthItemName, int widthAmount) {
        if (!isSelected(index)) {
            int lineXStart = itemNameX + widthItemName + LINE_SIDE_MARGIN;
            int lineXEnd = right - widthAmount - LINE_SIDE_MARGIN;
            int lineY = AlignmentUtil.getYForAlignedCenter(1, top, bottom) - 1;

            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            parent.drawHorizontalLine(lineXStart, lineXEnd, lineY, 0x22FFFFFF);
        }
    }

    private void drawHoveringText(ItemStack item, int slotX, int slotY) {
        // TODO(johnrowl) implement this
//        if (mouseX > slotX && mouseY > slotY && mouseX <= slotX + 18 && mouseY <= slotY + 18) {
//            parent.setTaskHoveringText(mouseX, mouseY, parent.getItemToolTip(item));
//        }
    }

    private void drawIcon(ItemStack item, int slotX, int slotY) {
        GL11.glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();

        var renderItem = new RenderItem();
        var mc = Minecraft.getMinecraft();
        var fontRenderer = mc.fontRenderer;
        var textureManager = mc.getTextureManager();
        renderItem.renderItemIntoGUI(fontRenderer, textureManager, item, slotX, slotY, true);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor3f(1, 1, 1);
        GL11.glPopMatrix();
    }

}