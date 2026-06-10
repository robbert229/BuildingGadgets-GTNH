package com.direwolf20.buildinggadgets.common.items.gadgets;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;

import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.GadgetsConfig;
import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.GeneralConfig;
import com.direwolf20.buildinggadgets.common.items.ItemModBase;
import com.direwolf20.buildinggadgets.common.tools.DirectionUtils;
import com.direwolf20.buildinggadgets.util.NBTTool;
import com.direwolf20.buildinggadgets.util.ref.NBTKeys;
import com.mojang.realmsclient.gui.ChatFormatting;

import cofh.api.energy.IEnergyContainerItem;

public abstract class GadgetGeneric extends ItemModBase implements IEnergyContainerItem {

    public GadgetGeneric(String name) {
        super(name);

        setMaxStackSize(1);
    }

    public int getEnergyMax() {
        return GadgetsConfig.maxEnergy;
    }

    protected boolean usesEnergySystem() {
        return GeneralConfig.poweredByFE && getEnergyMax() > 0;
    }

    protected static boolean isCreativeGadget(ItemStack stack) {
        return stack != null && stack.hasTagCompound()
            && stack.getTagCompound()
                .hasKey(NBTKeys.CREATIVE_MARKER);
    }

    private int getMaxReceive(ItemStack stack) {
        int max = getMaxEnergyStored(stack);
        if (max <= 0) {
            return 0;
        }

        return Math.max(1, max / 100);
    }

    private void setEnergyStored(ItemStack stack, int energy) {
        if (stack == null) {
            return;
        }

        int clamped = Math.max(0, Math.min(getMaxEnergyStored(stack), energy));
        NBTTool.getOrNewTag(stack)
            .setInteger(NBTKeys.ENERGY, clamped);
    }

    @Override
    public void onCreated(ItemStack stack, World world, EntityPlayer player) {
        super.onCreated(stack, world, player);
        if (usesEnergySystem() && !isCreativeGadget(stack)) {
            setEnergyStored(stack, 0);
        }
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (usesEnergySystem() && stack.getItem() instanceof IEnergyContainerItem energyContainerItem) {

            return 1D - ((double) energyContainerItem.getEnergyStored(stack)
                / (double) energyContainerItem.getMaxEnergyStored(stack));
        }

        return super.getDurabilityForDisplay(stack);
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        if (usesEnergySystem() && stack.getItem() instanceof IEnergyContainerItem energyItem) {
            return energyItem.getEnergyStored(stack) != energyItem.getMaxEnergyStored(stack);
        }

        return super.isDamaged(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (isCreativeGadget(stack)) {
            return false;
        }

        if (usesEnergySystem() && stack.getItem() instanceof IEnergyContainerItem energyItem) {
            return energyItem.getEnergyStored(stack) != energyItem.getMaxEnergyStored(stack);
        }

        return super.showDurabilityBar(stack);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        if (usesEnergySystem()) {
            return false;
        }

        return repair.getItem() == Items.diamond || super.getIsRepairable(toRepair, repair);
    }

    public static ItemStack getGadget(EntityPlayer player) {
        ItemStack heldItem = player.getHeldItem();
        if (heldItem == null || !(heldItem.getItem() instanceof GadgetGeneric)) {
            return null;
        }
        return heldItem;
    }

    public abstract int getEnergyCost(ItemStack tool);

    public abstract int getDamageCost(ItemStack tool);

    public boolean canUse(ItemStack tool, EntityPlayer player) {
        if (player.capabilities.isCreativeMode || !usesEnergySystem()) {
            return true;
        }

        if (isCreativeGadget(tool)) {
            return true;
        }

        if (tool.getItem() instanceof IEnergyContainerItem energyItem) {
            return getEnergyCost(tool) <= energyItem.getEnergyStored(tool);
        }
        return tool.getMaxDamage() <= 0 || tool.getItemDamage() < tool.getMaxDamage();
    }

    public void applyDamage(ItemStack tool, EntityPlayer player) {
        consumeUse(tool, player);
    }

    protected boolean consumeUse(ItemStack tool, EntityPlayer player) {
        if (player.capabilities.isCreativeMode || isCreativeGadget(tool)) {
            return true;
        }

        if (!usesEnergySystem()) {
            if (!(tool.getItem() instanceof IEnergyContainerItem)) {
                tool.damageItem(getDamageCost(tool), player);
            }
            return true;
        }

        if (tool.getItem() instanceof IEnergyContainerItem energyItem) {
            int cost = getEnergyCost(tool);
            if (cost <= 0) {
                return true;
            }
            if (energyItem.extractEnergy(tool, cost, true) < cost) {
                return false;
            }
            energyItem.extractEnergy(tool, cost, false);
            return true;
        }

        tool.damageItem(getDamageCost(tool), player);
        return true;
    }

    protected void addEnergyInformation(List<String> list, ItemStack stack) {
        if (!usesEnergySystem()) {
            return;
        }
        if (stack.getItem() instanceof IEnergyContainerItem energyItem) {
            list.add(
                ChatFormatting.WHITE + I18n.format("tooltip.gadget.energy")
                    + ": "
                    + energyItem.getEnergyStored(stack)
                    + "/"
                    + energyItem.getMaxEnergyStored(stack));
        }
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        if (!usesEnergySystem() || isCreativeGadget(container) || maxReceive <= 0) {
            return 0;
        }

        int stored = getEnergyStored(container);
        int space = getMaxEnergyStored(container) - stored;
        if (space <= 0) {
            return 0;
        }

        int received = Math.min(space, Math.min(getMaxReceive(container), maxReceive));
        if (!simulate && received > 0) {
            setEnergyStored(container, stored + received);
        }
        return received;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if (!usesEnergySystem() || isCreativeGadget(container) || maxExtract <= 0) {
            return 0;
        }

        int stored = getEnergyStored(container);
        int extracted = Math.min(stored, maxExtract);
        if (!simulate && extracted > 0) {
            setEnergyStored(container, stored - extracted);
        }
        return extracted;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        if (!usesEnergySystem()) {
            return 0;
        }
        if (isCreativeGadget(container)) {
            return getMaxEnergyStored(container);
        }

        int max = getMaxEnergyStored(container);
        if (max <= 0 || container == null || !container.hasTagCompound()) {
            return 0;
        }

        int stored = container.getTagCompound()
            .getInteger(NBTKeys.ENERGY);
        return Math.max(0, Math.min(max, stored));
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return usesEnergySystem() ? getEnergyMax() : 0;
    }

    public static boolean getFuzzy(ItemStack stack) {
        return NBTTool.getOrNewTag(stack)
            .getBoolean(NBTKeys.GADGET_FUZZY);
    }

    public static void toggleFuzzy(EntityPlayer player, ItemStack stack) {
        NBTTool.getOrNewTag(stack)
            .setBoolean(NBTKeys.GADGET_FUZZY, !getFuzzy(stack));
        player.addChatComponentMessage(
            new ChatComponentText(
                ChatFormatting.AQUA
                    + new ChatComponentTranslation("message.gadget.fuzzymode").getUnformattedTextForChat()
                    + ": "
                    + getFuzzy(stack)));
    }

    public static boolean getConnectedArea(ItemStack stack) {
        return !NBTTool.getOrNewTag(stack)
            .getBoolean(NBTKeys.GADGET_UNCONNECTED_AREA);
    }

    public static void toggleConnectedArea(EntityPlayer player, ItemStack stack) {
        NBTTool.getOrNewTag(stack)
            .setBoolean(NBTKeys.GADGET_UNCONNECTED_AREA, getConnectedArea(stack));
        String suffix = stack.getItem() instanceof GadgetDestruction ? "area" : "surface";
        player.addChatComponentMessage(
            new ChatComponentText(
                ChatFormatting.AQUA
                    + new ChatComponentTranslation("message.gadget.connected" + suffix).getUnformattedTextForChat()
                    + ": "
                    + getConnectedArea(stack)));
    }

    public static boolean shouldRayTraceFluid(ItemStack stack) {
        return NBTTool.getOrNewTag(stack)
            .getBoolean(NBTKeys.GADGET_RAYTRACE_FLUID);
    }

    public static void toggleRayTraceFluid(EntityPlayer player, ItemStack stack) {
        NBTTool.getOrNewTag(stack)
            .setBoolean(NBTKeys.GADGET_RAYTRACE_FLUID, !shouldRayTraceFluid(stack));
        player.addChatComponentMessage(
            new ChatComponentText(
                ChatFormatting.AQUA
                    + new ChatComponentTranslation("message.gadget.raytrace_fluid").getUnformattedTextForChat()
                    + ": "
                    + shouldRayTraceFluid(stack)));
    }

    public static void addInformationRayTraceFluid(List<String> tooltip, ItemStack stack) {
        tooltip.add(
            ChatFormatting.BLUE + I18n.format("tooltip.gadget.raytrace_fluid") + ": " + shouldRayTraceFluid(stack));
    }

    public static class EmitEvent {

        protected static boolean breakBlock(World world, ChunkCoordinates pos, Block block, EntityPlayer player) {
            // TODO(johnrowl) do I need to introduce a specific metadata?
            BlockEvent.BreakEvent breakEvent = new BlockEvent.BreakEvent(
                pos.posX,
                pos.posY,
                pos.posZ,
                world,
                block,
                0,
                player);
            MinecraftForge.EVENT_BUS.post(breakEvent);

            return !breakEvent.isCanceled();
        }

        protected static boolean placeBlock(EntityPlayer player, BlockSnapshot snapshot, EnumFacing direction) {
            BlockEvent.PlaceEvent event = ForgeEventFactory
                .onPlayerBlockPlace(player, snapshot, DirectionUtils.toForgeDirection(direction));
            return !event.isCanceled();
        }
    }

    /// renderOverlay will render the gadget specific overlay. This will be things like highlighting the blocks to be
    /// deleted for the destruction gadget, or the outline of blocks to be copied for the copy/paste gadget.
    public abstract void renderOverlay(RenderWorldLastEvent evt, EntityPlayer player, ItemStack heldItem);

    /// getShortcutMenuGUI returns the ModularUI gui to use for the gadget when the user presses the menu shortcut.
    public abstract void openShortcutMenu(ItemStack itemStack, boolean temporarilyEnabled);

    /// anchorBlocks sets the anchor to what the player is looking at.
    public abstract void anchorBlocks(EntityPlayer player, ItemStack stack);
}
