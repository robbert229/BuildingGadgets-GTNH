package com.direwolf20.buildinggadgets.common.items.pastes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.common.items.ItemModBase;
import com.direwolf20.buildinggadgets.common.tools.InventoryManipulation;

public class ConstructionPaste extends ItemModBase {

    public ConstructionPaste() {
        super("constructionpaste");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        ItemStack itemstack = player.getHeldItem();
        itemstack = InventoryManipulation.addPasteToContainer(player, itemstack);
        return itemstack;
    }
}
