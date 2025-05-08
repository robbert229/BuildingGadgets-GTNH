package com.direwolf20.buildinggadgets.common.tools;

import java.util.List;

import net.minecraft.util.ChunkCoordinates;

public class UndoState {

    public final int dimension;
    public final List<ChunkCoordinates> coordinates;

    public UndoState(int dim, List<ChunkCoordinates> coords) {
        dimension = dim;
        coordinates = coords;
    }

}
