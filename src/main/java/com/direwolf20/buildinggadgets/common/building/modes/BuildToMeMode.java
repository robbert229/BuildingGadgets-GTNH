package com.direwolf20.buildinggadgets.common.building.modes;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.IValidatorFactory;
import com.direwolf20.buildinggadgets.common.building.placement.ExclusiveAxisChasing;
import com.direwolf20.buildinggadgets.common.config.SyncedConfig;
import com.direwolf20.buildinggadgets.common.tools.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ChunkCoordinates;

/**
 * Logic is backed with {@link ExclusiveAxisChasing} where no attempt will be made at the ending (player) position.
 * <p>
 * This mode is designed for Building Gadget and does not guarantee to work with other gadgets.
 */
public class BuildToMeMode extends AtopSupportedMode {

    private static final ResourceLocation NAME = new ResourceLocation(BuildingGadgets.MODID, "axis_chasing");

    public BuildToMeMode(IValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    @Override
    public IPlacementSequence computeWithTransformed(EntityPlayer player, ChunkCoordinates transformed, ChunkCoordinates original, EnumFacing sideHit, ItemStack tool) {
        return ExclusiveAxisChasing.create(transformed, new ChunkCoordinates((int) Math.floor(player.posX), (int) Math.floor(player.posY), (int) Math.floor(player.posZ)), sideHit, SyncedConfig.maxRange);
    }

    @Override
    public ChunkCoordinates transformAtop(EntityPlayer player, ChunkCoordinates hit, EnumFacing sideHit, ItemStack tool) {
        return WorldUtils.offset(hit, sideHit);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

}
