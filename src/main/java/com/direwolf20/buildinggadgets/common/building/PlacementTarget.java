package com.direwolf20.buildinggadgets.common.building;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.util.datatypes.BlockState;

/**
 * Indicates the position and type of block to use for placing something in a {@link World}.
 */
public final class PlacementTarget {

    private final BlockState state;
    private final ChunkCoordinates pos;

    public PlacementTarget(BlockState state, ChunkCoordinates pos) {
        this.state = state;
        this.pos = pos;
    }

    public BlockState getState() {
        return state;
    }

    public ChunkCoordinates getPos() {
        return pos;
    }

    /**
     * Sets the block state of the {@link #getPos()} to {@link #getState()}.
     *
     * @param world the world to place block
     */
    public void placeIn(World world) {
        world.setBlock(pos.posX, pos.posY, pos.posZ, state.getBlock(), state.getMetadata(), 2);
    }

}
