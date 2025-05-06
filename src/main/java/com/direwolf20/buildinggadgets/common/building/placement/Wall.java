package com.direwolf20.buildinggadgets.common.building.placement;

import java.util.Iterator;
import java.util.Spliterator;

import javax.annotation.Nonnull;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;

import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.Region;
import com.direwolf20.buildinggadgets.common.tools.DirectionUtils;
import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

/**
 * A wall is a plane of blocks described with a starting and an ending position. The positions will and must have 1
 * coordinate that is the same.
 * <p>
 * See the static factory methods for more information.
 */
public final class Wall implements IPlacementSequence {

    /**
     * Creates a wall centered at the given position.
     *
     * @param center the center of the wall
     * @param side   front face of the wall
     * @param radius radius of the wall
     */
    public static Wall clickedSide(ChunkCoordinates center, EnumFacing side, int radius) {
        return new Wall(center, side, radius, null, 0);
    }

    /**
     * Creates a wall extending to some direction with the given position as its bottom.
     *
     * @param posHit    bottom of the wall
     * @param extension top side (growing direction) of the wall
     * @param flatSide  front face of the wall
     * @param radius    radius of the wall.
     * @param extra     amount of blocks to add beyond the radius
     */
    public static Wall extendingFrom(ChunkCoordinates posHit, EnumFacing extension, EnumFacing flatSide, int radius,
        int extra) {
        Preconditions.checkArgument(
            extension != flatSide,
            "Cannot have a wall extending to " + extension + " and flat at " + flatSide);
        return new Wall(ChunkCoordinateUtils.offset(posHit, extension, radius + 1), flatSide, radius, extension, extra);
    }

    private Region region;

    @VisibleForTesting
    private Wall(ChunkCoordinates posHit, EnumFacing side, int radius, EnumFacing extendingSide, int extendingSize) {
        this.region = new Region(posHit).expand(
            radius * (1 - Math.abs(side.getFrontOffsetX())),
            radius * (1 - Math.abs(side.getFrontOffsetY())),
            radius * (1 - Math.abs(side.getFrontOffsetZ())));

        if (extendingSize != 0) {

            if (DirectionUtils.isEnumFacingPositive(extendingSide)) {
                this.region = new Region(
                    region.getMin(),
                    ChunkCoordinateUtils.offset(region.getMax(), extendingSide, extendingSize));
            } else {
                this.region = new Region(
                    ChunkCoordinateUtils.offset(region.getMin(), extendingSide, extendingSize),
                    region.getMax());
            }
        }
    }

    /**
     * For {@link #copy()}
     */
    @VisibleForTesting
    private Wall(Region region) {
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
        return new Wall(region);
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
