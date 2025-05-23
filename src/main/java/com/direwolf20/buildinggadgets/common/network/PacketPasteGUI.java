package com.direwolf20.buildinggadgets.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetCopyPaste;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class PacketPasteGUI implements IMessage {

    int X, Y, Z;

    @Override
    public void fromBytes(ByteBuf buf) {
        X = buf.readInt();
        Y = buf.readInt();
        Z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(X);
        buf.writeInt(Y);
        buf.writeInt(Z);
    }

    public PacketPasteGUI() {

    }

    public PacketPasteGUI(int x, int y, int z) {
        X = x;
        Y = y;
        Z = z;
    }

    public static class Handler implements IMessageHandler<PacketPasteGUI, IMessage> {

        @Override
        public IMessage onMessage(PacketPasteGUI message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                return null;
            }

            handle(message, ctx);

            return null;
        }

        private void handle(PacketPasteGUI message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;

            // TODO(johnrowl) implement
            ItemStack heldItem = GadgetCopyPaste.getGadget(playerEntity);
            if (heldItem == null) {
                return;
            }

            GadgetCopyPaste.setX(heldItem, message.X);
            GadgetCopyPaste.setY(heldItem, message.Y);
            GadgetCopyPaste.setZ(heldItem, message.Z);
        }
    }
}
