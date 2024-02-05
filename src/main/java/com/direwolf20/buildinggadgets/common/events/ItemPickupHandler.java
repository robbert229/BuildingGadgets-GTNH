package com.direwolf20.buildinggadgets.common.events;

import com.direwolf20.buildinggadgets.common.items.pastes.ConstructionPaste;
import com.direwolf20.buildinggadgets.common.tools.InventoryManipulation;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import cpw.mods.fml.common.Mod.EventBusSubscriber;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class ItemPickupHandler {
    @SubscribeEvent
    public static void GetDrops(EntityItemPickupEvent event) {
        EntityItem entityItem = event.getItem();
        ItemStack itemStack = entityItem.getItem();
        if (itemStack.getItem() instanceof ConstructionPaste) {
            itemStack = InventoryManipulation.addPasteToContainer(event.getEntityPlayer(), itemStack);
            entityItem.setItem(itemStack);
        }
    }
}
