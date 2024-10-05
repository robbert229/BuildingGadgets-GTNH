package com.direwolf20.buildinggadgets.common.tools;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldUtils {
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

    public static ChunkCoordinates fromLong(long packedPos) {
        int x = (int) (packedPos >> 38);
        int y = (int) ((packedPos >> 26) & 0xFFF);
        int z = (int) (packedPos << 38 >> 38);

        return new ChunkCoordinates(x, y, z);
    }

    public static long toLong(ChunkCoordinates coordinates) {
        return ((long) coordinates.posX & 0x3FFFFFF) << 38 | ((long) coordinates.posY & 0xFFF) << 26 | ((long) coordinates.posZ & 0x3FFFFFF);
    }

    public static long toLong(int x, int y, int z) {
        return toLong(new ChunkCoordinates(x, y, z));
    }

    public static BlockState getBlockState(World world, int x, int y, int z) {
        var block = world.getBlock(x, y, z);
        var metadata = world.getBlockMetadata(x, y, z);

        return new BlockState(block, metadata);
    }

    public static BlockState getBlockState(World world, ChunkCoordinates coordinates) {
        return getBlockState(world, coordinates.posX, coordinates.posY, coordinates.posZ);
    }

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
