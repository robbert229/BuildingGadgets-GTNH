package com.direwolf20.buildinggadgets.util;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WorldUtils {
    public static double getDistance(ChunkCoordinates first, ChunkCoordinates second) {
        double dx = first.posX - second.posX;
        double dy = first.posY - second.posY;
        double dz = first.posZ - second.posZ;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public static double distanceSqToCenter(ChunkCoordinates first, ChunkCoordinates second) {
        return distanceSqToCenter(first, second.posX, second.posY, second.posZ);
    }

    public static double distanceSqToCenter(ChunkCoordinates first, double x, double y, double z) {
        double dx = first.posX + 0.5D - x;
        double dy = first.posY + 0.5D - y;
        double dz = first.posZ + 0.5D - z;
        return dx * dx + dy * dy + dz * dz;
    }

    public static boolean isInsideWorldLimits(World worldIn, ChunkCoordinates coordinates) {
        if (coordinates.posY >= 0 && coordinates.posY < 256) {
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public static Block getBlock(World world, ChunkCoordinates coordinates) {
        return world.getBlock(coordinates.posX, coordinates.posY, coordinates.posZ);
    }

    public static TileEntity getTileEntity(World world, ChunkCoordinates coordinates) {
        return world.getTileEntity(coordinates.posX, coordinates.posY, coordinates.posZ);
    }

    public static boolean isBlockModifiable(EntityPlayer player, ChunkCoordinates coordinates, ItemStack itemStack) {
        return isBlockModifiable(player, coordinates, EnumFacing.DOWN, itemStack);
    }

    public static boolean isBlockModifiable(EntityPlayer player, ChunkCoordinates coordinates, EnumFacing side, ItemStack itemStack) {
        if (side == null) {
            side = EnumFacing.DOWN;
        }

        return player.canPlayerEdit(coordinates.posX, coordinates.posY, coordinates.posZ, side.ordinal(), itemStack);
    }
}
