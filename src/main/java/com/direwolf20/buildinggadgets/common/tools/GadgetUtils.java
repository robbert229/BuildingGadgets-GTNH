package com.direwolf20.buildinggadgets.common.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import cofh.lib.util.helpers.MathHelper;
import com.direwolf20.buildinggadgets.common.blocks.BlockModBase;
import com.direwolf20.buildinggadgets.common.config.SyncedConfig;
import com.direwolf20.buildinggadgets.common.integration.NetworkProvider;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetBuilding;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetExchanger;
import com.direwolf20.buildinggadgets.util.NBTTool;
import com.direwolf20.buildinggadgets.util.VectorTools;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;
import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class GadgetUtils {

    private static class ChunkCoordinateComparatorHelper {

        public static int getX(ChunkCoordinates coordinates) {
            return coordinates.posX;
        }

        public static int getY(ChunkCoordinates coordinates) {
            return coordinates.posY;
        }

        public static int getZ(ChunkCoordinates coordinates) {
            return coordinates.posZ;
        }
    }

    private static final ImmutableList<String> LINK_STARTS = ImmutableList.of("http", "www");
    private static Supplier<IInventory> remoteInventorySupplier;

    public static boolean mightBeLink(final String s) {
        return LINK_STARTS.stream().anyMatch(s::startsWith);
    }

    //
    public static final Comparator<ChunkCoordinates> POSITION_COMPARATOR = Comparator.comparingInt(ChunkCoordinateComparatorHelper::getX).thenComparingInt(ChunkCoordinateComparatorHelper::getY).thenComparingInt(ChunkCoordinateComparatorHelper::getZ);

    public static String getStackErrorSuffix(ItemStack stack) {
        return getStackErrorText(stack) + " with NBT tag: " + stack.getTagCompound();
    }

    private static String getStackErrorText(ItemStack stack) {
        return "the following stack: [" + stack + "]";
    }


    @Nullable
    public static ByteArrayOutputStream getPasteStream(@Nonnull NBTTagCompound compound, @Nullable String name) throws IOException {
        NBTTagCompound withText = name != null && !name.isEmpty() ? (NBTTagCompound) compound.copy() : compound;
        if (name != null && !name.isEmpty()) {
            withText.setString("name", name);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CompressedStreamTools.writeCompressed(withText, baos);
        return baos.size() < Short.MAX_VALUE - 200 ? baos : null;
    }


    public static NBTTagCompound getStackTag(ItemStack stack) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null)
            throw new IllegalArgumentException("An NBT tag could net be retrieved from " + getStackErrorText(stack));

        return tag;
    }

    //
    public static void pushUndoList(ItemStack stack, UndoState undoState) {
        //When we have a new set of Undo Coordinates, push it onto a list stored in NBT, max 10
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        NBTTagList undoStates = (NBTTagList) tagCompound.getTag("undoStack");
        if (undoStates == null) {
            undoStates = new NBTTagList();
        }
        if (undoStates.tagCount() >= 10) {
            undoStates.removeTag(0);
        }
        undoStates.appendTag(undoStateToNBT(undoState));
        tagCompound.setTag("undoStack", undoStates);
        stack.setTagCompound(tagCompound);
    }

    //
    @Nullable
    public static UndoState popUndoList(ItemStack stack) {
        //Get the most recent Undo Coordinate set from the list in NBT
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            return null;
        }
        NBTTagList undoStates = (NBTTagList) tagCompound.getTag("undoStack");
        if (undoStates == null || undoStates.tagCount() == 0) {
            return null;
        }
        UndoState undoState = NBTToUndoState(undoStates.getCompoundTagAt(undoStates.tagCount() - 1));
        undoStates.removeTag(undoStates.tagCount() - 1);
        tagCompound.setTag("undoStack", undoStates);
        return undoState;
    }

    //
    private static NBTTagCompound undoStateToNBT(UndoState undoState) {
        //Convert an UndoState object into NBT data. Uses ints to store relative positions to a start block for data
        // compression..
        NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("dim", undoState.dimension);
        ChunkCoordinates startBlock = undoState.coordinates.get(0);
        int[] array = new int[undoState.coordinates.size()];
        int idx = 0;
        for (ChunkCoordinates coord : undoState.coordinates) {
            //Converts relative chunkCoordinates coordinates to a single integer value. Max range 127 due to 8 bits.
            int px = (((coord.posX - startBlock.posX) & 0xff) << 16);
            int py = (((coord.posY - startBlock.posY) & 0xff) << 8);
            int pz = (((coord.posZ - startBlock.posZ) & 0xff));
            int p = (px + py + pz);
            array[idx++] = p;
        }
        compound.setTag("startBlock", NBTTool.createPosTag(startBlock));
        compound.setIntArray("undoIntCoords", array);
        return compound;
    }

    private static UndoState NBTToUndoState(NBTTagCompound compound) {
        //Convert an integer list stored in NBT into UndoState
        int dim = compound.getInteger("dim");
        List<ChunkCoordinates> coordinates = new ArrayList<ChunkCoordinates>();
        int[] array = compound.getIntArray("undoIntCoords");

        ChunkCoordinates startBlock = NBTTool.getPosFromTag(compound.getCompoundTag("startBlock"));
        for (int i = 0; i <= array.length - 1; i++) {
            int p = array[i];
            int x = startBlock.posX + (byte) ((p & 0xff0000) >> 16);
            int y = startBlock.posY + (byte) ((p & 0x00ff00) >> 8);
            int z = startBlock.posZ + (byte) (p & 0x0000ff);
            coordinates.add(new ChunkCoordinates(x, y, z));
        }

        return new UndoState(dim, coordinates);
    }

    public static void setAnchor(ItemStack stack, List<ChunkCoordinates> coordinates) {
        //Store 1 set of ChunkCoordinates in NBT to anchor the Ghost Blocks in the world when the anchor key is pressed
        NBTTagCompound tagCompound = stack.getTagCompound();
        NBTTagList coords = new NBTTagList();

        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }

        for (ChunkCoordinates coord : coordinates) {
            coords.appendTag(NBTTool.createPosTag(coord));
        }

        tagCompound.setTag("anchorcoords", coords);
        stack.setTagCompound(tagCompound);
    }

    public static List<ChunkCoordinates> getAnchor(ItemStack stack) {
        //Return the list of coordinates in the NBT Tag for anchor Coordinates
        NBTTagCompound tagCompound = stack.getTagCompound();
        List<ChunkCoordinates> coordinates = new ArrayList<ChunkCoordinates>();

        if (tagCompound == null) {
            setAnchor(stack, coordinates);
            tagCompound = stack.getTagCompound();
            return coordinates;
        }

        NBTTagList coordList = (NBTTagList) tagCompound.getTag("anchorcoords");
        if (coordList == null) {
            setAnchor(stack, coordinates);
            tagCompound = stack.getTagCompound();
            return coordinates;
        }

        if (coordList.tagCount() == 0) {
            return coordinates;
        }

        for (int i = 0; i < coordList.tagCount(); i++) {
            coordinates.add(NBTTool.getPosFromTag(coordList.getCompoundTagAt(i)));
        }
        return coordinates;
    }

    public static void setToolRange(ItemStack stack, int range) {
        //Store the tool's range in NBT as an Integer
        NBTTagCompound tagCompound = NBTTool.getOrNewTag(stack);
        tagCompound.setInteger("range", range);
    }

    public static int getToolRange(ItemStack stack) {
        NBTTagCompound tagCompound = NBTTool.getOrNewTag(stack);
        return MathHelper.clamp(tagCompound.getInteger("range"), 1, SyncedConfig.maxRange);
    }

//     public static IBlockState rotateOrMirrorBlock(EntityPlayer player, PacketRotateMirror.Operation operation,
//     IBlockState state) {
//     if (operation == PacketRotateMirror.Operation.MIRROR)
//     return state.withMirror(player.getHorizontalFacing().getAxis() == Axis.X ? Mirror.LEFT_RIGHT :
//     Mirror.FRONT_BACK);
//
//     return state.withRotation(Rotation.CLOCKWISE_90);
//     }
//
//     public static void rotateOrMirrorToolBlock(ItemStack stack, EntityPlayer player, PacketRotateMirror.Operation
//     operation) {
//     setToolBlock(stack, rotateOrMirrorBlock(player, operation, getToolBlock(stack)));
//     setToolActualBlock(stack, rotateOrMirrorBlock(player, operation, getToolActualBlock(stack)));
//     }

    private static void setToolBlock(ItemStack stack, @Nullable BlockState state) {
        //Store the selected block in the tool's NBT
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }

        if (state == null) {
            state = new BlockState(Blocks.air, 0);
        }

        var stateTag = NBTTool.blockToCompound(state);
        tagCompound.setTag("blockstate", stateTag);
        stack.setTagCompound(tagCompound);
    }

    //
    // private static void setToolActualBlock(ItemStack stack, @Nullable IBlockState state) {
    // //Store the selected block actual state in the tool's NBT
    // NBTTagCompound tagCompound = stack.getTagCompound();
    // if (tagCompound == null) {
    // tagCompound = new NBTTagCompound();
    // }
    // if (state == null) {
    // state = Blocks.AIR.getDefaultState();
    // }
    // NBTTagCompound stateTag = new NBTTagCompound();
    // NBTUtil.writeBlockState(stateTag, state);
    // tagCompound.setTag("actualblockstate", stateTag);
    // stack.setTagCompound(tagCompound);
    // }
    //
    public static BlockState getToolBlock(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            var air = new BlockState(Blocks.air, 0);
            setToolBlock(stack, air);
            return air;
        }

        return NBTTool.blockFromCompound(tagCompound.getCompoundTag("blockstate"));
    }


    public static BlockState getToolActualBlock(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            var air = new BlockState(Blocks.air, 0);
            setToolBlock(stack, air);
            tagCompound = stack.getTagCompound();
            return air;
        }

        return NBTTool.blockFromCompound(tagCompound.getCompoundTag("actualblockstate"));
    }

    //
    // public static void selectBlock(ItemStack stack, EntityPlayer player) {
    // //Used to find which block the player is looking at, and store it in NBT on the tool.
    // World world = player.world;
    // MovingObjectPosition lookingAt = VectorTools.getLookingAt(player, false);
    // if (lookingAt == null)
    // return;
    //
    // ChunkCoordinates pos = VectorTools.getPosFromMovingObjectPosition(lookingAt);
    // EnumActionResult result = setRemoteInventory(stack, player, world, pos, true);
    // if (result == EnumActionResult.SUCCESS)
    // return;
    //
    // IBlockState state = world.getBlockState(pos);
    // if (result == EnumActionResult.FAIL || SyncedConfig.blockBlacklist.contains(state.getBlock()) || state.getBlock()
    // instanceof EffectBlock ) {
    // player.sendStatusMessage(new TextComponentString(TextFormatting.RED + new
    // TextComponentTranslation("message.gadget.invalidblock").getUnformattedComponentText()), true);
    // return;
    // }
    // IBlockState placeState = InventoryManipulation.getSpecificStates(state, world, player, pos, stack);
    // IBlockState actualState = placeState.getActualState(world, pos);
    // setToolBlock(stack, placeState);
    // setToolActualBlock(stack, actualState);
    // }
    //
    // public static EnumActionResult setRemoteInventory(ItemStack stack, EntityPlayer player, World world,
    // ChunkCoordinates pos, boolean setTool) {
    // TileEntity te = world.getTileEntity(pos);
    // if (te == null)
    // return EnumActionResult.PASS;
    //
    // if (setTool && te instanceof ConstructionBlockTileEntity && ((ConstructionBlockTileEntity) te).getBlockState() !=
    // null) {
    // setToolBlock(stack, ((ConstructionBlockTileEntity) te).getActualBlockState());
    // setToolActualBlock(stack, ((ConstructionBlockTileEntity) te).getActualBlockState());
    // return EnumActionResult.SUCCESS;
    // }
    // if (setRemoteInventory(player, stack, pos, world.provider.getDimension(), world))
    // return EnumActionResult.SUCCESS;
    //
    // return EnumActionResult.FAIL;
    // }
    //
    public static boolean anchorBlocks(EntityPlayer player, ItemStack stack) {
        //Stores the current visual blocks in NBT on the tool, so the player can look around without moving the visual
        // render
        World world = player.worldObj;
        List<ChunkCoordinates> currentCoords = getAnchor(stack);
        if (currentCoords.isEmpty()) { //If we don't have an anchor, find the block we're supposed to anchor to
            MovingObjectPosition lookingAt = VectorTools.getLookingAt(player, stack);
            if (lookingAt == null) { //If we aren't looking at anything, exit
                return false;
            }
            ChunkCoordinates startBlock = VectorTools.getPosFromMovingObjectPosition(lookingAt);
            var startBlockBlock = world.getBlock(startBlock.posX, startBlock.posY, startBlock.posZ);

            int sideHit = lookingAt.sideHit;
            if (startBlock == null || startBlockBlock == Blocks.air || startBlockBlock == null) {
                //If we are looking
                // at air, exit
                return false;
            }
            var side = BlockModBase.BlockSide.fromValue(sideHit);
            List<ChunkCoordinates> coords = new ArrayList<ChunkCoordinates>();

            if (stack.getItem() instanceof GadgetBuilding) {
                coords = BuildingModes.collectPlacementPos(world, player, startBlock, EnumFacing.getFront(sideHit), stack, startBlock); // Build the
                //positions list based on tool mode and range
            } else if (stack.getItem() instanceof GadgetExchanger) {
                coords = ExchangingModes.collectPlacementPos(world, player, startBlock, EnumFacing.getFront(sideHit), stack, startBlock); // Build the
                // positions list based on tool mode and range
            }

            setAnchor(stack, coords); //Set the anchor NBT
            player.addChatMessage(new ChatComponentText(ChatFormatting.AQUA + new ChatComponentTranslation("message.gadget.anchorrender").getUnformattedText()));
        } else {
            //If there's already an anchor, remove it.
            setAnchor(stack, new ArrayList<ChunkCoordinates>());
            player.addChatMessage(new ChatComponentText(ChatFormatting.AQUA + new ChatComponentTranslation("message.gadget.anchorremove").getUnformattedText()));
        }
        return true;
    }

    public static boolean setRemoteInventory(EntityPlayer player, ItemStack tool, ChunkCoordinates pos, int dim, World world) {
        if (getRemoteInventory(pos, dim, world, player) != null) {
            boolean same = pos.equals(getPOSFromNBT(tool, "boundTE"));
            writePOSToNBT(tool, same ? null : pos, "boundTE", dim);
            player.addChatMessage(new ChatComponentText(ChatFormatting.AQUA + new ChatComponentTranslation("message.gadget." + (same ? "unboundTE" : "boundTE")).getUnformattedText()));
            return true;
        }
        return false;
    }

    public static void clearCachedRemoteInventory() {
        remoteInventorySupplier = null;
    }

    @Nullable
    public static IInventory getRemoteInventory(ItemStack tool, World world, EntityPlayer player) {
        return getRemoteInventory(tool, world, player, NetworkIO.Operation.EXTRACT);
    }

    /**
     * Call {@link #clearCachedRemoteInventory clearCachedRemoteInventory} when done using this method
     */
    @Nullable
    public static IInventory getRemoteInventory(ItemStack tool, World world, EntityPlayer player, NetworkIO.Operation operation) {
        if (remoteInventorySupplier == null) {
            remoteInventorySupplier = Suppliers.memoizeWithExpiration(() -> {
                Integer dim = getDIMFromNBT(tool, "boundTE");
                if (dim == null) return null;

                // Check if the Dimension actually exists. (Thanks RFTools...)
                if (DimensionManager.getWorld(dim) == null) return null;

                ChunkCoordinates pos = getPOSFromNBT(tool, "boundTE");
                return pos == null ? null : getRemoteInventory(pos, dim, world, player, operation);
            }, 500, TimeUnit.MILLISECONDS);
        }
        return remoteInventorySupplier.get();
    }

    @Nullable
    public static IInventory getRemoteInventory(ChunkCoordinates pos, int dim, World world, EntityPlayer player) {
        return getRemoteInventory(pos, dim, world, player, NetworkIO.Operation.EXTRACT);
    }

    @Nullable
    public static IInventory getRemoteInventory(ChunkCoordinates pos, int dim, World world, EntityPlayer player, NetworkIO.Operation operation) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server == null) {
            return null;
        }

        World worldServer = server.worldServerForDimension(dim);
        if (worldServer == null) {
            return null;
        }

        return getRemoteInventory(pos, worldServer, player, operation);
    }

    @Nullable
    public static IInventory getRemoteInventory(ChunkCoordinates coordinates, World world, EntityPlayer player, NetworkIO.Operation operation) {
        TileEntity te = world.getTileEntity(coordinates.posX, coordinates.posY, coordinates.posZ);
        if (te == null) {
            return null;
        }

        IInventory network = NetworkProvider.getWrappedNetwork(te, player, operation);
        if (network != null) {
            return network;
        }

        if (te instanceof IInventory iInventory) {
            return iInventory;
        }

        return null;
    }

    public static String withSuffix(int count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f%c", count / Math.pow(1000, exp), "kMGTPE".charAt(exp - 1));
    }

    public static void writePOSToNBT(ItemStack stack, @Nullable ChunkCoordinates pos, String tagName) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        if (pos == null) {
            if (tagCompound.getTag(tagName) != null) {
                tagCompound.removeTag(tagName);
                stack.setTagCompound(tagCompound);
            }
            return;
        }

        tagCompound.setTag(tagName, NBTTool.createPosTag(pos));
        stack.setTagCompound(tagCompound);
    }

    public static void writePOSToNBT(ItemStack stack, @Nullable ChunkCoordinates pos, String tagName, Integer dim) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        if (pos == null) {
            if (tagCompound.getTag(tagName) != null) {
                tagCompound.removeTag(tagName);
                stack.setTagCompound(tagCompound);
            }
            return;
        }
        NBTTagCompound posTag = NBTTool.createPosTag(pos);
        posTag.setInteger("dim", dim);
        tagCompound.setTag(tagName, posTag);
        stack.setTagCompound(tagCompound);
    }

    public static void writePOSToNBT(NBTTagCompound tagCompound, @Nullable ChunkCoordinates pos, String tagName, Integer dim) {
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        if (pos == null) {
            if (tagCompound.getTag(tagName) != null) {
                tagCompound.removeTag(tagName);
            }
            return;
        }
        tagCompound.setTag(tagName, NBTTool.createPosTag(pos));
        tagCompound.setInteger("dim", dim);
    }

    @Nullable
    public static ChunkCoordinates getPOSFromNBT(ItemStack stack, String tagName) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            return null;
        }
        NBTTagCompound posTag = tagCompound.getCompoundTag(tagName);
        if (posTag.equals(new NBTTagCompound())) {
            return null;
        }
        return NBTTool.getPosFromTag(posTag);
    }

    public static void writeIntToNBT(ItemStack stack, int tagInt, String tagName) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        tagCompound.setInteger(tagName, tagInt);
        stack.setTagCompound(tagCompound);
    }

    public static int getIntFromNBT(ItemStack stack, String tagName) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        return tagCompound == null ? 0 : tagCompound.getInteger(tagName);
    }

    public static void writeStringToNBT(ItemStack stack, String string, String tagName) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }

        if (string.equals(null)) {
            if (tagCompound.getTag(tagName) != null) {
                tagCompound.removeTag(tagName);
            }
            return;
        }
        tagCompound.setString(tagName, string);
    }

    public static void writeStringToNBT(NBTTagCompound tagCompound, String string, String tagName) {// TODO unused
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        if (string.equals(null)) {
            if (tagCompound.getTag(tagName) != null) {
                tagCompound.removeTag(tagName);
            }
            return;
        }
        tagCompound.setString(tagName, string);
    }

    @Nullable
    public static String getStringFromNBT(ItemStack stack, String tagName) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            return null;
        }
        return tagCompound.getString(tagName);
    }

    @Nullable
    public static ChunkCoordinates getPOSFromNBT(NBTTagCompound tagCompound, String tagName) {
        if (tagCompound == null) {
            return null;
        }
        NBTTagCompound posTag = tagCompound.getCompoundTag(tagName);
        if (posTag.equals(new NBTTagCompound())) {
            return null;
        }
        return NBTTool.getPosFromTag(posTag);
    }

    @Nullable
    public static Integer getDIMFromNBT(ItemStack stack, String tagName) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            return null;
        }
        NBTTagCompound posTag = tagCompound.getCompoundTag(tagName);
        if (posTag.equals(new NBTTagCompound())) {
            return null;
        }
        return posTag.getInteger("dim");
    }

    public static int relPosToInt(ChunkCoordinates startPos, ChunkCoordinates relPos) {
        int px = (((relPos.posX - startPos.posX) & 0xff) << 16);
        int py = (((relPos.posY - startPos.posY) & 0xff) << 8);
        int pz = (((relPos.posZ - startPos.posZ) & 0xff));
        int p = (px + py + pz);
        return p;
    }

    public static ChunkCoordinates relIntToPos(ChunkCoordinates startPos, int relInt) {
        int p = relInt;
        int x = startPos.posX + (byte) ((p & 0xff0000) >> 16);
        int y = startPos.posY + (byte) ((p & 0x00ff00) >> 8);
        int z = startPos.posZ + (byte) (p & 0x0000ff);
        return new ChunkCoordinates(x, y, z);
    }

    public static NBTTagList itemCountToNBT(Multiset<UniqueItem> itemCountMap) {
        NBTTagList tagList = new NBTTagList();

        for (Multiset.Entry<UniqueItem> entry : itemCountMap.entrySet()) {
            int item = Item.getIdFromItem(entry.getElement().item);
            int meta = entry.getElement().meta;
            int count = entry.getCount();

            NBTTagCompound tagCompound = new NBTTagCompound();
            tagCompound.setInteger("item", item);
            tagCompound.setInteger("meta", meta);
            tagCompound.setInteger("count", count);

            tagList.appendTag(tagCompound);
        }

        return tagList;
    }

    public static Multiset<UniqueItem> nbtToItemCount(@Nullable NBTTagList tagList) {
        if (tagList == null) return HashMultiset.create();
        Multiset<UniqueItem> itemCountMap = HashMultiset.create(tagList.tagCount());
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound tagCompound = tagList.getCompoundTagAt(i);
            UniqueItem uniqueItem = new UniqueItem(Item.getItemById(tagCompound.getInteger("item")), tagCompound.getInteger("meta"));
            int count = tagCompound.getInteger("count");
            itemCountMap.setCount(uniqueItem, count);
        }

        return itemCountMap;
    }
}
