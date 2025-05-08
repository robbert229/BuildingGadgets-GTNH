package com.direwolf20.buildinggadgets.common.items;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import com.cleanroommc.modularui.factory.ClientGUI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import com.direwolf20.buildinggadgets.client.events.EventTooltip;
import com.direwolf20.buildinggadgets.client.gui.MaterialListGUI;
import com.direwolf20.buildinggadgets.common.tools.GadgetUtils;
import com.direwolf20.buildinggadgets.common.tools.WorldSave;
import com.direwolf20.buildinggadgets.util.ref.NBTKeys;
import com.mojang.realmsclient.gui.ChatFormatting;

public class Template extends ItemModBase implements ITemplate {

    public Template() {
        super("template");
        setMaxStackSize(1);
    }

    @Override
    public WorldSave getWorldSave(World world) {
        return WorldSave.getTemplateWorldSave(world);
    }

    @Override
    @Nullable
    public String getUUID(ItemStack stack) {
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

    public static void setName(ItemStack stack, String name) {
        GadgetUtils.writeStringToNBT(stack, name, "TemplateName");
    }

    public static String getName(ItemStack stack) {
        return GadgetUtils.getStringFromNBT(stack, "TemplateName");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        // Add tool information to the tooltip
        super.addInformation(stack, player, list, b);
        list.add(ChatFormatting.AQUA + StatCollector.translateToLocal("tooltip.template.name") + ": " + getName(stack));
        EventTooltip.addTemplatePadding(stack, list);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
        float hitX, float hitY, float hitZ) {
        // Open GUI
        onItemRightClick(stack, world, player);
        return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            ClientGUI.open(MaterialListGUI.createGUI(stack));
        }

        return stack; // Return the same item stack as per 1.7.10 convention
    }
}
