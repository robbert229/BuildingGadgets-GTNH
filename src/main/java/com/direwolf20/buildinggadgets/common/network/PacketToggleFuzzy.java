package com.direwolf20.buildinggadgets.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.direwolf20.buildinggadgets.BuildingGadgetsConfig;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetBuilding;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetDestruction;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetExchanger;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public class PacketToggleFuzzy extends PacketEmpty {

    public static class Handler implements IMessageHandler<PacketToggleFuzzy, IMessage> {

        @Override
        public IMessage onMessage(PacketToggleFuzzy message, MessageContext ctx) {
            if (ctx.side != Side.SERVER) {
                return null;
            }

            EntityPlayer player = ctx.getServerHandler().playerEntity;
            ItemStack stack = GadgetGeneric.getGadget(player);
            GadgetGeneric item = (GadgetGeneric) stack.getItem();
            if (item instanceof GadgetExchanger || item instanceof GadgetBuilding
                || (item instanceof GadgetDestruction
                    && BuildingGadgetsConfig.GadgetsConfig.GadgetDestructionConfig.nonFuzzyEnabled))
                item.toggleFuzzy(player, stack);

            return null;
        }
    }
}
