package com.direwolf20.buildinggadgets.common.building.modes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.IValidatorFactory;
import com.direwolf20.buildinggadgets.common.building.placement.Wall;
import com.direwolf20.buildinggadgets.common.tools.*;
import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import com.direwolf20.buildinggadgets.util.MathTool;
import com.direwolf20.buildinggadgets.util.VectorTools;

/**
 * Building mode creating a wall which will always be perpendicular to the XZ world plane.
 * <p>
 * When the player selects top or bottom of a block, it will build a plane of blocks where the bottom/top center will
 * locate at the neighboring position from the target.
 * If range is an even number, the wall will be 1 taller than usual. Otherwise the wall will be a square with range as
 * its
 * side length.
 * <p>
 * When the player selects any horizontal side of a block, it will build a plane facing the player centered at
 * the target position.
 * Range used as its side length will be rounded down towards the nearest odd number that is at least 1.
 *
 * @see Wall
 */
public class VerticalWallMode extends AtopSupportedMode {

    private static final ResourceLocation NAME = new ResourceLocation(BuildingGadgets.MODID, "vertical_wall");

    public VerticalWallMode(IValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    @Override
    public IPlacementSequence computeWithTransformed(EntityPlayer player, ChunkCoordinates transformed,
        ChunkCoordinates original, EnumFacing sideHit, ItemStack tool) {
        int range = GadgetUtils.getToolRange(tool);
        int radius = MathTool.floorToOdd(range) / 2;
        if (VectorTools.isAxisVertical(sideHit)) return Wall.extendingFrom(
            ChunkCoordinateUtils.offset(transformed, DirectionUtils.getOppositeEnumFacing(sideHit)),
            sideHit,
            VectorTools.getHorizontalFacingFromPlayer(player),
            radius,
            MathTool.isEven(range) ? 1 : 0);
        return Wall.clickedSide(transformed, sideHit, radius);
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
