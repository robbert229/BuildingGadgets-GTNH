package com.direwolf20.buildinggadgets.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.direwolf20.buildinggadgets.common.items.gadgets.*;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class PacketAnchor extends PacketEmpty {

    public static class Handler implements IMessageHandler<PacketAnchor, IMessage> {

        @Override
        public IMessage onMessage(PacketAnchor message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                return null;
            }

            handle(ctx);

            return null;
        }

        private void handle(MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;

            ItemStack heldItem = GadgetGeneric.getGadget(playerEntity);
            if (heldItem == null || heldItem.getItem() == null) {
                return;
            }

            if (!(heldItem.getItem() instanceof GadgetGeneric gadget)) {
                return;
            }

            gadget.anchorBlocks(playerEntity, heldItem);
        }
    }
}
