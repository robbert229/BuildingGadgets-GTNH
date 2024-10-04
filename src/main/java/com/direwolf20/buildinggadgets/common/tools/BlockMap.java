package com.direwolf20.buildinggadgets.common.tools;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;

public class BlockMap {

    public final ChunkCoordinates pos;
    public final Block state;
    public int xOffset = 0;
    public int yOffset = 0;
    public int zOffset = 0;

    public BlockMap(ChunkCoordinates blockPos, Block iBlockState) {
        pos = blockPos;
        state = iBlockState;
    }

    public BlockMap(ChunkCoordinates blockPos, Block iBlockState, int x, int y, int z) {
        pos = blockPos;
        state = iBlockState;
        xOffset = x;
        yOffset = y;
        zOffset = z;
    }

    public boolean equals(BlockMap map) {
        return (map.pos.equals(pos) && map.state.equals(state));
    }
}
