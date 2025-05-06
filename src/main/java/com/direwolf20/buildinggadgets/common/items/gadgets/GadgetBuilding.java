package com.direwolf20.buildinggadgets.common.items.gadgets;

import com.direwolf20.buildinggadgets.common.blocks.ModBlocks;
import com.direwolf20.buildinggadgets.common.entities.BlockBuildEntity;
import com.direwolf20.buildinggadgets.common.items.FakeBuilderWorld;
import com.direwolf20.buildinggadgets.common.items.ModItems;
import com.direwolf20.buildinggadgets.common.tools.*;
import com.direwolf20.buildinggadgets.util.NBTTool;
import com.direwolf20.buildinggadgets.util.VectorTools;
import com.direwolf20.buildinggadgets.util.WorldUtils;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import com.direwolf20.buildinggadgets.common.config.SyncedConfig;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GadgetBuilding extends GadgetGeneric {
    private static final FakeBuilderWorld fakeWorld = new FakeBuilderWorld();

    public GadgetBuilding() {
        super("buildingtool");

        setMaxDamage(SyncedConfig.durabilityBuilder);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return SyncedConfig.poweredByFE ? 0 : SyncedConfig.durabilityBuilder;
    }

    @Override
    public int getEnergyCost(ItemStack tool) {
        return SyncedConfig.energyCostBuilder;
    }

    @Override
    public int getDamageCost(ItemStack tool) {
        return SyncedConfig.damageCostBuilder;
    }

    private static void setToolMode(ItemStack tool, BuildingModes mode) {
        //Store the tool's mode in NBT as a string
        NBTTagCompound tagCompound = NBTTool.getOrNewTag(tool);
        tagCompound.setString("mode", mode.getRegistryName());
    }

    public static BuildingModes getToolMode(ItemStack tool) {
        NBTTagCompound tagCompound = NBTTool.getOrNewTag(tool);
        return BuildingModes.byName(tagCompound.getString("mode"));
    }

    public static boolean shouldPlaceAtop(ItemStack stack) {
        return !NBTTool.getOrNewTag(stack)
                .getBoolean("start_inside");
    }

    public static void togglePlaceAtop(EntityPlayer player, ItemStack stack) {
        NBTTool.getOrNewTag(stack)
                .setBoolean("start_inside", shouldPlaceAtop(stack));
        String prefix = "message.gadget.building.placement";
        player.addChatComponentMessage(
                new ChatComponentText(
                        ChatFormatting.AQUA + new ChatComponentTranslation(
                                prefix,
                                new ChatComponentTranslation(prefix + (shouldPlaceAtop(stack) ? ".atop" : ".inside")))
                                .getUnformattedTextForChat()));
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        //Add tool information to the tooltip
        super.addInformation(stack, player, list, b);

        list.add(ChatFormatting.DARK_GREEN + StatCollector.translateToLocal("tooltip.gadget.block") + ": " +
                GadgetUtils.getToolBlock(stack).getBlock().getLocalizedName());

        BuildingModes mode = getToolMode(stack);

        list.add(ChatFormatting.AQUA + StatCollector.translateToLocal("tooltip.gadget.mode") + ": " + (mode == BuildingModes.Surface &&
                getConnectedArea(stack) ? StatCollector.translateToLocal("tooltip.gadget.connected") + " " : "") + mode);

        if (getToolMode(stack) != BuildingModes.BuildToMe) {
            list.add(ChatFormatting.LIGHT_PURPLE + StatCollector.translateToLocal("tooltip.gadget.range") + ": " + GadgetUtils.getToolRange(stack));
        }

        if (getToolMode(stack) == BuildingModes.Surface)
            list.add(ChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.gadget.fuzzy") + ": " + getFuzzy(stack));

        addInformationRayTraceFluid(list, stack);

        list.add(ChatFormatting.YELLOW + StatCollector.translateToLocal("tooltip.gadget.building.place_atop") + ": " +
                shouldPlaceAtop(stack));

        addEnergyInformation(list, stack);
    }

//    @Override
//    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
//        //On item use, if sneaking, select the block clicked on, else build -- This is called when you right click a tool
//        //NOT on a block.
//        ItemStack itemstack = player.getHeldItem(hand);
//     /*NBTTagCompound tagCompound = itemstack.getTagCompound();
//     ByteBuf buf = Unpooled.buffer(16);
//     ByteBufUtils.writeTag(buf,tagCompound);
//     System.out.println(buf.readableBytes());*/
//        player.setActiveHand(hand);
//        if (!world.isRemote) {
//            if (player.isSneaking()) {
//                selectBlock(itemstack, player);
//            } else {
//                build(player, itemstack);
//            }
//        } else if (!player.isSneaking()) {
//            ToolRenders.updateInventoryCache();
//        }
//        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
//    }

    public void setMode(ItemStack heldItem, int modeInt) {
        // Called when we specify a mode with the radial menu
        BuildingModes mode = BuildingModes.values()[modeInt];
        setToolMode(heldItem, mode);
    }

    public void rangeChange(EntityPlayer player, ItemStack heldItem) {
        //Called when the range change hotkey is pressed
        int range = GadgetUtils.getToolRange(heldItem);
        int changeAmount = (getToolMode(heldItem) != BuildingModes.Surface || (range % 2 == 0)) ? 1 : 2;

        if (player.isSneaking()) {
            range = (range == 1) ? SyncedConfig.maxRange : range - changeAmount;
        } else {
            range = (range >= SyncedConfig.maxRange) ? 1 : range + changeAmount;
        }

        GadgetUtils.setToolRange(heldItem, range);
        player.addChatMessage(
                new ChatComponentText(
                        ChatFormatting.DARK_AQUA + new ChatComponentTranslation("message.gadget.toolrange").getUnformattedText() + ": " + range
                )
        );
    }

    private boolean build(EntityPlayer player, ItemStack stack) {
        //Build the blocks as shown in the visual render
        World world = player.worldObj;
        List<ChunkCoordinates> coords = GadgetUtils.getAnchor(stack);

        if (coords.isEmpty()) { //If we don't have an anchor, build in the current spot
            MovingObjectPosition lookingAt = VectorTools.getLookingAt(player, stack);
            if (lookingAt == null) { //If we aren't looking at anything, exit
                return false;
            }
            ChunkCoordinates startBlock = VectorTools.getPosFromMovingObjectPosition(lookingAt);
            EnumFacing sideHit = EnumFacing.getFront(lookingAt.sideHit);
            coords = BuildingModes.collectPlacementPos(world, player, startBlock, sideHit, stack, startBlock);
        } else { //If we do have an anchor, erase it (Even if the build fails)
            GadgetUtils.setAnchor(stack, new ArrayList<ChunkCoordinates>());
        }
        List<ChunkCoordinates> undoCoords = new ArrayList<ChunkCoordinates>();
        Set<ChunkCoordinates> coordinates = new HashSet<ChunkCoordinates>(coords);

        ItemStack heldItem = getGadget(player);
        if (heldItem == null) {
            return false;
        }

        BlockState blockState = GadgetUtils.getToolBlock(heldItem);

        if (!blockState.isAir()) {
            //Don't attempt a build if a block is not chosen -- Typically
            //only happens on a new tool.

            BlockState state = null;
//            BlockState state = Blocks.AIR.getDefaultState(); //Initialize a new State Variable for use in the fake world

            fakeWorld.setWorldAndState(player.worldObj, blockState, coordinates); // Initialize the fake world's blocks
            for (ChunkCoordinates coordinate : coords) {

                try { //Get the state of the block in the fake world (This lets fences be connected, etc)
                    int metadata = fakeWorld.getBlockMetadata(coordinate.posX, coordinate.posY, coordinate.posZ);
                    var block = fakeWorld.getBlock(coordinate.posX, coordinate.posY, coordinate.posZ);
                    if (block != null) {
                        state = new BlockState(block, metadata);
                    }
                } catch (Exception var8) {
                    // do nothing?
                }

                //Get the extended block state in the fake world
                //Disabled to fix Chisel
                //state = state.getBlock().getExtendedState(state, fakeWorld, coordinate);

                if (placeBlock(world, player, coordinate, state)) {
                    undoCoords.add(coordinate);//If we successfully place the block, add the location to the undo list.
                }
            }
            GadgetUtils.clearCachedRemoteInventory();
            if (!undoCoords.isEmpty()) { //If the undo list has any data in it, add it to NBT on the tool.
                UndoState undoState = new UndoState(player.dimension, undoCoords);
                GadgetUtils.pushUndoList(heldItem, undoState);
            }
        }

        Sorter.Blocks.byDistance(coords, player);
        return true;
    }

    public boolean undoBuild(EntityPlayer player) {
        // TODO(johnrowl) implement

//        ItemStack heldItem = getGadget(player);
//        if (heldItem == null || heldItem.getItem() == null)
//            return false;
//
//        UndoState undoState = popUndoList(heldItem); //Get the undo list off the tool, exit if empty
//        if (undoState == null) {
//            player.sendStatusMessage(new TextComponentString(TextFormatting.AQUA + new
//                    TextComponentTranslation("message.gadget.nothingtoundo").getUnformattedComponentText()), true);
//            return false;
//        }
//        World world = player.world;
//        if (!world.isRemote) {
//            IBlockState currentBlock = Blocks.AIR.getDefaultState();
//            List<ChunkCoordinates> undoCoords = undoState.coordinates; //Get the Coords to undo
//            int dimension = undoState.dimension; //Get the Dimension to undo
//            List<ChunkCoordinates> failedRemovals = new ArrayList<ChunkCoordinates>(); //Build a list of removals that fail
//            ItemStack silkTool = heldItem.copy(); //Setup a Silk Touch version of the tool so we can return stone instead of
//            cobblestone, etc.
//                    silkTool.addEnchantment(Enchantments.SILK_TOUCH, 1);
//            for (ChunkCoordinates coord : undoCoords) {
//                currentBlock = world.getBlockState(coord);
//                // ItemStack itemStack = currentBlock.getBlock().getPickBlock(currentBlock, null, world, coord, player);
//                double distance = coord.getDistance(player.getPosition().getX(), player.getPosition().getY(),
//                        player.getPosition().getZ());
//                boolean sameDim = (player.dimension == dimension);
//
//                boolean cancelled = !GadgetGeneric.EmitEvent.breakBlock(world, coord, currentBlock, player);
//
//                if (distance < 64 && sameDim && currentBlock != ModBlocks.effectBlock.getDefaultState() && !cancelled) { //Don't
//                    // allow us to undo a block while its still being placed or too far away
//                    if (currentBlock != Blocks.AIR.getDefaultState()) {
//                        if (!player.capabilities.isCreativeMode)
//                            currentBlock.getBlock().harvestBlock(world, player, coord, currentBlock, world.getTileEntity(coord), silkTool);
//                        world.spawnEntity(new BlockBuildEntity(world, coord, player, currentBlock, 2, getToolActualBlock(heldItem),
//                                false));
//                    }
//                } else { //If you're in the wrong dimension or too far away, fail the undo.
//                    player.sendStatusMessage(new TextComponentString(TextFormatting.RED + new
//                            TextComponentTranslation("message.gadget.undofailed").getUnformattedComponentText()), true);
//                    failedRemovals.add(coord);
//                }
//            }
//            GadgetUtils.clearCachedRemoteInventory();
//            if (failedRemovals.size() != 0) { //Add any failed undo blocks to the undo stack.
//                UndoState failedState = new UndoState(player.dimension, failedRemovals);
//                pushUndoList(heldItem, failedState);
//            }
//        }
//        return true;
        return true;
    }

    private boolean placeBlock(World world, EntityPlayer player, ChunkCoordinates pos, BlockState setBlock) {
        if (!player.capabilities.allowEdit) {
            return false;
        }

        if (!WorldUtils.isInsideWorldLimits(world, pos)) {
            return false;
        }

        ItemStack heldItem = getGadget(player);
        if (heldItem == null || heldItem.getItem() == null) {
            return false;
        }

        boolean useConstructionPaste = false;

        ItemStack itemStack;
        if (setBlock.getBlock().canSilkHarvest(world, player, pos.posX, pos.posY, pos.posZ, 0)) {
            itemStack = InventoryManipulation.getSilkTouchDrop(setBlock.getBlock(), setBlock.getMetadata());
        } else {
            itemStack = setBlock.getBlock().getPickBlock(null, world, pos.posX, pos.posY, pos.posZ, player);
        }
        if (itemStack == null || itemStack.getItem() == null) {
            itemStack = setBlock.getBlock().getPickBlock(null, world, pos.posX, pos.posY, pos.posZ, player);
        }

        List<ItemStack> drops = setBlock.getBlock().getDrops(world, pos.posX, pos.posY, pos.posZ, 0, 0);
        int neededItems = 0;
        for (ItemStack drop : drops) {
            if (drop.getItem().equals(itemStack.getItem())) {
                neededItems++;
            }
        }
        if (neededItems == 0) {
            neededItems = 1;
        }

        if (!world.canMineBlock(player, pos.posX, pos.posY, pos.posZ)) {
            return false;
        }

        BlockSnapshot blockSnapshot = BlockSnapshot.getBlockSnapshot(world, pos.posX, pos.posY, pos.posZ);
        if (ForgeEventFactory.onPlayerBlockPlace(player, blockSnapshot, ForgeDirection.UP).isCanceled()) {
            return false;
        }

        ItemStack constructionPaste = new ItemStack(ModItems.constructionPaste);
        if (InventoryManipulation.countItem(itemStack, player, world) < neededItems) {
            //if (InventoryManipulation.countItem(constructionStack, player) == 0) {
            if (InventoryManipulation.countPaste(player) < neededItems) {
                return false;
            }
            itemStack = constructionPaste.copy();
            useConstructionPaste = true;
        }

        if (!this.canUse(heldItem, player)) {
            return false;
        }

        InventoryManipulation.getSilkTouchDrop(ModBlocks.constructionBlock, 0);

        boolean useItemSuccess;
        if (useConstructionPaste) {
            useItemSuccess = InventoryManipulation.usePaste(player, 1);
        } else {

            useItemSuccess = InventoryManipulation.useItem(itemStack, player, neededItems, world);
        }

        if (useItemSuccess) {
            this.applyDamage(heldItem, player);
            world.spawnEntityInWorld(new BlockBuildEntity(world, pos, player, setBlock, 1, GadgetUtils.getToolActualBlock(heldItem),
                    useConstructionPaste));
            return true;
        }

        return false;
    }

    public static ItemStack getGadget(EntityPlayer player) {
        ItemStack stack = GadgetGeneric.getGadget(player);
        if (!(stack.getItem() instanceof GadgetBuilding)) {
            return null;
        }

        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 20;
    }

}
