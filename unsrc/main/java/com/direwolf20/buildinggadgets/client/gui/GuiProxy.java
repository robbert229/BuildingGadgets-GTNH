package com.direwolf20.buildinggadgets.client.gui;

import com.direwolf20.buildinggadgets.client.gui.materiallist.MaterialListGUI;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerContainer;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerGUI;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;
import com.direwolf20.buildinggadgets.common.items.ITemplate;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetCopyPaste;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetDestruction;
import com.direwolf20.buildinggadgets.common.tools.InventoryManipulation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiProxy implements IGuiHandler {

    public static final int CopyPasteID = 0;
    public static final int DestructionID = 1;
    public static final int PasteID = 2;
    public static final int MaterialListID = 3;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
        TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
        if (te instanceof TemplateManagerTileEntity) {
            return new TemplateManagerContainer(player.inventory, (TemplateManagerTileEntity) te);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ChunkCoordinates pos = new ChunkCoordinates(x, y, z);
        TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);
        if (te instanceof TemplateManagerTileEntity) {
            TemplateManagerTileEntity containerTileEntity = (TemplateManagerTileEntity) te;
            return new TemplateManagerGUI(containerTileEntity, new TemplateManagerContainer(player.inventory, containerTileEntity));
        }
        if (ID == CopyPasteID) {

            if (player.getHeldItem().getItem() instanceof GadgetCopyPaste) {
                return new CopyPasteGUI(player.getHeldItem());
            }
            //else if (player.getHeldItemOffhand().getItem() instanceof GadgetCopyPaste) {
            //    return new CopyPasteGUI(player.getHeldItemOffhand());
            //}
            else {
                return null;
            }
        } else if (ID == DestructionID) {
            if (player.getHeldItem().getItem() instanceof GadgetDestruction) {
                return new DestructionGUI(player.getHeldItem());
            }
            //else if (player.getHeldItemOffhand().getItem() instanceof GadgetDestruction) {
            //    return new DestructionGUI(player.getHeldItemOffhand());
            //}
            else {
                return null;
            }
        } else if (ID == PasteID) {
            if (player.getHeldItem().getItem() instanceof GadgetCopyPaste) {
                return new PasteGUI(player.getHeldItem());
            }
            //else if (player.getHeldItemOffhand().getItem() instanceof GadgetCopyPaste) {
            //    return new PasteGUI(player.getHeldItemOffhand());
            //}
            else {
                return null;
            }
        } else if (ID == MaterialListID) {
            ItemStack template = InventoryManipulation.getStackInEitherHand(player, ITemplate.class);
            if (template != null && template.getItem() != null){
                return new MaterialListGUI(template);
            }

            return null;
        }
        return null;
    }

}
