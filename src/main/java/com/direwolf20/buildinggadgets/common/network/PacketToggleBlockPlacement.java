package com.direwolf20.buildinggadgets.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetBuilding;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class PacketToggleBlockPlacement extends PacketEmpty {

    public static class Handler implements IMessageHandler<PacketToggleBlockPlacement, IMessage> {

        @Override
        public IMessage onMessage(PacketToggleBlockPlacement message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                return null;
            }

            EntityPlayer player = ctx.getServerHandler().playerEntity;
            ItemStack stack = GadgetGeneric.getGadget(player);
            if (stack.getItem() instanceof GadgetBuilding) {
                GadgetBuilding.togglePlaceAtop(player, stack);
            }

            return null;
        }
    }
}
