package com.direwolf20.buildinggadgets.common.network;

import com.direwolf20.buildinggadgets.common.tools.WorldSave;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestBlockMap implements IMessage {

    private String UUID = "";
    private boolean isTemplate;

    @Override
    public void fromBytes(ByteBuf buf) {
        UUID = ByteBufUtils.readUTF8String(buf);
        isTemplate = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, UUID);
        buf.writeBoolean(isTemplate);
    }

    public PacketRequestBlockMap() {
    }

    public PacketRequestBlockMap(String ID, boolean isTemplate) {
        UUID = ID;
        this.isTemplate = isTemplate;
    }

    public static class Handler implements IMessageHandler<PacketRequestBlockMap, IMessage> {
        @Override
        public IMessage onMessage(PacketRequestBlockMap message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                handle(message, ctx);
            }

            return null;
        }

        private void handle(PacketRequestBlockMap message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            NBTTagCompound tagCompound = (message.isTemplate ? WorldSave.getTemplateWorldSave(player.worldObj) : WorldSave.getWorldSave(player.worldObj)).getCompoundFromUUID(message.UUID);
            if (tagCompound != null) {
                PacketHandler.INSTANCE.sendTo(new PacketBlockMap(tagCompound), player);
                //System.out.println("Sending BlockMap Packet");
            }
        }
    }
}
