package com.direwolf20.buildinggadgets.common.tools;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

import javax.annotation.Nullable;

/**
 * Used to store a single Blocks Position along with it's state
 * It's important that we also remember if we're storing a construction
 * block as this will be important to know for many of our tools.
 * <p>
 * This class also comes with a handy method to allow us to convert our data
 * into a NBTTagCompound and back out of a NBTTagCompound making this ideal
 * for any code that uses this data to store to the world save.
 */
public class BlockPosState {
    private static final String NBT_BLOCK_POS = "block_pos";
    private static final String NBT_BLOCK_STATE = "block_state";
    private static final String NBT_BLOCK_PASTE = "block_is_paste";

    private ChunkCoordinates pos;
    private Block block;
    private int meta;
    private boolean isPaste;

    public BlockPosState(ChunkCoordinates pos, Block block, int meta, boolean isPaste) {
        this.pos = pos;
        this.meta = meta;
        this.block = block;
        this.isPaste = isPaste;
    }

    public ChunkCoordinates getPos() {
        return pos;
    }

    public Block getBlock() {
        return block;
    }

    public int getMeta() {
        return meta;
    }

    public boolean isPaste() {
        return isPaste;
    }

    /**
     * Convert our data to an NBTTagCompound
     *
     * @return NBTTagCompound a compound with a position, state and paste
     */
    public NBTTagCompound toCompound() {
        NBTTagCompound compound = new NBTTagCompound();

        NBTTagCompound posCompound = new NBTTagCompound();
        posCompound.setInteger("x", this.pos.posX);
        posCompound.setInteger("y", this.pos.posY);
        posCompound.setInteger("z", this.pos.posZ);

        compound.setTag(NBT_BLOCK_STATE, NBTTool.blockToCompound(this.block, this.meta));
        compound.setTag(NBT_BLOCK_POS, posCompound);
        compound.setBoolean(NBT_BLOCK_PASTE, this.isPaste);

        return compound;
    }

    /**
     * Convert our NBTTagCompound from toCompound back out to a new
     * BlockPosState.
     */
    @Nullable
    public static BlockPosState fromCompound(NBTTagCompound compound) {
        if (!compound.hasKey(NBT_BLOCK_POS)) {
            return null;
        }

        // Retrieve position
        NBTTagCompound posCompound = compound.getCompoundTag(NBT_BLOCK_POS);
        ChunkCoordinates pos = new ChunkCoordinates(
                posCompound.getInteger("x"),
                posCompound.getInteger("y"),
                posCompound.getInteger("z")
        );

        // Retrieve block and metadata
        var decoded = NBTTool.blockFromCompound(compound.getCompoundTag(NBT_BLOCK_STATE));

        boolean isPaste = compound.getBoolean(NBT_BLOCK_PASTE);

        return new BlockPosState(pos, decoded.getBlock(), decoded.getMeta(), isPaste);
    }
}
