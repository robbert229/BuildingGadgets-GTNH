package com.direwolf20.buildinggadgets.common.building;

import java.util.function.BiPredicate;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.util.datatypes.BlockState;

/**
 * Creates a validator for determining whether an build attempt is valid or not. The created validator is bound to a
 * fixed set of parameters such as {@link World},
 */
@FunctionalInterface
public interface IValidatorFactory {

    /**
     * @param world   the world that blocks will be placed in
     * @param tool    the gadget item used to activate the process
     * @param player  the player who activated the process
     * @param initial position selected by the player
     * @return BiPredicate where the first parameter is the attempt position, second parameter is the block that will be
     *         placed there.
     */
    BiPredicate<ChunkCoordinates, BlockState> createValidatorFor(World world, ItemStack tool, EntityPlayer player,
        ChunkCoordinates initial);
}
