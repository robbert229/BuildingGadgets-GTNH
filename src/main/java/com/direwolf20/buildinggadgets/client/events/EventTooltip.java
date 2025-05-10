package com.direwolf20.buildinggadgets.client.events;

/**
 * This class was adapted from code written by Vazkii
 * Thanks Vazkii!!
 */

import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.direwolf20.buildinggadgets.client.RemoteInventoryCache;
import com.direwolf20.buildinggadgets.common.items.ITemplate;
import com.direwolf20.buildinggadgets.common.items.ModItems;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetCopyPaste;
import com.direwolf20.buildinggadgets.common.tools.*;
import com.direwolf20.buildinggadgets.util.MathTool;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;
import com.google.common.collect.Multiset;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EventTooltip {

    private static final int STACKS_PER_LINE = 8;
    private static RemoteInventoryCache cache = new RemoteInventoryCache(true);

    public static void init() {
        FMLCommonHandler.instance()
            .bus()
            .register(new EventTooltip());
    }

    public static void setCache(Multiset<UniqueItem> cache) {
        EventTooltip.cache.setCache(cache);
    }

    @SideOnly(Side.CLIENT)
    private static void tooltipIfShift(@SuppressWarnings("unused") List<String> tooltip, Runnable r) {
        if (GuiScreen.isShiftKeyDown()) {
            r.run();
        }
    }

    public static void addTemplatePadding(ItemStack stack, List<String> tooltip) {
        // This method extends the tooltip box size to fit the item's we will render in onDrawTooltip
        Minecraft mc = Minecraft.getMinecraft();
        if (stack.getItem() instanceof ITemplate template) {
            String UUID = template.getUUID(stack);
            if (UUID == null) {
                return;
            }

            Multiset<UniqueItem> itemCountMap = template.getItemCountMap(stack);

            Map<ItemStack, Integer> itemStackCount = new HashMap<ItemStack, Integer>();
            for (Multiset.Entry<UniqueItem> entry : itemCountMap.entrySet()) {
                ItemStack itemStack = new ItemStack(entry.getElement().item, 1, entry.getElement().meta);
                itemStackCount.put(itemStack, entry.getCount());
            }
            List<Map.Entry<ItemStack, Integer>> list = new ArrayList<>(itemStackCount.entrySet());

            int totalMissing = 0;
            // Look through all the ItemStacks and draw each one in the specified X/Y position
            for (Map.Entry<ItemStack, Integer> entry : list) {
                int hasAmt = InventoryManipulation.countItem(entry.getKey(), Minecraft.getMinecraft().thePlayer, cache);
                if (hasAmt < entry.getValue()) totalMissing = totalMissing + Math.abs(entry.getValue() - hasAmt);
            }

            int count = (totalMissing > 0) ? itemStackCount.size() + 1 : itemStackCount.size();
            // boolean creative = ((IReagentHolder) stack.getItem()).isCreativeReagentHolder(stack);

            if (count > 0) tooltipIfShift(tooltip, () -> {
                int lines = (((count - 1) / STACKS_PER_LINE) + 1) * 2;
                int width = Math.min(STACKS_PER_LINE, count) * 18;
                String spaces = "\u00a7r\u00a7r\u00a7r\u00a7r\u00a7r";
                while (mc.fontRenderer.getStringWidth(spaces) < width) spaces += " ";

                for (int j = 0; j < lines; j++) tooltip.add(spaces);
            });
        }
    }

    @SubscribeEvent
    public void onDrawTooltip(ItemStack stack, List<String> tooltip, int bx, int by) {
        if ((stack.getItem() instanceof ITemplate stackTemplate) && GuiScreen.isShiftKeyDown()) {
            long totalMissing = 0;

            Multiset<UniqueItem> itemCountMap = stackTemplate.getItemCountMap(stack);

            // Create an ItemStack -> Integer Map
            Map<ItemStack, Integer> itemStackCount = new HashMap<ItemStack, Integer>();
            for (Multiset.Entry<UniqueItem> entry : itemCountMap.entrySet()) {
                ItemStack itemStack = new ItemStack(entry.getElement().item, 1, entry.getElement().meta);
                itemStackCount.put(itemStack, entry.getCount());
            }
            // Sort the ItemStack -> Integer map, first by Required Items, then ItemID, then Meta
            List<Map.Entry<ItemStack, Integer>> list = new ArrayList<>(itemStackCount.entrySet());
            Comparator<Map.Entry<ItemStack, Integer>> comparator = Comparator.comparing(entry -> entry.getValue());
            comparator = comparator.reversed();
            comparator = comparator.thenComparing(
                Comparator.comparing(
                    entry -> Item.getIdFromItem(
                        entry.getKey()
                            .getItem())));
            comparator = comparator.thenComparing(
                Comparator.comparing(
                    entry -> entry.getKey()
                        .getItemDamage()));
            list.sort(comparator);

            for (String s : tooltip) {
                if (s.trim()
                    .equals("\u00a77\u00a7r\u00a7r\u00a7r\u00a7r\u00a7r")) break;
                by += 10;
            }

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            // GlStateManager.enableBlend();
            // GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            // Gui.drawRect(bx, by, bx + width, by + height, 0x55000000);

            int j = 0;
            // Look through all the ItemStacks and draw each one in the specified X/Y position
            for (Map.Entry<ItemStack, Integer> entry : list) {
                int hasAmt = InventoryManipulation.countItem(entry.getKey(), Minecraft.getMinecraft().thePlayer, cache);
                int x = bx + (j % STACKS_PER_LINE) * 18;
                int y = by + (j / STACKS_PER_LINE) * 20;
                totalMissing += renderRequiredBlocks(entry.getKey(), x, y, hasAmt, entry.getValue());
                j++;
            }
            if (totalMissing > 0) {
                ItemStack pasteItemStack = new ItemStack(ModItems.constructionPaste);
                int hasAmt = InventoryManipulation.countPaste(Minecraft.getMinecraft().thePlayer);
                int x = bx + (j % STACKS_PER_LINE) * 18;
                int y = by + (j / STACKS_PER_LINE) * 20;
                renderRequiredBlocks(pasteItemStack, x, y, hasAmt, MathTool.longToInt(totalMissing));
                j++;
            }
        }
    }

    private int renderRequiredBlocks(ItemStack itemStack, int x, int y, int count, int req) {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        var renderItem = new RenderItem();

        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
        renderItem.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemStack, x, y);

        String s1 = req == Integer.MAX_VALUE ? "\u221E" : Integer.toString(req);
        int w1 = mc.fontRenderer.getStringWidth(s1);
        int color = 0xFFFFFF;

        boolean hasReq = req > 0;

        GL11.glPushMatrix();
        GL11.glTranslatef(x + 8 - (float) w1 / 4, y + (hasReq ? 12 : 14), 0);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        mc.fontRenderer.drawStringWithShadow(s1, 0, 0, color);
        GL11.glPopMatrix();

        int missingCount = 0;

        if (hasReq && count < req) {
            String fs = Integer.toString(req - count);
            String s2 = "(" + fs + ")";
            int w2 = mc.fontRenderer.getStringWidth(s2);

            GL11.glPushMatrix();
            GL11.glTranslatef(x + 8 - (float) w2 / 4, y + 17, 0);
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            mc.fontRenderer.drawStringWithShadow(fs, 0, 0, 0xFF0000);
            GL11.glPopMatrix();
            missingCount = (req - count);
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        return missingCount;
    }

    public Map<UniqueItem, Integer> makeRequiredList(String UUID) {// TODO unused
        Map<UniqueItem, Integer> itemCountMap = new HashMap<UniqueItem, Integer>();
        Map<BlockState, UniqueItem> IntStackMap = GadgetCopyPaste
            .getBlockMapIntState(PasteToolBufferBuilder.getTagFromUUID(UUID))
            .getIntStackMap();
        List<BlockMap> blockMapList = GadgetCopyPaste.getBlockMapList(PasteToolBufferBuilder.getTagFromUUID(UUID));

        for (BlockMap blockMap : blockMapList) {
            UniqueItem uniqueItem = IntStackMap.get(blockMap.state);

            List<ItemStack> drops = blockMap.state.block()
                .getDrops(Minecraft.getMinecraft().theWorld, 0, 0, 0, blockMap.state.metadata(), 0);
            int neededItems = 0;
            for (ItemStack drop : drops) {
                if (drop.getItem()
                    .equals(uniqueItem.item)) {
                    neededItems++;
                }
            }

            if (neededItems == 0) {
                neededItems = 1;
            }

            if (uniqueItem.item != null) {
                boolean found = false;
                for (Map.Entry<UniqueItem, Integer> entry : itemCountMap.entrySet()) {
                    if (entry.getKey()
                        .equals(uniqueItem)) {
                        itemCountMap.put(entry.getKey(), itemCountMap.get(entry.getKey()) + neededItems);
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    itemCountMap.put(uniqueItem, neededItems);
                }
            }
        }
        return itemCountMap;
    }
}
