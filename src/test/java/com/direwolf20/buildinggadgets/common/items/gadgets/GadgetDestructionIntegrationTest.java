package com.direwolf20.buildinggadgets.common.items.gadgets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

        NBTTool.getOrNewTag(stack)
            .setBoolean(NBTKeys.GADGET_FUZZY, false);

        GadgetDestruction gadget = new GadgetDestruction();

        assertEquals(200, gadget.getEnergyCost(stack));
        assertEquals(10, gadget.getDamageCost(stack));
    }

    @Test
    void nonFuzzyMultiplierDoesNotApplyWhenFuzzyIsOn() {
        GadgetDestructionConfig.nonFuzzyEnabled = true;
        ItemStack stack = new ItemStack(new GadgetDestruction());

        NBTTool.getOrNewTag(stack)
            .setBoolean(NBTKeys.GADGET_FUZZY, true);
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

        int minX = area.stream()
            .mapToInt(p -> p.posX)
            .min()
            .orElseThrow(() -> new AssertionError("Area must not be empty"));
        int maxX = area.stream()
            .mapToInt(p -> p.posX)
            .max()
            .orElseThrow(() -> new AssertionError("Area must not be empty"));
        int minY = area.stream()
            .mapToInt(p -> p.posY)
            .min()
            .orElseThrow(() -> new AssertionError("Area must not be empty"));
        int maxY = area.stream()
            .mapToInt(p -> p.posY)
            .max()
            .orElseThrow(() -> new AssertionError("Area must not be empty"));
        int minZ = area.stream()
            .mapToInt(p -> p.posZ)
            .min()
            .orElseThrow(() -> new AssertionError("Area must not be empty"));
        int maxZ = area.stream()
            .mapToInt(p -> p.posZ)
            .max()
            .orElseThrow(() -> new AssertionError("Area must not be empty"));

        assertEquals(24, area.size());
        assertEquals(4, maxX - minX + 1);
        assertEquals(64, minY);
        assertEquals(65, maxY);
        assertEquals(10, minZ);
        assertEquals(12, maxZ);
        assertTrue(area.contains(clickPos));
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

    @Test
    void getAreaUsesAnchorBlockAsNonFuzzyTarget() {
        GadgetDestructionConfig.nonFuzzyEnabled = true;
        ItemStack stack = prepareAreaStack(0, 0, 0, 0, 2);
        NBTTool.getOrNewTag(stack)
            .setBoolean(NBTKeys.GADGET_FUZZY, false);

        ChunkCoordinates anchor = new ChunkCoordinates(30, 70, 40);
        GadgetDestruction.setAnchor(stack, anchor);
        GadgetDestruction.setAnchorSide(stack, EnumFacing.UP);

        ChunkCoordinates incomingPos = new ChunkCoordinates(5, 5, 5);
        Set<ChunkCoordinates> area = GadgetDestruction.getArea(
            createWorldWithDifferentIncomingBlock(incomingPos),
            incomingPos,
            EnumFacing.NORTH,
            createHoldingPlayer(stack),
            stack);

        assertEquals(2, area.size());
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
        NBTTool.getOrNewTag(stack)
            .setBoolean(NBTKeys.GADGET_UNCONNECTED_AREA, true);
        NBTTool.getOrNewTag(stack)
            .setBoolean(NBTKeys.GADGET_FUZZY, true);
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

    private static World createWorldWithDifferentIncomingBlock(ChunkCoordinates incomingPos) {
        World world = mock(World.class);
        Block anchorBlock = mock(Block.class);
        Block incomingBlock = mock(Block.class);

        when(world.getBlock(anyInt(), anyInt(), anyInt())).thenAnswer(invocation -> {
            int x = invocation.getArgument(0, Integer.class);
            int y = invocation.getArgument(1, Integer.class);
            int z = invocation.getArgument(2, Integer.class);
            if (x == incomingPos.posX && y == incomingPos.posY && z == incomingPos.posZ) {
                return incomingBlock;
            }
            return anchorBlock;
        });
        when(world.getTileEntity(anyInt(), anyInt(), anyInt())).thenReturn(null);
        when(anchorBlock.isAir(any(World.class), anyInt(), anyInt(), anyInt())).thenReturn(false);
        when(incomingBlock.isAir(any(World.class), anyInt(), anyInt(), anyInt())).thenReturn(false);
        when(anchorBlock.getBlockHardness(any(World.class), anyInt(), anyInt(), anyInt())).thenReturn(1.0f);
        when(incomingBlock.getBlockHardness(any(World.class), anyInt(), anyInt(), anyInt())).thenReturn(1.0f);

        return world;
    }

    private static EntityPlayer createHoldingPlayer(ItemStack heldStack) {
        EntityPlayer player = mock(EntityPlayer.class);
        when(player.getHeldItem()).thenReturn(heldStack);
        when(player.canPlayerEdit(anyInt(), anyInt(), anyInt(), anyInt(), any(ItemStack.class))).thenReturn(true);
        return player;
    }
}
