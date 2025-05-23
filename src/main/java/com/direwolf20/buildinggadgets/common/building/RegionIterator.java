package com.direwolf20.buildinggadgets.common.building;

import net.minecraft.util.ChunkCoordinates;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.PeekingIterator;

class RegionIterator extends AbstractIterator<ChunkCoordinates> implements PeekingIterator<ChunkCoordinates> {

    // 'minX' does not exist since it is not needed
    private final int minY;
    private final int minZ;

    private final int maxX;
    private final int maxY;
    private final int maxZ;

    private int posX;
    private int posY;
    private int posZ;

    RegionIterator(Region region) {
        this(
            region.getMinX(),
            region.getMinY(),
            region.getMinZ(),
            region.getMaxX(),
            region.getMaxY(),
            region.getMaxZ());
    }

    private RegionIterator(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        // Does not need minX
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        this.posX = minX;
        this.posY = minY;
        this.posZ = minZ;
    }

    @Override
    protected ChunkCoordinates computeNext() {
        if (isXOverflowed()) return endOfData();

        ChunkCoordinates pos = new ChunkCoordinates(posX, posY, posZ);

        posZ++;
        if (isZOverflowed()) {
            posZ = minZ;
            posY++;
        } else {
            return pos;
        }

        if (isYOverflowed()) {
            posY = minY;
            posX++;
        } else {
            return pos;
        }

        // Returns (maxX, maxY, maxZ)
        return pos;
    }

    private boolean isXOverflowed() {
        return posX > maxX;
    }

    private boolean isYOverflowed() {
        return posY > maxY;
    }

    private boolean isZOverflowed() {
        return posZ > maxZ;
    }

}
