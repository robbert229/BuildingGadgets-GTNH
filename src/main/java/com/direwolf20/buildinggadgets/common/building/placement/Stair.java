package com.direwolf20.buildinggadgets.common.building.placement;

import java.util.Iterator;

import javax.annotation.Nonnull;

import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;

import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.Region;
import com.direwolf20.buildinggadgets.common.tools.MutableChunkCoordinates;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.AbstractIterator;

/**
 * A sequence of blocks that offsets in 2 different directions where one is vertical, one is horizontal.
 * <p>
 * For example, a regular climbing up stair facing north would have (UP, NORTH) as its parameter. This also applies to
 * descending stair like (DOWN, SOUTH) where each block is lower than the latter.
 */
public final class Stair implements IPlacementSequence {

    public static Stair create(ChunkCoordinates base, EnumFacing horizontalAdvance, EnumFacing verticalAdvance,
        int range) {
        return new Stair(base, horizontalAdvance, verticalAdvance, range);
    }

    private final ChunkCoordinates base;
    private final ChunkCoordinates target;
    private final EnumFacing horizontalAdvance;
    private final EnumFacing verticalAdvance;
    private final Region region;
    private final int range;

    @VisibleForTesting
    private Stair(ChunkCoordinates base, EnumFacing horizontalAdvance, EnumFacing verticalAdvance, int range) {
        this.base = base;
        this.target = ChunkCoordinateUtils
            .offset(ChunkCoordinateUtils.offset(base, horizontalAdvance, range - 1), verticalAdvance, range - 1);
        this.horizontalAdvance = horizontalAdvance;
        this.verticalAdvance = verticalAdvance;
        this.region = new Region(base, target);
        this.range = range;
    }

    /**
     * For {@link #copy()}
     */
    @VisibleForTesting
    private Stair(ChunkCoordinates base, ChunkCoordinates target, EnumFacing horizontalAdvance,
        EnumFacing verticalAdvance, Region region, int range) {
        this.base = base;
        this.target = target;
        this.horizontalAdvance = horizontalAdvance;
        this.verticalAdvance = verticalAdvance;
        this.region = region;
        this.range = range;
    }

    @Override
    public Region getBoundingBox() {
        return region;
    }

    @Override
    public boolean mayContain(int x, int y, int z) {
        return region.mayContain(x, y, z);
    }

    @Override
    public IPlacementSequence copy() {
        return new Stair(base, target, horizontalAdvance, verticalAdvance, region, range);
    }

    @Override
    @Nonnull
    public Iterator<ChunkCoordinates> iterator() {
        return new AbstractIterator<ChunkCoordinates>() {

            private MutableChunkCoordinates current = new MutableChunkCoordinates(base);
            private int i = 0;

            {
                current.move(horizontalAdvance, -1)
                    .move(verticalAdvance, -1);
            }

            @Override
            protected ChunkCoordinates computeNext() {
                if (i >= range) return endOfData();
                i++;

                current.move(horizontalAdvance, 1)
                    .move(verticalAdvance, 1);
                return current.toImmutable();
            }
        };
    }

}
