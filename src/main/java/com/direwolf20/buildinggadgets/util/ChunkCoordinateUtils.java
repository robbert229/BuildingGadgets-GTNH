package com.direwolf20.buildinggadgets.util;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;

public class ChunkCoordinateUtils {

    public static ChunkCoordinates offset(ChunkCoordinates coordinates, EnumFacing facing, int distance) {
        return new ChunkCoordinates(
            coordinates.posX + (facing.getFrontOffsetX() * distance),
            coordinates.posY + (facing.getFrontOffsetY() * distance),
            coordinates.posZ + (facing.getFrontOffsetZ() * distance));
    }

    public static ChunkCoordinates offset(ChunkCoordinates coordinates, EnumFacing facing) {
        return offset(coordinates, facing, 1);
    }

    public static ChunkCoordinates add(ChunkCoordinates first, ChunkCoordinates second) {
        return new ChunkCoordinates(first.posX + second.posX, first.posY + second.posY, first.posZ + second.posZ);
    }

    public static ChunkCoordinates add(int firstX, int firstY, int firstZ, int secondX, int secondY, int secondZ) {
        return new ChunkCoordinates(firstX + secondX, firstY + secondY, firstZ + secondZ);
    }

    public static ChunkCoordinates up(ChunkCoordinates coordinates, int y) {
        return new ChunkCoordinates(coordinates.posX, coordinates.posY + y, coordinates.posZ);
    }

    public static ChunkCoordinates up(ChunkCoordinates coordinates) {
        return up(coordinates, 1);
    }

    public static ChunkCoordinates down(ChunkCoordinates coordinates) {
        return down(coordinates, 1);
    }

    public static ChunkCoordinates down(ChunkCoordinates coordinates, int y) {
        return up(coordinates, -1 * y);
    }

    public static ChunkCoordinates fromLong(long packedPos) {
        int x = (int) (packedPos >> 38);
        int y = (int) ((packedPos >> 26) & 0xFFF);
        int z = (int) (packedPos << 38 >> 38);

        return new ChunkCoordinates(x, y, z);
    }

    public static long toLong(ChunkCoordinates coordinates) {
        return ((long) coordinates.posX & 0x3FFFFFF) << 38 | ((long) coordinates.posY & 0xFFF) << 26
            | ((long) coordinates.posZ & 0x3FFFFFF);
    }

    public static long toLong(int x, int y, int z) {
        return toLong(new ChunkCoordinates(x, y, z));
    }
}
