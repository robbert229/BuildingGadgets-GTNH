package com.direwolf20.buildinggadgets.common.building.placement;

import com.direwolf20.buildinggadgets.common.building.IBlockProvider;
import com.direwolf20.buildinggadgets.common.building.TranslationWrapper;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ChunkCoordinates;

/**
 * Immutable block provider that always return the same block state regardless of which position is requested.
 */
public class SingleTypeProvider implements IBlockProvider {

    private final Block state;

    /**
     * @param state value that {@link #at(ChunkCoordinates)} will return
     */
    public SingleTypeProvider(Block state) {
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
    public Block at(ChunkCoordinates pos) {
        return state;
    }

    public Block getBlockState() {
        return state;
    }

    @Override
    public void serialize(NBTTagCompound tag) {
//        NBTUtil.writeBlockState(tag, state);

    }

    @Override
    public SingleTypeProvider deserialize(NBTTagCompound tag) {
        return new SingleTypeProvider(NBTUtil.readBlockState(tag));
    }

}
