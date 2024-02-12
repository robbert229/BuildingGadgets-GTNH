package com.direwolf20.buildinggadgets.common.commands;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.network.PacketBlockMap;
import com.direwolf20.buildinggadgets.common.network.PacketHandler;
import com.direwolf20.buildinggadgets.common.tools.WorldSave;
import com.google.common.collect.Lists;

public abstract class CommandAlterBlockMaps extends CommandBase {

    private final String name;
    private final List<String> aliases;
    private final boolean removeData;

    public CommandAlterBlockMaps(String name, boolean removeData) {
        this.name = name;
        this.removeData = removeData;
        aliases = Lists.newArrayList(BuildingGadgets.MODID, name, name.toLowerCase());
    }

    @Override
    public String getCommandName() {
        return name;
    }

    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return name + " <player>";
    }

    @Override
    public List<String> getCommandAliases() {
        return aliases;
    }

    abstract protected String getActionFeedback(NBTTagCompound tagCompound);

    abstract protected String getCompletionFeedback(int counter);

    @Override
    public void processCommand(final ICommandSender sender, String[] args) throws CommandException {
        if (args.length > 0) {
            if (!(sender.canCommandSenderUseCommand(4, this.getCommandName()))) {
                sender.addChatMessage(
                    new ChatComponentText(EnumChatFormatting.RED + "Only OPS can use this command with an argument."));

                return;
            }
        }

        WorldSave worldSave = WorldSave.getWorldSave(sender.getEntityWorld());
        Map<String, NBTTagCompound> tagMap = worldSave.getTagMap();
        Map<String, NBTTagCompound> newMap = new HashMap<String, NBTTagCompound>(tagMap);
        String searchName = (args.length == 0) ? sender.getCommandSenderName() : args[0];
        int counter = 0;
        for (Map.Entry<String, NBTTagCompound> entry : tagMap.entrySet()) {
            NBTTagCompound tagCompound = entry.getValue();
            if (tagCompound.getString("owner")
                .equals(searchName) || searchName.equals("*")) {
                sender.addChatMessage(new ChatComponentText(getActionFeedback(tagCompound)));
                counter++;
                if (removeData) newMap.remove(entry.getKey());
            }
        }
        if (removeData && counter > 0) {
            worldSave.setTagMap(newMap);
            worldSave.markForSaving();
            if (searchName.equals(sender.getCommandSenderName())) {
                PacketHandler.INSTANCE.sendTo(new PacketBlockMap(new NBTTagCompound()), (EntityPlayerMP) sender);
                // System.out.println("Sending BlockMap Packet");
            }
        }

        sender.addChatMessage(new ChatComponentText(getCompletionFeedback(counter)));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
