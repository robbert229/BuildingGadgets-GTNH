package com.direwolf20.buildinggadgets.common.building.placement;

import java.util.Iterator;
import java.util.function.Function;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.Region;
import com.direwolf20.buildinggadgets.common.tools.WorldUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.AbstractIterator;

/**
 * Surface mode where no connectivity is required. All blocks within the region (wall centered at some position) will
 * be selected if it fulfills the requirement -- has its underside same as the starting position.
 *
 * @see ConnectedSurface
 */
public final class Surface implements IPlacementSequence {

    /**
     * @param world           Block access for searching reference
     * @param searchingCenter Center of the searching region
     * @param side            Facing to offset from the {@code searchingCenter} to get to the reference region center
     */
    public static Surface create(IBlockAccess world, ChunkCoordinates searchingCenter, EnumFacing side, int range,
        boolean fuzzy) {
        Region searchingRegion = Wall.clickedSide(searchingCenter, side, range)
            .getBoundingBox();
        return create(world, searchingCenter, searchingRegion, pos -> WorldUtils.offset(pos, side, 1), fuzzy);
    }

    public static Surface create(IBlockAccess world, ChunkCoordinates center, Region searchingRegion,
        Function<ChunkCoordinates, ChunkCoordinates> searching2referenceMapper, boolean fuzzy) {
        return new Surface(world, center, searchingRegion, searching2referenceMapper, fuzzy);
    }

    private final IBlockAccess world;
    private final Block selectedBase;
    private final Function<ChunkCoordinates, ChunkCoordinates> searching2referenceMapper;
    private final Region searchingRegion;
    private final boolean fuzzy;

    @VisibleForTesting
    private Surface(IBlockAccess world, ChunkCoordinates center, Region searchingRegion,
        Function<ChunkCoordinates, ChunkCoordinates> searching2referenceMapper, boolean fuzzy) {
        this.world = world;

        ChunkCoordinates searchResult = searching2referenceMapper.apply(center);

        this.selectedBase = world.getBlock(searchResult.posX, searchResult.posY, searchResult.posZ);
        this.searchingRegion = searchingRegion;
        this.searching2referenceMapper = searching2referenceMapper;
        this.fuzzy = fuzzy;
    }

    /**
     * For {@link #copy()}
     */
    private Surface(IBlockAccess world, Block selectedBase,
        Function<ChunkCoordinates, ChunkCoordinates> searching2referenceMapper, Region searchingRegion, boolean fuzzy) {
        this.world = world;
        this.selectedBase = selectedBase;
        this.searching2referenceMapper = searching2referenceMapper;
        this.searchingRegion = searchingRegion;
        this.fuzzy = fuzzy;
    }

    @Override
    public Region getBoundingBox() {
        return searchingRegion;
    }

    /**
     * {@inheritDoc}<br>
     * <b>inaccurate representation (case 2)</b>:
     */
    @Override
    public boolean mayContain(int x, int y, int z) {
        return searchingRegion.contains(x, y, z);
    }

    @Override
    public IPlacementSequence copy() {
        return new Surface(world, selectedBase, searching2referenceMapper, searchingRegion, fuzzy);
    }

    @Nonnull
    @Override
    public Iterator<ChunkCoordinates> iterator() {
        return new AbstractIterator<ChunkCoordinates>() {

            private final Iterator<ChunkCoordinates> it = searchingRegion.iterator();

            @Override
            protected ChunkCoordinates computeNext() {
                while (it.hasNext()) {
                    ChunkCoordinates pos = it.next();
                    ChunkCoordinates referencePos = searching2referenceMapper.apply(pos);
                    Block baseBlock = world.getBlock(referencePos.posX, referencePos.posY, referencePos.posZ);
                    if ((fuzzy || baseBlock == selectedBase)
                        && !baseBlock.isAir(world, referencePos.posX, referencePos.posY, referencePos.posZ)) return pos;
                }
                return endOfData();
            }
        };
    }

}
