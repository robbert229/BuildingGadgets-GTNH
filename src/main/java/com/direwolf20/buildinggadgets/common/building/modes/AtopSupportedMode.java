package com.direwolf20.buildinggadgets.common.building.modes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;

import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.IValidatorFactory;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetBuilding;

/**
 * Base class for modes want to support "Place on Top" modifier.
 */
public abstract class AtopSupportedMode extends AbstractMode {

    public AtopSupportedMode(IValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    @Override
    public IPlacementSequence computeCoordinates(EntityPlayer player, ChunkCoordinates hit, EnumFacing sideHit,
        ItemStack tool) {
        if (GadgetBuilding.shouldPlaceAtop(tool))
            return this.computeWithTransformed(player, transformAtop(player, hit, sideHit, tool), hit, sideHit, tool);
        return this.computeWithTransformed(player, hit, hit, sideHit, tool);
    }

    /**
     * @param transformed transformed starting position with
     *                    {@link #transformAtop(EntityPlayer, ChunkCoordinates, EnumFacing, ItemStack)}
     * @param original    original starting position
     * @implSpec Implementation should work with {@code transformed.equals(original)}
     */
    public abstract IPlacementSequence computeWithTransformed(EntityPlayer player, ChunkCoordinates transformed,
        ChunkCoordinates original, EnumFacing sideHit, ItemStack tool);

    /**
     * Calculate the block pos if "Place on Top" was enabled.
     */
    public abstract ChunkCoordinates transformAtop(EntityPlayer player, ChunkCoordinates hit, EnumFacing sideHit,
        ItemStack tool);

}
