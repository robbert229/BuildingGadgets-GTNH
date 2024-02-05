package com.direwolf20.buildinggadgets.building.placementTests;

import com.direwolf20.buildinggadgets.common.building.placement.ExclusiveAxisChasing;
import com.direwolf20.buildinggadgets.common.tools.VectorTools;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ChunkCoordinates;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ExclusiveAxisChasingTest {

    private final Random random = new Random();

    private Axis randomAxis() {
        Axis[] axises = Axis.values();
        return axises[random.nextInt(axises.length)];
    }

    @Test
    void sequenceShouldNotContainSourceAndTargetPositionRandom() {
        ChunkCoordinates source = new ChunkCoordinates(random.nextInt(3) - 2, random.nextInt(3) - 2, random.nextInt(3) - 2);
        ChunkCoordinates target = new ChunkCoordinates(random.nextInt(3) - 2, random.nextInt(3) - 2, random.nextInt(3) - 2);

        ExclusiveAxisChasing sequence = ExclusiveAxisChasing.create(source, target, randomAxis(), Integer.MAX_VALUE);
        for (ChunkCoordinates pos : sequence) {
            assertNotEquals(source, pos);
            assertNotEquals(target, pos);
        }
    }

    @Test
    void sequenceShouldHaveDifferenceAndMinimum0AsSizeCaseRandom() {
        ChunkCoordinates source = new ChunkCoordinates(random.nextInt(3) - 2, random.nextInt(3) - 2, random.nextInt(3) - 2);
        ChunkCoordinates target = new ChunkCoordinates(random.nextInt(3) - 2, random.nextInt(3) - 2, random.nextInt(3) - 2);
        Axis axis = randomAxis();
        int difference = VectorTools.getAxisValue(source, axis) - VectorTools.getAxisValue(target, axis);
        int expected = difference <= 1 ? 0 : Math.abs(difference);

        ExclusiveAxisChasing sequence = ExclusiveAxisChasing.create(source, target, randomAxis(), Integer.MAX_VALUE);
        assertEquals(expected, sequence.collect().size());
    }

    @Test
    void sequenceShouldHaveDifferenceMinus1AndMinimum0SizeCaseSourceAndTargetOffsetBy1HardCoded() {
        for (EnumFacing facing : EnumFacing.VALUES) {
            //X changed by 1
            ExclusiveAxisChasing sequence = ExclusiveAxisChasing.create(new ChunkCoordinates(13, 43, - 424), new ChunkCoordinates(14, 43, - 424), facing, Integer.MAX_VALUE);
            assertEquals(0, sequence.collect().size());
        }
    }

    @Test
    void sequenceShouldHaveDifferenceMinus1AndMinimum0SizeCaseSourceAndTargetOffsetBy1Random() {
        EnumFacing facing = EnumFacing.random(random);
        ExclusiveAxisChasing sequence = ExclusiveAxisChasing.create(new ChunkCoordinates(0,0,0), new ChunkCoordinates(0,0,0).offset(facing), facing, Integer.MAX_VALUE);
        assertEquals(0, sequence.collect().size());
    }

    @Test
    void sequenceShouldHaveDifferenceMinus1AndMinimum0SizeCaseSameSourceAndTargetRandom() {
        EnumFacing facing = EnumFacing.random(random);
        int i = random.nextInt(32);
        ChunkCoordinates pos = new ChunkCoordinates(0,0,0).offset(facing, i);
        ExclusiveAxisChasing sequence = ExclusiveAxisChasing.create(pos, pos, facing, Integer.MAX_VALUE);
        assertEquals(0, sequence.collect().size());
    }

}
