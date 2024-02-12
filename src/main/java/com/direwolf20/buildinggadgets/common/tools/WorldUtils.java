package com.direwolf20.buildinggadgets.common.tools;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class WorldUtils {

    public static boolean isInsideWorldLimits(World worldIn, ChunkCoordinates coordinates) {
        if (coordinates.posY >= 0 && coordinates.posY < 256) {
            return true;
        } else {
            return false;
        }
    }
}
