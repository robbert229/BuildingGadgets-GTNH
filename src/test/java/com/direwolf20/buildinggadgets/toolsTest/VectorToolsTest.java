package com.direwolf20.buildinggadgets.toolsTest;

import com.direwolf20.buildinggadgets.common.tools.VectorTools;
import net.minecraft.util.ChunkCoordinates;
import org.junit.jupiter.api.*;

import java.util.Random;

import static net.minecraft.util.EnumFacing.Axis;
import static org.junit.jupiter.api.Assertions.*;

public class VectorToolsTest {

    private final Random random = new Random();

    @RepeatedTest(8)
    void getAxisValueShouldReturnSameValueAsBlockPosGetterMethod() {
        int x = random.nextInt();
        int y = random.nextInt();
        int z = random.nextInt();
        ChunkCoordinates pos = new ChunkCoordinates(x, y, z);

        assertEquals(pos.posX, VectorTools.getAxisValue(pos, Axis.X));
        assertEquals(pos.posY, VectorTools.getAxisValue(pos, Axis.Y));
        assertEquals(pos.posZ, VectorTools.getAxisValue(pos, Axis.Z));
    }

    @RepeatedTest(2)
    void perpendicularSurfaceOffsetShouldChangeXAndZWithAxisY() {
        int i = random.nextInt();
        int j = random.nextInt();
        ChunkCoordinates offset = VectorTools.perpendicularSurfaceOffset(new ChunkCoordinates(0,0,0), Axis.Y, i, j);
        assertEquals(new ChunkCoordinates(0,0,0).add(i, 0, j), offset);
    }

    @RepeatedTest(2)
    void perpendicularSurfaceOffsetShouldChangeYAndZWithAxisX() {
        int i = random.nextInt();
        int j = random.nextInt();
        ChunkCoordinates offset = VectorTools.perpendicularSurfaceOffset(new ChunkCoordinates(0,0,0), Axis.X, i, j);
        assertEquals(new ChunkCoordinates(0,0,0).add(0, i, j), offset);
    }

    @RepeatedTest(2)
    void perpendicularSurfaceOffsetShouldChangeXAndYWithAxisZ() {
        int i = random.nextInt();
        int j = random.nextInt();
        ChunkCoordinates offset = VectorTools.perpendicularSurfaceOffset(new ChunkCoordinates(0,0,0), Axis.Z, i, j);
        assertEquals(new ChunkCoordinates(0,0,0).add(i, j, 0), offset);
    }

}
