package com.direwolf20.buildinggadgets.common.network;

// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerCommands;
// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerContainer;
// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerCommands;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerContainer;
import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerTileEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class PacketTemplateManagerLoad implements IMessage {

    private ChunkCoordinates pos;

    @Override
    public void fromBytes(ByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();

        pos = new ChunkCoordinates(x, y, z);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(pos.posX);
        buf.writeInt(pos.posY);
        buf.writeInt(pos.posZ);
    }

    public PacketTemplateManagerLoad() {}

    public PacketTemplateManagerLoad(ChunkCoordinates blockPos) {
        pos = blockPos;
    }

    public static class Handler implements IMessageHandler<PacketTemplateManagerLoad, IMessage> {

        @Override
        public IMessage onMessage(PacketTemplateManagerLoad message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                return null;
            }

            handle(message, ctx);

            return null;
        }

        private void handle(PacketTemplateManagerLoad message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            World world = player.worldObj;
            ChunkCoordinates pos = message.pos;
            TileEntity te = world.getTileEntity(pos.posX, pos.posY, pos.posZ);

            if (!(te instanceof TemplateManagerTileEntity)) return;
            TemplateManagerContainer container = ((TemplateManagerTileEntity) te).getContainer(player);
            TemplateManagerCommands.loadTemplate(container, player);
        }
    }
}
