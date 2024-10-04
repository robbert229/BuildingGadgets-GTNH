package com.direwolf20.buildinggadgets;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentTranslation;

import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.direwolf20.buildinggadgets.common.commands.DeleteBlockMapsCommand;
import com.direwolf20.buildinggadgets.common.commands.FindBlockMapsCommand;
import com.direwolf20.buildinggadgets.common.items.ModItems;
import com.direwolf20.buildinggadgets.common.proxy.CommonProxy;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(
    modid = BuildingGadgets.MODID,
    name = "buildinggadgets",
    version = Tags.VERSION,
    acceptedMinecraftVersions = "[1.7.10]")
public class BuildingGadgets {

    public static final String MODID = "buildinggadgets";
    public static final Logger LOG = LogManager.getLogger(MODID);

    public static Configuration config;

    public static final CreativeTabs BUILDING_CREATIVE_TAB = new CreativeTabs(
        new ChatComponentTranslation("buildingGadgets").getUnformattedTextForChat()) {

        @SideOnly(Side.CLIENT)
        @Override
        public Item getTabIconItem() {
            return ModItems.gadgetBuilding;
        }
    };

    @SidedProxy(
        clientSide = "com.direwolf20.buildinggadgets.client.proxy.ClientProxy",
        serverSide = "com.direwolf20.buildinggadgets.common.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.Instance
    public static BuildingGadgets instance;

    @Mod.EventHandler
    public void preInit(final FMLPreInitializationEvent event) {
        proxy.preInit(event);

        config = new Configuration(event.getSuggestedConfigurationFile());
        loadConfig();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new FindBlockMapsCommand());
        event.registerServerCommand(new DeleteBlockMapsCommand());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }

    public static void loadConfig() {
        try {
            config.load(); // Load the config file
            // Add any config-specific loading logic here, such as default values or validation
        } catch (Exception e) {
            LOG.error("Error loading configuration for " + MODID, e);
        } finally {
            if (config.hasChanged()) {
                config.save(); // Save the config file if any changes were made
            }
        }
    }
}
