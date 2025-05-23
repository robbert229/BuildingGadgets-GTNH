package com.direwolf20.buildinggadgets.common.tools;

import net.minecraft.util.ChunkCoordinates;

import com.direwolf20.buildinggadgets.util.datatypes.BlockState;

public class BlockMap {

    public final ChunkCoordinates pos;
    public final BlockState state;
    public int xOffset = 0;
    public int yOffset = 0;
    public int zOffset = 0;

    public BlockMap(ChunkCoordinates blockPos, BlockState iBlockState) {
        pos = blockPos;
        state = iBlockState;
    }

    public BlockMap(ChunkCoordinates blockPos, BlockState iBlockState, int x, int y, int z) {
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
