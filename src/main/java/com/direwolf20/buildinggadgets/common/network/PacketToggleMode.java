package com.direwolf20.buildinggadgets.common.network;

import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetBuilding;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetCopyPaste;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetExchanger;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketToggleMode implements IMessage {

    private int mode;

    @Override
    public void fromBytes(ByteBuf buf) {
        mode = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(mode);
    }

    public PacketToggleMode() {
    }

    public PacketToggleMode(int modeInt) {
        mode = modeInt;
    }

    public static class Handler implements IMessageHandler<PacketToggleMode, IMessage> {
        @Override
        public IMessage onMessage(PacketToggleMode message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                return null;
            }

//            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            this.handle(message, ctx);

            return null;
        }

        private void handle(PacketToggleMode message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            ItemStack heldItem = GadgetGeneric.getGadget(playerEntity);
            if (heldItem == null)
                return;

            if (heldItem.getItem() instanceof GadgetBuilding) {
                GadgetBuilding gadgetBuilding = (GadgetBuilding) (heldItem.getItem());
                gadgetBuilding.setMode(heldItem, message.mode);
            } else if (heldItem.getItem() instanceof GadgetExchanger) {
                GadgetExchanger gadgetExchanger = (GadgetExchanger) (heldItem.getItem());
                gadgetExchanger.setMode(heldItem, message.mode);
            } else if (heldItem.getItem() instanceof GadgetCopyPaste) {
                GadgetCopyPaste gadgetCopyPaste = (GadgetCopyPaste) (heldItem.getItem());
                gadgetCopyPaste.setMode(heldItem, message.mode);
            }
        }
    }
}
