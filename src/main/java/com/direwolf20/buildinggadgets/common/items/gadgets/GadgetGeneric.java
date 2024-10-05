package com.direwolf20.buildinggadgets.common.items.gadgets;

import java.util.List;

import cofh.api.energy.IEnergyContainerItem;
import com.direwolf20.buildinggadgets.common.tools.DirectionUtils;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
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

//     @Override
//     @Nullable
//     public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound tag) {
//     return new MultiCapabilityProvider(new CapabilityProviderEnergy(stack, this::getEnergyMax), new
//     CapabilityProviderBlockProvider(stack));
//     }

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
        if (stack.getItem() instanceof IEnergyContainerItem energyContainerItem) {

            return 1D - ((double) energyContainerItem.getEnergyStored(stack) / (double) energyContainerItem.getMaxEnergyStored(stack));
        }

        return super.getDurabilityForDisplay(stack);
    }


//    public int getRGBDurabilityForDisplay(ItemStack stack) {
//        if (stack.getItem() instanceof IEnergyContainerItem energyItem) {
//            float energyRatio = Math.max(0.0F, (float) energyItem.getEnergyStored(stack) / (float) energyItem.getMaxEnergyStored(stack));
//            return MathHelper.hsvToRGB(energyRatio / 3.0F, 1.0F, 1.0F);
//        }
//        return super.getRGBDurabilityForDisplay(stack);
//    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        if (stack.getItem() instanceof IEnergyContainerItem energyItem) {
            return energyItem.getEnergyStored(stack) != energyItem.getMaxEnergyStored(stack);
        }

        return super.isDamaged(stack);
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("creative")) {
            return false;
        }

        if (stack.getItem() instanceof IEnergyContainerItem energyItem) {
            return energyItem.getEnergyStored(stack) != energyItem.getMaxEnergyStored(stack);
        }

        return super.showDurabilityBar(stack);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        if (toRepair.getItem() instanceof IEnergyContainerItem) {
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
        if (player.capabilities.isCreativeMode || getEnergyMax() == 0) {
            return true;
        }

        if (tool.getItem() instanceof IEnergyContainerItem energyItem) {
            return getEnergyCost(tool) <= energyItem.getEnergyStored(tool);
        }
        return tool.getMaxDamage() <= 0 || tool.getItemDamage() < tool.getMaxDamage();
    }

    public void applyDamage(ItemStack tool, EntityPlayer player) {
        if (player.capabilities.isCreativeMode || getEnergyMax() == 0) {
            return;
        }

        if (tool.getItem() instanceof IEnergyContainerItem) {
            IEnergyContainerItem energyItem = (IEnergyContainerItem) tool.getItem();
            energyItem.extractEnergy(tool, getEnergyCost(tool), false);
        } else {
            tool.damageItem(getDamageCost(tool), player);
        }
    }

    protected void addEnergyInformation(List<String> list, ItemStack stack) {
        if (getEnergyMax() == 0) {
            return;
        }
        if (stack.getItem() instanceof IEnergyContainerItem energyItem) {
            list.add(ChatFormatting.WHITE + I18n.format("tooltip.gadget.energy") + ": " +
                    energyItem.getEnergyStored(stack) + "/" + energyItem.getMaxEnergyStored(stack));
        }
    }
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
        tooltip.add(ChatFormatting.BLUE + I18n.format("tooltip.gadget.raytrace_fluid") + ": " + shouldRayTraceFluid(stack));
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
            BlockEvent.PlaceEvent event = ForgeEventFactory.onPlayerBlockPlace(player, snapshot, DirectionUtils.toForgeDirection(direction));
            return !event.isCanceled();
        }
    }
}
