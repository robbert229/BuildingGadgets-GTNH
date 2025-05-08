package com.direwolf20.buildinggadgets.common.events;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import com.direwolf20.buildinggadgets.common.config.SyncedConfig;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class AnvilRepairHandler {

    // Registering the event manually, as there is no @Mod.EventBusSubscriber
    public AnvilRepairHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onPlayerEvent(PlayerEvent event) {
        if (SyncedConfig.poweredByFE && (event.entityPlayer.getCurrentEquippedItem()
            .getItem() instanceof GadgetGeneric)
            && (event.entityPlayer.inventory.getCurrentItem()
                .getItem() == Items.diamond)) {

            event.entityPlayer.inventory.getCurrentItem()
                .setRepairCost(3);

            ItemStack newItem = event.entityPlayer.getCurrentEquippedItem()
                .copy();
            newItem.setItemDamage(0);

            event.entityPlayer.setCurrentItemOrArmor(0, newItem);
        }
    }
}
