package com.direwolf20.buildinggadgets.common.building;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

import com.direwolf20.buildinggadgets.util.datatypes.BlockState;

/**
 * Abstract representation mapping from position to block state.
 * <p>
 * "abstract" means such block provider can be used for constant mapper (fill with certain type), or some sort of
 * complex structure. It does not specify a boundary and implementation should return {@code
 * Blocks.AIR.getDefaultState()} when the given position is out of boundary. All positions passed as parameter will be
 * translated by a specific vector which can be accessed by {@link #getTranslation()}.
 *
 * @param <T>
 */
public interface IBlockProvider<T extends IBlockProvider<T>> {

    /**
     * @param origin the new origin
     * @return A block provider with all calls to {@link #at(ChunkCoordinates)} translated by the parameter.
     * @implSpec {@code pos.add(this.getTranslation())} should be applied when accessing the current object.
     */
    IBlockProvider translate(ChunkCoordinates origin);

    /**
     * @return the translation used as translation of the parameter of {@link #at(ChunkCoordinates)}
     * @implSpec the value should remain constant in the whole life of the object
     */
    default ChunkCoordinates getTranslation() {
        return new ChunkCoordinates(0, 0, 0); // Origin equivalent in 1.7.10
    }

    /**
     * The parameter will be translated by {@link #getTranslation} before being used to read a block state.
     *
     * @return block that should be placed at the position
     * @implNote In most cases, {@code pos.add(this.getTranslation())} should be sufficient.
     */
    BlockState at(ChunkCoordinates pos);

    /**
     * Write the containing data into the given tag.
     */
    void serialize(NBTTagCompound tag);

    /**
     * @see #serialize(NBTTagCompound) - create a new {@link NBTTagCompound} instead of writing into an existing one.
     */
    default NBTTagCompound serialize() {
        NBTTagCompound tag = new NBTTagCompound();
        this.serialize(tag);
        return tag;
    }

    /**
     * Reads the data contained in the given tag and write them into a new object.
     *
     * @implSpec The returning object should have the same type. Neither a child nor a parent.
     */
    T deserialize(NBTTagCompound tag);
}
