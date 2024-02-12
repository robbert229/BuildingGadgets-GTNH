package com.direwolf20.buildinggadgets.common.items.gadgets;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.world.BlockEvent;

import com.direwolf20.buildinggadgets.common.config.SyncedConfig;
import com.direwolf20.buildinggadgets.common.items.ItemModBase;
import com.direwolf20.buildinggadgets.common.tools.NBTTool;
import com.mojang.realmsclient.gui.ChatFormatting;

public abstract class GadgetGeneric extends ItemModBase {

    public GadgetGeneric(String name) {
        super(name);

        setMaxStackSize(1);
    }

    public int getEnergyMax() {
        return SyncedConfig.energyMax;
    }

    // @Override
    // @Nullable
    // public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound tag) {
    // return new MultiCapabilityProvider(new CapabilityProviderEnergy(stack, this::getEnergyMax), new
    // CapabilityProviderBlockProvider(stack));
    // }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public boolean isRepairable() {
        return false;
    }

    // @Override
    // public double getDurabilityForDisplay(ItemStack stack) {
    // if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
    // IEnergyStorage energy = CapabilityProviderEnergy.getCap(stack);
    // return 1D - ((double) energy.getEnergyStored() / (double) energy.getMaxEnergyStored());
    // }
    // //return (double)stack.getItemDamage() / (double)stack.getMaxDamage();
    // return super.getDurabilityForDisplay(stack);
    // }

    // @Override
    // public int getRGBDurabilityForDisplay(ItemStack stack) {
    // if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
    // IEnergyStorage energy = CapabilityProviderEnergy.getCap(stack);
    // return MathHelper.hsvToRGB(Math.max(0.0F, (float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored())
    // / 3.0F, 1.0F, 1.0F);
    // }
    // //return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - getDurabilityForDisplay(stack))) / 3.0F, 1.0F, 1.0F);
    // return super.getRGBDurabilityForDisplay(stack);
    // }

    // @Override
    // public boolean isDamaged(ItemStack stack) {
    // if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
    // IEnergyStorage energy = CapabilityProviderEnergy.getCap(stack);
    // return energy.getEnergyStored() != energy.getMaxEnergyStored();
    // }
    // //return (stack.getItemDamage() > 0);
    // return super.isDamaged(stack);
    // }

    // @Override
    // public boolean showDurabilityBar(ItemStack stack) {
    // if (stack.hasTagCompound() && stack.getTagCompound().hasKey("creative", Constants.NBT.TAG_BYTE))
    // return false;
    //
    // if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
    // IEnergyStorage energy = CapabilityProviderEnergy.getCap(stack);
    // return energy.getEnergyStored() != energy.getMaxEnergyStored();
    // }
    // //return stack.isItemDamaged();
    // return super.showDurabilityBar(stack);
    // }

    // @Override
    // public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    // if (toRepair.hasCapability(CapabilityEnergy.ENERGY, null)) {
    // return false;
    // }
    //
    // if (repair.getItem() == Items.diamond) {
    // return true;
    // }
    //
    // return false;
    // }

    public static ItemStack getGadget(EntityPlayer player) {
        ItemStack heldItem = player.getHeldItem();
        if (!(heldItem.getItem() instanceof GadgetGeneric)) {
            return null;
        }
        return heldItem;
    }

    public abstract int getEnergyCost(ItemStack tool);

    public abstract int getDamageCost(ItemStack tool);

    // public boolean canUse(ItemStack tool, EntityPlayer player) {
    // // You can always use in creative or when max energy is set to 0
    // if (player.capabilities.isCreativeMode || getEnergyMax() == 0)
    // return true;
    //
    // if (tool.hasCapability(CapabilityEnergy.ENERGY, null)) {
    // IEnergyStorage energy = CapabilityProviderEnergy.getCap(tool);
    // return getEnergyCost(tool) <= energy.getEnergyStored();
    // }
    // return tool.getMaxDamage() <= 0 || tool.getItemDamage() < tool.getMaxDamage() || tool.isItemStackDamageable();
    // }
    //
    // public void applyDamage(ItemStack tool, EntityPlayer player) {
    // // don't apply damage in creative or if there is no power to be had
    // if (player.capabilities.isCreativeMode || getEnergyMax() == 0)
    // return;
    //
    // if (tool.hasCapability(CapabilityEnergy.ENERGY, null)) {
    // ItemEnergyForge energy = (ItemEnergyForge) CapabilityProviderEnergy.getCap(tool);
    // energy.extractPower(getEnergyCost(tool), false);
    // } else
    // tool.damageItem(getDamageCost(tool), player);
    // }
    //
    // protected void addEnergyInformation(List<String> list, ItemStack stack) {
    // // Don't display energy for gadgets that can't accept it.
    // if( getEnergyMax() == 0 ) return;
    // if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
    // IEnergyStorage energy = CapabilityProviderEnergy.getCap(stack);
    // list.add(TextFormatting.WHITE + I18n.format("tooltip.gadget.energy") + ": " +
    // withSuffix(energy.getEnergyStored()) + "/" + withSuffix(energy.getMaxEnergyStored()));
    // }
    // }

    public static boolean getFuzzy(ItemStack stack) {
        return NBTTool.getOrNewTag(stack)
            .getBoolean("fuzzy");
    }

    public static void toggleFuzzy(EntityPlayer player, ItemStack stack) {
        NBTTool.getOrNewTag(stack)
            .setBoolean("fuzzy", !getFuzzy(stack));
        player.addChatComponentMessage(
            new ChatComponentText(
                ChatFormatting.AQUA
                    + new ChatComponentTranslation("message.gadget.fuzzymode").getUnformattedTextForChat()
                    + ": "
                    + getFuzzy(stack)));
    }

    public static boolean getConnectedArea(ItemStack stack) {
        return !NBTTool.getOrNewTag(stack)
            .getBoolean("unconnectedarea");
    }

    public static void toggleConnectedArea(EntityPlayer player, ItemStack stack) {
        NBTTool.getOrNewTag(stack)
            .setBoolean("unconnectedarea", getConnectedArea(stack));
        // TODO(johnrowl) re-enable to destruction gadget support here.
        // String suffix = stack.getItem() instanceof GadgetDestruction ? "area" : "surface";
        String suffix = "surface";
        player.addChatComponentMessage(
            new ChatComponentText(
                ChatFormatting.AQUA
                    + new ChatComponentTranslation("message.gadget.connected" + suffix).getUnformattedTextForChat()
                    + ": "
                    + getConnectedArea(stack)));
    }

    public static boolean shouldRayTraceFluid(ItemStack stack) {
        return NBTTool.getOrNewTag(stack)
            .getBoolean("raytrace_fluid");
    }

    public static void toggleRayTraceFluid(EntityPlayer player, ItemStack stack) {
        NBTTool.getOrNewTag(stack)
            .setBoolean("raytrace_fluid", !shouldRayTraceFluid(stack));
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

        protected static boolean placeBlock(EntityPlayer player, BlockSnapshot snapshot, ForgeDirection direction) {
            BlockEvent.PlaceEvent event = ForgeEventFactory.onPlayerBlockPlace(player, snapshot, direction);

            return !event.isCanceled();
        }
    }
}
