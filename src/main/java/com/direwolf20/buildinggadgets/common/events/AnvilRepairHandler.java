package com.direwolf20.buildinggadgets.common.events;

import com.direwolf20.buildinggadgets.common.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.config.SyncedConfig;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import cpw.mods.fml.common.Mod.EventBusSubscriber;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = BuildingGadgets.MODID)
public class AnvilRepairHandler {
    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        if (SyncedConfig.poweredByFE && (event.getLeft().getItem() instanceof GadgetGeneric) && (event.getRight().getItem() == Items.DIAMOND)) {
            event.setCost(3);
            event.setMaterialCost(1);
            ItemStack newItem = event.getLeft().copy();
            newItem.setItemDamage(0);
            event.setOutput(newItem);
        }
    }
}
