package com.direwolf20.buildinggadgets.common.building;

import net.minecraft.init.Blocks;

import com.direwolf20.buildinggadgets.common.building.placement.SingleTypeProvider;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;

public final class CapabilityBlockProvider {

    public static IBlockProvider BLOCK_PROVIDER = null;

    static IBlockProvider DEFAULT_AIR_PROVIDER = new SingleTypeProvider(new BlockState(Blocks.air, 0));

    private CapabilityBlockProvider() {}
}
