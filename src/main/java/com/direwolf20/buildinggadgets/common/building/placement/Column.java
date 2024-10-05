package com.direwolf20.buildinggadgets.common.building.placement;

import java.util.Iterator;
import java.util.Spliterator;

import javax.annotation.Nonnull;

import com.direwolf20.buildinggadgets.common.tools.DirectionUtils;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;

import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.Region;
import com.direwolf20.buildinggadgets.common.tools.MathTool;
import com.direwolf20.buildinggadgets.common.tools.WorldUtils;
import com.google.common.annotations.VisibleForTesting;

/**
 * Column is a line of blocks that is aligned to some axis, starting from a position to another where 2 and only 2
 * coordinates
 * are the same. Whether the resulting {@link ChunkCoordinates}es include the start/end position is up to the factory
 * methods'
 * specification.
 */
public final class Column implements IPlacementSequence {

    /**
     * Construct a column object with a starting point, including {@code range} amount of elements.
     *
     * @param hit   the source position, will not be included
     * @param side  side to grow the column into
     * @param range length of the column
     * @implSpec this sequence includes the source position
     */
    public static Column extendFrom(ChunkCoordinates hit, EnumFacing side, int range) {
        return new Column(hit, WorldUtils.offset(hit, side, range - 1));
    }

    /**
     * Construct a column object of the specified length, centered at a point and aligned to the given axis.
     *
     * @param center center of the column
     * @param axis   which axis will the column align to
     * @param length length of the column, will be floored to an odd number if it is not one already
     */
    public static Column centerAt(ChunkCoordinates center, EnumFacing axis, int length) {
        EnumFacing negative = DirectionUtils.getOppositeEnumFacing(axis);
        ChunkCoordinates base = WorldUtils.offset(center, negative, (length - 1) / 2);
        // -1 because Region's vertexes are inclusive
        return new Column(base, WorldUtils.offset(base, axis, MathTool.floorToOdd(length) - 1));
    }

    private final Region region;

    private Column(ChunkCoordinates source, ChunkCoordinates target) {
        this.region = new Region(source, target);
    }

    /**
     * For {@link #copy()}
     */
    @VisibleForTesting
    private Column(Region region) {
        this.region = region;
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
        return new Column(region);
    }

    @Override
    @Nonnull
    public Iterator<ChunkCoordinates> iterator() {
        return region.iterator();
    }

    @Override
    public Spliterator<ChunkCoordinates> spliterator() {
        return region.spliterator();
    }

}
