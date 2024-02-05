package com.direwolf20.buildinggadgets.building.placementTests;

import com.direwolf20.buildinggadgets.common.building.placement.Stair;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ChunkCoordinates;
import org.junit.jupiter.api.*;

import java.util.Iterator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class StairTest {

    private final Random random = new Random();

    @RepeatedTest(4)
    void stairShouldContainSameAmountOfBlocksPlus1AsSizeParameter() {
        int size = random.nextInt(16);
        Stair stair = Stair.create(new ChunkCoordinates(0,0,0), EnumFacing.NORTH, EnumFacing.UP, size);

        assertEquals(size + 1, stair.collect().size());
    }

    @RepeatedTest(4)
    void positionShouldOffsetBy1InBothHorizontalAndVerticalDirectionCaseNorthUp() {
        int size = random.nextInt(16);
        Stair stair = Stair.create(new ChunkCoordinates(0,0,0), EnumFacing.NORTH, EnumFacing.UP, size);
        Iterator<ChunkCoordinates> it = stair.iterator();
        ChunkCoordinates last = it.next();
        if (!it.hasNext()) {
            return;
        }

        ChunkCoordinates current = it.next();
        while (it.hasNext()) {
            assertEquals(1, Math.abs(current.posY - last.posY));
            assertEquals(1, Math.abs(current.posZ - last.posZ));

            last = current;
            current = it.next();
        }
    }

}
