package com.direwolf20.buildinggadgets.common.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.PasteConfig;
import com.direwolf20.buildinggadgets.common.items.ModItems;

public class ConstructionBlockDense extends BlockModBase {

    public ConstructionBlockDense() {
        super(Material.rock, 3F, "constructionblock_dense");
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return ModItems.constructionPaste;
    }

    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return PasteConfig.pasteDroppedMin
            + random.nextInt(PasteConfig.pasteDroppedMax - PasteConfig.pasteDroppedMin + 1);
    }
}
