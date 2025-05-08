package com.direwolf20.buildinggadgets.client.gui;

// import com.direwolf20.buildinggadgets.client.gui.materiallist.MaterialListGUI;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerContainer;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerGUI;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetCopyPaste;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetDestruction;

import cpw.mods.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {

    public static final int CopyPasteID = 0;
    public static final int DestructionID = 1;
    public static final int PasteID = 2;
    public static final int MaterialListID = 3;

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

        if (ID == CopyPasteID) {
            if (player.getHeldItem() != null && player.getHeldItem()
                .getItem() instanceof GadgetCopyPaste) {
                return new CopyPasteGUI(player.getHeldItem());
            } else {
                return null;
            }
        } else if (ID == DestructionID) {
            if (player.getHeldItem() != null && player.getHeldItem()
                .getItem() instanceof GadgetDestruction) {
                return new DestructionGUI(player.getHeldItem());
            } else return null;
        } else if (ID == PasteID) {
            if (player.getHeldItem() != null && player.getHeldItem()
                .getItem() instanceof GadgetCopyPaste) return new PasteGUI(player.getHeldItem());
            else return null;
        }

        return null;
    }

}
