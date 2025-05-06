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
import com.direwolf20.buildinggadgets.util.MathTool;
import com.direwolf20.buildinggadgets.util.VectorTools;

/**
 * Horizontal column mode for Exchanging Gadget.
 * <p>
 * If a 2D x-y coordinate plane was built on the selected side with the selected block as origin, the column will be the
 * X axis in the plane.
 * The column will be centered at the origin. Length of the column will be the tool range that is floored to an odd
 * number with a lower bound of 1.
 *
 * @see Column
 */
public class ExchangingHorizontalColumnMode extends AbstractMode {

    private static final ResourceLocation NAME = new ResourceLocation(BuildingGadgets.MODID, "horizontal_column");

    public ExchangingHorizontalColumnMode(IValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    @Override
    public IPlacementSequence computeCoordinates(EntityPlayer player, ChunkCoordinates hit, EnumFacing sideHit,
        ItemStack tool) {
        int range = GadgetUtils.getToolRange(tool);
        int radius = MathTool.floorToOdd(range);
        // @TODO(johnrowl) this is suspicious

        // return Column.centerAt(hit, (sideHit.getAxis().isVertical() ? player.getHorizontalFacing() :
        // sideHit).rotateY().getAxis(), radius);
        var facing = VectorTools.isAxisVertical(sideHit) ? VectorTools.getHorizontalFacingFromPlayer(player) : sideHit;

        return Column.centerAt(hit, VectorTools.rotateY(facing), radius);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

}
