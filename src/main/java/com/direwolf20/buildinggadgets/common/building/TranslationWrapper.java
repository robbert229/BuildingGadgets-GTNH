package com.direwolf20.buildinggadgets.common.building;

import com.direwolf20.buildinggadgets.util.datatypes.BlockState;
import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

/**
 * Wraps an {@link IBlockProvider} such that all access to the provider will be translated by the given amount as the
 * BlockPos passed into the constructor.
 */
public final class TranslationWrapper implements IBlockProvider {

    private final IBlockProvider provider;
    /**
     * Translation done by the current wrapper.
     */
    private final ChunkCoordinates translation;
    /**
     * Translation done by the current wrapper and its provider.
     */
    private final ChunkCoordinates accumulatedTranslation;

    public TranslationWrapper(IBlockProvider provider, ChunkCoordinates origin) {
        this.provider = provider;
        this.translation = origin;
        this.accumulatedTranslation = ChunkCoordinateUtils.add(provider.getTranslation(), origin);
    }

    @Override
    public IBlockProvider translate(ChunkCoordinates origin) {
        // Since provider is the same, just adding the two translation together would be sufficient
        return new TranslationWrapper(provider, ChunkCoordinateUtils.add(translation, origin));
    }

    /**
     * @return all translation done with the wrapper and its underlying block provider.
     */
    @Override
    public ChunkCoordinates getTranslation() {
        return accumulatedTranslation;
    }

    /**
     * @return the translation applied by the current wrapper
     */
    public ChunkCoordinates getAppliedTranslation() {
        return translation;
    }

    /**
     * The underlying block provider that was wrapped in the constructor.
     */
    public IBlockProvider getProvider() {
        return provider;
    }

    /**
     * Redirects the call to the wrapped IBlockProvider.
     */
    @Override
    public BlockState at(ChunkCoordinates pos) {
        return provider.at(ChunkCoordinateUtils.add(pos, translation));
    }

    @Override
    public void serialize(NBTTagCompound tag) {
        this.provider.serialize(tag);
    }

    @Override
    public TranslationWrapper deserialize(NBTTagCompound tag) {
        this.provider.deserialize(tag);
        return this;
    }

}