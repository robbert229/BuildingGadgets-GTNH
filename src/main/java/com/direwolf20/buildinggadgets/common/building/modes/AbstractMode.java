package com.direwolf20.buildinggadgets.common.building.modes;

import java.util.function.BiPredicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.common.building.Context;
import com.direwolf20.buildinggadgets.common.building.IBuildingMode;
import com.direwolf20.buildinggadgets.common.building.IValidatorFactory;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;

/**
 * Base class for Building Gadget's native mode implementations to allow reuse validator implementation
 * All ':' in the translation key with '.'.
 */
public abstract class AbstractMode implements IBuildingMode {

    protected final IValidatorFactory validatorFactory;
    private final String translationKey;

    public AbstractMode(IValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
        this.translationKey = "modes." + getRegistryName().toString()
            .replace(':', '.');
    }

    @Override
    public BiPredicate<ChunkCoordinates, BlockState> createValidatorFor(World world, ItemStack tool,
        EntityPlayer player, ChunkCoordinates initial) {
        return validatorFactory.createValidatorFor(world, tool, player, initial);
    }

    @Override
    public Context createExecutionContext(EntityPlayer player, ChunkCoordinates hit, EnumFacing sideHit,
        ItemStack tool) {
        return new Context(computeCoordinates(player, hit, sideHit, tool), getBlockProvider(tool), validatorFactory);
    }

    @Override
    public String getTranslationKey() {
        return translationKey;
    }

}
