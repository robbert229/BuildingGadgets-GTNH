package com.direwolf20.buildinggadgets.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;

import com.direwolf20.buildinggadgets.common.items.ModItems;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetCopyPaste;
import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class PacketCopyCoords implements IMessage {

    private ChunkCoordinates start;
    private ChunkCoordinates end;

    @Override
    public void fromBytes(ByteBuf buf) {
        start = ChunkCoordinateUtils.fromLong(buf.readLong());
        end = ChunkCoordinateUtils.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(ChunkCoordinateUtils.toLong(start));
        buf.writeLong(ChunkCoordinateUtils.toLong(end));
    }

    public PacketCopyCoords() {

    }

    public PacketCopyCoords(ChunkCoordinates startPos, ChunkCoordinates endPos) {
        start = startPos;
        end = endPos;
    }

    public static class Handler implements IMessageHandler<PacketCopyCoords, IMessage> {

        @Override
        public IMessage onMessage(PacketCopyCoords message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                return null;
            }

            handle(message, ctx);

            return null;
        }

        private void handle(PacketCopyCoords message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;

            ItemStack heldItem = GadgetCopyPaste.getGadget(playerEntity);
            if (heldItem == null) {
                return;
            }

            ChunkCoordinates startPos = message.start;
            ChunkCoordinates endPos = message.end;
            GadgetCopyPaste tool = ModItems.gadgetCopyPaste;
            if (startPos.equals(new ChunkCoordinates(0, 0, 0)) && endPos.equals(new ChunkCoordinates(0, 0, 0))) {
                tool.setStartPos(heldItem, null);
                tool.setEndPos(heldItem, null);
                playerEntity.addChatMessage(
                    new ChatComponentText(
                        ChatFormatting.AQUA
                            + new ChatComponentTranslation("message.gadget.areareset").getUnformattedText()));
            } else {
                tool.setStartPos(heldItem, startPos);
                tool.setEndPos(heldItem, endPos);
                GadgetCopyPaste.copyBlocks(
                    heldItem,
                    playerEntity,
                    playerEntity.worldObj,
                    tool.getStartPos(heldItem),
                    tool.getEndPos(heldItem));
            }
        }
    }
}
