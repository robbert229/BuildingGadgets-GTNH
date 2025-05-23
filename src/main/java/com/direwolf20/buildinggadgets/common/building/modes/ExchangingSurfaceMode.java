package com.direwolf20.buildinggadgets.common.building.modes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.building.IPlacementSequence;
import com.direwolf20.buildinggadgets.common.building.IValidatorFactory;
import com.direwolf20.buildinggadgets.common.building.Region;
import com.direwolf20.buildinggadgets.common.building.placement.ConnectedSurface;
import com.direwolf20.buildinggadgets.common.building.placement.Surface;
import com.direwolf20.buildinggadgets.common.building.placement.Wall;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import com.direwolf20.buildinggadgets.common.tools.GadgetUtils;

/**
 * Surface mode for Exchanging Gadget.
 * <p>
 * Selects blocks that is same as the selected block. Reference region and searching region are the same
 * if compared to {@link BuildingSurfaceMode}.
 *
 * @see BuildingSurfaceMode
 */
public class ExchangingSurfaceMode extends AbstractMode {

    private static final ResourceLocation NAME = new ResourceLocation(BuildingGadgets.MODID, "surface");

    public ExchangingSurfaceMode(IValidatorFactory validatorFactory) {
        super(validatorFactory);
    }

    @Override
    public IPlacementSequence computeCoordinates(EntityPlayer player, ChunkCoordinates hit, EnumFacing sideHit,
        ItemStack tool) {
        int range = GadgetUtils.getToolRange(tool) / 2;
        boolean fuzzy = GadgetGeneric.getFuzzy(tool);
        Region region = Wall.clickedSide(hit, sideHit, range)
            .getBoundingBox();
        if (GadgetGeneric.getConnectedArea(tool))
            return ConnectedSurface.create(player.worldObj, region, pos -> pos, hit, sideHit, fuzzy);
        return Surface.create(player.worldObj, hit, region, pos -> pos, fuzzy);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

}
