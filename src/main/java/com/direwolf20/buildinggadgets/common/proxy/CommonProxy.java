package com.direwolf20.buildinggadgets.common.proxy;

// import com.direwolf20.buildinggadgets.client.gui.GuiProxy;
// import com.direwolf20.buildinggadgets.common.BuildingGadgets;
// import com.direwolf20.buildinggadgets.common.ModSounds;
// import com.direwolf20.buildinggadgets.common.blocks.*;
// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManager;
// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;
// import com.direwolf20.buildinggadgets.common.building.CapabilityBlockProvider;

import com.direwolf20.buildinggadgets.common.blocks.ModBlocks;
import com.direwolf20.buildinggadgets.common.items.ModItems;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

// @Mod.EventBusSubscriber
public class CommonProxy {

    private boolean applyCompatConfig = false;

    public void preInit(FMLPreInitializationEvent e) {
        ModItems.init();
        ModBlocks.init();

        // ModEntities.init();
        // PacketHandler.registerMessages();
        // File cfgFile = new File(e.getModConfigurationDirectory(), "BuildingGadgets.cfg");
        // if (cfgFile.exists()) {
        // BuildingGadgets.logger.info("Preparing to migrate old config Data to new Format");
        // applyCompatConfig = CompatConfig.readConfig(cfgFile);
        // }
        // IntegrationHandler.preInit(e);
    }

    public void init(FMLInitializationEvent event) {
        // CapabilityBlockProvider.register();
        //
        // NetworkRegistry.INSTANCE.registerGuiHandler(BuildingGadgets.instance, new GuiProxy());
        // if (applyCompatConfig) {
        // BuildingGadgets.logger.info("Migrating old config Data.");
        // CompatConfig.applyCompatConfig();
        // }
        // IntegrationHandler.init();
    }

    public void postInit(FMLPostInitializationEvent event) {
        // IntegrationHandler.postInit();
    }
    /*
     * @SubscribeEvent
     * public static void registerBlocks(RegistryEvent.Register<Block> event) {
     * event.getRegistry().register(new EffectBlock());
     * event.getRegistry().register(new TemplateManager());
     * GameRegistry.registerTileEntity(TemplateManagerTileEntity.class, new ResourceLocation(BuildingGadgets.MODID,
     * "templateManager"));
     * if (SyncedConfig.enablePaste) {
     * event.getRegistry().register(new ConstructionBlockDense());
     * event.getRegistry().register(new ConstructionBlock());
     * event.getRegistry().register(new ConstructionBlockPowder());
     * GameRegistry.registerTileEntity(ConstructionBlockTileEntity.class, new ResourceLocation(BuildingGadgets.MODID,
     * "_constructionBlock"));
     * }
     * }
     * @SubscribeEvent
     * public static void registerItems(RegistryEvent.Register<Item> event) {
     * event.getRegistry().register(new GadgetBuilding());
     * event.getRegistry().register(new GadgetExchanger());
     * event.getRegistry().register(new GadgetCopyPaste());
     * event.getRegistry().register(new
     * ItemBlock(ModBlocks.templateManager).setRegistryName(ModBlocks.templateManager.getRegistryName()));
     * event.getRegistry().register(new Template());
     * if (SyncedConfig.enableDestructionGadget) {
     * event.getRegistry().register(new GadgetDestruction());
     * }
     * if (SyncedConfig.enablePaste) {
     * event.getRegistry().register(new
     * ItemBlock(ModBlocks.constructionBlockDense).setRegistryName(ModBlocks.constructionBlockDense.getRegistryName()));
     * event.getRegistry().register(new
     * ItemBlock(ModBlocks.constructionBlock).setRegistryName(ModBlocks.constructionBlock.getRegistryName()));
     * event.getRegistry().register(new
     * ItemBlock(ModBlocks.constructionBlockPowder).setRegistryName(ModBlocks.constructionBlockPowder.getRegistryName())
     * );
     * event.getRegistry().register(new ConstructionPaste());
     * event.getRegistry().register(new ConstructionChunkDense());
     * for (RegularPasteContainerTypes type : RegularPasteContainerTypes.values()) {
     * event.getRegistry().register(new ConstructionPasteContainer(type.itemSuffix, type.capacitySupplier));
     * }
     * event.getRegistry().register(new ConstructionPasteContainerCreative());
     * }
     * }
     * @SubscribeEvent
     * public static void registerSounds(RegistryEvent.Register<SoundEvent> event) {
     * for (ModSounds sound : ModSounds.values()) {
     * event.getRegistry().register(sound.getSound());
     * }
     * }
     */

    public void serverStarting(FMLServerStartingEvent event) {

    }
}
