package com.direwolf20.buildinggadgets.common.building.modes;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.IValidatorFactory;
import com.direwolf20.buildinggadgets.common.building.placement.Stair;
import com.direwolf20.buildinggadgets.common.tools.DirectionUtils;
import com.direwolf20.buildinggadgets.common.tools.GadgetUtils;
import com.direwolf20.buildinggadgets.common.tools.VectorTools;
import com.direwolf20.buildinggadgets.common.tools.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ChunkCoordinates;

/**
 * Stair mode for Building Gadget.
 * <p>
 * When the target position is higher than player's head, it will extend downwards and towards the player.
 * When the target position is 2 lower than player's feet, it will extend upwards and towards the player.
 * Otherwise, it will extend upwards and away from the player.
 *
 * @see Stair
 */
public class StairMode extends AtopSupportedMode {

    private static final ResourceLocation NAME = new ResourceLocation(BuildingGadgets.MODID, "stair");

    public StairMode(IValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    @Override
    public IPlacementSequence computeWithTransformed(EntityPlayer player, ChunkCoordinates transformed, ChunkCoordinates original, EnumFacing sideHit, ItemStack tool) {
        int range = GadgetUtils.getToolRange(tool);
        EnumFacing side = VectorTools.isAxisVertical(sideHit) ? DirectionUtils.getOppositeEnumFacing(VectorTools.getHorizontalFacingFromPlayer(player)) : sideHit;

        if (original.posY > player.posY + 1)
            return Stair.create(transformed, side, EnumFacing.DOWN, range);
        else if (original.posY < player.posY - 2)
            return Stair.create(transformed, side, EnumFacing.UP, range);
        return Stair.create(transformed, DirectionUtils.getOppositeEnumFacing(side), EnumFacing.UP, range);
    }

    @Override
    public ChunkCoordinates transformAtop(EntityPlayer player, ChunkCoordinates hit, EnumFacing sideHit, ItemStack tool) {
        if (hit.posY > player.posY + 1) {
            EnumFacing side = VectorTools.isAxisVertical(sideHit) ? VectorTools.getHorizontalFacingFromPlayer(player) : sideHit;
            return WorldUtils.offset(WorldUtils.down(hit), side);
        }

        return WorldUtils.up(hit);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

}
