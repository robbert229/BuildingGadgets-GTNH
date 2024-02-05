package com.direwolf20.buildinggadgets.building.placementTests;

import com.direwolf20.buildinggadgets.common.building.placement.Wall;
import com.direwolf20.buildinggadgets.common.tools.MathTool;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.math.MathHelper;
import org.junit.jupiter.api.*;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class WallTest {

    private final Random random = new Random();

    @RepeatedTest(8)
    void clickedSideShouldReturnWallWithFlooredNearestOddNumberAsLengthCaseFacingUpRandomInputAndValidatesWithMathTool() {
        int range = random.nextInt(16);
        int floored = MathHelper.clamp(MathTool.floorToOdd(range), 1, 15);
        Wall wall = Wall.clickedSide(new ChunkCoordinates(0,0,0), EnumFacing.UP, range);

        assertEquals(floored, wall.getBoundingBox().getXSize());
        assertEquals(floored, wall.getBoundingBox().getZSize());
    }

    @Test
    void clickedSideWithFacingUpShouldReturnWallWithSameYCase5By5RandomSizeRandomYLevel() {
        int y = random.nextInt() - 16;
        Wall wall = Wall.clickedSide(new ChunkCoordinates(0,0,0).up(y), EnumFacing.UP, random.nextInt(16));
        for (ChunkCoordinates pos : wall) {
            assertEquals(y, pos.posY);
        }
    }

    @Test
    void clickedSideWithFacingDownShouldReturnWallWithSameYCase5By5() {
        int y = random.nextInt() - 16;
        Wall wall = Wall.clickedSide(new ChunkCoordinates(0,0,0).up(y), EnumFacing.DOWN, random.nextInt(16));
        for (ChunkCoordinates pos : wall) {
            assertEquals(y, pos.posY);
        }
    }

    @Test
    void clickedSideWithRange0ShouldReturnWallWithSize1() {
        Wall wall = Wall.clickedSide(new ChunkCoordinates(0,0,0), EnumFacing.UP, 0);
        assertEquals(1, wall.getBoundingBox().size());
    }

    @Test
    void extendingFromWithExtensionUpFlatSideNorthRange5ShouldHaveAllPositionsWithSameZHardcoded() {
        Wall wall = Wall.extendingFrom(new ChunkCoordinates(0,0,0), EnumFacing.UP, EnumFacing.NORTH, 5, 0);
        for (ChunkCoordinates pos : wall) {
            assertEquals(0, pos.getZ());
        }
    }

    @Test
    void extendingFromShouldRejectRequestWithSameExtensionAndFlatSide() {
        for (EnumFacing side : EnumFacing.VALUES) {
            assertThrows(IllegalArgumentException.class, () -> Wall.extendingFrom(new ChunkCoordinates(0,0,0), side, side, 5, 0));
        }
    }

}
