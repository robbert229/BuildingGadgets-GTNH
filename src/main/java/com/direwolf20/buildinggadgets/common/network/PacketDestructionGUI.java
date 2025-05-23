package com.direwolf20.buildinggadgets.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetDestruction;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class PacketDestructionGUI implements IMessage {

    int left, right, up, down, depth;

    @Override
    public void fromBytes(ByteBuf buf) {
        left = buf.readInt();
        right = buf.readInt();
        up = buf.readInt();
        down = buf.readInt();
        depth = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(left);
        buf.writeInt(right);
        buf.writeInt(up);
        buf.writeInt(down);
        buf.writeInt(depth);
    }

    public PacketDestructionGUI() {}

    public PacketDestructionGUI(int l, int r, int u, int d, int dep) {
        left = l;
        right = r;
        up = u;
        down = d;
        depth = dep;
    }

    public static class Handler implements IMessageHandler<PacketDestructionGUI, IMessage> {

        @Override
        public IMessage onMessage(PacketDestructionGUI message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                return null;
            }

            this.handle(message, ctx);

            return null;
        }

        private void handle(PacketDestructionGUI message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;

            ItemStack heldItem = GadgetDestruction.getGadget(playerEntity);
            if (heldItem == null) {
                return;
            }

            GadgetDestruction.setToolValue(heldItem, message.left, "left");
            GadgetDestruction.setToolValue(heldItem, message.right, "right");
            GadgetDestruction.setToolValue(heldItem, message.up, "up");
            GadgetDestruction.setToolValue(heldItem, message.down, "down");
            GadgetDestruction.setToolValue(heldItem, message.depth, "depth");
        }
    }
}
