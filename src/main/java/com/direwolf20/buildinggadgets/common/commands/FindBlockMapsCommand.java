package com.direwolf20.buildinggadgets.common.commands;

import net.minecraft.nbt.NBTTagCompound;

import com.mojang.realmsclient.gui.ChatFormatting;

public class FindBlockMapsCommand extends CommandAlterBlockMaps {

    public FindBlockMapsCommand() {
        super("FindBlockMaps", false);
    }

    @Override
    protected String getActionFeedback(NBTTagCompound tagCompound) {
        return ChatFormatting.WHITE + tagCompound.getString("owner") + ":" + tagCompound.getString("UUID");
    }

    @Override
    protected String getCompletionFeedback(int counter) {
        return ChatFormatting.WHITE + "Found " + counter + " blockmaps in world data.";
    }
}
