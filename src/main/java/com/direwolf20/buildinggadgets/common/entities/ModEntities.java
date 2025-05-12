package com.direwolf20.buildinggadgets.common.entities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderManager;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlockTileEntity;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModEntities {

    public static void init() {
        int id = 1;
        EntityRegistry
            .registerModEntity(BlockBuildEntity.class, "LaserGunEntity", id++, BuildingGadgets.instance, 64, 1, true);
        EntityRegistry.registerModEntity(
            ConstructionBlockEntity.class,
            "ConstructionBlockEntity",
            id++,
            BuildingGadgets.instance,
            64,
            1,
            true);

        GameRegistry.registerTileEntity(ConstructionBlockTileEntity.class, "ConstructionBlockTileEntity");
        GameRegistry.registerTileEntity(TemplateManagerTileEntity.class, "TemplateManagerTileEntity");
    }

    @SideOnly(Side.CLIENT)
    public static void initModels() {
        RenderingRegistry
            .registerEntityRenderingHandler(BlockBuildEntity.class, new BlockBuildEntityRender(RenderManager.instance));
//         RenderingRegistry.registerEntityRenderingHandler(ConstructionBlockEntity.class, new
//         ConstructionBlockEntityRender.Factory());
    }
}
