package com.direwolf20.buildinggadgets.common.building;

import com.direwolf20.buildinggadgets.util.datatypes.BlockState;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.function.BiPredicate;

/**
 * Execution context that uses {@link IPlacementSequence} to filter the unusable positions.
 * <p>
 * Testing is done with the predicate produced with {@link #validatorFactory}. If the predicate returns {@code true},
 * the position will be kept and returned in the iterator. If the predicate returns {@code false} on the other hand, the
 * position will be voided.
 *
 * @implNote Execution context in Strategy Pattern
 */
public class Context {

    private final IPlacementSequence positions;
    private final IValidatorFactory validatorFactory;
    private final IBlockProvider blocks;

    /**
     * @see Context#Context(IPlacementSequence, IBlockProvider, IValidatorFactory)
     */
    public Context(IPlacementSequence positions, IBlockProvider blocks) {
        this(positions, blocks, (world, stack, player, initial) -> (pos, state) -> true);
    }

    /**
     * Note that it is assumed that this method will return a block provider which uses the first value returned by
     * {@link #positions} as translation.
     *
     * @param validatorFactory Creates predicate for determining whether a position should be used or not
     */
    public Context(IPlacementSequence positions, IBlockProvider blocks, IValidatorFactory validatorFactory) {
        this.positions = positions;
        this.blocks = blocks;
        this.validatorFactory = validatorFactory;
    }

    public IPlacementSequence getPositionSequence() {
        return positions;
    }

    /**
     * Wrap raw sequence ({@link #getPositionSequence()}) so that the new iterator only returns positions passing the
     * test of {@link #getValidatorFactory()} with the given World object.
     *
     * @return {@link AbstractIterator} that wraps {@code getPositionSequence().iterator()}
     */
    public Iterator<ChunkCoordinates> getFilteredSequence(World world, ItemStack stack, EntityPlayer player, ChunkCoordinates initial) {
        Iterator<ChunkCoordinates> positions = getPositionSequence().iterator();
        BiPredicate<ChunkCoordinates, BlockState> validator = validatorFactory.createValidatorFor(world, stack, player, initial);
        return new AbstractIterator<ChunkCoordinates>() {
            @Override
            protected ChunkCoordinates computeNext() {
                while (positions.hasNext()) {
                    ChunkCoordinates next = positions.next();

                    var blockState = BlockState.getBlockState(world, next);
                    if (validator.test(next, blockState)) {
                        return next;
                    }
                }

                return endOfData();
            }
        };
    }

    /**
     * @see IPlacementSequence#collect()
     */
    public ImmutableList<ChunkCoordinates> collectFilteredSequence(World world, ItemStack stack, EntityPlayer player, ChunkCoordinates initial) {
        return ImmutableList.copyOf(getFilteredSequence(world, stack, player, initial));
    }

    public IValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }
}