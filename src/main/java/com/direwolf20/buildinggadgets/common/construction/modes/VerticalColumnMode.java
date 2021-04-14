package com.direwolf20.buildinggadgets.common.construction.modes;

import com.direwolf20.buildinggadgets.common.construction.ModeUseContext;
import com.direwolf20.buildinggadgets.common.construction.XYZ;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class VerticalColumnMode extends Mode {
    public VerticalColumnMode(boolean isExchanging) {
        super("vertical_column", isExchanging);
    }

    // @todo: clean up
    @Override
    List<BlockPos> collect(ModeUseContext context, PlayerEntity player, BlockPos start) {
        List<BlockPos> coordinates = new ArrayList<>();

        // If up or down, full height from start block
        int halfRange = context.getRange() / 2;

        if (context.getHitSide().getAxis() == Direction.Axis.Y) {
            // The exchanger handles the Y completely differently :sad: means more code
            if (this.isExchanging()) {
                Direction playerFacing = player.getDirection();
                for (int i = -halfRange; i <= halfRange; i++) {
                    coordinates.add(XYZ.extendPosSingle(i, start, playerFacing, playerFacing.getAxis()));
                }
            } else {
                for (int i = 0; i < context.getRange(); i++) {
                    coordinates.add(XYZ.extendPosSingle(i, start, context.getHitSide(), Direction.Axis.Y));
                }
            }
            // Else, half and half
        } else {
            for (int i = -halfRange; i <= halfRange; i++) {
                coordinates.add(XYZ.extendPosSingle(i, start, context.getHitSide(), Direction.Axis.Y));
            }
        }

        return coordinates;
    }

    /**
     * We need to modify where the offset is for this mode as when looking at any
     * face that isn't up or down, we need to push the offset back into the block
     * and ignore placeOnTop as this mode does the action by default.
     */
    @Override
    public BlockPos withOffset(BlockPos pos, Direction side, boolean placeOnTop) {
        return side.getAxis() == Direction.Axis.Y
            ? super.withOffset(pos, side, placeOnTop)
            : pos;
    }
}

