package com.direwolf20.buildinggadgets.common.building.placement;

import java.util.Iterator;

import javax.annotation.Nonnull;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;

import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.Region;
import com.direwolf20.buildinggadgets.util.MathTool;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.AbstractIterator;

/**
 * Grid is a set of blocks where each block is equidistant from its neighboring blocks. The distance between the blocks
 * is a periodic sequence with a certain size.
 */
public final class Grid implements IPlacementSequence {

    public static Grid create(ChunkCoordinates base, int range, int periodSize) {
        return new Grid(base, range, periodSize);
    }

    private final int periodSize;
    private final Region region;
    private final ChunkCoordinates center;
    private final int range;

    @VisibleForTesting
    private Grid(ChunkCoordinates center, int range, int periodSize) {
        this.region = Wall.clickedSide(center, EnumFacing.UP, range)
            .getBoundingBox();
        this.range = range;
        this.center = center;
        this.periodSize = periodSize;
    }

    /**
     * For {@link #copy()}
     */
    @VisibleForTesting
    private Grid(Region region, ChunkCoordinates center, int range, int periodSize) {
        this.region = region;
        this.center = center;
        this.range = range;
        this.periodSize = periodSize;
    }

    @Override
    public Region getBoundingBox() {
        return region;
    }

    /**
     * {@inheritDoc}<br>
     * <b>inaccurate representation (case 2)</b>:
     */
    @Override
    public boolean mayContain(int x, int y, int z) {
        return region.contains(x, y, z);
    }

    @Override
    public IPlacementSequence copy() {
        return new Grid(region, center, range, periodSize);
    }

    @Override
    @Nonnull
    public Iterator<ChunkCoordinates> iterator() {
        /*
         * Distance between blocks + block itself
         * arithmetic sequence of [2,7] where -1 for range being 1~15, +2 to shift the sequence from [0,5] to [2,7]
         */
        int period = (range - 1) % periodSize + 2;

        // Random design choice by Dire
        int end = (range + 1) * 7 / 5;
        // Floor to the nearest multiple of period
        int start = MathTool.floorMultiple(-end, period);

        return new AbstractIterator<ChunkCoordinates>() {

            private int x = start;
            private int z = start;

            @Override
            protected ChunkCoordinates computeNext() {
                if (z > end) {
                    return endOfData();
                }

                ChunkCoordinates pos = new ChunkCoordinates(center.posX + x, center.posY, center.posZ + z);

                x += period;
                if (x > end) {
                    x = start;
                    z += period;
                }

                return pos;
            }
        };
    }

}
