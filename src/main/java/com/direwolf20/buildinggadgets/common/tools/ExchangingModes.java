package com.direwolf20.buildinggadgets.common.tools;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlockTileEntity;
import com.direwolf20.buildinggadgets.common.blocks.ModBlocks;
import com.direwolf20.buildinggadgets.common.building.IBuildingMode;
import com.direwolf20.buildinggadgets.common.building.modes.ExchangingGridMode;
import com.direwolf20.buildinggadgets.common.building.modes.ExchangingHorizontalColumnMode;
import com.direwolf20.buildinggadgets.common.building.modes.ExchangingSurfaceMode;
import com.direwolf20.buildinggadgets.common.building.modes.ExchangingVerticalColumnMode;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import com.direwolf20.buildinggadgets.util.NBTTool;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;
import com.google.common.collect.ImmutableList;

public enum ExchangingModes {

    Surface("surface.png", new ExchangingSurfaceMode(ExchangingModes::combineTester)),
    VerticalColumn("vertical_column.png", new ExchangingVerticalColumnMode(ExchangingModes::combineTester)),
    HorizontalColumn("horizontal_column.png", new ExchangingHorizontalColumnMode(ExchangingModes::combineTester)),
    Grid("grid.png", new ExchangingGridMode(ExchangingModes::combineTester));

    private static final ExchangingModes[] VALUES = values();
    private final ResourceLocation icon;
    private final IBuildingMode modeImpl;

    ExchangingModes(String iconFile, IBuildingMode modeImpl) {
        this.icon = new ResourceLocation(BuildingGadgets.MODID, "textures/gui/mode/" + iconFile);
        this.modeImpl = modeImpl;
    }

    public ResourceLocation getIcon() {
        return icon;
    }

    public IBuildingMode getModeImplementation() {
        return modeImpl;
    }

    public String getRegistryName() {
        return getModeImplementation().getRegistryName()
            .toString() + "/ExchangingGadget";
    }

    @Override
    public String toString() {
        return getModeImplementation().getLocalized();
    }

    public ExchangingModes next() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    public static ExchangingModes byName(String name) {
        return Arrays.stream(values())
            .filter(
                mode -> mode.getRegistryName()
                    .equals(name))
            .findFirst()
            .orElse(Surface);
    }

    private static final ImmutableList<ResourceLocation> ICONS = Arrays.stream(values())
        .map(ExchangingModes::getIcon)
        .collect(ImmutableList.toImmutableList());

    public static ImmutableList<ResourceLocation> getIcons() {
        return ICONS;
    }

    public static List<ChunkCoordinates> collectPlacementPos(World world, EntityPlayer player, ChunkCoordinates hit,
        EnumFacing sideHit, ItemStack tool, ChunkCoordinates initial) {
        IBuildingMode mode = byName(
            NBTTool.getOrNewTag(tool)
                .getString("mode")).getModeImplementation();
        return mode.createExecutionContext(player, hit, sideHit, tool)
            .collectFilteredSequence(world, tool, player, initial);
    }

    public static BiPredicate<ChunkCoordinates, BlockState> combineTester(World world, ItemStack tool,
        EntityPlayer player, ChunkCoordinates initial) {
        BlockState initialBlockState = BlockState.getBlockState(world, initial);
        BlockState target = GadgetUtils.getToolBlock(tool);
        return (pos, state) -> {
            BlockState worldBlockState = BlockState.getBlockState(world, pos);

            // Don't try to replace for the same block
            if (worldBlockState == target) return false;

            // No need to replace if source and target are the same if fuzzy mode is off
            if (!GadgetGeneric.getFuzzy(tool) && worldBlockState != initialBlockState) return false;

            // If the target is already enqueued, don't replace it
            if (worldBlockState.getBlock()
                .equals(ModBlocks.effectBlock)) {
                return false;
            }

            // Only replace existing blocks, don't place more
            if (worldBlockState.isAir(worldBlockState, world, pos)) return false;

            // TODO(johnrowl) re-enable the black list.
            // Messy, lovely.
            // if (SyncedConfig.blockBlacklist.contains(worldBlockState.getBlock().getDefaultState().getBlock()))
            // return false;

            TileEntity tile = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
            // Only replace construction block with same block state
            if (tile instanceof ConstructionBlockTileEntity cbte && cbte.getBlockMetadata() == state.getMetadata()
                && cbte.blockType.equals(state.getBlock())) {
                return false;
            } else if (tile != null) {
                // Otherwise if the block has a tile entity, ignore it

                return false;
            }

            // Bedrock, End Portal Frame, etc.
            if (worldBlockState.getBlock()
                .getBlockHardness(world, pos.posX, pos.posY, pos.posZ) < 0) return false;

            // Don't replace liquids
            return !worldBlockState.getMaterial()
                .isLiquid();
        };
    }

}
