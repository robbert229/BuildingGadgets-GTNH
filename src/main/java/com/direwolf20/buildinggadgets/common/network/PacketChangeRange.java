package com.direwolf20.buildinggadgets.common.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import com.direwolf20.buildinggadgets.common.items.gadgets.*;
import com.direwolf20.buildinggadgets.common.tools.GadgetUtils;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class PacketChangeRange implements IMessage {

    private int range;

    public PacketChangeRange() {
        range = -1;
    }

    public PacketChangeRange(int range) {
        this.range = range;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        range = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(range);
    }

    public static class Handler implements IMessageHandler<PacketChangeRange, IMessage> {

        @Override
        public IMessage onMessage(PacketChangeRange message, MessageContext ctx) {
            // TODO(johnrowl) is this the right way? Ignore the addScheduled task, and just manually handle things?
            // FMLCommonHandler.instance().get(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            if (ctx.side == Side.CLIENT) {
                return null;
            }

            this.handle(message, ctx);

            return null;
        }

        private void handle(PacketChangeRange message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            ItemStack heldItem = GadgetGeneric.getGadget(playerEntity);
            if (heldItem == null) return;

            if (message.range >= 0) GadgetUtils.setToolRange(heldItem, message.range);
            else if (heldItem.getItem() instanceof GadgetBuilding) {
                GadgetBuilding gadgetBuilding = (GadgetBuilding) (heldItem.getItem());
                gadgetBuilding.rangeChange(playerEntity, heldItem);
            } else if (heldItem.getItem() instanceof GadgetExchanger) {
                GadgetExchanger gadgetExchanger = (GadgetExchanger) (heldItem.getItem());
                gadgetExchanger.rangeChange(playerEntity, heldItem);
            }
        }
    }
}
