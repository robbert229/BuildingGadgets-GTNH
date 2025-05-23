package com.direwolf20.buildinggadgets.common.building.placement;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.Region;
import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import com.direwolf20.buildinggadgets.util.VectorTools;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.AbstractIterator;

/**
 * Surface that limits its attempt to blocks that are connected through either on its sides or corners. Candidates are
 * selected from a wall region centered at a point and filtered with a 8-way adjacent flood fill.
 *
 * @see #iterator()
 * @see Surface
 */
public final class ConnectedSurface implements IPlacementSequence {

    /**
     * @param world           Block access for searching reference
     * @param searchingCenter Center of the searching region
     * @param side            Facing to offset from the {@code searchingCenter} to get to the reference region center
     */
    public static ConnectedSurface create(IBlockAccess world, ChunkCoordinates searchingCenter, EnumFacing side,
        int range, boolean fuzzy) {
        Region searchingRegion = Wall.clickedSide(searchingCenter, side, range)
            .getBoundingBox();

        // TODO(johnrowl) offset's refactor here is suspicious.
        // After revisiting, maybe not?
        // return create(world, searchingRegion, pos -> pos.offset(side), searchingCenter, side, fuzzy);
        return create(
            world,
            searchingRegion,
            pos -> ChunkCoordinateUtils.offset(pos, side),
            searchingCenter,
            side,
            fuzzy);
    }

    public static ConnectedSurface create(IBlockAccess world, Region searchingRegion,
        Function<ChunkCoordinates, ChunkCoordinates> searching2referenceMapper, ChunkCoordinates searchingCenter,
        @Nullable EnumFacing side, boolean fuzzy) {
        return new ConnectedSurface(world, searchingRegion, searching2referenceMapper, searchingCenter, side, fuzzy);
    }

    public static ConnectedSurface create(IBlockAccess world, Region searchingRegion,
        Function<ChunkCoordinates, ChunkCoordinates> searching2referenceMapper, ChunkCoordinates searchingCenter,
        @Nullable EnumFacing side, BiPredicate<Block, ChunkCoordinates> predicate) {
        return new ConnectedSurface(
            world,
            searchingRegion,
            searching2referenceMapper,
            searchingCenter,
            side,
            predicate);
    }

    private final IBlockAccess world;
    private final Region searchingRegion;
    private final Function<ChunkCoordinates, ChunkCoordinates> searching2referenceMapper;
    private final ChunkCoordinates searchingCenter;
    private final EnumFacing side;
    private final BiPredicate<Block, ChunkCoordinates> predicate;

    @VisibleForTesting
    private ConnectedSurface(IBlockAccess world, Region searchingRegion,
        Function<ChunkCoordinates, ChunkCoordinates> searching2referenceMapper, ChunkCoordinates searchingCenter,
        @Nullable EnumFacing side, boolean fuzzy) {
        this(world, searchingRegion, searching2referenceMapper, searchingCenter, side, (filter, pos) -> {
            ChunkCoordinates position = searching2referenceMapper.apply(pos);
            Block reference = world.getBlock(position.posX, position.posY, position.posZ);
            boolean isAir = reference.isAir(world, pos.posX, pos.posY, pos.posZ);
            // If fuzzy=true, we ignore the block for reference
            return !isAir && (fuzzy || filter == reference);
        });
    }

    ConnectedSurface(IBlockAccess world, Region searchingRegion,
        Function<ChunkCoordinates, ChunkCoordinates> searching2referenceMapper, ChunkCoordinates searchingCenter,
        @Nullable EnumFacing side, BiPredicate<Block, ChunkCoordinates> predicate) {
        this.world = world;
        this.searchingRegion = searchingRegion;
        this.searching2referenceMapper = searching2referenceMapper;
        this.searchingCenter = searchingCenter;
        this.side = side;
        this.predicate = predicate;
    }

    /**
     * The bounding box of the searchingRegion that is being searched.
     */
    @Override
    public Region getBoundingBox() {
        return searchingRegion;
    }

    /**
     * {@inheritDoc}<br>
     * <b>inaccurate representation (case 2)</b>:
     *
     * @return {@code getBoundingBox().contains(x, y, z)} since it would be costly to check each position
     */
    @Override
    public boolean mayContain(int x, int y, int z) {
        return searchingRegion.mayContain(x, y, z);
    }

    @Override
    public IPlacementSequence copy() {
        return new ConnectedSurface(
            world,
            searchingRegion,
            searching2referenceMapper,
            searchingCenter,
            side,
            predicate);
    }

    /**
     * Uses a 8-way adjacent flood fill algorithm with Breadth-First Search to identify blocks with a valid path. A
     * position
     * is valid if and only if it connects to the center and its underside block is the same as the underside of the
     * center.
     *
     * @implNote Uses a 8-way adjacent flood fill algorithm with Breadth-First Search to identify blocks with a valid
     *           path.
     */
    @Nonnull
    @Override
    public Iterator<ChunkCoordinates> iterator() {
        Block selectedBlock = getReferenceFor(searchingCenter);

        return new AbstractIterator<ChunkCoordinates>() {

            private Queue<ChunkCoordinates> queue = new LinkedList<>();
            private Set<ChunkCoordinates> searched = new HashSet<>(searchingRegion.size());

            {
                if (isValid(searchingCenter)) { // The destruction Gadget might be facing Bedrock or something similar -
                                                // this would not be valid!
                    queue.add(searchingCenter);
                    searched.add(searchingCenter);
                }
            }

            @Override
            protected ChunkCoordinates computeNext() {
                if (queue.isEmpty()) return endOfData();

                // The position is guaranteed to be valid
                ChunkCoordinates current = queue.remove();
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (side != null) {
                            ChunkCoordinates neighbor = VectorTools.perpendicularSurfaceOffset(current, side, i, j);
                            addNeighbour(neighbor);
                        } else {
                            for (int k = -1; k <= 1; k++) {
                                ChunkCoordinates neighbor = new ChunkCoordinates(
                                    current.posX + i,
                                    current.posY + j,
                                    current.posZ + k);
                                addNeighbour(neighbor);
                            }
                        }
                    }
                }

                return current;
            }

            private void addNeighbour(ChunkCoordinates neighbor) {
                boolean isSearched = !searched.add(neighbor);
                if (isSearched || !isValid(neighbor)) return;
                queue.add(neighbor);
            }

            private boolean isValid(ChunkCoordinates pos) {
                return searchingRegion.contains(pos) && predicate.test(selectedBlock, pos);
            }
        };
    }

    private Block getReferenceFor(ChunkCoordinates pos) {
        ChunkCoordinates coordinates = searching2referenceMapper.apply(pos);
        return world.getBlock(coordinates.posX, coordinates.posY, coordinates.posZ);
    }

}
