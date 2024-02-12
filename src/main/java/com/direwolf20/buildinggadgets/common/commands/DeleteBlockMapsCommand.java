package com.direwolf20.buildinggadgets.common.commands;

import net.minecraft.nbt.NBTTagCompound;

import com.mojang.realmsclient.gui.ChatFormatting;

public class DeleteBlockMapsCommand extends CommandAlterBlockMaps {

    public DeleteBlockMapsCommand() {
        super("DeleteBlockMaps", true);
    }

    @Override
    protected String getActionFeedback(NBTTagCompound tagCompound) {
        return ChatFormatting.RED + "Deleted stored map for "
            + tagCompound.getString("owner")
            + " with UUID:"
            + tagCompound.getString("UUID");
    }

    @Override
    protected String getCompletionFeedback(int counter) {
        return ChatFormatting.WHITE + "Deleted " + counter + " blockmaps in world data.";
    }
}
