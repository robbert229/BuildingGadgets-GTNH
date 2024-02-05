package com.direwolf20.buildinggadgets.common.building.modes;

import com.direwolf20.buildinggadgets.common.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.IValidatorFactory;
import com.direwolf20.buildinggadgets.common.building.placement.Grid;
import com.direwolf20.buildinggadgets.common.tools.GadgetUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ChunkCoordinates;

/**
 * Grid mode for Building Gadget.
 * <p>
 * See {@link Grid} for more information. Period size is can be changed through config, default is {@code 6}
 */
public class GridMode extends AtopSupportedMode {

    //TODO give config option
    //     min = 1, max = 15 (tool range)
    private static final int DEFAULT_PERIOD_SIZE = 6;
    private static final ResourceLocation NAME = new ResourceLocation(BuildingGadgets.MODID, "grid");

    public GridMode(IValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    @Override
    public IPlacementSequence computeWithTransformed(EntityPlayer player, ChunkCoordinates transformed, ChunkCoordinates original, EnumFacing sideHit, ItemStack tool) {
        return Grid.create(transformed, GadgetUtils.getToolRange(tool), DEFAULT_PERIOD_SIZE);
    }

    @Override
    public ChunkCoordinates transformAtop(EntityPlayer player, ChunkCoordinates hit, EnumFacing sideHit, ItemStack tool) {
        EnumFacing locked = sideHit.getAxis().isVertical() ? sideHit : EnumFacing.UP;
        return hit.offset(locked);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

}
