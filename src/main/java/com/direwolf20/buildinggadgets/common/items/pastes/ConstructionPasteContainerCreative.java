package com.direwolf20.buildinggadgets.common.items.pastes;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ConstructionPasteContainerCreative extends GenericPasteContainer {

    public ConstructionPasteContainerCreative() {
        super("constructionpastecontainercreative");
    }

    /**
     * allows items to add custom lines of information to the mouseover description
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        list.add(ChatFormatting.WHITE + I18n.format("tooltip.pasteContainer.creative.amountMsg"));
    }

    @Override
    public void setPasteCount(ItemStack stack, int amount) {}

    @Override
    public int getPasteCount(ItemStack stack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxCapacity() {
        return Integer.MAX_VALUE;
    }

}
