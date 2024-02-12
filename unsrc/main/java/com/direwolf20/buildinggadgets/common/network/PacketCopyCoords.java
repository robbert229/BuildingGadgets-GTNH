package com.direwolf20.buildinggadgets.common.network;

import com.direwolf20.buildinggadgets.common.items.ModItems;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetCopyPaste;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketCopyCoords implements IMessage {

    private ChunkCoordinates start;
    private ChunkCoordinates end;

    @Override
    public void fromBytes(ByteBuf buf) {
        start = ChunkCoordinates.fromLong(buf.readLong());
        end = ChunkCoordinates.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(start.toLong());
        buf.writeLong(end.toLong());
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

            // TODO(johnrowl) re-enable

//            ItemStack heldItem = GadgetCopyPaste.getGadget(playerEntity);
//            if (heldItem.isEmpty()) return;
//
//            ChunkCoordinates startPos = message.start;
//            ChunkCoordinates endPos = message.end;
//            GadgetCopyPaste tool = ModItems.gadgetCopyPaste;
//            if (startPos.equals(new ChunkCoordinates(0,0,0)) && endPos.equals(new ChunkCoordinates(0,0,0))) {
//                tool.setStartPos(heldItem, null);
//                tool.setEndPos(heldItem, null);
//                playerEntity.sendStatusMessage(new TextComponentString(TextFormatting.AQUA + new TextComponentTranslation("message.gadget.areareset").getUnformattedComponentText()), true);
//            } else {
//                tool.setStartPos(heldItem, startPos);
//                tool.setEndPos(heldItem, endPos);
//                GadgetCopyPaste.copyBlocks(heldItem, playerEntity, playerEntity.world, tool.getStartPos(heldItem), tool.getEndPos(heldItem));
//            }
        }
    }
}