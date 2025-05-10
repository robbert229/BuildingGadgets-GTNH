package com.direwolf20.buildinggadgets.client.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerContainer;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerGUI;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TemplateManagerTileEntity) {
            return new TemplateManagerContainer(player.inventory, (TemplateManagerTileEntity) te);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof TemplateManagerTileEntity containerTileEntity) {
            return new TemplateManagerGUI(
                containerTileEntity,
                new TemplateManagerContainer(player.inventory, containerTileEntity));
        }

        return null;
    }

}
