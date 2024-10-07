package com.direwolf20.buildinggadgets.common.items;

import com.direwolf20.buildinggadgets.common.tools.GadgetUtils;
import com.direwolf20.buildinggadgets.common.tools.UniqueItem;
import com.direwolf20.buildinggadgets.common.tools.WorldSave;
import com.google.common.collect.Multiset;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.direwolf20.buildinggadgets.common.util.ref.NBTKeys.*;

public interface ITemplate {

    @Nullable
    String getUUID(ItemStack stack);

    WorldSave getWorldSave(World world);

    default void setItemCountMap(ItemStack stack, Multiset<UniqueItem> tagMap) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        NBTTagList tagList = GadgetUtils.itemCountToNBT(tagMap);
        tagCompound.setTag("itemcountmap", tagList);
        stack.setTagCompound(tagCompound);
    }

    @Nonnull
    default Multiset<UniqueItem> getItemCountMap(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        Multiset<UniqueItem> tagMap = tagCompound == null ? null : GadgetUtils.nbtToItemCount((NBTTagList) tagCompound.getTag("itemcountmap"));
        if (tagMap == null)
            throw new IllegalArgumentException("ITemplate#getItemCountMap faild to retieve tag map from " + GadgetUtils.getStackErrorSuffix(stack));

        return tagMap;
    }

    default int getCopyCounter(ItemStack stack) {
        return GadgetUtils.getStackTag(stack).getInteger(TEMPLATE_COPY_COUNT);
    }

    default void setCopyCounter(ItemStack stack, int counter) {//TODO unused
        NBTTagCompound tagCompound = GadgetUtils.getStackTag(stack);
        tagCompound.setInteger(TEMPLATE_COPY_COUNT, counter);
        stack.setTagCompound(tagCompound);
    }

    default void incrementCopyCounter(ItemStack stack) {
        NBTTagCompound tagCompound = GadgetUtils.getStackTag(stack);
        tagCompound.setInteger(TEMPLATE_COPY_COUNT, tagCompound.getInteger(TEMPLATE_COPY_COUNT) + 1);
        stack.setTagCompound(tagCompound);
    }

    default void setStartPos(ItemStack stack, ChunkCoordinates startPos) {
        GadgetUtils.writePOSToNBT(stack, startPos, GADGET_START_POS);
    }

    @Nullable
    default ChunkCoordinates getStartPos(ItemStack stack) {
        return GadgetUtils.getPOSFromNBT(stack, GADGET_START_POS);
    }

    default void setEndPos(ItemStack stack, ChunkCoordinates startPos) {
        GadgetUtils.writePOSToNBT(stack, startPos, GADGET_END_POS);
    }

    @Nullable
    default ChunkCoordinates getEndPos(ItemStack stack) {
        return GadgetUtils.getPOSFromNBT(stack, GADGET_END_POS);
    }
}
