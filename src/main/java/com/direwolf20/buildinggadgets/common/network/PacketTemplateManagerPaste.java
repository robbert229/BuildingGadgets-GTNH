package com.direwolf20.buildinggadgets.common.network;

// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerCommands;
// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerContainer;
// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerCommands;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerContainer;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;
import com.direwolf20.buildinggadgets.util.NBTJson;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class PacketTemplateManagerPaste implements IMessage {

    private ChunkCoordinates pos;
    private String jsonString;
    private String templateName;

    @Override
    public void fromBytes(ByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        pos = new ChunkCoordinates(x, y, z);
        templateName = ByteBufUtils.readUTF8String(buf);
        jsonString = PacketUtils.decompress(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.posX);
        buf.writeInt(pos.posY);
        buf.writeInt(pos.posZ);
        ByteBufUtils.writeUTF8String(buf, templateName);
        buf.writeBytes(PacketUtils.compress(jsonString));
    }

    public PacketTemplateManagerPaste() {}

    public PacketTemplateManagerPaste(String cbString, ChunkCoordinates TMpos, String name) {
        this.pos = TMpos;
        this.jsonString = cbString;
        this.templateName = name;
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
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            try {
                var parsed = new JsonParser().parse(message.jsonString);
                var nbt = NBTJson.toNbt(parsed);

                if (nbt.equals(new NBTTagCompound()) || !(nbt instanceof NBTTagCompound)) {
                    TemplateManagerCommands.pasteFailed(player);
                    return;
                }

                BuildingGadgets.LOG.debug("handling packet paste: {}", nbt.toString());

                World world = player.worldObj;
                ChunkCoordinates pos = message.pos;
                TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);

                if (!(te instanceof TemplateManagerTileEntity)) {
                    return;
                }

                TemplateManagerContainer container = ((TemplateManagerTileEntity) te).getContainer(player);
                TemplateManagerCommands.pasteTemplate(container, player, (NBTTagCompound) nbt, message.templateName);
            } catch (JsonSyntaxException e) {
                TemplateManagerCommands.pasteFailed(player);
            }
        }
    }
}
