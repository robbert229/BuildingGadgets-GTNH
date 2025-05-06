package com.direwolf20.buildinggadgets.common.blocks.templatemanager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.direwolf20.buildinggadgets.BuildingGadgets;

public class TemplateManagerContainer extends Container {

    public static final String TEXTURE_LOC_SLOT_TOOL = BuildingGadgets.MODID + ":gui/slot_copypastetool";
    public static final String TEXTURE_LOC_SLOT_TEMPLATE = BuildingGadgets.MODID + ":gui/slot_template";
    private final TemplateManagerTileEntity te;

    public TemplateManagerContainer(IInventory playerInventory, TemplateManagerTileEntity te) {
        this.te = te;

        // This container references items out of our own inventory (the 9 slots we hold ourselves)
        // as well as the slots from the player inventory so that the user can transfer items between
        // both inventories. The two calls below make sure that slots are defined for both inventories.
        addOwnSlots();
        addPlayerSlots(playerInventory);
    }

    private void addPlayerSlots(IInventory playerInventory) {
        // Slots for the main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 8 + col * 18;
                int y = row * 18 + 84;
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x, y));
            }
        }

        // Slots for the hotbar
        for (int row = 0; row < 9; ++row) {
            int x = 8 + row * 18;
            int y = 58 + 84;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
        }
    }

    private void addOwnSlots() {
        addSlotToContainer(new SlotTemplateManager(te, 0, 86, 41, TEXTURE_LOC_SLOT_TOOL));
        addSlotToContainer(new SlotTemplateManager(te, 1, 144, 41, TEXTURE_LOC_SLOT_TEMPLATE));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack() && slot.getStack() != null) {
            ItemStack itemstack1 = slot.getStack();

            itemstack = itemstack1.copy();

            if (index < TemplateManagerTileEntity.SIZE) {
                if (!this
                    .mergeItemStack(itemstack1, TemplateManagerTileEntity.SIZE, this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, TemplateManagerTileEntity.SIZE, false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return te.canInteractWith(playerIn);
    }
}
