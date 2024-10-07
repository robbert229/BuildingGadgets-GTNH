package com.direwolf20.buildinggadgets.common.blocks.templatemanager;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class SlotTemplateManager extends Slot {

    private String backgroundLoc;

    public SlotTemplateManager(IInventory inventory, int index, int xPosition, int yPosition, String backgroundLoc) {
        super(inventory, index, xPosition, yPosition);
        // TODO(johnrowl) resolve background texture issues.

        this.backgroundLoc = backgroundLoc;
        this.setBackgroundIconTexture(new ResourceLocation(backgroundLoc));
    }

    @Override
    public int getSlotStackLimit() {
        return 1;
    }
}
