package com.direwolf20.buildinggadgets.common.tools;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldUtils {
    public static boolean isEnumFacingPositive(EnumFacing facing) {
        return facing.getFrontOffsetX() > 0 || facing.getFrontOffsetY() > 0 || facing.getFrontOffsetZ() > 0;
    }

    public static ChunkCoordinates offset(ChunkCoordinates coordinates, EnumFacing facing, int distance) {
        return new ChunkCoordinates(
                coordinates.posX + (facing.getFrontOffsetX() * distance),
                coordinates.posY + (facing.getFrontOffsetY() * distance),
                coordinates.posZ + (facing.getFrontOffsetZ() * distance)
            );
    }

    public static ChunkCoordinates up(ChunkCoordinates coordinates, int y) {
        return new ChunkCoordinates(coordinates.posX, coordinates.posY + y, coordinates.posZ);
    }
    public static boolean isInsideWorldLimits(World worldIn, ChunkCoordinates coordinates) {
        if (coordinates.posY >= 0 && coordinates.posY < 256) {
            return true;
        } else {
            return false;
        }
    }
}
