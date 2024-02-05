package com.direwolf20.buildinggadgets.common.events;

import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import com.direwolf20.buildinggadgets.common.tools.InventoryManipulation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import cpw.mods.fml.common.Mod.EventBusSubscriber;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@EventBusSubscriber
public class BreakEventHandler {
    @SubscribeEvent
    public static void GetDrops(BlockEvent.HarvestDropsEvent event) {
        //If you are holding an exchanger gadget and break a block, put it into your inventory
        //This allows us to use the BreakBlock event on our exchanger, to properly remove blocks from the world.
        EntityPlayer player = event.getHarvester();
        if (player == null || player.capabilities.isCreativeMode)
            return;

        ItemStack heldItem = GadgetGeneric.getGadget(player);
        if (heldItem.isEmpty())
            return;

        List<ItemStack> drops = event.getDrops();
        drops.forEach(drop -> drop = InventoryManipulation.giveItem(drop, player, event.getWorld()));

//        drops.removeIf(item -> InventoryManipulation.giveItem(item, player, event.getWorld()).isEmpty());
    }
}

