package com.direwolf20.buildinggadgets.common.tools;

import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import static com.direwolf20.buildinggadgets.common.config.SyncedConfig.rayTraceRange;

public class VectorTools {

    public static MovingObjectPosition getLookingAt(EntityPlayer player, ItemStack tool) {
        return getLookingAt(player, GadgetGeneric.shouldRayTraceFluid(tool));
    }

    public static MovingObjectPosition getLookingAt(EntityPlayer player, boolean rayTraceFluid) {
        World world = player.world;
        Vec3 look = player.getLookVec();

        Vec3 start = Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        //rayTraceRange here refers to SyncedConfig.rayTraceRange
        Vec3 end = new Vec3(player.posX + look.x * rayTraceRange, player.posY + player.getEyeHeight() + look.y * rayTraceRange, player.posZ + look.z * rayTraceRange);
        return world.rayTraceBlocks(start, end, rayTraceFluid, false, false);
    }

    public static ChunkCoordinates getPosFromMovingObjectPosition(MovingObjectPosition movingObjectPosition) {
        //@TODO(johnrowl) movingObjectPosition may be null in some cases. We should make sure that we handle it.
        return new ChunkCoordinates(movingObjectPosition.blockX, movingObjectPosition.blockY, movingObjectPosition.blockZ);
    }

    @Nullable
    public static ChunkCoordinates getPosLookingAt(EntityPlayer player, ItemStack tool) {
        MovingObjectPosition lookingAt = VectorTools.getLookingAt(player, tool);
        if (lookingAt == null)
            return null;

        return VectorTools.getPosFromMovingObjectPosition(lookingAt);
    }

    public static int getAxisValue(ChunkCoordinates pos, Axis axis) {
        switch (axis) {
            case X:
                return pos.getX();
            case Y:
                return pos.getY();
            case Z:
                return pos.getZ();
        }
        throw new IllegalArgumentException("Trying to find the value an imaginary axis of a BlockPos");
    }

    public static int getAxisValue(int x, int y, int z, Axis axis) {
        switch (axis) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
                return z;
        }
        throw new IllegalArgumentException("Trying to find the value an imaginary axis of a set of 3 values");
    }

    public static ChunkCoordinates perpendicularSurfaceOffset(ChunkCoordinates pos, EnumFacing intersector, int i, int j) {
        return perpendicularSurfaceOffset(pos, intersector.getAxis(), i, j);
    }

    public static ChunkCoordinates perpendicularSurfaceOffset(ChunkCoordinates pos, Axis intersector, int i, int j) {
        switch (intersector) {
            case X:
                return pos.add(0, i, j);
            case Y:
                return pos.add(i, 0, j);
            case Z:
                return pos.add(i, j, 0);
        }
        throw new IllegalArgumentException("Unknown facing " + intersector);
    }

}
