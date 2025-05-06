package com.direwolf20.buildinggadgets.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;

public class ItemStackKey {
    public final Item item;  // In 1.7.10, just store the Item directly
    public final NBTTagCompound nbt;  // Use NBTTagCompound for item data
    private final int hash;

    public ItemStackKey(ItemStack stack, boolean compareNBT) {
        this.item = stack.getItem();
        this.nbt = compareNBT && stack.hasTagCompound() ? stack.getTagCompound() : null;
        this.hash = Objects.hash(item, nbt);
    }

    // Method to get an ItemStack from the ItemStackKey with a quantity of 1
    public ItemStack getStack() {
        ItemStack stack = new ItemStack(item, 1);
        if (nbt != null) {
            stack.setTagCompound((NBTTagCompound) nbt.copy());  // Copy the NBT data into the new stack
        }
        return stack;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemStackKey other)) {
            return false;
        }

        return other.item == this.item
                && Objects.equals(other.nbt, this.nbt);
    }
}