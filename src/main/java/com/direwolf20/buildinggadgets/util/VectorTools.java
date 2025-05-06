package com.direwolf20.buildinggadgets.util;

import static com.direwolf20.buildinggadgets.common.config.SyncedConfig.rayTraceRange;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;

public class VectorTools {

    public static MovingObjectPosition getLookingAt(EntityPlayer player, ItemStack tool) {
        return getLookingAt(player, GadgetGeneric.shouldRayTraceFluid(tool));
    }

    public static MovingObjectPosition getLookingAt(EntityPlayer player, boolean rayTraceFluid) {
        World world = player.worldObj;
        Vec3 look = player.getLookVec();

        Vec3 start = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        // rayTraceRange here refers to SyncedConfig.rayTraceRange
        Vec3 end = Vec3.createVectorHelper(
            player.posX + look.xCoord * rayTraceRange,
            player.posY + player.getEyeHeight() + look.yCoord * rayTraceRange,
            player.posZ + look.zCoord * rayTraceRange);

        return world.rayTraceBlocks(start, end, rayTraceFluid);
    }

    public static ChunkCoordinates getPosFromMovingObjectPosition(MovingObjectPosition movingObjectPosition) {
        if (movingObjectPosition == null) {
            return null;
        }

        // @TODO(johnrowl) movingObjectPosition may be null in some cases. We should make sure that we handle it.
        return new ChunkCoordinates(
            movingObjectPosition.blockX,
            movingObjectPosition.blockY,
            movingObjectPosition.blockZ);
    }

    @Nullable
    public static ChunkCoordinates getPosLookingAt(EntityPlayer player, ItemStack tool) {
        MovingObjectPosition lookingAt = VectorTools.getLookingAt(player, tool);
        if (lookingAt == null) return null;

        return VectorTools.getPosFromMovingObjectPosition(lookingAt);
    }

    public static boolean isAxisVertical(EnumFacing facing) {
        return facing == EnumFacing.DOWN || facing == EnumFacing.UP;
    }

    // Helper function to get horizontal facing from player's yaw
    public static EnumFacing getHorizontalFacingFromYaw(float yaw) {
        int direction = MathHelper.floor_double((yaw * 4.0F / 360.0F) + 0.5D) & 3;

        switch (direction) {
            case 0: return EnumFacing.SOUTH;
            case 1: return EnumFacing.WEST;
            case 2: return EnumFacing.NORTH;
            case 3: return EnumFacing.EAST;
            default: return EnumFacing.NORTH; // Fallback
        }
    }

    public static EnumFacing getHorizontalFacingFromPlayer(EntityPlayer player) {
        return getHorizontalFacingFromYaw(player.rotationYaw);
    }

    public static EnumFacing rotateY(EnumFacing facing) {
        return switch (facing) {
            case NORTH -> EnumFacing.EAST;
            case EAST -> EnumFacing.SOUTH;
            case SOUTH -> EnumFacing.WEST;
            case WEST -> EnumFacing.NORTH;
            default -> facing; // If it's vertical (UP/DOWN), no rotation is applied
        };
    }

    public static EnumFacing rotateYCCW(EnumFacing facing) {
        return switch (facing) {
            case NORTH -> EnumFacing.WEST;
            case EAST -> EnumFacing.NORTH;
            case SOUTH -> EnumFacing.EAST;
            case WEST -> EnumFacing.SOUTH;
            default -> facing; // If it's vertical (UP/DOWN), no rotation is applied
        };
    }

    public static int getAxisValue(ChunkCoordinates pos, EnumFacing axis) {
        if (axis.getFrontOffsetX() != 0) {
            return pos.posX;
        }

        if (axis.getFrontOffsetZ() != 0) {
            return pos.posY;
        }

        if (axis.getFrontOffsetZ() != 0) {
            return pos.posZ;
        }

        throw new IllegalArgumentException("Trying to find the value an imaginary axis of a BlockPos");
    }

    public static int getAxisValue(int x, int y, int z, EnumFacing axis) {
        return getAxisValue(new ChunkCoordinates(x, y, z), axis);
    }

    public static ChunkCoordinates perpendicularSurfaceOffset(ChunkCoordinates pos, EnumFacing intersector, int i,
        int j) {
        // TODO(johnrowl) this area is suspicious.
        if (intersector.getFrontOffsetX() != 0) {
            return new ChunkCoordinates(pos.posX, pos.posY + i, pos.posZ + j);
        }

        if (intersector.getFrontOffsetY() != 0) {
            return new ChunkCoordinates(pos.posX + i, pos.posY, pos.posZ + j);
        }

        if (intersector.getFrontOffsetZ() != 0) {
            return new ChunkCoordinates(pos.posX + i, pos.posY + j, pos.posZ);
        }
        throw new IllegalArgumentException("Unknown facing " + intersector);
    }
}
