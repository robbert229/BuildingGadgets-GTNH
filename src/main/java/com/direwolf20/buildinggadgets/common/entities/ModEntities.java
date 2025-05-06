package com.direwolf20.buildinggadgets.common.entities;

import com.direwolf20.buildinggadgets.BuildingGadgets;

import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlockTileEntity;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class ModEntities {

    public static void init() {
        int id = 1;
        EntityRegistry.registerModEntity(BlockBuildEntity.class, "LaserGunEntity", id++, BuildingGadgets.instance, 64, 1, true);
        EntityRegistry.registerModEntity(ConstructionBlockEntity.class, "ConstructionBlockEntity", id++, BuildingGadgets.instance, 64, 1, true);

        GameRegistry.registerTileEntity(ConstructionBlockTileEntity.class, "ConstructionBlockTileEntity");
        GameRegistry.registerTileEntity(TemplateManagerTileEntity.class, "TemplateManagerTileEntity");
    }

    public static void initModels() {
        RenderingRegistry.registerEntityRenderingHandler(BlockBuildEntity.class, new BlockBuildEntityRender(RenderManager.instance));
//     RenderingRegistry.registerEntityRenderingHandler(ConstructionBlockEntity.class, new
//     ConstructionBlockEntityRender.Factory());
    }
}
