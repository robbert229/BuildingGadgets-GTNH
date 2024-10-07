package com.direwolf20.buildinggadgets.common.entities;

import com.direwolf20.buildinggadgets.BuildingGadgets;

import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlockTileEntity;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModEntities {

    public static void init() {
        int id = 1;
        // EntityRegistry.registerModEntity(new ResourceLocation(BuildingGadgets.MODID, "BlockBuildEntity"),
        // BlockBuildEntity.class, "LaserGunEntity", id++, BuildingGadgets.instance, 64, 1, true);

        GameRegistry.registerTileEntity(ConstructionBlockTileEntity.class, "ConstructionBlockTileEntity");
        GameRegistry.registerTileEntity(TemplateManagerTileEntity.class, "TemplateManagerTileEntity");

    }

    // public static void initModels() {
    // RenderingRegistry.registerEntityRenderingHandler(BlockBuildEntity.class, new BlockBuildEntityRender.Factory());
    // RenderingRegistry.registerEntityRenderingHandler(ConstructionBlockEntity.class, new
    // ConstructionBlockEntityRender.Factory());
    // }
}
