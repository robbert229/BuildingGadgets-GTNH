package com.direwolf20.buildinggadgets.common.tools;

import cofh.lib.util.helpers.MathHelper;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;

/**
 * MutableChunkCoordinates is a back-ported shim of MutableBlockPos from 1.12.x designed to work with ChunkCoordinates.
 */
public class MutableChunkCoordinates extends ChunkCoordinates{
    public MutableChunkCoordinates( ChunkCoordinates base) {
        super(base);
    }

    public MutableChunkCoordinates move(EnumFacing facing) {
        this.posX += facing.getFrontOffsetX();
        this.posY += facing.getFrontOffsetY();
        this.posZ += facing.getFrontOffsetZ();
        return this;
    }

    public MutableChunkCoordinates setPos(ChunkCoordinates pos) {
        this.posX = pos.posX;
        this.posY = pos.posY;
        this.posZ = pos.posZ;
        return this;
    }

    public MutableChunkCoordinates move(EnumFacing facing, int distance) {
        this.posX += facing.getFrontOffsetX() * distance;
        this.posY += facing.getFrontOffsetY() * distance;
        this.posZ += facing.getFrontOffsetZ() * distance;
        return this;
    }

    public MutableChunkCoordinates move(EnumFacing facing, double distance) {
        this.posX += MathHelper.floor(facing.getFrontOffsetX() * distance);
        this.posY += MathHelper.floor(facing.getFrontOffsetY() * distance);
        this.posZ += MathHelper.floor(facing.getFrontOffsetZ() * distance);
        return this;
    }

    public ChunkCoordinates toImmutable() {
        return new ChunkCoordinates(this.posX, this.posY, this.posZ);
    }
}
