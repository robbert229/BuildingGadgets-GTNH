package com.direwolf20.buildinggadgets.common.building.modes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.IValidatorFactory;
import com.direwolf20.buildinggadgets.common.building.placement.Column;
import com.direwolf20.buildinggadgets.common.tools.GadgetUtils;
import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import com.direwolf20.buildinggadgets.util.MathTool;
import com.direwolf20.buildinggadgets.util.VectorTools;

/**
 * Vertical column mode for Building Gadget.
 * <p>
 * When the player selects top or bottom of a block, it will build a column perpendicular to the ground with length of
 * tool range.
 * <p>
 * When the player selects any horizontal side of a block, it will build a column centered at the selected block with a
 * length of floored tool range.
 *
 * @see Column
 */
public class BuildingVerticalColumnMode extends AtopSupportedMode {

    private static final ResourceLocation NAME = new ResourceLocation(BuildingGadgets.MODID, "vertical_column");

    public BuildingVerticalColumnMode(IValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    @Override
    public IPlacementSequence computeWithTransformed(EntityPlayer player, ChunkCoordinates transformed,
        ChunkCoordinates original, EnumFacing sideHit, ItemStack tool) {
        int range = GadgetUtils.getToolRange(tool);
        if (VectorTools.isAxisVertical(sideHit)) {
            return Column.extendFrom(transformed, sideHit, range);
        }

        int radius = MathTool.floorToOdd(range);
        return Column.centerAt(transformed, EnumFacing.UP, radius);
    }

    @Override
    public ChunkCoordinates transformAtop(EntityPlayer player, ChunkCoordinates hit, EnumFacing sideHit,
        ItemStack tool) {
        return ChunkCoordinateUtils.offset(hit, sideHit);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

}
