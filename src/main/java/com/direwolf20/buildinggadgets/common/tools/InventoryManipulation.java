package com.direwolf20.buildinggadgets.common.tools;

// import com.direwolf20.buildinggadgets.common.integration.IItemAccess;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.direwolf20.buildinggadgets.common.integration.IItemAccess;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.direwolf20.buildinggadgets.common.items.ModItems;
import com.direwolf20.buildinggadgets.common.items.pastes.ConstructionPaste;
import com.direwolf20.buildinggadgets.common.items.pastes.GenericPasteContainer;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class InventoryManipulation {

    private enum InventoryType {
        PLAYER, LINKED, OTHER
    }

//     private static IProperty AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class);
    // private static final Set<IProperty> SAFE_PROPERTIES = ImmutableSet.of(BlockSlab.HALF, BlockStairs.HALF,
    // BlockLog.LOG_AXIS, AXIS, BlockDirectional.FACING, BlockStairs.FACING, BlockTrapDoor.HALF, BlockTorch.FACING,
    // BlockStairs.SHAPE, BlockLever.FACING, BlockLever.POWERED, BlockRedstoneRepeater.DELAY, BlockStoneSlab.VARIANT,
    // BlockWoodSlab.VARIANT, BlockDoubleWoodSlab.VARIANT, BlockDoubleStoneSlab.VARIANT);
    //
    // private static final Set<IProperty> SAFE_PROPERTIES_COPY_PASTE =
    // ImmutableSet.<IProperty>builder().addAll(SAFE_PROPERTIES).addAll(ImmutableSet.of(BlockDoubleWoodSlab.VARIANT,
    // BlockRail.SHAPE, BlockRailPowered.SHAPE)).build();

    public static ItemStack giveItem(ItemStack targetStack, EntityPlayer player, World world) {
        if (player.capabilities.isCreativeMode) {
            return null;
        }

        // Attempt to dump any construction paste back in it's container.
        ItemStack target = targetStack.getItem() instanceof ConstructionPaste ? addPasteToContainer(player, targetStack) : targetStack;

        if (target.stackSize == 0) {
            return null;
        }

        ItemStack tool = GadgetGeneric.getGadget(player);
        for (Pair<InventoryType, IInventory> inv : collectInventories(tool, player, world, NetworkIO.Operation.INSERT)) {
            target = insertIntoInventory(inv.getValue(), target, inv.getKey());
            if (target == null) {
                return null;
            }
        }

        return target;
    }

    private static ItemStack insertIntoInventory(IInventory inventory, ItemStack target, InventoryType type) {
        if (inventory == null) return target;

        // First try and deposit the majority to slots that contain that item.
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (target == null || target.getItem() == null) {
                return target; // Return here to not run the next for loop
            }

            ItemStack containerItem = inventory.getStackInSlot(i);
            if ((containerItem != null && containerItem.getItem() != null) && containerItem.getItem() == target.getItem() && containerItem.getItemDamage() == target.getItemDamage()) {
                // Chunk and calculate how much to insert per stack.
                int insertCount = (target.stackSize - containerItem.stackSize) > containerItem.getMaxStackSize() ? (target.stackSize - containerItem.stackSize) : target.stackSize;

                if (containerItem.stackSize + insertCount > target.getMaxStackSize()) {
                    continue;
                }

                ItemStack insertStack = containerItem.copy();
                insertStack.stackSize = insertCount;

                inventory.setInventorySlotContents(i, insertStack);
                if (target.stackSize >= insertCount) {
                    target.stackSize -= insertCount;
                } else {
                    target.stackSize = 0;
                }
            }
        }

        // Finally, just dump it in any empty slots. (we'll throw it on the ground if there is still some left so don't
        // worry about the remainder)
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (target == null || target.getItem() == null) {
                break;
            }

            ItemStack containerItem = inventory.getStackInSlot(i);
            if ((containerItem != null && containerItem.getItem() != null) || !inventory.isItemValidForSlot(i, target) || type == InventoryType.PLAYER && i == 40) {
                continue;

            }

            ItemStack insertStack = target.copy();
            insertStack.stackSize = target.stackSize > target.getMaxStackSize() ? containerItem.getMaxStackSize() : target.stackSize;

            inventory.setInventorySlotContents(i, insertStack);
            // TODO(johnrowl) come back here and verify everything is sane later.

            //ItemStack stack = inventory.insertItem(i, insertStack, true);
            //if (stack.getCount() == insertStack.getCount()) {
            // continue;
            //}

//            inventory.insertItem(i, insertStack, false);
//            target.shrink(insertStack.getCount());
        }

        return target;
    }

    /**
     * Runs though each inventory to find and use the items required for the building inventory.
     * See {@link #collectInventories(ItemStack, EntityPlayer, World, NetworkIO.Operation)} to find the order of
     * inventories returned.
     *
     * @return boolean based on if the method was able to supply any amount of items. If the method is called requiring
     *         10 items and we only find 5 we still return true. We only return false if no items where supplied.
     *         This is by design.
     * @implNote Call {@link GadgetUtils#clearCachedRemoteInventory GadgetUtils#clearCachedRemoteInventory} when done
     *           using this method
     */
    // public static boolean useItem(ItemStack target, EntityPlayer player, int amountRequired, World world) {
    // if (player.capabilities.isCreativeMode) return true;
    //
    // int amountLeft = amountRequired;
    // for (Pair<InventoryType, IItemHandler> inv : collectInventories(GadgetGeneric.getGadget(player), player, world,
    // NetworkIO.Operation.EXTRACT)) {
    // amountLeft -= extractFromInventory(inv.getValue(), target, amountLeft, player);
    //
    // if (amountLeft <= 0) return true;
    // }
    //
    // return amountLeft < amountRequired;
    // }

    // private static int extractFromInventory(IInventory inventory, ItemStack target, int amountRequired, EntityPlayer
    // player) {
    // int amountSaturated = 0;
    // if (inventory == null) return amountSaturated;
    //
    // if (inventory instanceof IItemAccess) {
    // amountSaturated += ((IItemAccess) inventory).extractItems(target, amountRequired - amountSaturated, player);
    // return amountSaturated;
    // }
    //
    // for (int i = 0; i < inventory.getSlots(); i++) {
    // ItemStack containerItem = inventory.getStackInSlot(i);
    // if (containerItem.getItem() == target.getItem() && containerItem.getMetadata() == target.getMetadata()) {
    // ItemStack stack = inventory.extractItem(i, amountRequired, false);
    // amountSaturated += stack.getCount();
    // }
    //
    // // Don't continue to check if we've saturated the amount.
    // if (amountSaturated >= amountRequired) break;
    // }
    //
    // return amountSaturated;
    // }

    /**
     * Collect all the inventories and return them in a pretty order to allow for inventory flowing (where the system
     * will use the items from each inventory in a flow like order instead of being bound to a single inventory at
     * a time)
     *
     * @return a list of inventories in the order of: Linked, Player, Player inventory slotted inventories (dank null)
     */
    private static List<Pair<InventoryType, IInventory>> collectInventories(ItemStack gadget, EntityPlayer player, World world, NetworkIO.Operation operation) {
        List<Pair<InventoryType, IInventory>> inventories = new ArrayList<>();

        // Always provide the remote inventory first
        IInventory linked = GadgetUtils.getRemoteInventory(gadget, world, player, operation);
        if (linked != null) {
            inventories.add(Pair.of(InventoryType.LINKED, linked));
        }

        // Then supply the player inventory if it exists (it should)
        IInventory currentInv = player.inventory;
        if (currentInv == null) {
            return inventories;
        }

        inventories.add(Pair.of(InventoryType.PLAYER, currentInv));

        // Finally, add all inventory bound inventories to the list. Then return them all.
        for (int i = 0; i < currentInv.getSizeInventory(); ++i) {
            ItemStack itemStack = currentInv.getStackInSlot(i);
            if (itemStack == null) {
                continue;
            }

            if (itemStack.getItem() instanceof IInventory itemInventory) {
                inventories.add(Pair.of(InventoryType.OTHER, itemInventory));
            }
        }

        return inventories;
    }

    /**
     * -------------------------------------
     * START WEIRD COUNT ITEM IMPLEMENTATION
     * -------------------------------------
     */
    public interface IRemoteInventoryProvider {
        int countItem(ItemStack tool, ItemStack stack);
    }

    /**
     * Call {@link GadgetUtils#clearCachedRemoteInventory GadgetUtils#clearCachedRemoteInventory} when done using this
     * method
     */
    public static int countItem(ItemStack itemStack, EntityPlayer player, World world) {
        return countItem(itemStack, player, (tool, stack) -> {
            IInventory remoteInventory = GadgetUtils.getRemoteInventory(tool, world, player);
            if (remoteInventory instanceof IItemAccess)
                return ((IItemAccess) remoteInventory).getItemsForExtraction(stack, player);
            return remoteInventory == null ? 0 : countInContainer(remoteInventory, stack.getItem(), stack.getItemDamage());
        });
    }

    public static int countItem(ItemStack itemStack, EntityPlayer player, IRemoteInventoryProvider remoteInventory) {
        if (player.capabilities.isCreativeMode) return Integer.MAX_VALUE;

        long count = remoteInventory.countItem(GadgetGeneric.getGadget(player), itemStack);

        IInventory currentInv = player.inventory;
        if (currentInv == null) return 0;

        List<Integer> slots = findItem(itemStack.getItem(), itemStack.getItemDamage(), currentInv);
        List<IInventory> invContainers = findInvContainers(player);
        if (slots.size() == 0 && invContainers.size() == 0 && count == 0) {
            return 0;
        }

        if (invContainers.size() > 0) {
            for (IInventory container : invContainers) {
                {
                    if (container instanceof IItemAccess) {
                        count += ((IItemAccess) container).getItemsForExtraction(itemStack, player);
                    } else {
                        count += countInContainer(container, itemStack.getItem(), itemStack.getItemDamage());
                    }
                }
            }
        }

        for (int slot : slots) {
            ItemStack stackInSlot = currentInv.getStackInSlot(slot);
            count += stackInSlot.stackSize;
        }

        return MathTool.longToInt(count);
    }

    public static IntList countItems(List<ItemStack> items, EntityPlayer player) {
        IntList result = new IntArrayList();
        for (ItemStack item : items) {
            result.add(countItem(item, player, player.worldObj));
        }
        return result;
    }

    public static int countPaste(EntityPlayer player) {
        if (player.capabilities.isCreativeMode) {
            return Integer.MAX_VALUE;
        }

        IInventory currentInv = player.inventory;
        if (currentInv == null) {
            return 0;
        }

        long count = 0;
        Item item = ModItems.constructionPaste;
        List<Integer> slots = findItem(item, 0, currentInv);
        if (slots.size() > 0) {
            for (int slot : slots) {
                ItemStack stackInSlot = currentInv.getStackInSlot(slot);
                count += stackInSlot.stackSize;
            }
        }
        List<Integer> containerSlots = findItemClass(GenericPasteContainer.class, currentInv);
        if (containerSlots.size() > 0) {
            for (int slot : containerSlots) {
                ItemStack stackInSlot = currentInv.getStackInSlot(slot);
                if (stackInSlot.getItem() instanceof GenericPasteContainer) {
                    count += GenericPasteContainer.getPasteAmount(stackInSlot);
                }
            }
        }
        return MathTool.longToInt(count);
    }

    /**
     * -------------------------------------
     * END WEIRD COUNT ITEM IMPLEMENTATION
     * -------------------------------------
     */

    public static ItemStack addPasteToContainer(EntityPlayer player, ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ConstructionPaste)) return itemStack;

        InventoryPlayer currentInv = player.inventory;
        if (currentInv == null) return itemStack;

        List<Integer> slots = findItemClass(GenericPasteContainer.class, currentInv);
        if (slots.isEmpty()) return itemStack;

        Map<Integer, Integer> slotMap = new HashMap<>();
        for (int slot : slots) {
            slotMap.put(slot, GenericPasteContainer.getPasteAmount(currentInv.getStackInSlot(slot)));
        }
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(slotMap.entrySet());
        Comparator<Map.Entry<Integer, Integer>> comparator = Comparator.comparing(Map.Entry::getValue);
        comparator = comparator.reversed();
        list.sort(comparator);

        for (Map.Entry<Integer, Integer> entry : list) {
            ItemStack containerStack = currentInv.getStackInSlot(entry.getKey());
            int maxAmount = ((GenericPasteContainer) containerStack.getItem()).getMaxCapacity();
            int pasteInContainer = GenericPasteContainer.getPasteAmount(containerStack);
            int freeSpace = maxAmount - pasteInContainer;
            ;
            int remainingPaste = itemStack.stackSize - freeSpace;
            if (remainingPaste < 0) {
                remainingPaste = 0;
            }
            int usedPaste = Math.abs(itemStack.stackSize - remainingPaste);
            itemStack.stackSize = remainingPaste;

            GenericPasteContainer.setPasteAmount(containerStack, pasteInContainer + usedPaste);
        }
        return itemStack;
    }

    public static boolean usePaste(EntityPlayer player, int count) {
        if (player.capabilities.isCreativeMode) {
            return true;
        }

        IInventory currentInv = player.inventory;
        if (currentInv == null) return false;

        List<Integer> slots = findItem(ModItems.constructionPaste, 0, currentInv);
        if (!slots.isEmpty()) {
            for (int slot : slots) {
                ItemStack pasteStack = currentInv.getStackInSlot(slot);
                if (pasteStack.stackSize >= count) {
                    pasteStack.stackSize = count;
                    // pasteStack.shrink(count);
                    return true;
                }
            }
        }

        List<Integer> containerSlots = findItemClass(GenericPasteContainer.class, currentInv);
        if (containerSlots.size() > 0) {
            for (int slot : containerSlots) {
                ItemStack containerStack = currentInv.getStackInSlot(slot);
                if (containerStack.getItem() instanceof GenericPasteContainer) {
                    int pasteAmt = GenericPasteContainer.getPasteAmount(containerStack);
                    if (pasteAmt >= count) {
                        GenericPasteContainer.setPasteAmount(containerStack, pasteAmt - count);
                        return true;
                    }

                }
            }
        }

        return false;
    }

    private static List<IInventory> findInvContainers(EntityPlayer player) {
        List<IInventory> containers = new ArrayList<>();

        IInventory currentInv = player.inventory;
        if (currentInv == null) return containers;

        for (int i = 0; i < currentInv.getSizeInventory(); ++i) {
            ItemStack itemStack = currentInv.getStackInSlot(i);
            if (itemStack != null && itemStack.getItem() instanceof IInventory inventoryStack) {
                containers.add(inventoryStack);
            }
        }

        return containers;
    }

    public static int countInContainer(IInventory container, Item item, int meta) {
        int count = 0;
        ItemStack tempItem;
        for (int i = 0; i < container.getSizeInventory(); i++) {
            tempItem = container.getStackInSlot(i);
            if (tempItem.getItem() == item && tempItem.getItemDamage() == meta) {
                count += tempItem.stackSize;
            }
        }
        return count;
    }

    private static List<Integer> findItem(Item item, int meta, IInventory itemHandler) {
        List<Integer> slots = new ArrayList<>();
        if (itemHandler == null) return slots;

        for (int i = 0; i < itemHandler.getSizeInventory(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack != null && stack.getItem() != null && stack.getItem() == item && meta == stack.getItemDamage())
                slots.add(i);
        }

        return slots;
    }

    private static List<Integer> findItemClass(Class c, IInventory itemHandler) {
        List<Integer> slots = new ArrayList<>();

        for (int i = 0; i < itemHandler.getSizeInventory(); i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (stack != null && stack.getItem() != null && c.isInstance(stack.getItem())) {
                slots.add(i);
            }
        }
        return slots;
    }

    public static ItemStack getSilkTouchDrop(Block block, int meta) {
        Item item = Item.getItemFromBlock(block);
        if (item == null) {
            return null;
        }

        int i = 0;

        if (item.getHasSubtypes()) {
            i = block.damageDropped(meta);
        }

        return new ItemStack(item, 1, i);
    }

    // public static IBlockState getSpecificStates(IBlockState originalState, World world, EntityPlayer player,
    // ChunkCoordinates pos, ItemStack tool) {
    // IBlockState placeState;
    // Block block = originalState.getBlock();
    //
    // ItemStack item;
    // try {
    // item = block.getPickBlock(originalState, null, world, pos, player);
    // } catch (Exception ignored) {
    // // This may introduce issues. I hope it doesn't
    // item = InventoryManipulation.getSilkTouchDrop(originalState);
    // }
    //
    // int meta = item.getMetadata();
    // try {
    // placeState = originalState.getBlock().getStateForPlacement(world, pos, EnumFacing.UP, 0, 0, 0, meta, player,
    // EnumHand.MAIN_HAND);
    // } catch (Exception var8) {
    // placeState = originalState.getBlock().getDefaultState();
    // }
    // for (IProperty prop : placeState.getPropertyKeys()) {
    // if (tool.getItem() instanceof GadgetCopyPaste) {
    // if (SAFE_PROPERTIES_COPY_PASTE.contains(prop)) {
    // placeState = placeState.withProperty(prop, originalState.getValue(prop));
    // }
    // } else {
    // if (SAFE_PROPERTIES.contains(prop)) {
    // placeState = placeState.withProperty(prop, originalState.getValue(prop));
    // }
    // }
    // }
    // return placeState;
    //
    // }

    /**
     * Find an item stack in either hand that delegates to the given {@code itemClass}.
     * <p>
     * This method will prioritize primary hand, which means if player hold the desired item on both hands, it will
     * choose his primary hand first. If neither hands have the desired item stack, it will return {@link
     * null}.
     *
     * @return {@link null} when neither hands met the parameter.
     */
    public static ItemStack getStackInEitherHand(EntityPlayer player, Class<?> itemClass) {
        ItemStack mainHand = player.getHeldItem();
        if (itemClass.isInstance(mainHand.getItem())) return mainHand;

        return null;
    }

    public static String formatItemCount(int maxSize, int count) {
        int stacks = count / maxSize; // Integer division automatically floors
        int leftover = count % maxSize;
        if (stacks == 0) return String.valueOf(leftover);
        return stacks + "×" + maxSize + "+" + leftover;
    }
}
