package com.direwolf20.buildinggadgets.util.datatypes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

import com.direwolf20.buildinggadgets.util.NBTTool;

public class TagPos {

    public NBTTagCompound tag;
    public ChunkCoordinates pos;

    private static final String NBT_BLOCK_TAG_KEY = "blocktag";
    private static final String NBT_BLOCK_CHUNK_COORDINATES = "blockpos";
    private static final String NBT_TILE_ENTITY_DATA_KEY = "tedata";

    public TagPos(NBTTagCompound tag, ChunkCoordinates pos) {
        this.tag = tag;
        this.pos = pos;
    }

    public TagPos(NBTTagCompound compoundTag) {
        var blockStateCompound = compoundTag.getCompoundTag(NBT_BLOCK_TAG_KEY);
        var blockChunkCoordinates = compoundTag.getCompoundTag(NBT_BLOCK_CHUNK_COORDINATES);

        if (blockStateCompound == null || blockChunkCoordinates == null) {
            this.tag = null;
            this.pos = null;
        }

        this.tag = compoundTag.getCompoundTag(NBT_TILE_ENTITY_DATA_KEY);

        var coordinateTag = compoundTag.getCompoundTag(NBT_BLOCK_CHUNK_COORDINATES);
        if (coordinateTag != null) {
            this.pos = NBTTool.getPosFromTag(coordinateTag);
        }
    }

    public NBTTagCompound getTag() {
        NBTTagCompound compoundTag = new NBTTagCompound();
        compoundTag.setTag(NBT_TILE_ENTITY_DATA_KEY, tag);
        compoundTag.setTag(NBT_BLOCK_CHUNK_COORDINATES, NBTTool.createPosTag(pos));
        return compoundTag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TagPos) {
            return ((TagPos) obj).tag.equals(this.tag) && ((TagPos) obj).pos.equals(this.pos);
        }
        return false;
    }
}
