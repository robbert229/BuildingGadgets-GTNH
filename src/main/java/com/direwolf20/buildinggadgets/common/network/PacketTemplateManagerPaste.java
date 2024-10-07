package com.direwolf20.buildinggadgets.common.network;

// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerCommands;
// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerContainer;
// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;

import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerCommands;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerContainer;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class PacketTemplateManagerPaste implements IMessage {

    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private ChunkCoordinates pos;
    private byte[] data;
    private String templateName;

    @Override
    public void fromBytes(ByteBuf buf) {
        // System.out.println("Buf size: " + buf.readableBytes());
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        pos = new ChunkCoordinates(x, y, z);
        templateName = ByteBufUtils.readUTF8String(buf);
        data = new byte[buf.readableBytes()];
        buf.readBytes(data);

    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.posX);
        buf.writeInt(pos.posY);
        buf.writeInt(pos.posZ);
        ByteBufUtils.writeUTF8String(buf, templateName);
        buf.writeBytes(data);

        // System.out.println("Buf size: " + buf.readableBytes());
    }

    public PacketTemplateManagerPaste() {
    }

    public PacketTemplateManagerPaste(ByteArrayOutputStream pasteStream, ChunkCoordinates TMpos, String name) {
        pos = TMpos;
        data = pasteStream.toByteArray();
        templateName = name;
    }

    public static class Handler implements IMessageHandler<PacketTemplateManagerPaste, IMessage> {

        @Override
        public IMessage onMessage(PacketTemplateManagerPaste message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                return null;
            }

            handle(message, ctx);

            return null;
        }

        private void handle(PacketTemplateManagerPaste message, MessageContext ctx) {
            ByteArrayInputStream bais = new ByteArrayInputStream(message.data);
            try {
                NBTTagCompound newTag = CompressedStreamTools.readCompressed(bais);
                if (newTag.equals(new NBTTagCompound())) return;

                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                World world = player.worldObj;
                ChunkCoordinates pos = message.pos;
                TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);

                if (!(te instanceof TemplateManagerTileEntity)) {
                    return;
                }

                TemplateManagerContainer container = ((TemplateManagerTileEntity) te).getContainer(player);
                TemplateManagerCommands.pasteTemplate(container, player, newTag, message.templateName);
            } catch (Throwable t) {
                System.out.println(t);
            }

        }
    }
}
