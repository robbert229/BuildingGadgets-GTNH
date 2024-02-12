package com.direwolf20.buildinggadgets.common.network;

import com.direwolf20.buildinggadgets.common.items.gadgets.*;
import com.direwolf20.buildinggadgets.common.tools.GadgetUtils;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketAnchor extends PacketEmpty {

    public static class Handler implements IMessageHandler<PacketAnchor, IMessage> {
        @Override
        public IMessage onMessage(PacketAnchor message, MessageContext ctx) {
            // TODO(johnrowl) do I need to move this to another thread?
//            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(ctx));

            // I think that adding the conditional here is the equivalent of the old way.
            if (ctx.side == Side.SERVER) {
                handle(ctx);
            }

            return null;
        }

        private void handle(MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().playerEntity;
            // TODO(johnrowl) re-enable this.
//            ItemStack heldItem = GadgetGeneric.getGadget(playerEntity);
//            if (heldItem == null && heldItem.getItem() == null)
//                return;
//
//            if (heldItem.getItem() instanceof GadgetBuilding) {
//                GadgetUtils.anchorBlocks(playerEntity, heldItem);
//            } else if (heldItem.getItem() instanceof GadgetExchanger) {
//                GadgetUtils.anchorBlocks(playerEntity, heldItem);
//            } else if (heldItem.getItem() instanceof GadgetCopyPaste) {
//                GadgetCopyPaste.anchorBlocks(playerEntity, heldItem);
//            } else if (heldItem.getItem() instanceof GadgetDestruction) {
//                GadgetDestruction.anchorBlocks(playerEntity, heldItem);
//            }
        }
    }
}