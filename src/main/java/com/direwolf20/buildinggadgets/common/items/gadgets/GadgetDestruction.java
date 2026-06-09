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
import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.GadgetsConfig.GadgetDestructionConfig;
import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.GeneralConfig;
import com.direwolf20.buildinggadgets.client.gui.DestructionGUI;
import com.direwolf20.buildinggadgets.common.blocks.ConstructionBlockTileEntity;
import com.direwolf20.buildinggadgets.common.blocks.ModBlocks;
import com.direwolf20.buildinggadgets.common.building.Region;
import com.direwolf20.buildinggadgets.common.building.placement.ConnectedSurface;
import com.direwolf20.buildinggadgets.common.entities.BlockBuildEntity;
import com.direwolf20.buildinggadgets.common.tools.*;
import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import com.direwolf20.buildinggadgets.util.VectorTools;
import com.direwolf20.buildinggadgets.util.WorldUtils;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;
import com.direwolf20.buildinggadgets.util.ref.NBTKeys;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GadgetDestruction extends GadgetGeneric {

    /**
     * Initializes the destruction gadget with configured durability.
     */
    public GadgetDestruction() {
        super("destructiontool");
        setMaxDamage(GadgetDestructionConfig.durabilityDestruction);
    }

    /**
     * Returns max damage for durability mode, or {@code 0} when FE mode is enabled.
     *
     * @param stack gadget stack
     * @return max damage value for the current power mode
     */
    @Override
    public int getMaxDamage(ItemStack stack) {
        return GeneralConfig.poweredByFE ? 0 : GadgetDestructionConfig.durabilityDestruction;
    }

    /**
     * Returns the configured FE capacity for this gadget.
     *
     * @return max FE capacity
     */
    @Override
    public int getEnergyMax() {
        return GadgetDestructionConfig.energyMaxDestruction;
    }

    /**
     * Returns FE cost per action, including optional non-fuzzy multiplier.
     *
     * @param tool gadget stack
     * @return FE cost for one destruction action
     */
    @Override
    public int getEnergyCost(ItemStack tool) {
        return GadgetDestructionConfig.energyCostDestruction * getCostMultiplier(tool);
    }

    /**
     * Returns durability cost per action, including optional non-fuzzy multiplier.
     *
     * @param tool gadget stack
     * @return durability cost for one destruction action
     */
    @Override
    public int getDamageCost(ItemStack tool) {
        return GadgetDestructionConfig.damageCostDestruction * getCostMultiplier(tool);
    }

    /**
     * Renders the destruction selection preview in-world.
     *
     * @param evt current render event
     * @param player rendering player
     * @param heldItem held gadget stack
     */
    @Override
    public void renderOverlay(RenderWorldLastEvent evt, EntityPlayer player, ItemStack heldItem) {
        ToolRenders.renderDestructionOverlay(evt, player, heldItem);
    }

    /**
     * Opens the destruction gadget shortcut menu on the client.
     *
     * @param itemStack gadget stack
     * @param temporarilyEnabled whether temporary mode is active
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void openShortcutMenu(ItemStack itemStack, boolean temporarilyEnabled) {
        var p = new DestructionGUI(itemStack, temporarilyEnabled);
        ClientGUI.open(p);
    }

    // Non-fuzzy mode can be configured to cost more, so precision targeting has a balancing tradeoff.
    private int getCostMultiplier(ItemStack tool) {
        return (int) (GadgetDestructionConfig.nonFuzzyEnabled && !getFuzzy(tool)
            ? GadgetDestructionConfig.nonFuzzyMultiplier
            : 1);
    }

    /**
     * Adds tooltip lines for warning, mode state, and energy/fluid settings.
     *
     * @param stack gadget stack
     * @param player local player
     * @param list tooltip lines to append to
     * @param advanced whether advanced tooltips are enabled
     */
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);

        list.add(EnumChatFormatting.RED + StatCollector.translateToLocal("tooltip.gadget.destroywarning"));

        list.add(
            EnumChatFormatting.YELLOW + StatCollector.translateToLocal("tooltip.gadget.connected_area")
                + ": "
                + getConnectedArea(stack));

        // Check for the configuration setting (or your method for it in 1.7.10)
        if (GadgetDestructionConfig.nonFuzzyEnabled) {
            list.add(
                EnumChatFormatting.GOLD + StatCollector.translateToLocal("tooltip.gadget.fuzzy")
                    + ": "
                    + getFuzzy(stack));
        }

        addInformationRayTraceFluid(list, stack);
        addEnergyInformation(list, stack);
    }

    /**
     * Returns a persistent UUID stored on the stack, creating one if missing.
     *
     * @param stack gadget stack
     * @return gadget UUID from NBT
     */
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

    /**
     * Persists the current anchor position to NBT.
     *
     * @param stack gadget stack
     * @param pos anchor position, or {@code null} to clear
     */
    public static void setAnchor(ItemStack stack, ChunkCoordinates pos) {
        GadgetUtils.writePOSToNBT(stack, pos, "anchor");
    }

    /**
     * Reads the stored anchor position from NBT.
     *
     * @param stack gadget stack
     * @return stored anchor position, or {@code null} when absent
     */
    public static ChunkCoordinates getAnchor(ItemStack stack) {
        return GadgetUtils.getPOSFromNBT(stack, "anchor");
    }

    /**
     * Persists or clears the anchor side used for anchored operations.
     *
     * @param stack gadget stack
     * @param side anchor side, or {@code null} to clear
     */
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

    /**
     * Reads the stored anchor side from NBT, or {@code null} when unset.
     *
     * @param stack gadget stack
     * @return stored anchor side or {@code null}
     */
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

    /**
     * Stores a numeric area setting ({@code left/right/up/down/depth}) in NBT.
     *
     * @param stack gadget stack
     * @param value value to store
     * @param valueName NBT key name
     */
    public static void setToolValue(ItemStack stack, int value, String valueName) {
        // Store the tool's range in NBT as an Integer
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        tagCompound.setInteger(valueName, value);
        stack.setTagCompound(tagCompound);
    }

    /**
     * Reads a numeric area setting from NBT.
     *
     * @param stack gadget stack
     * @param valueName NBT key name
     * @return stored value, or {@code 0} when missing
     */
    public static int getToolValue(ItemStack stack, String valueName) {
        // Store the tool's range in NBT as an Integer
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        return tagCompound.getInteger(valueName);
    }

    // Resolves local left/right/up/down/depth directions based on hit side and player facing.
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

    /**
     * Triggers destruction on right click (server-side), using anchor when present.
     *
     * @param stack gadget stack
     * @param world world context
     * @param player acting player
     * @return original gadget stack
     */
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            if (player.isSneaking()) {
                // ClientGUI.open(new DestructionGUI(stack));
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

    /**
     * Toggles anchor state at the looked-at block and reports result to chat.
     *
     * @param player acting player
     * @param stack gadget stack
     */
    @Override
    public void anchorBlocks(EntityPlayer player, ItemStack stack) {
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

    /**
     * Computes the candidate destruction area, then filters by mode and block validity.
     *
     * @param world world context
     * @param pos clicked position
     * @param incomingSide clicked face
     * @param player acting player
     * @param stack gadget stack
     * @return valid positions that can be destroyed
     */
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

        boolean fuzzy = !GadgetDestructionConfig.nonFuzzyEnabled || GadgetGeneric.getFuzzy(stack);
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

    // Validates whether a block can be targeted for destruction under current constraints.
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

        return WorldUtils.isBlockModifiableUsingItem(player, voidPos, tool);
    }

    // Destroys all valid blocks in area and stores undo data for successful removals.
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

            if (blockState.block() == ModBlocks.constructionBlock) {
                TileEntity te = world.getTileEntity(voidPos.posX, voidPos.posY, voidPos.posZ);
                if (te instanceof ConstructionBlockTileEntity cbte && cbte.getBlock() != null) {
                    pasteState = new BlockState(cbte.getActualBlock(), cbte.getActualBlockMeta());
                }
            }

            isPaste = pasteState != null && !pasteState.isAir();
            if (!destroyBlock(world, voidPos, player)) continue;

            blockList.add(new BlockPosState(voidPos, isPaste ? pasteState : blockState, isPaste));
        }

        if (!blockList.isEmpty()) {
            storeUndo(world, blockList, stack, player);
        }
    }

    // Writes destruction history for this gadget UUID so undo can restore affected blocks.
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

    /**
     * Replays stored destruction history by spawning placement entities when space is available.
     *
     * @param player acting player
     * @param stack gadget stack
     */
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

    // Performs one block-destruction action with permission, event, and cost checks.
    private boolean destroyBlock(World world, ChunkCoordinates voidPos, EntityPlayer player) {
        ItemStack tool = getGadget(player);
        if (tool == null) return false;

        if (!this.canUse(tool, player)) return false;

        if (!GadgetGeneric.EmitEvent
            .breakBlock(world, voidPos, world.getBlock(voidPos.posX, voidPos.posY, voidPos.posZ), player)) return false;

        if (!this.consumeUse(tool, player)) return false;

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

    /**
     * Returns the held gadget only when it is a destruction gadget.
     *
     * @param player acting player
     * @return held destruction gadget stack, or {@code null}
     */
    public static ItemStack getGadget(EntityPlayer player) {
        ItemStack stack = GadgetGeneric.getGadget(player);

        if (stack == null || !(stack.getItem() instanceof GadgetDestruction)) {
            return null;
        }

        return stack;
    }
}
