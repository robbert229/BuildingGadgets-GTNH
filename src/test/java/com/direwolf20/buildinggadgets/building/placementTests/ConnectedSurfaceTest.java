package com.direwolf20.buildinggadgets.building.placementTests;

import com.direwolf20.buildinggadgets.common.building.Region;
import com.direwolf20.buildinggadgets.common.building.placement.ConnectedSurface;
import com.direwolf20.buildinggadgets.common.tools.VectorTools;
import com.direwolf20.buildinggadgets.util.CasedBlockView;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ChunkCoordinates;
import org.junit.jupiter.api.*;

import java.util.Random;
import java.util.Set;

import static com.direwolf20.buildinggadgets.util.CasedBlockView.regionAtOriginWithRandomTargets;
import static org.junit.jupiter.api.Assertions.*;

@Disabled("requires custom runner with minecraft started")
public class ConnectedSurfaceTest {

    private final Random random = new Random();

    @Test
    void connectedSurfaceShouldUseFuzzyMayContainsToRegion5By5() {
        CasedBlockView world = new CasedBlockView(new Region(-2, 0, -2, 2, 0, 2), CasedBlockView.base, CasedBlockView.target);
        ConnectedSurface surface = ConnectedSurface.create(world, new ChunkCoordinates(0,0,0), EnumFacing.UP, 5, false);

        //5 + 1 = 6, (5 / 2) + 1 = 3
        int x = random.nextInt(6) - 3;
        int z = random.nextInt(6) - 3;
        ChunkCoordinates randomPos = new ChunkCoordinates(x, 0, z);
        assertEquals(surface.getBoundingBox().contains(randomPos), surface.mayContain(x, 0, z));
    }

    @Test
    void connectedSurfaceShouldOnlyIncludeBlocksThatHasSameBlockUnderAsStartingPosition5By5RandomSelected30DifferPositions() {
        CasedBlockView world = regionAtOriginWithRandomTargets(5, 30);
        IBlockState selectedBlock = world.getBlockState(new ChunkCoordinates(0,0,0));

        for (EnumFacing side : EnumFacing.VALUES) {
            ConnectedSurface surface = ConnectedSurface.create(world, new ChunkCoordinates(0,0,0), side, 5, false);
            Set<ChunkCoordinates> calculated = surface.collect(new ObjectOpenHashSet<>());

            for (ChunkCoordinates pos : calculated) {
                assertEquals(selectedBlock, world.getBlockState(pos.offset(side.getOpposite())));
            }
        }
    }

    @Test
    void connectedSurfaceResultsShouldOnlyIncludeBlocksThatHasSameAxisValueAsStarted() {
        CasedBlockView world = regionAtOriginWithRandomTargets(5, 30);

        for (EnumFacing side : EnumFacing.VALUES) {
            ConnectedSurface surface = ConnectedSurface.create(world, new ChunkCoordinates(0,0,0).offset(side), side.getOpposite(), 5, false);
            Set<ChunkCoordinates> calculated = surface.collect(new ObjectOpenHashSet<>());

            int expected = VectorTools.getAxisValue(new ChunkCoordinates(0,0,0).offset(side), side.getAxis());
            for (ChunkCoordinates pos : calculated) {
                assertEquals(expected, VectorTools.getAxisValue(pos, side.getAxis()));
            }
        }
    }

    @Test
    void connectedSurfaceShouldFindAllConnectedBlocksHardcoded() {
        int radius = 3;
        CasedBlockView world = new CasedBlockView(new Region(-radius, 0, -radius, radius, 0, radius), CasedBlockView.base, CasedBlockView.target)
                //Connected
                .setOtherAt(new ChunkCoordinates(0,0,0))
                .setOtherAt(new ChunkCoordinates(0,0,0).north())
                .setOtherAt(new ChunkCoordinates(0,0,0).east())
                .setOtherAt(new ChunkCoordinates(0,0,0).south())
                .setOtherAt(new ChunkCoordinates(0,0,0).west())
                .setOtherAt(new ChunkCoordinates(0,0,0).north().east())
                .setOtherAt(new ChunkCoordinates(0,0,0).east().south())
                .setOtherAt(new ChunkCoordinates(0,0,0).south().west())
                .setOtherAt(new ChunkCoordinates(0,0,0).west().north())
                //Not connected
                .setOtherAt(new ChunkCoordinates(0,0,0).north(3))
                .setOtherAt(new ChunkCoordinates(0,0,0).east(3))
                .setOtherAt(new ChunkCoordinates(0,0,0).south(3))
                .setOtherAt(new ChunkCoordinates(0,0,0).west(3));

        ConnectedSurface surface = ConnectedSurface.create(world, new ChunkCoordinates(0,0,0), EnumFacing.UP, 5, false);
        assertEquals(9, surface.collect().size());
    }

}


