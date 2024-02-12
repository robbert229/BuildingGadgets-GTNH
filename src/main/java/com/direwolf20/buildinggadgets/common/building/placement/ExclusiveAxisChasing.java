package com.direwolf20.buildinggadgets.common.building.placement;

import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.Region;
import com.direwolf20.buildinggadgets.common.tools.VectorTools;
import com.direwolf20.buildinggadgets.common.tools.WorldUtils;
import com.google.common.collect.AbstractIterator;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ChunkCoordinates;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * Starts from the selected position, and extend a column of blocks towards a target position on the axis of the selected face.
 */
public final class ExclusiveAxisChasing implements IPlacementSequence {

    /*public static ExclusiveAxisChasing create(ChunkCoordinates source, ChunkCoordinates target, EnumFacing axis, int maxProgression) {
        int difference = VectorTools.getAxisValue(target, axis) - VectorTools.getAxisValue(source, axis);
        if (difference < 0)
            return create(source, target, EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis), maxProgression);
        return create(source, target, EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axis), maxProgression);
    }*/

    /**
     * <p>Note that this factory method does not verify that {@code offsetDirection} is appropriate. Use {@link #create(ChunkCoordinates, ChunkCoordinates, Axis, int)} if this is required.</p>
     */
    public static ExclusiveAxisChasing create(ChunkCoordinates source, ChunkCoordinates target, EnumFacing offsetDirection, int maxProgression) {
        int difference = VectorTools.getAxisValue(target, offsetDirection) - VectorTools.getAxisValue(source, offsetDirection);
        maxProgression = Math.min(Math.abs(difference), maxProgression);

        return new ExclusiveAxisChasing(source, offsetDirection, maxProgression);
    }

    private final ChunkCoordinates source;
    private final EnumFacing offsetDirection;
    private final int maxProgression;

    public ExclusiveAxisChasing(ChunkCoordinates source, EnumFacing offsetDirection, int maxProgression) {
        this.source = source;
        this.offsetDirection = offsetDirection;
        this.maxProgression = maxProgression;
    }

    @Override
    public Region getBoundingBox() {
        return null;
    }

    @Override
    public boolean mayContain(int x, int y, int z) {
        int value = VectorTools.getAxisValue(x, y, z, offsetDirection);
        int sourceValue = VectorTools.getAxisValue(source, offsetDirection);
        int difference = Math.abs(value - sourceValue);
        return difference > 0 && difference < maxProgression;
    }

    @Override
    public IPlacementSequence copy() {
        return new ExclusiveAxisChasing(source, offsetDirection, maxProgression);
    }

    @Nonnull
    @Override
    public Iterator<ChunkCoordinates> iterator() {
        return new AbstractIterator<ChunkCoordinates>() {
            private int progression = 0;

            @Override
            protected ChunkCoordinates computeNext() {
                if (progression >= maxProgression)
                    return endOfData();

                return WorldUtils.offset(source, offsetDirection, progression++);
            }
        };
    }

}
