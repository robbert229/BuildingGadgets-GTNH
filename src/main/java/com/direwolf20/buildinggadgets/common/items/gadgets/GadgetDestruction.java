package com.direwolf20.buildinggadgets.common.items.gadgets;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.Constants;

import com.cleanroommc.modularui.factory.ClientGUI;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.direwolf20.buildinggadgets.client.gui.DestructionGUI;
import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlockTileEntity;
import com.direwolf20.buildinggadgets.common.blocks.ModBlocks;
import com.direwolf20.buildinggadgets.common.building.Region;
import com.direwolf20.buildinggadgets.common.building.placement.ConnectedSurface;
import com.direwolf20.buildinggadgets.common.config.SyncedConfig;
import com.direwolf20.buildinggadgets.common.entities.BlockBuildEntity;
import com.direwolf20.buildinggadgets.common.tools.*;
import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import com.direwolf20.buildinggadgets.util.VectorTools;
import com.direwolf20.buildinggadgets.util.WorldUtils;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;
import com.direwolf20.buildinggadgets.util.ref.NBTKeys;

public class GadgetDestruction extends GadgetGeneric {

    public GadgetDestruction() {
        super("destructiontool");
        setMaxDamage(SyncedConfig.durabilityDestruction);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return SyncedConfig.poweredByFE ? 0 : SyncedConfig.durabilityDestruction;
    }

    @Override
    public int getEnergyMax() {
        return SyncedConfig.energyMaxDestruction;
    }

    @Override
    public int getEnergyCost(ItemStack tool) {
        return SyncedConfig.energyCostDestruction * getCostMultiplier(tool);
    }

    @Override
    public int getDamageCost(ItemStack tool) {
        return SyncedConfig.damageCostDestruction * getCostMultiplier(tool);
    }

    @Override
    public void renderOverlay(RenderWorldLastEvent evt, EntityPlayer player, ItemStack heldItem) {
        ToolRenders.renderDestructionOverlay(evt, player, heldItem);
    }

    @Override
    public ModularScreen getShortcutMenuGUI(ItemStack itemStack, boolean temporarilyEnabled) {
        return new DestructionGUI(itemStack, temporarilyEnabled);
    }

    private int getCostMultiplier(ItemStack tool) {
        return (int) (SyncedConfig.nonFuzzyEnabledDestruction && !getFuzzy(tool)
            ? SyncedConfig.nonFuzzyMultiplierDestruction
            : 1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);

        list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("tooltip.gadget.destroywarning"));

        list.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocal("tooltip.gadget.connected_area")
                + ": "
                + getConnectedArea(stack));

        // Check for the configuration setting (or your method for it in 1.7.10)
        if (SyncedConfig.nonFuzzyEnabledDestruction) {
            list.add(
                EnumChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.gadget.fuzzy")
                    + ": "
                    + getFuzzy(stack));
        }

        addInformationRayTraceFluid(list, stack);
        addEnergyInformation(list, stack);
    }

    @Nullable
    public static String getUUID(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        String uuid = tagCompound.getString(NBTKeys.GADGET_UUID);
        if (uuid.isEmpty()) {
            UUID uid = UUID.randomUUID();
            tagCompound.setString(NBTKeys.GADGET_UUID, uid.toString());
            stack.setTagCompound(tagCompound);
            uuid = uid.toString();
        }
        return uuid;
    }

    public static void setAnchor(ItemStack stack, ChunkCoordinates pos) {
        GadgetUtils.writePOSToNBT(stack, pos, "anchor");
    }

    public static ChunkCoordinates getAnchor(ItemStack stack) {
        return GadgetUtils.getPOSFromNBT(stack, "anchor");
    }

    public static void setAnchorSide(ItemStack stack, EnumFacing side) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        if (side == null) {
            if (tagCompound.getTag("anchorSide") != null) {
                tagCompound.removeTag("anchorSide");
                stack.setTagCompound(tagCompound);
            }
            return;
        }
        tagCompound.setString("anchorSide", side.name());
        stack.setTagCompound(tagCompound);
    }

    public static EnumFacing getAnchorSide(ItemStack stack) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            return null;
        }

        String facing = tagCompound.getString("anchorSide");
        if (facing.isEmpty()) {
            return null;
        }

        return DirectionUtils.enumFacingByName(facing);
    }

    public static void setToolValue(ItemStack stack, int value, String valueName) {
        // Store the tool's range in NBT as an Integer
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        tagCompound.setInteger(valueName, value);
        stack.setTagCompound(tagCompound);
    }

    public static int getToolValue(ItemStack stack, String valueName) {
        // Store the tool's range in NBT as an Integer
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        return tagCompound.getInteger(valueName);
    }

    /**
     *
     * @param side
     * @param player
     * @return
     */
    private static List<EnumFacing> assignDirections(EnumFacing side, EntityPlayer player) {
        List<EnumFacing> dirs = new ArrayList<>();
        EnumFacing depth = DirectionUtils.getOppositeEnumFacing(side);
        boolean vertical = VectorTools.isAxisVertical(side);

        EnumFacing up = vertical ? VectorTools.getHorizontalFacingFromPlayer(player) : EnumFacing.UP;
        EnumFacing left = vertical ? VectorTools.rotateY(up) : VectorTools.rotateYCCW(side);
        EnumFacing right = DirectionUtils.getOppositeEnumFacing(left);

        if (side == EnumFacing.DOWN) {
            up = DirectionUtils.getOppositeEnumFacing(up);
        }

        EnumFacing down = DirectionUtils.getOppositeEnumFacing(up);
        dirs.add(left);
        dirs.add(right);
        dirs.add(up);
        dirs.add(down);
        dirs.add(depth);
        return dirs;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            if (player.isSneaking()) {
                ClientGUI.open(new DestructionGUI(stack));
                return stack;
            }

            return stack;
        }

        if (!player.isSneaking()) {
            MovingObjectPosition lookingAt = VectorTools.getLookingAt(player, stack);
            if (lookingAt == null && getAnchor(stack) == null) {
                // If we aren't looking at anything, exit
                return stack;
            }

            ChunkCoordinates startBlock = (getAnchor(stack) == null)
                ? VectorTools.getPosFromMovingObjectPosition(lookingAt)
                : getAnchor(stack);

            EnumFacing sideHit = (getAnchorSide(stack) == null) ? EnumFacing.getFront(lookingAt.sideHit)
                : getAnchorSide(stack);

            clearArea(world, startBlock, sideHit, player, stack);

            if (getAnchor(stack) != null) {
                setAnchor(stack, null);
                setAnchorSide(stack, null);

                player.addChatMessage(
                    new ChatComponentText(
                        EnumChatFormatting.AQUA
                            + new ChatComponentTranslation("message.gadget.anchorremove").getUnformattedText()));
            }
        }

        return stack;
    }

    public static void anchorBlocks(EntityPlayer player, ItemStack stack) {
        ChunkCoordinates currentAnchor = getAnchor(stack);
        if (currentAnchor == null) {
            MovingObjectPosition lookingAt = VectorTools.getLookingAt(player, stack);
            if (lookingAt == null) {
                return;
            }
            currentAnchor = VectorTools.getPosFromMovingObjectPosition(lookingAt);

            setAnchor(stack, currentAnchor);
            setAnchorSide(stack, EnumFacing.getFront(lookingAt.sideHit));

            player.addChatComponentMessage(
                new ChatComponentText(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("message.gadget.anchorrender")));
        } else {
            setAnchor(stack, null);
            setAnchorSide(stack, null);
            player.addChatComponentMessage(
                new ChatComponentText(
                    EnumChatFormatting.AQUA + StatCollector.translateToLocal("message.gadget.anchorremove")));
        }
    }

    public static Set<ChunkCoordinates> getArea(World world, ChunkCoordinates pos, EnumFacing incomingSide,
        EntityPlayer player, ItemStack stack) {
        int depth = getToolValue(stack, "depth");
        if (depth == 0) return Collections.emptySet();

        ChunkCoordinates startPos = (getAnchor(stack) == null) ? pos : getAnchor(stack);
        EnumFacing side = (getAnchorSide(stack) == null) ? incomingSide : getAnchorSide(stack);

        // Build the region
        List<EnumFacing> directions = assignDirections(side, player);
        String[] directionNames = new String[] { "left", "right", "up", "down", "depth" };
        Region selectionRegion = new Region(startPos);
        for (int i = 0; i < directionNames.length; i++) {
            var offset = ChunkCoordinateUtils
                .offset(startPos, directions.get(i), getToolValue(stack, directionNames[i]) - (i == 4 ? 1 : 0));;
            selectionRegion = selectionRegion.union(new Region(offset));
        }

        boolean fuzzy = !SyncedConfig.nonFuzzyEnabledDestruction || GadgetGeneric.getFuzzy(stack);
        Block stateTarget = fuzzy ? null : WorldUtils.getBlock(world, pos);
        if (GadgetGeneric.getConnectedArea(stack)) {
            return ConnectedSurface
                .create(
                    world,
                    selectionRegion,
                    searchPos -> searchPos,
                    startPos,
                    null,
                    (s, p) -> validBlock(world, p, player, s, fuzzy))
                .stream()
                .collect(Collectors.toSet());

        } else {
            return selectionRegion.stream()
                .filter(e -> validBlock(world, e, player, stateTarget, fuzzy))
                .collect(Collectors.toSet());
        }
    }

    private static boolean validBlock(World world, ChunkCoordinates voidPos, EntityPlayer player, Block block,
        boolean fuzzy) {
        var currentBlock = WorldUtils.getBlock(world, voidPos);
        if (!fuzzy && currentBlock != block) {
            return false;
        }

        var te = WorldUtils.getTileEntity(world, voidPos);
        if (currentBlock.isAir(world, voidPos.posX, voidPos.posY, voidPos.posZ)) {
            return false;
        }

        // if (currentBlock.getBlock().getMaterial(currentBlock).isLiquid()) return false;
        if (currentBlock.equals(ModBlocks.effectBlock)) {
            return false;
        }

        if ((te != null) && !(te instanceof ConstructionBlockTileEntity)) {
            return false;
        }

        if (currentBlock.getBlockHardness(world, voidPos.posX, voidPos.posY, voidPos.posZ) < 0) {
            return false;
        }

        ItemStack tool = getGadget(player);
        if (tool == null) {
            return false;
        }

        return WorldUtils.isBlockModifiable(player, voidPos, tool);
    }

    private void clearArea(World world, ChunkCoordinates pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        Set<ChunkCoordinates> voidPosArray = getArea(world, pos, side, player, stack);
        List<BlockPosState> blockList = new ArrayList<>();

        for (ChunkCoordinates voidPos : voidPosArray) {
            boolean isPaste;

            BlockState blockState = BlockState.getBlockState(world, voidPos);
            BlockState pasteState = new BlockState(Blocks.air, 0);

            if (blockState == null || blockState.isAir()) {
                continue;
            }

            if (blockState.getBlock() == ModBlocks.constructionBlock) {
                TileEntity te = world.getTileEntity(voidPos.posX, voidPos.posY, voidPos.posZ);
                if (te instanceof ConstructionBlockTileEntity cbte && cbte.getBlock() != null) {
                    pasteState = new BlockState(cbte.getActualBlock(), cbte.getActualBlockMeta());
                }
            }

            isPaste = pasteState != null && !pasteState.isAir();
            if (!destroyBlock(world, voidPos, player)) continue;

            blockList.add(new BlockPosState(voidPos, isPaste ? pasteState : blockState, isPaste));
        }

        if (blockList.size() > 0) {
            storeUndo(world, blockList, stack, player);
        }
    }

    private static void storeUndo(World world, List<BlockPosState> blockList, ItemStack stack, EntityPlayer player) {
        WorldSave worldSave = WorldSave.getWorldSaveDestruction(world);

        NBTTagCompound tagCompound = new NBTTagCompound();
        NBTTagList list = new NBTTagList();

        blockList.forEach(e -> list.appendTag(e.toCompound()));

        tagCompound.setTag("mapping", list);
        tagCompound.setInteger("dimension", player.dimension);

        worldSave.addToMap(getUUID(stack), tagCompound);
        worldSave.markForSaving();
    }

    public void undo(EntityPlayer player, ItemStack stack) {
        World world = player.worldObj;
        WorldSave worldSave = WorldSave.getWorldSaveDestruction(world);

        NBTTagCompound saveCompound = worldSave.getCompoundFromUUID(getUUID(stack));
        if (saveCompound == null) return;

        int dimension = saveCompound.getInteger("dimension");
        if (dimension != player.dimension) return;

        NBTTagList list = saveCompound.getTagList("mapping", Constants.NBT.TAG_COMPOUND);
        if (list.tagCount() == 0) return;

        boolean success = false;
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            BlockPosState posState = BlockPosState.fromCompound(compound);

            if (posState == null) return;

            // Check that there is no blocks where we want to put the new blocks.
            BlockState state = BlockState.getBlockState(world, posState.getPos());
            if (!state.isAir(state, world, posState.getPos()) && !state.getMaterial()
                .isLiquid()) return;

            // Per block place event to let mods override specific parts of the undo.
            var posStatePos = posState.getPos();
            BlockSnapshot blockSnapshot = BlockSnapshot
                .getBlockSnapshot(world, posStatePos.posX, posStatePos.posY, posStatePos.posZ);

            var placed = !GadgetGeneric.EmitEvent.placeBlock(player, blockSnapshot, EnumFacing.UP);
            if (placed) {
                continue;
            }

            var entity = new BlockBuildEntity(
                world,
                posState.getPos(),
                player,
                posState.getBlock(),
                1,
                posState.getBlock(),
                posState.isPaste());
            world.spawnEntityInWorld(entity);
            success = true;
        }

        if (success) {
            NBTTagCompound newTag = new NBTTagCompound();
            worldSave.addToMap(getUUID(stack), newTag);
            worldSave.markForSaving();
        }
    }

    private boolean destroyBlock(World world, ChunkCoordinates voidPos, EntityPlayer player) {
        ItemStack tool = getGadget(player);
        if (tool == null) return false;

        if (!this.canUse(tool, player)) return false;

        if (!GadgetGeneric.EmitEvent
            .breakBlock(world, voidPos, world.getBlock(voidPos.posX, voidPos.posY, voidPos.posZ), player)) return false;

        this.applyDamage(tool, player);

        world.spawnEntityInWorld(
            new BlockBuildEntity(
                world,
                voidPos,
                player,
                BlockState.getBlockState(world, voidPos),
                2,
                new BlockState(Blocks.air, 0),
                false));
        return true;
    }

    public static ItemStack getGadget(EntityPlayer player) {
        ItemStack stack = GadgetGeneric.getGadget(player);

        if (stack == null || !(stack.getItem() instanceof GadgetDestruction)) {
            return null;
        }

        return stack;
    }
}
