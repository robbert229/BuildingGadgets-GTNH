package com.direwolf20.buildinggadgets.common.building.placement;

import com.direwolf20.buildinggadgets.common.building.IBlockProvider;
import com.direwolf20.buildinggadgets.common.building.TranslationWrapper;
import com.direwolf20.buildinggadgets.common.tools.BlockState;
import com.direwolf20.buildinggadgets.common.tools.NBTTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;


/**
 * Immutable block provider that always return the same block state regardless of which position is requested.
 */
public class SingleTypeProvider implements IBlockProvider {

    private final BlockState state;

    /**
     * @param state value that {@link #at(ChunkCoordinates)} will return
     */
    public SingleTypeProvider(BlockState state) {
        this.state = state;
    }

    @Override
    public TranslationWrapper translate(ChunkCoordinates origin) {
        return new TranslationWrapper(this, origin);
    }

    /**
     * @return {@link #state}, which is initialized in the constructor, regardless of the parameter.
     */
    @Override
    public BlockState at(ChunkCoordinates pos) {
        return state;
    }

    public BlockState getBlockState() {
        return state;
    }

    @Override
    public void serialize(NBTTagCompound tag) {
        NBTTool.writeBlockToCompound(tag, state);
    }

    @Override
    public SingleTypeProvider deserialize(NBTTagCompound tag) {
        return new SingleTypeProvider(NBTTool.blockFromCompound(tag));
    }

}