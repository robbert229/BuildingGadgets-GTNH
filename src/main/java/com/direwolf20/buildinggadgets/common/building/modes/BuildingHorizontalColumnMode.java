package com.direwolf20.buildinggadgets.common.building.modes;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.IValidatorFactory;
import com.direwolf20.buildinggadgets.common.building.placement.Column;
import com.direwolf20.buildinggadgets.common.tools.DirectionUtils;
import com.direwolf20.buildinggadgets.common.tools.GadgetUtils;
import com.direwolf20.buildinggadgets.util.VectorTools;
import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ChunkCoordinates;

/**
 * Horizontal column mode for Building Gadget.
 * <p>
 * If the player clicks on any horizontal side of the target position, it will start from the there and build column
 * towards the side clicked. If the player clicks on the top side, however, it will use player's facing instead.
 *
 * @see Column
 */
public class BuildingHorizontalColumnMode extends AtopSupportedMode {

    private static final ResourceLocation NAME = new ResourceLocation(BuildingGadgets.MODID, "horizontal_column");

    public BuildingHorizontalColumnMode(IValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    @Override
    public IPlacementSequence computeWithTransformed(EntityPlayer player, ChunkCoordinates transformed, ChunkCoordinates original, EnumFacing sideHit, ItemStack tool) {
        int range = GadgetUtils.getToolRange(tool);

        if (VectorTools.isAxisVertical(sideHit)) {
            return Column.extendFrom(transformed, VectorTools.getHorizontalFacingFromPlayer(player), range);
        }

        return Column.extendFrom(transformed, DirectionUtils.getOppositeEnumFacing(sideHit), range);
    }

    @Override
    public ChunkCoordinates transformAtop(EntityPlayer player, ChunkCoordinates hit, EnumFacing sideHit, ItemStack tool) {
        return VectorTools.isAxisVertical(sideHit) ? ChunkCoordinateUtils.offset(hit, VectorTools.getHorizontalFacingFromPlayer(player)) : ChunkCoordinateUtils.offset(hit, DirectionUtils.getOppositeEnumFacing(sideHit));
    }

    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

}
