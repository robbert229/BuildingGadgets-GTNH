package com.direwolf20.buildinggadgets.common.blocks.templatemanager;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.items.ITemplate;
import com.direwolf20.buildinggadgets.common.items.ModItems;
import com.direwolf20.buildinggadgets.common.items.Template;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetCopyPaste;
import com.direwolf20.buildinggadgets.common.network.PacketBlockMap;
import com.direwolf20.buildinggadgets.common.network.PacketHandler;
import com.direwolf20.buildinggadgets.common.tools.*;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;
import com.direwolf20.buildinggadgets.util.ref.NBTKeys;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.client.gui.GuiScreen.setClipboardString;

public class TemplateManagerCommands {
    private static final Set<Item> allowedItemsRight = Stream.of(Items.paper, ModItems.template).collect(Collectors.toSet());

    public static void loadTemplate(TemplateManagerContainer container, EntityPlayer player) {
        ItemStack itemStack0 = container.getSlot(0).getStack();
        ItemStack itemStack1 = container.getSlot(1).getStack();
        if (!(itemStack0.getItem() instanceof ITemplate) || !(allowedItemsRight.contains(itemStack1.getItem()))) {
            return;
        }
        ITemplate template = (ITemplate) itemStack0.getItem();
        if (itemStack1.getItem().equals(Items.paper)) {
            return;
        }

        World world = player.worldObj;

        ChunkCoordinates startPos = template.getStartPos(itemStack1);
        ChunkCoordinates endPos = template.getEndPos(itemStack1);
        Multiset<UniqueItem> tagMap = template.getItemCountMap(itemStack1);
        String UUIDTemplate = ModItems.template.getUUID(itemStack1);
        if (UUIDTemplate == null) {
            return;
        }

        WorldSave worldSave = WorldSave.getWorldSave(world);
        WorldSave templateWorldSave = WorldSave.getTemplateWorldSave(world);
        NBTTagCompound tagCompound;

        template.setStartPos(itemStack0, startPos);
        template.setEndPos(itemStack0, endPos);
        template.setItemCountMap(itemStack0, tagMap);
        String UUID = template.getUUID(itemStack0);

        if (UUID == null) {
            return;
        }

        NBTTagCompound templateTagCompound = templateWorldSave.getCompoundFromUUID(UUIDTemplate);
        tagCompound = (NBTTagCompound) templateTagCompound.copy();
        template.incrementCopyCounter(itemStack0);
        tagCompound.setInteger(NBTKeys.TEMPLATE_COPY_COUNT, template.getCopyCounter(itemStack0));
        tagCompound.setString("UUID", template.getUUID(itemStack0));
        tagCompound.setString("owner", player.getCommandSenderName());
        if (template.equals(ModItems.gadgetCopyPaste)) {
            worldSave.addToMap(UUID, tagCompound);
        } else {
            templateWorldSave.addToMap(UUID, tagCompound);
            Template.setName(itemStack0, Template.getName(itemStack1));
        }
        container.putStackInSlot(0, itemStack0);
        PacketHandler.INSTANCE.sendTo(new PacketBlockMap(tagCompound), (EntityPlayerMP) player);
    }

    public static void saveTemplate(TemplateManagerContainer container, EntityPlayer player, String templateName) {
        ItemStack itemStack0 = container.getSlot(0).getStack();
        ItemStack itemStack1 = container.getSlot(1).getStack();

        if (itemStack0 == null && itemStack1.getItem() instanceof Template && !templateName.isEmpty()) {
            Template.setName(itemStack1, templateName);
            container.putStackInSlot(1, itemStack1);
            return;
        }

        if (!(itemStack0.getItem() instanceof ITemplate) || !(allowedItemsRight.contains(itemStack1.getItem()))) {
            return;
        }
        ITemplate template = (ITemplate) itemStack0.getItem();
        World world = player.worldObj;
        ItemStack templateStack;
        if (itemStack1.getItem().equals(Items.paper)) {
            templateStack = new ItemStack(ModItems.template, 1);
            container.putStackInSlot(1, templateStack);
        }
        if (!(container.getSlot(1).getStack().getItem().equals(ModItems.template))) return;
        templateStack = container.getSlot(1).getStack();
        WorldSave worldSave = WorldSave.getWorldSave(world);
        WorldSave templateWorldSave = WorldSave.getTemplateWorldSave(world);
        NBTTagCompound templateTagCompound;

        String UUID = template.getUUID(itemStack0);
        String UUIDTemplate = ModItems.template.getUUID(templateStack);
        if (UUID == null) return;
        if (UUIDTemplate == null) return;

        boolean isTool = itemStack0.getItem().equals(ModItems.gadgetCopyPaste);
        NBTTagCompound tagCompound = isTool ? worldSave.getCompoundFromUUID(UUID) : templateWorldSave.getCompoundFromUUID(UUID);
        templateTagCompound = (NBTTagCompound) tagCompound.copy();
        template.incrementCopyCounter(templateStack);
        templateTagCompound.setInteger(NBTKeys.TEMPLATE_COPY_COUNT, template.getCopyCounter(templateStack));
        templateTagCompound.setString("UUID", ModItems.template.getUUID(templateStack));

        templateWorldSave.addToMap(UUIDTemplate, templateTagCompound);
        ChunkCoordinates startPos = template.getStartPos(itemStack0);
        ChunkCoordinates endPos = template.getEndPos(itemStack0);
        Multiset<UniqueItem> tagMap = template.getItemCountMap(itemStack0);
        template.setStartPos(templateStack, startPos);
        template.setEndPos(templateStack, endPos);
        template.setItemCountMap(templateStack, tagMap);
        if (isTool) {
            Template.setName(templateStack, templateName);
        } else {
            if (templateName.isEmpty()) {
                Template.setName(templateStack, Template.getName(itemStack0));
            } else {
                Template.setName(templateStack, templateName);
            }
        }
        container.putStackInSlot(1, templateStack);
        PacketHandler.INSTANCE.sendTo(new PacketBlockMap(templateTagCompound), (EntityPlayerMP) player);
    }

    public static void pasteTemplate(TemplateManagerContainer container, EntityPlayer player, NBTTagCompound sentTagCompound, String templateName) {
        ItemStack itemStack1 = container.getSlot(1).getStack();

        if (!(allowedItemsRight.contains(itemStack1.getItem()))) {
            return;
        }

        World world = player.worldObj;
        ItemStack templateStack;
        if (itemStack1.getItem().equals(Items.paper)) {
            templateStack = new ItemStack(ModItems.template, 1);
            container.putStackInSlot(1, templateStack);
        }

        if (!(container.getSlot(1).getStack().getItem().equals(ModItems.template))) {
            return;
        }
        templateStack = container.getSlot(1).getStack();

        WorldSave templateWorldSave = WorldSave.getTemplateWorldSave(world);
        Template template = ModItems.template;
        String UUIDTemplate = template.getUUID(templateStack);
        if (UUIDTemplate == null) {
            return;
        }

        NBTTagCompound templateTagCompound;

        templateTagCompound = (NBTTagCompound) sentTagCompound.copy();
        ChunkCoordinates startPos = GadgetUtils.getPOSFromNBT(templateTagCompound, NBTKeys.GADGET_START_POS);
        ChunkCoordinates endPos = GadgetUtils.getPOSFromNBT(templateTagCompound, NBTKeys.GADGET_END_POS);
        template.incrementCopyCounter(templateStack);
        templateTagCompound.setInteger(NBTKeys.TEMPLATE_COPY_COUNT, template.getCopyCounter(templateStack));
        templateTagCompound.setString("UUID", template.getUUID(templateStack));
        //GadgetUtils.writePOSToNBT(templateTagCompound, startPos, GADGET_START_POS, 0);
        //GadgetUtils.writePOSToNBT(templateTagCompound, endPos, GADGET_END_POS, 0);
        //Map<UniqueItem, Integer> tagMap = GadgetUtils.nbtToItemCount((NBTTagList) templateTagCompound.getTag("itemcountmap"));
        //templateTagCompound.removeTag("itemcountmap");

        NBTTagList MapIntStateTag = (NBTTagList) templateTagCompound.getTag("mapIntState");

        BlockMapIntState mapIntState = new BlockMapIntState();
        mapIntState.getIntStateMapFromNBT(MapIntStateTag);
        mapIntState.makeStackMapFromStateMap(player);
        templateTagCompound.setTag("mapIntStack", mapIntState.putIntStackMapIntoNBT());
        templateTagCompound.setString("owner", player.getCommandSenderName());

        Multiset<UniqueItem> itemCountMap = HashMultiset.create();
        Map<BlockState, UniqueItem> intStackMap = mapIntState.intStackMap;
        List<BlockMap> blockMapList = GadgetCopyPaste.getBlockMapList(templateTagCompound);
        for (BlockMap blockMap : blockMapList) {
            UniqueItem uniqueItem = intStackMap.get(blockMap.state);
            if (!(uniqueItem == null)) {
                List<ItemStack> drops = blockMap.state.getBlock().getDrops(world, 0, 0, 0, blockMap.state.getMetadata(), 0);

                int neededItems = 0;
                for (ItemStack drop : drops) {
                    if (drop.getItem().equals(uniqueItem.item)) {
                        neededItems++;
                    }
                }
                if (neededItems == 0) {
                    neededItems = 1;
                }
                if (uniqueItem.item != null) {
                    itemCountMap.add(uniqueItem,neededItems);
                }
            }
        }

        templateWorldSave.addToMap(UUIDTemplate, templateTagCompound);


        template.setStartPos(templateStack, startPos);
        template.setEndPos(templateStack, endPos);
        //template.setItemCountMap(templateStack, tagMap);
        template.setItemCountMap(templateStack, itemCountMap);
        Template.setName(templateStack, templateName);
        container.putStackInSlot(1, templateStack);
        PacketHandler.INSTANCE.sendTo(new PacketBlockMap(templateTagCompound), (EntityPlayerMP) player);
    }

    public static void copyTemplate(TemplateManagerContainer container) {
        ItemStack itemStack0 = container.getSlot(0).getStack();
        if (itemStack0 != null && itemStack0.getItem() instanceof ITemplate) {
            NBTTagCompound tagCompound = PasteToolBufferBuilder.getTagFromUUID(ModItems.gadgetCopyPaste.getUUID(itemStack0));
            if (tagCompound == null) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(ChatFormatting.RED + new ChatComponentTranslation("message.gadget.copyfailed").getUnformattedTextForChat())
                );

                return;
            }

            NBTTagCompound newCompound = new NBTTagCompound();
            newCompound.setIntArray("stateIntArray", tagCompound.getIntArray("stateIntArray"));
            newCompound.setIntArray("posIntArray", tagCompound.getIntArray("posIntArray"));
            newCompound.setTag("mapIntState", tagCompound.getTag("mapIntState"));
            GadgetUtils.writePOSToNBT(newCompound, GadgetUtils.getPOSFromNBT(tagCompound, NBTKeys.GADGET_START_POS), NBTKeys.GADGET_START_POS, 0);
            GadgetUtils.writePOSToNBT(newCompound, GadgetUtils.getPOSFromNBT(tagCompound, NBTKeys.GADGET_END_POS), NBTKeys.GADGET_END_POS, 0);

            try {
                if (GadgetUtils.getPasteStream(newCompound, tagCompound.getString("name")) != null) {
                    String jsonTag = newCompound.toString();
                    setClipboardString(jsonTag);
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                            new ChatComponentText(ChatFormatting.AQUA + new ChatComponentTranslation("message.gadget.copysuccess").getUnformattedText())
                    );
                } else {
                    pasteIsTooLarge();
                }
            } catch (IOException e) {
                BuildingGadgets.LOG.error("Failed to evaluate template network size. Template will be considered too large.", e);
                pasteIsTooLarge();
            }
        }
    }

    private static void pasteIsTooLarge() {
        Minecraft.getMinecraft().thePlayer.addChatMessage(
                new ChatComponentText(ChatFormatting.RED + new ChatComponentTranslation("message.gadget.pastetoobig").getUnformattedText())
        );
    }
}