package com.direwolf20.buildinggadgets.common.events;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.config.SyncedConfig;
import com.direwolf20.buildinggadgets.common.network.PacketHandler;
import com.direwolf20.buildinggadgets.common.network.PacketRequestConfigSync;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class ConfigEventHandler {

    public ConfigEventHandler() {
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            BuildingGadgets.LOG
                .info("Sending SyncedConfig to freshly logged in player {}.", event.player.getCommandSenderName());
            SyncedConfig.sendConfigUpdateTo((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public static void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(BuildingGadgets.MODID)) {
            BuildingGadgets.LOG.info("Configuration changed. Syncing config File.");

            Configuration config = BuildingGadgets.config;
            config.load();

            PacketHandler.INSTANCE.sendToServer(new PacketRequestConfigSync());
        }
    }
}
