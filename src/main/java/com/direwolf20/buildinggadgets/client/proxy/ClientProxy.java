package com.direwolf20.buildinggadgets.client.proxy;

// import com.direwolf20.buildinggadgets.client.KeyBindings;
// import com.direwolf20.buildinggadgets.common.blocks.ModBlocks;
// import com.direwolf20.buildinggadgets.common.blocks.Models.BakedModelLoader;
// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerContainer;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.TextureStitchEvent;

import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import com.direwolf20.buildinggadgets.common.proxy.CommonProxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        // ModEntities.initModels();
        // ModelLoaderRegistry.registerLoader(new BakedModelLoader());
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        // KeyBindings.init();
        // ModBlocks.initColorHandlers();

        registerModels();
    }

    @SubscribeEvent
    public static void registerModels() {
        /*
         * ModBlocks.effectBlock.initModel();
         * ModBlocks.templateManager.initModel();
         * gadgetBuilding.initModel();
         * ModItems.gadgetExchanger.initModel();
         * ModItems.gadgetCopyPaste.initModel();
         * ModItems.template.initModel();
         * if (SyncedConfig.enableDestructionGadget) {
         * ModItems.gadgetDestruction.initModel();
         * }
         * if (SyncedConfig.enablePaste) {
         * ModItems.constructionPaste.initModel();
         * ModItems.constructionChunkDense.initModel();
         * ModItems.constructionPasteContainer.initModel();
         * ModItems.constructionPasteContainert2.initModel();
         * ModItems.constructionPasteContainert3.initModel();
         * ModItems.constructionPasteContainerCreative.initModel();
         * ModBlocks.constructionBlockDense.initModel();
         * ModBlocks.constructionBlock.initModel();
         * ModBlocks.constructionBlockPowder.initModel();
         * // ModelLoader.setCustomMeshDefinition(ModItems.constructionPasteContainer, new
         * PasteContainerMeshDefinition());
         * // ModelLoader.setCustomMeshDefinition(ModItems.constructionPasteContainert2, new
         * PasteContainerMeshDefinition());
         * // ModelLoader.setCustomMeshDefinition(ModItems.constructionPasteContainert3, new
         * PasteContainerMeshDefinition());
         * }
         */
    }

    public void registerEntityRenderers() {
        // RenderingRegistry.registerEntityRenderingHandler(BlockBuildEntity.class, new
        // BlockBuildEntityRender.Factory());
        // RenderingRegistry.registerEntityRenderingHandler(ConstructionBlockEntity.class, new
        // ConstructionBlockEntityRender.Factory());
    }

    @SubscribeEvent
    public static void renderWorldLastEvent(RenderWorldLastEvent evt) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        ItemStack heldItem = GadgetGeneric.getGadget(player);
        if (heldItem == null || heldItem.getItem() == null) return;

        /*
         * if (heldItem.getItem() instanceof GadgetBuilding) {
         * ToolRenders.renderBuilderOverlay(evt, player, heldItem);
         * } else if (heldItem.getItem() instanceof GadgetExchanger) {
         * ToolRenders.renderExchangerOverlay(evt, player, heldItem);
         * } else if (heldItem.getItem() instanceof GadgetCopyPaste) {
         * ToolRenders.renderPasteOverlay(evt, player, heldItem);
         * } else if (heldItem.getItem() instanceof GadgetDestruction) {
         * ToolRenders.renderDestructionOverlay(evt, player, heldItem);
         * }
         */

    }
    /*
     * @SubscribeEvent
     * public static void registerSprites(TextureStitchEvent.Pre event) {
     * registerSprite(event, TemplateManagerContainer.TEXTURE_LOC_SLOT_TOOL);
     * registerSprite(event, TemplateManagerContainer.TEXTURE_LOC_SLOT_TEMPLATE);
     * }
     */

    private static void registerSprite(TextureStitchEvent.Pre event, String loc) {
        // event.getMap().registerSprite(new ResourceLocation(loc));
    }

    // public static void playSound(SoundEvent sound, float pitch) {
    //
    // Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(sound, pitch));
    // }

    public static Color getColor(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
