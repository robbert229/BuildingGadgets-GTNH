package com.direwolf20.buildinggadgets.common.crafting;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.direwolf20.buildinggadgets.common.items.pastes.GenericPasteContainer;

public class ConstructionPasteContainerUpgradeRecipe extends ShapedOreRecipe {

    public ConstructionPasteContainerUpgradeRecipe(ItemStack result, Object... recipe) {
        super(result, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inventory) {
        ItemStack output = super.getCraftingResult(inventory);
        if (output == null) {
            return null;
        }

        int totalPaste = 0;
        for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
            ItemStack ingredient = inventory.getStackInSlot(slot);
            if (ingredient != null && ingredient.getItem() instanceof GenericPasteContainer) {
                totalPaste += GenericPasteContainer.getPasteAmount(ingredient);
            }
        }

        GenericPasteContainer.setPasteAmount(output, totalPaste);
        return output;
    }
}
