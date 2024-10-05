package com.direwolf20.buildinggadgets.common.blocks;

import net.minecraft.item.Item;

import java.util.Random;

public class EffectBlock extends BlockModBase {

    public EffectBlock() {
        super(EffectBlockMaterial.EFFECTBLOCKMATERIAL, 20F, "effectblock");
        setCreativeTab(null);
    }

    @Override
    public int getRenderType() {
        return -1; // Invisible
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;  // Not a full cube
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return null;
    }

    @Override
    public int quantityDropped(Random rand) {
        return 0;  // No item dropped
    }

    @Override
    public int getMobilityFlag() {
        return 2;  // Prevents the block from being pushed by pistons
    }
}
