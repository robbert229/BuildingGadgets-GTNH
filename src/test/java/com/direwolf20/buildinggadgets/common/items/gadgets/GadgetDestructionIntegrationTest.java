package com.direwolf20.buildinggadgets.common.items.gadgets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.GadgetsConfig.GadgetDestructionConfig;
import com.direwolf20.buildinggadgets.util.NBTTool;
import com.direwolf20.buildinggadgets.util.ref.NBTKeys;

class GadgetDestructionIntegrationTest {

    private int previousEnergyCost;
    private int previousDamageCost;
    private boolean previousNonFuzzyEnabled;
    private double previousNonFuzzyMultiplier;

    @BeforeEach
    void saveAndSetConfig() {
        previousEnergyCost = GadgetDestructionConfig.energyCostDestruction;
        previousDamageCost = GadgetDestructionConfig.damageCostDestruction;
        previousNonFuzzyEnabled = GadgetDestructionConfig.nonFuzzyEnabled;
        previousNonFuzzyMultiplier = GadgetDestructionConfig.nonFuzzyMultiplier;

        GadgetDestructionConfig.energyCostDestruction = 100;
        GadgetDestructionConfig.damageCostDestruction = 5;
        GadgetDestructionConfig.nonFuzzyMultiplier = 2;
    }

    @AfterEach
    void restoreConfig() {
        GadgetDestructionConfig.energyCostDestruction = previousEnergyCost;
        GadgetDestructionConfig.damageCostDestruction = previousDamageCost;
        GadgetDestructionConfig.nonFuzzyEnabled = previousNonFuzzyEnabled;
        GadgetDestructionConfig.nonFuzzyMultiplier = previousNonFuzzyMultiplier;
    }

    @Test
    void nonFuzzyMultiplierAppliesWhenEnabledAndFuzzyIsOff() {
        GadgetDestructionConfig.nonFuzzyEnabled = true;
        ItemStack stack = new ItemStack(new GadgetDestruction());

        NBTTool.getOrNewTag(stack).setBoolean(NBTKeys.GADGET_FUZZY, false);

        GadgetDestruction gadget = new GadgetDestruction();

        assertEquals(200, gadget.getEnergyCost(stack));
        assertEquals(10, gadget.getDamageCost(stack));
    }

    @Test
    void nonFuzzyMultiplierDoesNotApplyWhenFuzzyIsOn() {
        GadgetDestructionConfig.nonFuzzyEnabled = true;
        ItemStack stack = new ItemStack(new GadgetDestruction());

        NBTTool.getOrNewTag(stack).setBoolean(NBTKeys.GADGET_FUZZY, true);
        GadgetDestruction gadget = new GadgetDestruction();

        assertEquals(100, gadget.getEnergyCost(stack));
        assertEquals(5, gadget.getDamageCost(stack));
    }

    @Test
    void nonFuzzyMultiplierDoesNotApplyWhenFeatureDisabled() {
        GadgetDestructionConfig.nonFuzzyEnabled = false;
        ItemStack stack = new ItemStack(new GadgetDestruction());

        GadgetDestruction gadget = new GadgetDestruction();

        assertEquals(100, gadget.getEnergyCost(stack));
        assertEquals(5, gadget.getDamageCost(stack));
    }

    @Test
    void getAreaBuildsExpectedVolumeForNorthFaceInUnconnectedMode() {
        ItemStack stack = prepareAreaStack(1, 2, 1, 0, 3);
        ChunkCoordinates clickPos = new ChunkCoordinates(10, 64, 10);

        Set<ChunkCoordinates> area = GadgetDestruction
            .getArea(createAlwaysValidWorld(), clickPos, EnumFacing.NORTH, createHoldingPlayer(stack), stack);

        int minX = area.stream().mapToInt(p -> p.posX).min().orElseThrow(() -> new AssertionError("Area must not be empty"));
        int maxX = area.stream().mapToInt(p -> p.posX).max().orElseThrow(() -> new AssertionError("Area must not be empty"));
        int minY = area.stream().mapToInt(p -> p.posY).min().orElseThrow(() -> new AssertionError("Area must not be empty"));
        int maxY = area.stream().mapToInt(p -> p.posY).max().orElseThrow(() -> new AssertionError("Area must not be empty"));
        int minZ = area.stream().mapToInt(p -> p.posZ).min().orElseThrow(() -> new AssertionError("Area must not be empty"));
        int maxZ = area.stream().mapToInt(p -> p.posZ).max().orElseThrow(() -> new AssertionError("Area must not be empty"));

        assertEquals(24, area.size());
        assertEquals(4, maxX - minX + 1);
        assertEquals(64, minY);
        assertEquals(65, maxY);
        assertEquals(10, minZ);
        assertEquals(12, maxZ);
        assertTrue(area.contains(clickPos));
    }

    /**
     * Regression test for the stateTarget-vs-anchor bug in non-fuzzy mode.
     *
     * <h3>How the bug could happen</h3>
     * <ol>
     *   <li>The player anchors the Destruction Gadget at block A (e.g. stone at (30,70,40))
     *       by shift-right-clicking it.</li>
     *   <li>The player then right-clicks the gadget while looking at a completely different
     *       block B (e.g. dirt at (5,5,5)). Because an anchor exists, the game passes
     *       the current look-target as the {@code pos} argument to {@link GadgetDestruction#getArea}
     *       while the actual operating position ({@code startPos}) is derived from the anchor.</li>
     *   <li>Before the fix, {@code stateTarget} was computed as
     *       {@code WorldUtils.getBlock(world, pos)} — that is, the block at the incoming
     *       look-target (block B / dirt). The selection region, however, is centred on
     *       {@code startPos} (block A / stone). Every block in that region is stone, but
     *       the non-fuzzy filter compares against dirt (block B). Because stone&nbsp;≠&nbsp;dirt,
     *       {@code validBlock} rejects every candidate and {@code getArea} returns an empty
     *       set — the gadget silently does nothing.</li>
     *   <li>After the fix, {@code stateTarget} is derived from {@code startPos} (block A /
     *       stone). Every block in the region matches stone, so the full area is returned.</li>
     * </ol>
     */
    @Test
    void nonFuzzyModeUsesAnchorBlockAsFilterTargetNotIncomingPos() {
        GadgetDestructionConfig.nonFuzzyEnabled = true;

        // Anchor at (30,70,40); depth=2 → region covers (30,70,38)-(30,70,40).
        ItemStack stack = prepareNonFuzzyAreaStack(0, 0, 0, 0, 2);
        ChunkCoordinates anchor = new ChunkCoordinates(30, 70, 40);
        GadgetDestruction.setAnchor(stack, anchor);
        GadgetDestruction.setAnchorSide(stack, EnumFacing.NORTH);

        // anchorBlock: the block type that lives at the anchor and throughout the region.
        Block anchorBlock = mock(Block.class);
        when(anchorBlock.isAir(any(World.class), anyInt(), anyInt(), anyInt())).thenReturn(false);
        when(anchorBlock.getBlockHardness(any(World.class), anyInt(), anyInt(), anyInt())).thenReturn(1.0f);

        // clickBlock: a completely different block type at the caller's incoming pos.
        // Before the fix this was mistakenly used as the non-fuzzy filter target.
        Block clickBlock = mock(Block.class);

        // The incoming pos is far from the anchor — simulating the player looking at
        // a different block than the one they anchored.
        ChunkCoordinates incomingPos = new ChunkCoordinates(5, 5, 5);

        World world = mock(World.class);
        // Default: every position in the world returns anchorBlock …
        when(world.getBlock(anyInt(), anyInt(), anyInt())).thenReturn(anchorBlock);
        // … except the incoming pos, which returns a different block.
        when(world.getBlock(incomingPos.posX, incomingPos.posY, incomingPos.posZ)).thenReturn(clickBlock);
        when(world.getTileEntity(anyInt(), anyInt(), anyInt())).thenReturn(null);

        Set<ChunkCoordinates> area = GadgetDestruction
            .getArea(world, incomingPos, EnumFacing.NORTH, createHoldingPlayer(stack), stack);

        // With the fix stateTarget == anchorBlock (from startPos), so every block in the
        // region passes the filter. Before the fix stateTarget == clickBlock (from pos),
        // causing all region blocks to be rejected and the area to be empty.
        assertFalse(area.isEmpty(),
            "Non-fuzzy mode must derive its block-type filter from startPos (the anchor), "
                + "not from the incoming pos argument. When an anchor is active the two positions "
                + "can differ, and using pos produces an empty area.");
        assertTrue(area.contains(anchor),
            "The anchor position itself must be included in the destruction area");
    }

    @Test
    void getAreaUsesAnchorAndAnchorSideInsteadOfIncomingTarget() {
        ItemStack stack = prepareAreaStack(0, 0, 0, 0, 2);
        ChunkCoordinates anchor = new ChunkCoordinates(30, 70, 40);
        GadgetDestruction.setAnchor(stack, anchor);
        GadgetDestruction.setAnchorSide(stack, EnumFacing.UP);

        ChunkCoordinates incomingPos = new ChunkCoordinates(5, 5, 5);
        Set<ChunkCoordinates> area = GadgetDestruction
            .getArea(createAlwaysValidWorld(), incomingPos, EnumFacing.NORTH, createHoldingPlayer(stack), stack);

        assertTrue(area.contains(anchor));
        assertTrue(area.contains(new ChunkCoordinates(30, 69, 40)));
        assertFalse(area.contains(incomingPos));
    }

    private static ItemStack prepareAreaStack(int left, int right, int up, int down, int depth) {
        ItemStack stack = new ItemStack(new GadgetDestruction());
        GadgetDestruction.setToolValue(stack, left, "left");
        GadgetDestruction.setToolValue(stack, right, "right");
        GadgetDestruction.setToolValue(stack, up, "up");
        GadgetDestruction.setToolValue(stack, down, "down");
        GadgetDestruction.setToolValue(stack, depth, "depth");
        NBTTool.getOrNewTag(stack).setBoolean(NBTKeys.GADGET_UNCONNECTED_AREA, true);
        NBTTool.getOrNewTag(stack).setBoolean(NBTKeys.GADGET_FUZZY, true);
        return stack;
    }

    /** Same as {@link #prepareAreaStack} but with fuzzy mode disabled. */
    private static ItemStack prepareNonFuzzyAreaStack(int left, int right, int up, int down, int depth) {
        ItemStack stack = prepareAreaStack(left, right, up, down, depth);
        NBTTool.getOrNewTag(stack).setBoolean(NBTKeys.GADGET_FUZZY, false);
        return stack;
    }

    private static World createAlwaysValidWorld() {
        World world = mock(World.class);
        Block block = mock(Block.class);

        when(world.getBlock(anyInt(), anyInt(), anyInt())).thenReturn(block);
        when(world.getTileEntity(anyInt(), anyInt(), anyInt())).thenReturn(null);
        when(block.isAir(any(World.class), anyInt(), anyInt(), anyInt())).thenReturn(false);
        when(block.getBlockHardness(any(World.class), anyInt(), anyInt(), anyInt())).thenReturn(1.0f);

        return world;
    }

    private static EntityPlayer createHoldingPlayer(ItemStack heldStack) {
        EntityPlayer player = mock(EntityPlayer.class);
        when(player.getHeldItem()).thenReturn(heldStack);
        when(player.canPlayerEdit(anyInt(), anyInt(), anyInt(), anyInt(), any(ItemStack.class)))
            .thenReturn(true);
        return player;
    }
}


