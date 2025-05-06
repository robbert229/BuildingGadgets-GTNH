package com.direwolf20.buildinggadgets.common.events;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import com.direwolf20.buildinggadgets.common.tools.InventoryManipulation;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BreakEventHandler {

    public BreakEventHandler() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void GetDrops(BlockEvent.HarvestDropsEvent event) {
        // If you are holding an exchanger gadget and break a block, put it into your inventory
        // This allows us to use the BreakBlock event on our exchanger, to properly remove blocks from the world.
        EntityPlayer player = event.harvester;
        if (player == null || player.capabilities.isCreativeMode) {
            return;
        }

        ItemStack heldItem = GadgetGeneric.getGadget(player);
        if (heldItem == null) {
            return;
        }

        List<ItemStack> drops = event.drops;
        drops.forEach(drop -> InventoryManipulation.giveItem(drop, player, event.world));
    }
}
