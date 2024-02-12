package com.direwolf20.buildinggadgets.common.blocks;

import com.direwolf20.buildinggadgets.BuildingGadgets;

import cpw.mods.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(BuildingGadgets.MODID)
public class ModBlocks {

    // @GameRegistry.ObjectHolder("effectblock")
    // public static EffectBlock effectBlock;
    // @GameRegistry.ObjectHolder("constructionblock")
    // public static ConstructionBlock constructionBlock;
    @GameRegistry.ObjectHolder("constructionblock_dense")
    public static ConstructionBlockDense constructionBlockDense;
    @GameRegistry.ObjectHolder("constructionblockpowder")
    public static ConstructionBlockPowder constructionBlockPowder;

    // @GameRegistry.ObjectHolder("templatemanager")
    // public static TemplateManager templateManager;

    public static void init() {
        constructionBlockDense = new ConstructionBlockDense();
        GameRegistry.registerBlock(constructionBlockDense, constructionBlockDense.getUnlocalizedName());

        constructionBlockPowder = new ConstructionBlockPowder();
        GameRegistry.registerBlock(constructionBlockPowder, constructionBlockPowder.getUnlocalizedName());
    }
}
