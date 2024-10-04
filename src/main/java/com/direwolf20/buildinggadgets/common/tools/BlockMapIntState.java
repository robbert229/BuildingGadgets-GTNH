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

    public Map<Short, BlockMeta> intStateMap;
    public Map<BlockMeta, UniqueItem> intStackMap;

    public BlockMapIntState() {
        intStateMap = new HashMap<Short, BlockMeta>();
        intStackMap = new HashMap<BlockMeta, UniqueItem>();
    }

    // Wrapper class to hold block and metadata, which replaces IBlockState
    public static class BlockMeta {
        public final Block block;
        public final int meta;

        public BlockMeta(Block block, int meta) {
            this.block = block;
            this.meta = meta;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            BlockMeta blockMeta = (BlockMeta) obj;

            if (meta != blockMeta.meta) return false;
            return block == blockMeta.block;
        }

        @Override
        public int hashCode() {
            int result = block.hashCode();
            result = 31 * result + meta;
            return result;
        }
    }

    public Map<Short, BlockMeta> getIntStateMap() {
        return intStateMap;
    }

    public Map<BlockMeta, UniqueItem> getIntStackMap() {
        return intStackMap;
    }

    public void addToMap(BlockMeta mapState) {
        if (findSlot(mapState) == -1) {
            short nextSlot = (short) intStateMap.size();
            nextSlot++;
            intStateMap.put(nextSlot, mapState);
        }
    }

    public void addToStackMap(UniqueItem uniqueItem, BlockMeta blockState) {
        if (findStackSlot(uniqueItem) != blockState) {
            intStackMap.put(blockState, uniqueItem);
        }
    }

    public Short findSlot(BlockMeta mapState) {
        for (Map.Entry<Short, BlockMeta> entry : intStateMap.entrySet()) {
            if (entry.getValue() == mapState) {
                return entry.getKey();
            }
        }
        return -1;
    }

    @Nullable
    private BlockMeta findStackSlot(UniqueItem uniqueItem) {
        for (Map.Entry<BlockMeta, UniqueItem> entry : intStackMap.entrySet()) {
            if (entry.getValue().item == uniqueItem.item && entry.getValue().meta == uniqueItem.meta) {
                return entry.getKey();
            }
        }
        return null;
    }

    public BlockMeta getStateFromSlot(Short slot) {
        return intStateMap.get(slot);
    }

    public UniqueItem getStackFromSlot(BlockMeta blockState) {//TODO unused
        return intStackMap.get(blockState);
    }

    public Map<Short, BlockMeta> getIntStateMapFromNBT(NBTTagList tagList) {
        intStateMap = new HashMap<Short, BlockMeta>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compound = tagList.getCompoundTagAt(i);

            var mapSlot = compound.getShort("mapSlot");
            var decoded = NBTTool.blockFromCompound(compound.getCompoundTag("mapState"));

            intStateMap.put(mapSlot, new BlockMeta(decoded.getBlock(), decoded.getMeta()));
        }
        return intStateMap;
    }

    public NBTTagList putIntStateMapIntoNBT() {
        NBTTagList tagList = new NBTTagList();
        for (Map.Entry<Short, BlockMeta> entry : intStateMap.entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            NBTTagCompound state = NBTTool.blockToCompound(entry.getValue().block, entry.getValue().meta);
            compound.setShort("mapSlot", entry.getKey());
            compound.setTag("mapState", state);
            tagList.appendTag(compound);
        }
        return tagList;
    }

    public Map<BlockMeta, UniqueItem> getIntStackMapFromNBT(NBTTagList tagList) {
        intStackMap = new HashMap<BlockMeta, UniqueItem>();
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compound = tagList.getCompoundTagAt(i);

            var state = NBTTool.blockFromCompound(compound.getCompoundTag("state"));
            var bm = new BlockMeta(state.getBlock(), state.getMeta());
            var ui = new UniqueItem(Item.getItemById(compound.getInteger("item")), compound.getInteger("meta"));

            intStackMap.put(bm, ui);
        }

        return intStackMap;
    }

    public NBTTagList putIntStackMapIntoNBT() {
        NBTTagList tagList = new NBTTagList();
        for (Map.Entry<BlockMeta, UniqueItem> entry : intStackMap.entrySet()) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("item", Item.getIdFromItem(entry.getValue().item));
            compound.setInteger("meta", entry.getValue().meta);

            var state = entry.getKey();
            compound.setTag("state", NBTTool.blockToCompound(state.block, state.meta));
            tagList.appendTag(compound);
        }

        return tagList;
    }

    @Nonnull
    public static UniqueItem blockStateToUniqueItem(BlockMeta state, EntityPlayer player, ChunkCoordinates pos) {
        ItemStack itemStack;

        try {
            itemStack = state.block.getPickBlock(null, player.worldObj, pos.posX, pos.posY, pos.posZ, player);
        } catch (Exception e) {
            itemStack = InventoryManipulation.getSilkTouchDrop(state.block, state.meta);
        }

        if (itemStack == null) {
            itemStack = InventoryManipulation.getSilkTouchDrop(state.block, state.meta);
        }

        if (itemStack != null) {
            return new UniqueItem(itemStack.getItem(), itemStack.getItemDamage());
        }

        return new UniqueItem(null, 0);
    }

    public void makeStackMapFromStateMap(EntityPlayer player) {
        intStackMap.clear();
        for (Map.Entry<Short, BlockMeta> entry : intStateMap.entrySet()) {
            try {
                intStackMap.put(entry.getValue(), blockStateToUniqueItem(entry.getValue(), player, new ChunkCoordinates(0, 0, 0)));
            } catch (IllegalArgumentException e) {
                //
            }
        }
    }
}
