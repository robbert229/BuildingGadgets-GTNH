package com.direwolf20.buildinggadgets.common.items.gadgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.BlockSnapshot;

import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.GadgetsConfig.GadgetCopyPasteConfig;
import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.GeneralConfig;
import com.direwolf20.buildinggadgets.client.events.EventTooltip;
import com.direwolf20.buildinggadgets.client.gui.CopyGUI;
import com.direwolf20.buildinggadgets.client.gui.CopyPasteGUI;
import com.direwolf20.buildinggadgets.client.gui.PasteGUI;
import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlock;
import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlockTileEntity;
import com.direwolf20.buildinggadgets.common.blocks.EffectBlock;
import com.direwolf20.buildinggadgets.common.entities.BlockBuildEntity;
import com.direwolf20.buildinggadgets.common.items.ITemplate;
import com.direwolf20.buildinggadgets.common.items.ModItems;
import com.direwolf20.buildinggadgets.common.network.PacketBlockMap;
import com.direwolf20.buildinggadgets.common.network.PacketHandler;
import com.direwolf20.buildinggadgets.common.tools.*;
import com.direwolf20.buildinggadgets.util.NBTTool;
import com.direwolf20.buildinggadgets.util.VectorTools;
import com.direwolf20.buildinggadgets.util.WorldUtils;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;
import com.direwolf20.buildinggadgets.util.ref.NBTKeys;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.mojang.realmsclient.gui.ChatFormatting;

public class GadgetCopyPaste extends GadgetGeneric implements ITemplate {

    public enum ToolMode {

        Copy,
        Paste;

        private static ToolMode[] vals = values();

        public ToolMode next() {
            return vals[(this.ordinal() + 1) % vals.length];
        }
    }

    public GadgetCopyPaste() {
        super("copypastetool");
        setMaxDamage(GadgetCopyPasteConfig.durabilityCopyPaste);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return GeneralConfig.poweredByFE ? 0 : GadgetCopyPasteConfig.durabilityCopyPaste;
    }

    @Override
    public int getEnergyCost(ItemStack tool) {
        return GadgetCopyPasteConfig.energyCostCopyPaste;
    }

    @Override
    public int getDamageCost(ItemStack tool) {
        return GadgetCopyPasteConfig.damageCostCopyPaste;
    }

    @Override
    public void renderOverlay(RenderWorldLastEvent evt, EntityPlayer player, ItemStack stack) {
        ToolRenders.renderPasteOverlay(evt, player, stack);
    }

    @Override
    public ModularScreen getShortcutMenuGUI(ItemStack itemStack, boolean temporarilyEnabled) {
        return new CopyPasteGUI(itemStack, temporarilyEnabled);
    }

    private static void setAnchor(ItemStack stack, ChunkCoordinates anchorPos) {
        GadgetUtils.writePOSToNBT(stack, anchorPos, "anchor");
    }

    public static void setX(ItemStack stack, int horz) {
        GadgetUtils.writeIntToNBT(stack, horz, "X");
    }

    public static void setY(ItemStack stack, int vert) {
        GadgetUtils.writeIntToNBT(stack, vert, "Y");
    }

    public static void setZ(ItemStack stack, int depth) {
        GadgetUtils.writeIntToNBT(stack, depth, "Z");
    }

    public static int getX(ItemStack stack) {
        return GadgetUtils.getIntFromNBT(stack, "X");
    }

    public static int getY(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) return 1;
        if (!tagCompound.hasKey("Y")) return 1;
        return tagCompound.getInteger("Y");
    }

    public static int getZ(ItemStack stack) {
        return GadgetUtils.getIntFromNBT(stack, "Z");
    }

    public static ChunkCoordinates getAnchor(ItemStack stack) {
        return GadgetUtils.getPOSFromNBT(stack, NBTKeys.GADGET_ANCHOR);
    }

    @Override
    public WorldSave getWorldSave(World world) {
        return WorldSave.getWorldSave(world);
    }

    @Override
    @Nullable
    public String getUUID(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            return null;
        }
        String uuid = tagCompound.getString(NBTKeys.GADGET_UUID);
        if (uuid.isEmpty()) {
            if (getStartPos(stack) == null && getEndPos(stack) == null) {
                return null;
            }
            UUID uid = UUID.randomUUID();
            tagCompound.setString(NBTKeys.GADGET_UUID, uid.toString());
            stack.setTagCompound(tagCompound);
            uuid = uid.toString();
        }
        return uuid;
    }

    public static String getOwner(ItemStack stack) {// TODO unused
        return GadgetUtils.getStackTag(stack)
            .getString("owner");
    }

    public static void setOwner(ItemStack stack, String owner) {// TODO unused
        NBTTagCompound tagCompound = GadgetUtils.getStackTag(stack);
        tagCompound.setString("owner", owner);
        stack.setTagCompound(tagCompound);
    }

    private static void setLastBuild(ItemStack stack, ChunkCoordinates anchorPos, Integer dim) {
        GadgetUtils.writePOSToNBT(stack, anchorPos, "lastBuild", dim);
    }

    private static ChunkCoordinates getLastBuild(ItemStack stack) {
        return GadgetUtils.getPOSFromNBT(stack, "lastBuild");
    }

    private static Integer getLastBuildDim(ItemStack stack) {
        return GadgetUtils.getDIMFromNBT(stack, "lastBuild");
    }

    public static List<BlockMap> getBlockMapList(@Nullable NBTTagCompound tagCompound) {
        return getBlockMapList(tagCompound, GadgetUtils.getPOSFromNBT(tagCompound, NBTKeys.GADGET_START_POS));
    }

    private static int[] getTagIntArrayFromTagCompound(NBTTagCompound tagCompound, String key) {
        var tag = tagCompound.getTag(key);
        if (tag == null) {
            return new int[] {};
        }

        // we intentionally don't do a null check here because we want to through an exception when a non array is in an
        // array's location.
        return ((NBTTagIntArray) tag).func_150302_c();
    }

    private static List<BlockMap> getBlockMapList(@Nullable NBTTagCompound tagCompound, ChunkCoordinates startBlock) {
        List<BlockMap> blockMap = new ArrayList<BlockMap>();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }

        NBTTagList mapIntStateTag = (NBTTagList) tagCompound.getTag("mapIntState");
        if (mapIntStateTag == null) {
            mapIntStateTag = new NBTTagList();
        }

        BlockMapIntState mapIntState = new BlockMapIntState();
        mapIntState.getIntStateMapFromNBT(mapIntStateTag);

        var posIntArray = getTagIntArrayFromTagCompound(tagCompound, NBTKeys.GADGET_POS_INT_ARRAY);
        var stateIntArray = getTagIntArrayFromTagCompound(tagCompound, NBTKeys.GADGET_STATE_INT_ARRAY);
        for (int i = 0; i < posIntArray.length; i++) {
            int p = posIntArray[i];
            ChunkCoordinates pos = GadgetUtils.relIntToPos(startBlock, p);
            short IntState = (short) stateIntArray[i];
            var blockState = mapIntState.getStateFromSlot(IntState);
            if (blockState == null) {
                continue;
            }

            blockMap.add(
                new BlockMap(
                    pos,
                    blockState,
                    (byte) ((p & 0xff0000) >> 16),
                    (byte) ((p & 0x00ff00) >> 8),
                    (byte) (p & 0x0000ff)));
        }
        return blockMap;
    }

    public static BlockMapIntState getBlockMapIntState(@Nullable NBTTagCompound tagCompound) {
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        NBTTagList MapIntStateTag = (NBTTagList) tagCompound.getTag("mapIntState");
        if (MapIntStateTag == null) {
            MapIntStateTag = new NBTTagList();
        }
        NBTTagList MapIntStackTag = (NBTTagList) tagCompound.getTag("mapIntStack");
        if (MapIntStackTag == null) {
            MapIntStackTag = new NBTTagList();
        }
        BlockMapIntState MapIntState = new BlockMapIntState();
        MapIntState.getIntStateMapFromNBT(MapIntStateTag);
        MapIntState.getIntStackMapFromNBT(MapIntStackTag);
        return MapIntState;
    }

    private static void setToolMode(ItemStack stack, ToolMode mode) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        tagCompound.setString("mode", mode.name());
        stack.setTagCompound(tagCompound);
    }

    public static ToolMode getToolMode(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        ToolMode mode = ToolMode.Copy;
        if (tagCompound == null) {
            setToolMode(stack, mode);
            return mode;
        }
        try {
            mode = ToolMode.valueOf(tagCompound.getString("mode"));
        } catch (Exception e) {
            setToolMode(stack, mode);
        }
        return mode;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        super.addInformation(stack, player, list, b);

        list.add(
            EnumChatFormatting.AQUA + StatCollector.translateToLocal("tooltip.gadget.mode")
                + ": "
                + getToolMode(stack));
        addInformationRayTraceFluid(list, stack);
        addEnergyInformation(list, stack);
        EventTooltip.addTemplatePadding(stack, list);
    }

    public void setMode(ItemStack heldItem, int modeInt) {
        // Called when we specify a mode with the radial menu
        ToolMode mode = ToolMode.values()[modeInt];
        setToolMode(heldItem, mode);
    }

    private void resetArea(ItemStack stack, World world, EntityPlayer player) {
        setStartPos(stack, null);
        setEndPos(stack, null);
        player.addChatMessage(
            new ChatComponentText(
                ChatFormatting.AQUA + new ChatComponentTranslation("message.gadget.areareset").getUnformattedText()));
    }

    /**
     * tryCopyBlocks tries to copy the blocks but only if the start and end positions have been set.
     */
    private static void tryCopyBlocks(ItemStack stack, EntityPlayer player, World world, ChunkCoordinates startPos,
        ChunkCoordinates endPos) {
        if (startPos == null || endPos == null) {
            return;
        }

        copyBlocks(stack, player, world, startPos, endPos);
    }

    private ItemStack onItemRightClickInPasteMode(ItemStack stack, World world, EntityPlayer player,
        ChunkCoordinates lookingAt) {
        if (player.isSneaking()) {
            if (world.isRemote) {
                ClientGUI.open(PasteGUI.createGUI());
            }

            return stack;
        }

        if (world.isRemote) {
            ToolRenders.updateInventoryCache();
            return stack;
        }

        if (getAnchor(stack) == null) {
            if (lookingAt == null) {
                return stack;
            }

            buildBlockMap(world, lookingAt, stack, player);
        } else {
            ChunkCoordinates startPos = getAnchor(stack);
            buildBlockMap(world, startPos, stack, player);
        }

        return stack;
    }

    private ItemStack onItemRightClickInCopyMode(ItemStack stack, World world, EntityPlayer player,
        ChunkCoordinates lookingAt) {
        // if the user sneak right clicks while looking at nothing then open up the copy area gui.
        if (isSneakClickOnAir(player, lookingAt)) {
            if (world.isRemote) {
                ClientGUI.open(CopyGUI.createGUI());
            }

            return stack;
        }

        // if the user sneak right clicks while looking at a block then we attempt to set that block as the remote
        // inventory, or if it isn't an inventory then we know we need to set it as the end location.
        if (isSneakClickOnBlock(player, lookingAt)) {
            if (!world.isRemote) {
                // otherwise set the end position.
                var oldEndPos = getEndPos(stack);
                setEndPos(stack, lookingAt);

                // only try to copy the blocks if the values haven't changed.
                if (oldEndPos != null && oldEndPos.equals(lookingAt)) {
                    tryCopyBlocks(stack, player, world, getStartPos(stack), getEndPos(stack));
                }
            }

            return stack;
        }

        // if the user right clicks while looking at a block then try to set the start location.
        if (isNormalClickOnBlock(player, lookingAt)) {
            if (!world.isRemote) {
                var oldStartPos = getStartPos(stack);
                setStartPos(stack, lookingAt);

                // only try to copy the blocks if the values haven't changed.
                if (oldStartPos != null && oldStartPos.equals(lookingAt)) {
                    tryCopyBlocks(stack, player, world, lookingAt, getEndPos(stack));
                }
            }

            return stack;
        }

        return stack;
    }

    private static boolean isSneakClickOnInventory(World world, EntityPlayer player, ChunkCoordinates lookingAt) {
        return isSneakClickOnBlock(player, lookingAt)
            && GadgetUtils.isRemoteInventory(player, lookingAt, player.dimension, world);
    }

    private boolean isNormalClickOnBlock(EntityPlayer player, ChunkCoordinates lookingAt) {
        return lookingAt != null && !player.isSneaking();
    }

    private static boolean isSneakClickOnBlock(EntityPlayer player, ChunkCoordinates lookingAt) {
        return lookingAt != null && player.isSneaking();
    }

    private static boolean isSneakClickOnAir(EntityPlayer player, ChunkCoordinates lookingAt) {
        return lookingAt == null && player.isSneaking();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        ChunkCoordinates lookingAt = VectorTools.getPosLookingAt(player, stack);

        // if the user is sneak clicking on an inventory then attempt to set that as the remote inventory.
        if (isSneakClickOnInventory(world, player, lookingAt)) {
            if (!world.isRemote) {
                GadgetUtils.setRemoteInventory(player, stack, lookingAt, player.dimension, world);
                return stack;
            }

            return stack;
        }

        // if the user is in copy mode
        if (getToolMode(stack) == ToolMode.Copy) {
            return this.onItemRightClickInCopyMode(stack, world, player, lookingAt);
        }

        // if the user is in paste mode
        if (getToolMode(stack) == ToolMode.Paste) {
            return this.onItemRightClickInPasteMode(stack, world, player, lookingAt);
        }

        return stack;
    }

    //
    // public static void rotateOrMirrorBlocks(ItemStack stack, EntityPlayer player, PacketRotateMirror.Operation
    // operation) {
    // if (!(getToolMode(stack) == ToolMode.Paste)) return;
    // if (player.world.isRemote) {
    // return;
    // }
    // GadgetCopyPaste tool = ModItems.gadgetCopyPaste;
    // List<BlockMap> blockMapList = new ArrayList<BlockMap>();
    // WorldSave worldSave = WorldSave.getWorldSave(player.world);
    // NBTTagCompound tagCompound = worldSave.getCompoundFromUUID(tool.getUUID(stack));
    // ChunkCoordinates startPos = tool.getStartPos(stack);
    // if (startPos == null) return;
    // blockMapList = getBlockMapList(tagCompound);
    // List<Integer> posIntArrayList = new ArrayList<Integer>();
    // List<Integer> stateIntArrayList = new ArrayList<Integer>();
    // BlockMapIntState blockMapIntState = new BlockMapIntState();
    //
    // for (BlockMap blockMap : blockMapList) {
    // ChunkCoordinates tempPos = blockMap.pos;
    //
    // int px = (tempPos.getX() - startPos.getX());
    // int pz = (tempPos.getZ() - startPos.getZ());
    // int nx, nz;
    // IBlockState alteredState = GadgetUtils.rotateOrMirrorBlock(player, operation, blockMap.state);
    // if (operation == PacketRotateMirror.Operation.MIRROR) {
    // if (player.getHorizontalFacing().getAxis() == Axis.X) {
    // nx = px;
    // nz = -pz;
    // } else {
    // nx = -px;
    // nz = pz;
    // }
    // } else {
    // nx = -pz;
    // nz = px;
    // }
    // ChunkCoordinates newPos = new ChunkCoordinates(startPos.getX() + nx, tempPos.getY(), startPos.getZ() + nz);
    // posIntArrayList.add(GadgetUtils.relPosToInt(startPos, newPos));
    // blockMapIntState.addToMap(alteredState);
    // stateIntArrayList.add((int) blockMapIntState.findSlot(alteredState));
    // UniqueItem uniqueItem = BlockMapIntState.blockStateToUniqueItem(alteredState, player, tempPos);
    // blockMapIntState.addToStackMap(uniqueItem, alteredState);
    // }
    // int[] posIntArray = posIntArrayList.stream().mapToInt(i -> i).toArray();
    // int[] stateIntArray = stateIntArrayList.stream().mapToInt(i -> i).toArray();
    // tagCompound.setTag("mapIntState", blockMapIntState.putIntStateMapIntoNBT());
    // tagCompound.setTag("mapIntStack", blockMapIntState.putIntStackMapIntoNBT());
    // tagCompound.setIntArray("posIntArray", posIntArray);
    // tagCompound.setIntArray("stateIntArray", stateIntArray);
    // tool.incrementCopyCounter(stack);
    // tagCompound.setInteger(TEMPLATE_COPY_COUNT, tool.getCopyCounter(stack));
    // worldSave.addToMap(tool.getUUID(stack), tagCompound);
    // worldSave.markForSaving();
    // PacketHandler.INSTANCE.sendTo(new PacketBlockMap(tagCompound), (EntityPlayerMP) player);
    // player.sendStatusMessage(new TextComponentString(TextFormatting.AQUA
    // + new TextComponentTranslation("message.gadget." + (player.isSneaking() ? "mirrored" :
    // "rotated")).getUnformattedComponentText()), true);
    // }
    //
    public static void copyBlocks(ItemStack stack, EntityPlayer player, World world, ChunkCoordinates startPos,
        ChunkCoordinates endPos) {
        if (startPos != null && endPos != null) {
            GadgetCopyPaste tool = ModItems.gadgetCopyPaste;
            if (findBlocks(world, startPos, endPos, stack, player, tool)) {
                tool.setStartPos(stack, startPos);
                tool.setEndPos(stack, endPos);
            }
        }
    }

    private static boolean findBlocks(World world, ChunkCoordinates start, ChunkCoordinates end, ItemStack stack,
        EntityPlayer player, GadgetCopyPaste tool) {
        setLastBuild(stack, null, 0);
        int foundTE = 0;
        int startX = start.posX;
        int startY = start.posY;
        int startZ = start.posZ;

        int endX = end.posX;
        int endY = end.posY;
        int endZ = end.posZ;

        if (Math.abs(startX - endX) >= 125 || Math.abs(startY - endY) >= 125 || Math.abs(startZ - endZ) >= 125) {
            player.addChatMessage(
                new ChatComponentText(
                    ChatFormatting.RED
                        + new ChatComponentTranslation("message.gadget.toobigarea").getUnformattedText()));

            return false;
        }

        int iStartX = Math.min(startX, endX);
        int iStartY = Math.min(startY, endY);
        int iStartZ = Math.min(startZ, endZ);
        int iEndX = Math.max(startX, endX);
        int iEndY = Math.max(startY, endY);
        int iEndZ = Math.max(startZ, endZ);
        WorldSave worldSave = WorldSave.getWorldSave(world);
        NBTTagCompound tagCompound = new NBTTagCompound();
        List<Integer> posIntArrayList = new ArrayList<Integer>();
        List<Integer> stateIntArrayList = new ArrayList<Integer>();
        BlockMapIntState blockMapIntState = new BlockMapIntState();
        Multiset<UniqueItem> itemCountMap = HashMultiset.create();

        int blockCount = 0;

        for (int x = iStartX; x <= iEndX; x++) {
            for (int y = iStartY; y <= iEndY; y++) {
                for (int z = iStartZ; z <= iEndZ; z++) {
                    ChunkCoordinates tempPos = new ChunkCoordinates(x, y, z);
                    BlockState tempState = BlockState.getBlockState(world, tempPos);

                    // var notBlacklisted = !SyncedConfig.blockBlacklist.contains(tempState.getBlock());
                    var notBlacklisted = true;

                    if (!(tempState.block() instanceof EffectBlock) && !tempState.isAir()
                        && (world.getTileEntity(tempPos.posX, tempPos.posY, tempPos.posZ) == null
                            || world.getTileEntity(
                                tempPos.posX,
                                tempPos.posY,
                                tempPos.posZ) instanceof ConstructionBlockTileEntity)
                        && !tempState.getMaterial()
                            .isLiquid()
                        && notBlacklisted) {

                        TileEntity te = world.getTileEntity(tempPos.posX, tempPos.posY, tempPos.posZ);
                        BlockState assignState = InventoryManipulation
                            .getSpecificStates(tempState, world, player, tempPos, stack);
                        // BlockState actualState = assignState.getActualState(world, tempPos);
                        var actualState = assignState;

                        if (te instanceof ConstructionBlockTileEntity cbte) {
                            if (cbte.getActualBlock() != null) {
                                actualState = new BlockState(cbte.getActualBlock(), cbte.getActualBlockMeta());
                            }
                        }

                        if (actualState != null) {
                            UniqueItem uniqueItem = BlockMapIntState
                                .blockStateToUniqueItem(actualState, player, tempPos);
                            if (uniqueItem.item != null) {
                                posIntArrayList.add(GadgetUtils.relPosToInt(start, tempPos));
                                blockMapIntState.addToMap(actualState);
                                stateIntArrayList.add((int) blockMapIntState.findSlot(actualState));

                                blockMapIntState.addToStackMap(uniqueItem, actualState);
                                blockCount++;
                                if (blockCount > 32768) {
                                    player.addChatMessage(
                                        new ChatComponentText(
                                            ChatFormatting.RED
                                                + new ChatComponentTranslation("message.gadget.toomanyblocks")
                                                    .getUnformattedText()));
                                    return false;
                                }

                                List<ItemStack> drops = new ArrayList<>();
                                if (actualState != null) {
                                    drops = actualState.block()
                                        .getDrops(world, 0, 0, 0, actualState.metadata(), 0);
                                }

                                int neededItems = 0;
                                for (ItemStack drop : drops) {
                                    if (drop.getItem()
                                        .equals(uniqueItem.item)) {
                                        neededItems++;
                                    }
                                }
                                if (neededItems == 0) {
                                    neededItems = 1;
                                }
                                itemCountMap.add(uniqueItem, neededItems);
                            }
                        }
                    } else if ((world.getTileEntity(tempPos.posX, tempPos.posY, tempPos.posZ) != null)
                        && !(world.getTileEntity(
                            tempPos.posX,
                            tempPos.posY,
                            tempPos.posZ) instanceof ConstructionBlockTileEntity)) {
                                foundTE++;
                            }
                }
            }
        }
        tool.setItemCountMap(stack, itemCountMap);
        tagCompound.setTag("mapIntState", blockMapIntState.putIntStateMapIntoNBT());
        tagCompound.setTag("mapIntStack", blockMapIntState.putIntStackMapIntoNBT());
        int[] posIntArray = posIntArrayList.stream()
            .mapToInt(i -> i)
            .toArray();
        int[] stateIntArray = stateIntArrayList.stream()
            .mapToInt(i -> i)
            .toArray();
        tagCompound.setIntArray(NBTKeys.GADGET_POS_INT_ARRAY, posIntArray);
        tagCompound.setIntArray(NBTKeys.GADGET_STATE_INT_ARRAY, stateIntArray);

        tagCompound.setTag(NBTKeys.GADGET_START_POS, NBTTool.createPosTag(start));
        tagCompound.setTag(NBTKeys.GADGET_END_POS, NBTTool.createPosTag(end));
        tagCompound.setInteger(NBTKeys.GADGET_DIM, player.dimension);
        tagCompound.setString(NBTKeys.GADGET_UUID, tool.getUUID(stack));
        tagCompound.setString("owner", player.getDisplayName());
        tool.incrementCopyCounter(stack);
        tagCompound.setInteger(NBTKeys.TEMPLATE_COPY_COUNT, tool.getCopyCounter(stack));

        worldSave.addToMap(tool.getUUID(stack), tagCompound);
        worldSave.markForSaving();
        PacketHandler.INSTANCE.sendTo(new PacketBlockMap(tagCompound), (EntityPlayerMP) player);

        if (foundTE > 0) {
            player.addChatMessage(
                new ChatComponentText(
                    ChatFormatting.YELLOW + new ChatComponentTranslation("message.gadget.TEinCopy").getUnformattedText()
                        + ": "
                        + foundTE));

        } else {
            player.addChatMessage(
                new ChatComponentText(
                    ChatFormatting.AQUA + new ChatComponentTranslation("message.gadget.copied").getUnformattedText()));
        }

        return true;
    }

    private void buildBlockMap(World world, ChunkCoordinates startPos, ItemStack stack, EntityPlayer player) {
        ChunkCoordinates anchorPos = getAnchor(stack);
        ChunkCoordinates pos = anchorPos == null ? startPos : anchorPos;
        NBTTagCompound tagCompound = WorldSave.getWorldSave(world)
            .getCompoundFromUUID(getUUID(stack));

        pos = VectorTools.Up(pos, GadgetCopyPaste.getY(stack));
        pos = VectorTools.East(pos, GadgetCopyPaste.getX(stack));
        pos = VectorTools.South(pos, GadgetCopyPaste.getZ(stack));

        List<BlockMap> blockMapList = getBlockMapList(tagCompound, pos);
        setLastBuild(stack, pos, player.dimension);

        for (BlockMap blockMap : blockMapList)
            placeBlock(world, blockMap.pos, player, blockMap.state, getBlockMapIntState(tagCompound).getIntStackMap());

        GadgetUtils.clearCachedRemoteInventory();
        setAnchor(stack, null);
        // System.out.printf("Built %d Blocks in %.2f ms%n", blockMapList.size(), (System.nanoTime() - time) * 1e-6);
    }

    private void placeBlock(World world, ChunkCoordinates pos, EntityPlayer player, BlockState state,
        Map<BlockState, UniqueItem> IntStackMap) {
        if (!WorldUtils.isInsideWorldLimits(pos) || state.isAir() || !player.capabilities.allowEdit) {
            return;
        }

        var testState = BlockState.getBlockState(world, pos);
        if (testState == null) {
            return;
        }

        if ((GeneralConfig.canOverwriteBlocks && !testState.block()
            .isReplaceable(world, pos.posX, pos.posY, pos.posZ))
            || (!GeneralConfig.canOverwriteBlocks && testState.block()
                .isAir(world, pos.posX, pos.posY, pos.posZ))) {
            return;
        }

        ItemStack heldItem = getGadget(player);
        if (heldItem == null) {
            return;
        }

        if (ModItems.gadgetCopyPaste.getStartPos(heldItem) == null
            || ModItems.gadgetCopyPaste.getEndPos(heldItem) == null) {
            return;
        }

        UniqueItem uniqueItem = IntStackMap.get(state);
        if (uniqueItem == null) {
            return; // This shouldn't happen I hope!
        }

        ItemStack itemStack = new ItemStack(uniqueItem.item, 1, uniqueItem.meta);
        var drops = state.block()
            .getDrops(world, pos.posX, pos.posY, pos.posZ, state.metadata(), 0);
        int neededItems = 0;

        for (ItemStack drop : drops) {
            if (drop.getItem()
                .equals(itemStack.getItem()) && drop.getItemDamage() == itemStack.getItemDamage()) {
                neededItems++;
            }
        }
        if (neededItems == 0) {
            neededItems = 1;
        }

        if (!WorldUtils.isBlockModifiable(world, player, pos)) {
            return;
        }

        BlockSnapshot blockSnapshot = BlockSnapshot.getBlockSnapshot(world, pos.posX, pos.posY, pos.posZ);
        if (!GadgetGeneric.EmitEvent.placeBlock(player, blockSnapshot, EnumFacing.UP)) {
            return;
        }

        ItemStack constructionPaste = new ItemStack(ModItems.constructionPaste);
        boolean useConstructionPaste = false;
        if (InventoryManipulation.countItem(itemStack, player, world) < neededItems) {
            if (InventoryManipulation.countPaste(player) < neededItems) {
                return;
            }

            itemStack = constructionPaste.copy();
            useConstructionPaste = true;
        }

        if (!this.canUse(heldItem, player)) return;

        boolean useItemSuccess;
        if (useConstructionPaste) {
            useItemSuccess = InventoryManipulation.usePaste(player, 1);
        } else {
            useItemSuccess = InventoryManipulation.useItem(itemStack, player, neededItems, world);
        }
        if (useItemSuccess) {
            this.applyDamage(heldItem, player);
            world.spawnEntityInWorld(new BlockBuildEntity(world, pos, player, state, 1, state, useConstructionPaste));
        }
    }

    public void anchorBlocks(EntityPlayer player, ItemStack stack) {
        ChunkCoordinates currentAnchor = getAnchor(stack);
        if (currentAnchor == null) {
            MovingObjectPosition lookingAt = VectorTools.getLookingAt(player, stack);
            if (lookingAt == null) {
                return;
            }

            currentAnchor = VectorTools.getPosFromMovingObjectPosition(lookingAt);
            setAnchor(stack, currentAnchor);
            player.addChatMessage(
                new ChatComponentText(
                    ChatFormatting.AQUA
                        + new ChatComponentTranslation("message.gadget.anchorrender").getUnformattedText()));
        } else {
            setAnchor(stack, null);
            player.addChatMessage(
                new ChatComponentText(
                    ChatFormatting.AQUA
                        + new ChatComponentTranslation("message.gadget.anchorremove").getUnformattedText()));
        }
    }

    public void undoBuild(EntityPlayer player, ItemStack heldItem) {
        NBTTagCompound tagCompound = WorldSave.getWorldSave(player.worldObj)
            .getCompoundFromUUID(ModItems.gadgetCopyPaste.getUUID(heldItem));
        World world = player.worldObj;
        if (world.isRemote) {
            return;
        }
        ChunkCoordinates startPos = getLastBuild(heldItem);
        if (startPos == null) return;

        Integer dimension = getLastBuildDim(heldItem);
        ItemStack silkTool = heldItem.copy(); // Setup a Silk Touch version of the tool so we can return stone instead
                                              // of cobblestone, etc.

        silkTool.addEnchantment(Enchantment.silkTouch, 1);
        List<BlockMap> blockMapList = getBlockMapList(tagCompound, startPos);
        boolean success = true;
        for (BlockMap blockMap : blockMapList) {

            var distance = Math.sqrt(player.getDistanceSq(blockMap.pos.posX, blockMap.pos.posY, blockMap.pos.posZ));

            boolean sameDim = (player.dimension == dimension);

            var currentBlock = BlockState.getBlockState(world, blockMap.pos);

            boolean cancelled = !GadgetGeneric.EmitEvent.breakBlock(world, blockMap.pos, currentBlock.block(), player);
            if (distance < 256 && !cancelled && sameDim) { // Don't allow us to undo a block while its still being
                                                           // placed or too far away
                if (currentBlock.block() == blockMap.state.block()
                    || currentBlock.block() instanceof ConstructionBlock) {
                    if (currentBlock.getBlockHardness(world, blockMap.pos) >= 0) {
                        if (!player.capabilities.isCreativeMode) {
                            // TODO(johnrowl) silk touch is currently busted. The 1.12.x version allowed you to pass in
                            // a silk touch tool, while the 1.7.10 does not.
                            // currentBlock.getBlock().harvestBlock(
                            // world,
                            // player,
                            // blockMap.pos,
                            // currentBlock,
                            // world.getTileEntity(blockMap.pos.posX, blockMap.pos.posY, blockMap.pos.posZ),
                            // silkTool
                            // );
                            currentBlock.block()
                                .harvestBlock(
                                    world,
                                    player,
                                    blockMap.pos.posX,
                                    blockMap.pos.posY,
                                    blockMap.pos.posZ,
                                    currentBlock.metadata());
                        }
                        world.spawnEntityInWorld(
                            new BlockBuildEntity(world, blockMap.pos, player, currentBlock, 2, currentBlock, false));

                    }
                }
            } else {
                player.addChatMessage(
                    new ChatComponentText(
                        ChatFormatting.RED
                            + new ChatComponentTranslation("message.gadget.undofailed").getUnformattedText()));
                success = false;
            }
            // System.out.printf("Undid %d Blocks in %.2f ms%n", blockMapList.size(), (System.nanoTime() - time) *
            // 1e-6);
        }
        if (success) {
            setLastBuild(heldItem, null, 0);
        }
    }

    public static ItemStack getGadget(EntityPlayer player) {
        ItemStack stack = GadgetGeneric.getGadget(player);
        if (!(stack.getItem() instanceof GadgetCopyPaste)) return null;

        return stack;
    }
}
