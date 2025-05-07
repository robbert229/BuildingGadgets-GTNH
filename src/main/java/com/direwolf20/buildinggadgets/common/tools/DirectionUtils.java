package com.direwolf20.buildinggadgets.common.tools;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.ForgeDirection;

public class DirectionUtils {

    public static boolean isEnumFacingPositive(EnumFacing facing) {
        return facing.getFrontOffsetX() > 0 || facing.getFrontOffsetY() > 0 || facing.getFrontOffsetZ() > 0;
    }

    public static EnumFacing enumFacingByName(String name) {
        for (EnumFacing facing : EnumFacing.values()) {
            if (facing.name()
                .equalsIgnoreCase(name)) {
                return facing;
            }
        }
        return null; // or handle unknown direction
    }

    public static EnumFacing getOppositeEnumFacing(EnumFacing facing) {
        return switch (facing) {
            case DOWN -> EnumFacing.UP;
            case UP -> EnumFacing.DOWN;
            case NORTH -> EnumFacing.SOUTH;
            case SOUTH -> EnumFacing.NORTH;
            case EAST -> EnumFacing.WEST;
            case WEST -> EnumFacing.EAST;
        };
    }

    // Converts EnumFacing to ForgeDirection
    public static ForgeDirection toForgeDirection(EnumFacing enumFacing) {
        if (enumFacing == null) {
            return null;
        }

        return ForgeDirection.getOrientation(enumFacing.ordinal());
    }

    // Converts ForgeDirection to EnumFacing
    public static EnumFacing toEnumFacing(ForgeDirection forgeDirection) {
        if (forgeDirection == null || forgeDirection == ForgeDirection.UNKNOWN) {
            return null;
        }
        return EnumFacing.values()[forgeDirection.ordinal()];
    }
}
