package com.direwolf20.buildinggadgets.common.network;

import com.direwolf20.buildinggadgets.common.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.config.SyncedConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This empty packets represents a Request from the Client to re-send the {@link SyncedConfig}.
 */
public class PacketRequestConfigSync extends PacketEmpty {

    /**
     * Server-Side Handler for {@link PacketRequestConfigSync}
     */
    public static class Handler implements IMessageHandler<PacketRequestConfigSync,IMessage> {

        @Override
        public IMessage onMessage(PacketRequestConfigSync message, MessageContext ctx) {
            if (ctx.side!= Side.SERVER)
                return null;
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> {
                BuildingGadgets.logger.info("Client requested Config update. Sending config to {}.",ctx.getServerHandler().player.getName());
                SyncedConfig.sendConfigUpdateTo(ctx.getServerHandler().player);
            });
            return null;
        }
    }
}
