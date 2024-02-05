package com.direwolf20.buildinggadgets.common.blocks;

import com.direwolf20.buildinggadgets.common.BuildingGadgets;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
//import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
//import net.minecraftforge.client.model.ModelLoader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockModBase extends Block {

    public BlockModBase(Material material, float hardness, String name) {
        super(material);
        init(this, hardness, name);
    }

    public static void init(Block block, float hardness, String name) {
        block.setHardness(hardness);
        block.setCreativeTab(BuildingGadgets.BUILDING_CREATIVE_TAB);
        block.setBlockName(name);
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        initModel(this);
    }
 
    public static void initModel(Block block) {
//        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
    }
}