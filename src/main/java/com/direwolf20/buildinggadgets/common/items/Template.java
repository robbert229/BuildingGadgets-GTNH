package com.direwolf20.buildinggadgets.common.items;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.client.events.EventTooltip;
import com.direwolf20.buildinggadgets.client.gui.GuiProxy;
import com.direwolf20.buildinggadgets.common.tools.GadgetUtils;
import com.direwolf20.buildinggadgets.common.tools.WorldSave;
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
        String uuid = tagCompound.getString("UUID");
        if (uuid.isEmpty()) {
            UUID uid = UUID.randomUUID();
            tagCompound.setString("UUID", uid.toString());
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
            player.openGui(
                BuildingGadgets.instance,
                GuiProxy.MaterialListID,
                world,
                (int) player.posX,
                (int) player.posY,
                (int) player.posZ);
        }

        // if (world.isRemote) {
        // ClientGUI.open(createGUI());
        // }

        return stack; // Return the same item stack as per 1.7.10 convention
    }

    private static ModularScreen createGUI() {
        ModularPanel panel = ModularPanel.defaultPanel("tutorial_panel")
            .size(256, 200);

        panel.child(
            IKey.str("My first screen")
                .asWidget()
                .top(7)
                .left(7));

        return new ModularScreen(panel);
    }
}
