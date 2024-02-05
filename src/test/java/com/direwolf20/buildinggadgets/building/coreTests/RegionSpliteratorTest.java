package com.direwolf20.buildinggadgets.building.coreTests;

import com.direwolf20.buildinggadgets.common.building.Region;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.ChunkCoordinates;
import org.junit.jupiter.api.*;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Both spliterator and streams
 */
public class RegionSpliteratorTest {

    /**
     * No need for comparision between iterator size and spliterator size, since they should both be the same as {@link Region#size()}.
     */
    @Test
    void spliteratorShouldHaveSameAmountOfElementsAsRegionSizeMethod() {
        Region region = new Region(-8, -8, -8, 8, 8, 8);

        assertEquals(region.size(), region.stream().toArray().length);
    }

    @Test
    void spliteratorShouldHaveSameAmountOfElementsAsRegionSizeMethodPositiveCase() {
        Region region = new Region(1, 1, 1, 8, 8, 8);
        assertEquals(region.size(), region.stream().toArray().length);
    }

    @Test
    void estimateSizeShouldBeSameAsActualSize() {
        Region region = new Region(-8, -8, -8, 8, 8, 8);
        Spliterator<ChunkCoordinates> spliterator = region.spliterator();
        long estimate = spliterator.estimateSize();
        long spliteratorSize = region.stream().toArray().length;

        assertEquals(spliteratorSize, estimate);
    }

    @Test
    void spliteratorShouldHaveSameSizeAsIterator() {
        Region region = new Region(-8, -8, -8, 8, 8, 8);
        Iterator<ChunkCoordinates> iterator = region.iterator();

        //Use collect to ignore potentially exceeding fixed limit
        assertEquals(ImmutableList.copyOf(iterator).size(), region.stream().collect(ImmutableList.toImmutableList()).size());
    }

    @Test
    void spliteratorShouldHaveSameOrderAsIterator() {
        Region region = new Region(-8, -8, -8, 8, 8, 8);
        Iterator<ChunkCoordinates> iterator = region.iterator();
        Spliterator<ChunkCoordinates> spliterator = region.spliterator();

        spliterator.forEachRemaining(actual -> {
            ChunkCoordinates expected = iterator.next();
            assertEquals(expected, actual);
        });
    }

    @Test
    void spliteratorShouldHaveSameOrderAsIteratorAfterSplittingInHalf() {
        Region region = new Region(-8, -8, -8, 8, 8, 8);
        Iterator<ChunkCoordinates> it = region.iterator();
        Spliterator<ChunkCoordinates> spliterator = region.spliterator();

        //Iteration 1, including the original one
        Spliterator<ChunkCoordinates> sp1 = spliterator.trySplit();

        //Iteration 2, produces 2 more + 2 from iteration 1
        Spliterator<ChunkCoordinates> sp1_1 = sp1.trySplit();
        Spliterator<ChunkCoordinates> sp0_1 = spliterator.trySplit();

        //Yielding spliterator inherits the state from parent, and parent starts working somewhere else
        //Yielding spliterator always have smaller coordinates than parent
        sp1_1.forEachRemaining(pos -> assertEquals(it.next(), pos));
        sp1.forEachRemaining(pos -> assertEquals(it.next(), pos));
        sp0_1.forEachRemaining(pos -> assertEquals(it.next(), pos));
        spliterator.forEachRemaining(pos -> assertEquals(it.next(), pos));
    }

    @Test
    void doesNotProduceSamePosition() {
        Region region = new Region(-4, -4, -4, 4, 4, 4);
        Spliterator<ChunkCoordinates> spliterator = region.spliterator();
        LongSet visited = new LongOpenHashSet();

        spliterator.forEachRemaining(pos -> {
            long actual = pos.toLong();
            assertFalse(visited.contains(actual));
            visited.add(actual);
        });
    }

    @Test
    void splittingDoesNotProduceSamePositionCaseSplitting2Times() {
        Region region = new Region(-8, -8, -8, 8, 8, 8);
        Spliterator<ChunkCoordinates> spliterator = region.spliterator();
        LongSet visited = new LongOpenHashSet();

        Consumer<ChunkCoordinates> test = pos -> {
            long actual = pos.toLong();
            assertFalse(visited.contains(actual));
            visited.add(actual);
        };

        Spliterator<ChunkCoordinates> sp1 = spliterator.trySplit();
        Spliterator<ChunkCoordinates> sp2 = spliterator.trySplit();

        spliterator.forEachRemaining(test);
        sp1.forEachRemaining(test);
        sp2.forEachRemaining(test);
    }

    @Test
    void splittingDoesNotProduceSamePositionCaseSplittingInHalf() {
        Region region = new Region(-8, -8, -8, 8, 8, 8);
        Spliterator<ChunkCoordinates> spliterator = region.spliterator();
        LongSet visited = new LongOpenHashSet();

        Consumer<ChunkCoordinates> test = pos -> {
            long actual = pos.toLong();
            assertFalse(visited.contains(actual));
            visited.add(actual);
        };

        //Iteration 1, including the original one
        Spliterator<ChunkCoordinates> sp1 = spliterator.trySplit();

        //Iteration 2, produces 2 more + 2 from iteration 1
        Spliterator<ChunkCoordinates> sp1_1 = sp1.trySplit();
        Spliterator<ChunkCoordinates> sp0_1 = spliterator.trySplit();

        sp1.forEachRemaining(test);
        spliterator.forEachRemaining(test);

        sp1_1.forEachRemaining(test);
        sp0_1.forEachRemaining(test);
    }

    @Test
    void splittingDoesNotChangeTheOverallSizeCaseSplittingInHalf() {
        Region region = new Region(-16, -6, -18, 17, 8, 20);
        Spliterator<ChunkCoordinates> spliterator = region.spliterator();

        long originalSize = spliterator.estimateSize();

        //Iteration 1, including the original one
        Spliterator<ChunkCoordinates> sp1 = spliterator.trySplit();

        //Iteration 2, produces 2 more + 2 from iteration 1
        Spliterator<ChunkCoordinates> sp1_1 = sp1.trySplit();
        Spliterator<ChunkCoordinates> sp0_1 = spliterator.trySplit();

        long actualSize = sp1.estimateSize() +
                spliterator.estimateSize() +
                sp1_1.estimateSize() +
                sp0_1.estimateSize();

        assertEquals(originalSize, actualSize);
    }

}
