package com.direwolf20.buildinggadgets.common.network;

import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerCommands;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerContainer;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketTemplateManagerLoad implements IMessage {

    private ChunkCoordinates pos;

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = ChunkCoordinates.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
    }

    public PacketTemplateManagerLoad() {
    }

    public PacketTemplateManagerLoad(ChunkCoordinates blockPos) {
        pos = blockPos;
    }

    public static class Handler implements IMessageHandler<PacketTemplateManagerLoad, IMessage> {
        @Override
        public IMessage onMessage(PacketTemplateManagerLoad message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(PacketTemplateManagerLoad message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            World world = player.world;
            ChunkCoordinates pos = message.pos;
            TileEntity te = world.getTileEntity(pos);
            if (!(te instanceof TemplateManagerTileEntity)) return;
            TemplateManagerContainer container = ((TemplateManagerTileEntity) te).getContainer(player);
            TemplateManagerCommands.loadTemplate(container, player);


        }
    }
}
