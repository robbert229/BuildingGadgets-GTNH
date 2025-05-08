package com.direwolf20.buildinggadgets.common.tools;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;

public abstract class NetworkIO<P extends NetworkIO.IStackProvider> implements IInventory {

    private final List<P> stackProviders;
    protected final EntityPlayer player;

    protected NetworkIO(EntityPlayer player, @Nullable Collection<P> stackProviders) {
        this.player = player;
        this.stackProviders = stackProviders != null ? ImmutableList.copyOf(stackProviders)
            : (ImmutableList<P>) ImmutableList.of(new StackProviderVanilla(null));
    }

    public static enum Operation {
        EXTRACT,
        INSERT
    }

    @Override
    public int getSizeInventory() {
        return stackProviders.size();
    }

    @Override
    @Nonnull
    public ItemStack getStackInSlot(int slot) {
        return getStackProviderInSlot(slot).getStack();
    }

    protected P getStackProviderInSlot(int slot) {
        return stackProviders.get(slot);
    }

    @Nullable
    protected abstract ItemStack insertItemInternal(ItemStack stack, boolean simulate);

    // @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return insertItemInternal(stack, simulate);
    }

    @Nonnull
    protected abstract IStackProvider extractItemInternal(P stackProvider, int amount, boolean simulate);

    // @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        P stackProvider = getStackProviderInSlot(slot);
        IStackProvider result = extractItemInternal(stackProvider, amount, simulate);
        stackProvider.shrinkStack(amount);
        return result.getStack();
    }

    // @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    public static interface IStackProvider {

        ItemStack getStack();

        void shrinkStack(int amount);
    }

    public static class StackProviderVanilla implements IStackProvider {

        private ItemStack stack;

        public StackProviderVanilla(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        @Nonnull
        public ItemStack getStack() {
            return stack;
        }

        @Override
        public void shrinkStack(int amount) {
            if (stack != null) {
                stack.stackSize -= amount; // Decrease stackSize by the amount
                if (stack.stackSize <= 0) {
                    stack = null; // If the stack size drops to zero, set the stack to null
                }
            }
        }
    }
}
