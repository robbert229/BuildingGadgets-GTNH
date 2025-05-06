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
 * Building mode where such wall will always be perpendicular to the XZ world plane.
 * <p>
 * When the player selects any horizontal side of a block, a wall sitting on clicked side of the target will be build
 * position with a length of tool range.
 * The wall will be a square when tool range is an even number, and 1 higher if tool range is an odd number.
 * <p>
 * When the player selects top or bottom of a block, it will build a wall centered at the target position with tool
 * range.
 * Range used as its side length will be rounded down towards the nearest odd number that is at least 1.
 *
 * @see Wall
 */
public class HorizontalWallMode extends AtopSupportedMode {

    private static final ResourceLocation NAME = new ResourceLocation(BuildingGadgets.MODID, "horizontal_wall");

    public HorizontalWallMode(IValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    @Override
    public IPlacementSequence computeWithTransformed(EntityPlayer player, ChunkCoordinates transformed,
        ChunkCoordinates original, EnumFacing sideHit, ItemStack tool) {
        int range = GadgetUtils.getToolRange(tool);
        int radius = MathTool.floorToOdd(range) / 2;
        if (VectorTools.isAxisVertical(sideHit)) return Wall.clickedSide(transformed, sideHit, radius);
        return Wall.extendingFrom(
            ChunkCoordinateUtils.offset(transformed, DirectionUtils.getOppositeEnumFacing(sideHit)),
            sideHit,
            EnumFacing.UP,
            radius,
            MathTool.isEven(range) ? 1 : 0);
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
