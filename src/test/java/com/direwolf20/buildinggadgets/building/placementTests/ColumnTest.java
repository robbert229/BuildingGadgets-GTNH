package com.direwolf20.buildinggadgets.building.placementTests;

import com.direwolf20.buildinggadgets.common.building.placement.Column;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import org.junit.jupiter.api.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ColumnTest {

    @Test
    void columnFacingUpShouldReturnSequenceWithAccentingY() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<Column> constructor = Column.class.getDeclaredConstructor(ChunkCoordinates.class, ChunkCoordinates.class);
        constructor.setAccessible(true);

        Column column = constructor.newInstance(new ChunkCoordinates(0,0,0), new ChunkCoordinates(0,0,0).up(4));
        Iterator<ChunkCoordinates> it = column.iterator();

        assertEquals(new ChunkCoordinates(0, 0, 0), it.next());
        assertEquals(new ChunkCoordinates(0, 1, 0), it.next());
        assertEquals(new ChunkCoordinates(0, 2, 0), it.next());
        assertEquals(new ChunkCoordinates(0, 3, 0), it.next());
        assertEquals(new ChunkCoordinates(0, 4, 0), it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void columnCreatedWithFactoryMethodExtendFromShouldOffsetBaseBy1ToGivenFacing() {
        for (EnumFacing facing : EnumFacing.VALUES) {
            Column column = Column.extendFrom(new ChunkCoordinates(0,0,0), facing, 15);
            Iterator<ChunkCoordinates> it = column.iterator();

            if (facing.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
                for (int i = 15; i >= 1; i--) {
                    assertEquals(new ChunkCoordinates(0,0,0).offset(facing, i), it.next());
                }
            } else {
                for (int i = 1; i <= 15; i++) {
                    assertEquals(new ChunkCoordinates(0,0,0).offset(facing, i), it.next());
                }
            }

            assertFalse(it.hasNext());
        }
    }

    @Test
    void columnOnXAxisCenteredAtOriginShouldHaveAccentingX() {
        Column column = Column.centerAt(new ChunkCoordinates(0,0,0), EnumFacing.Axis.X, 5);
        Iterator<ChunkCoordinates> it = column.iterator();

        assertEquals(new ChunkCoordinates(-2, 0, 0), it.next());
        assertEquals(new ChunkCoordinates(-1, 0, 0), it.next());
        assertEquals(new ChunkCoordinates(0, 0, 0), it.next());
        assertEquals(new ChunkCoordinates(1, 0, 0), it.next());
        assertEquals(new ChunkCoordinates(2, 0, 0), it.next());
        assertFalse(it.hasNext());
    }

    @RepeatedTest(4)
    void centerAtShouldCeilDownToNearestOddNumberAsSizeRandomParameterSize() {
        int size = MathHelper.clamp(random.nextInt(8), 1, Integer.MAX_VALUE) * 2;
        Column column = Column.centerAt(new ChunkCoordinates(0,0,0), EnumFacing.Axis.Y, size);
        Iterator<ChunkCoordinates> it = column.iterator();

        for (int i = 0; i < size - 1; i++) {
            it.next();
        }
        assertFalse(it.hasNext());
    }

    private final Random random = new Random();

}
