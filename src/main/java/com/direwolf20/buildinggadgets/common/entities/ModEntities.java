package com.direwolf20.buildinggadgets.common.entities;

import com.direwolf20.buildinggadgets.BuildingGadgets;

import cpw.mods.fml.common.registry.EntityRegistry;

public class ModEntities {

    public static void init() {
        int id = 1;
        // EntityRegistry.registerModEntity(new ResourceLocation(BuildingGadgets.MODID, "BlockBuildEntity"),
        // BlockBuildEntity.class, "LaserGunEntity", id++, BuildingGadgets.instance, 64, 1, true);
        EntityRegistry.registerModEntity(
            ConstructionBlockEntity.class,
            "ConstructionBlockEntity",
            id++,
            BuildingGadgets.instance,
            64,
            1,
            true);

    }

    // public static void initModels() {
    // RenderingRegistry.registerEntityRenderingHandler(BlockBuildEntity.class, new BlockBuildEntityRender.Factory());
    // RenderingRegistry.registerEntityRenderingHandler(ConstructionBlockEntity.class, new
    // ConstructionBlockEntityRender.Factory());
    // }
}
