package com.direwolf20.buildinggadgets.common.building.modes;

import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.IValidatorFactory;
import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ChunkCoordinates;

/**
 * Grid mode designed for Exchanging Gadget where it attempt to build on the same level as target position rather than
 * one higher.
 *
 * @see GridMode
 */
public class ExchangingGridMode extends GridMode {

    public ExchangingGridMode(IValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    /**
     * @implNote Exchanger replace at the same level, Building gadget build on top of the level
     */
    @Override
    public IPlacementSequence computeCoordinates(EntityPlayer player, ChunkCoordinates hit, EnumFacing sideHit, ItemStack tool) {
        return super.computeCoordinates(player, ChunkCoordinateUtils.offset(hit, EnumFacing.DOWN), sideHit, tool);
    }
}
