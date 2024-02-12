package com.direwolf20.buildinggadgets.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class PacketToggleRayTraceFluid extends PacketEmpty {

    public static class Handler implements IMessageHandler<PacketToggleRayTraceFluid, IMessage> {

        @Override
        public IMessage onMessage(PacketToggleRayTraceFluid message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                return null;
            }

            EntityPlayer player = ctx.getServerHandler().playerEntity;
            ItemStack stack = GadgetGeneric.getGadget(player);
            if (stack != null && stack.getItem() != null) {
                GadgetGeneric.toggleRayTraceFluid(player, stack);
            }

            return null;
        }
    }
}
