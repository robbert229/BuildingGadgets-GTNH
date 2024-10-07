package com.direwolf20.buildinggadgets.common.tools;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ChunkCoordinates;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class BlockMapIntState {

    public Map<Short, BlockState> intStateMap;
    public Map<BlockState, UniqueItem> intStackMap;

    public BlockMapIntState() {
        intStateMap = new HashMap<Short, BlockState>();
        intStackMap = new HashMap<BlockState, UniqueItem>();
    }

    public Map<Short, BlockState> getIntStateMap() {
        return intStateMap;
    }

    public Map<BlockState, UniqueItem> getIntStackMap() {
        return intStackMap;
    }

    public void addToMap(BlockState mapState) {
        if (findSlot(mapState) == -1) {
            short nextSlot = (short) intStateMap.size();
            nextSlot++;
            intStateMap.put(nextSlot, mapState);
        }
    }

    public void addToStackMap(UniqueItem uniqueItem, BlockState blockState) {
        if (findStackSlot(uniqueItem) != blockState) {
            intStackMap.put(blockState, uniqueItem);
        }
    }

    public Short findSlot(BlockState mapState) {
        for (Map.Entry<Short, BlockState> entry : intStateMap.entrySet()) {
            if (entry.getValue() == mapState) {
                return entry.getKey();
            }
        }
        return -1;
    }

    @Nullable
    private BlockState findStackSlot(UniqueItem uniqueItem) {
        for (Map.Entry<BlockState, UniqueItem> entry : intStackMap.entrySet()) {
            if (entry.getValue().item == uniqueItem.item && entry.getValue().meta == uniqueItem.meta) {
                return entry.getKey();
            }
        }
        return null;
    }

    public BlockState getStateFromSlot(Short slot) {
        return intStateMap.get(slot);
    }

    public UniqueItem getStackFromSlot(BlockState blockState) {//TODO unused
        return intStackMap.get(blockState);
    }

    public void getIntStateMapFromNBT(NBTTagList tagList) {
        intStateMap = new HashMap<Short, BlockState>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compound = tagList.getCompoundTagAt(i);

            var mapSlot = compound.getShort("mapSlot");
            var decoded = NBTTool.blockFromCompound(compound.getCompoundTag("mapState"));

            if (decoded.isAir()) {
                continue;
            }

            intStateMap.put(mapSlot, decoded);
        }
    }

    public NBTTagList putIntStateMapIntoNBT() {
        NBTTagList tagList = new NBTTagList();
        for (Map.Entry<Short, BlockState> entry : intStateMap.entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            NBTTagCompound state = NBTTool.blockToCompound(entry.getValue());
            compound.setShort("mapSlot", entry.getKey());
            compound.setTag("mapState", state);
            tagList.appendTag(compound);
        }
        return tagList;
    }

    public Map<BlockState, UniqueItem> getIntStackMapFromNBT(NBTTagList tagList) {
        intStackMap = new HashMap<BlockState, UniqueItem>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compound = tagList.getCompoundTagAt(i);

            var state = NBTTool.blockFromCompound(compound.getCompoundTag("state"));
            var bm = new BlockState(state.getBlock(), state.getMetadata());
            var ui = new UniqueItem(Item.getItemById(compound.getInteger("item")), compound.getInteger("meta"));

            intStackMap.put(bm, ui);
        }

        return intStackMap;
    }

    public NBTTagList putIntStackMapIntoNBT() {
        NBTTagList tagList = new NBTTagList();
        for (Map.Entry<BlockState, UniqueItem> entry : intStackMap.entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("item", Item.getIdFromItem(entry.getValue().item));
            compound.setInteger("meta", entry.getValue().meta);

            var state = entry.getKey();
            compound.setTag("state", NBTTool.blockToCompound(state));
            tagList.appendTag(compound);
        }

        return tagList;
    }

    @Nonnull
    public static UniqueItem blockStateToUniqueItem(BlockState state, EntityPlayer player, ChunkCoordinates pos) {
        ItemStack itemStack;

        try {
            itemStack = state.getBlock().getPickBlock(null, player.worldObj, pos.posX, pos.posY, pos.posZ, player);
        } catch (Exception e) {
            itemStack = InventoryManipulation.getSilkTouchDrop(state.getBlock(), state.getMetadata());
        }

        if (itemStack == null) {
            itemStack = InventoryManipulation.getSilkTouchDrop(state.getBlock(), state.getMetadata());
        }

        if (itemStack != null) {
            return new UniqueItem(itemStack.getItem(), itemStack.getItemDamage());
        }

        return new UniqueItem(null, 0);
    }

    public void makeStackMapFromStateMap(EntityPlayer player) {
        intStackMap.clear();

        for (Map.Entry<Short, BlockState> entry : intStateMap.entrySet()) {
            try {
                intStackMap.put(entry.getValue(), blockStateToUniqueItem(entry.getValue(), player, new ChunkCoordinates(0, 0, 0)));
            } catch (IllegalArgumentException e) {
                //
            }
        }
    }
}
