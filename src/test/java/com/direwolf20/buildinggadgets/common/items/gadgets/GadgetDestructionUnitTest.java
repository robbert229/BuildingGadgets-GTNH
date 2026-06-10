package com.direwolf20.buildinggadgets.common.items.gadgets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;

import org.junit.jupiter.api.Test;

import com.direwolf20.buildinggadgets.util.ref.NBTKeys;

class GadgetDestructionUnitTest {

    @Test
    void getUUIDGeneratesAndPersistsPerStack() {
        ItemStack stack = new ItemStack(new GadgetDestruction());

        String first = GadgetDestruction.getUUID(stack);
        String second = GadgetDestruction.getUUID(stack);

        assertNotNull(first);
        assertEquals(first, second);
        assertEquals(
            first,
            stack.getTagCompound()
                .getString(NBTKeys.GADGET_UUID));
    }

    @Test
    void setAnchorSideRoundTripsAndCanBeCleared() {
        ItemStack stack = new ItemStack(new GadgetDestruction());

        GadgetDestruction.setAnchorSide(stack, EnumFacing.WEST);
        assertSame(EnumFacing.WEST, GadgetDestruction.getAnchorSide(stack));

        GadgetDestruction.setAnchorSide(stack, null);
        assertNull(GadgetDestruction.getAnchorSide(stack));
    }

    @Test
    void toolValuesRoundTripAndDefaultToZeroWhenUnset() {
        ItemStack stack = new ItemStack(new GadgetDestruction());

        GadgetDestruction.setToolValue(stack, 7, "depth");
        assertEquals(7, GadgetDestruction.getToolValue(stack, "depth"));
        assertEquals(0, GadgetDestruction.getToolValue(stack, "left"));
    }

    @Test
    void getAreaReturnsEmptySetWhenDepthIsZero() {
        ItemStack stack = new ItemStack(new GadgetDestruction());
        GadgetDestruction.setToolValue(stack, 0, "depth");

        Set<ChunkCoordinates> area = GadgetDestruction
            .getArea(null, new ChunkCoordinates(0, 64, 0), EnumFacing.NORTH, null, stack);

        assertNotNull(area);
        assertTrue(area.isEmpty());
    }
}
