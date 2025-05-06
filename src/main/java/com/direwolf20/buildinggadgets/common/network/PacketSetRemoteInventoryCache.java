package com.direwolf20.buildinggadgets.common.network;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.direwolf20.buildinggadgets.client.events.EventTooltip;
import com.direwolf20.buildinggadgets.common.tools.GadgetUtils;
import com.direwolf20.buildinggadgets.common.tools.InventoryManipulation;
import com.direwolf20.buildinggadgets.common.tools.UniqueItem;
import com.direwolf20.buildinggadgets.util.ChunkCoordinateUtils;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class PacketSetRemoteInventoryCache implements IMessage {

    private boolean isCopyPaste;
    private Multiset<UniqueItem> cache;
    private Pair<Integer, ChunkCoordinates> loc;

    public PacketSetRemoteInventoryCache() {}

    public PacketSetRemoteInventoryCache(Multiset<UniqueItem> cache, boolean isCopyPaste) {
        this.cache = cache;
        this.isCopyPaste = isCopyPaste;
    }

    public PacketSetRemoteInventoryCache(Pair<Integer, ChunkCoordinates> loc, boolean isCopyPaste) {
        this.loc = loc;
        this.isCopyPaste = isCopyPaste;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        isCopyPaste = buf.readBoolean();
        if (buf.readBoolean()) {
            loc = new ImmutablePair<>(buf.readInt(), ChunkCoordinateUtils.fromLong(buf.readLong()));
            return;
        }
        int len = buf.readInt();
        ImmutableMultiset.Builder<UniqueItem> builder = ImmutableMultiset.builder();
        for (int i = 0; i < len; i++)
            builder.addCopies(new UniqueItem(Item.getItemById(buf.readInt()), buf.readInt()), buf.readInt());

        cache = builder.build();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isCopyPaste);
        boolean isRequest = cache == null;
        buf.writeBoolean(isRequest);
        if (isRequest) {
            buf.writeInt(loc.getLeft());
            buf.writeLong(ChunkCoordinateUtils.toLong(loc.getRight()));
            return;
        }
        Set<Entry<UniqueItem>> items = cache.entrySet();
        buf.writeInt(items.size());
        for (Entry<UniqueItem> entry : items) {
            UniqueItem uniqueItem = entry.getElement();
            buf.writeInt(Item.getIdFromItem(uniqueItem.item));
            buf.writeInt(uniqueItem.meta);
            buf.writeInt(entry.getCount());
        }
    }

    public static class Handler implements IMessageHandler<PacketSetRemoteInventoryCache, IMessage> {

        @Override
        public IMessage onMessage(PacketSetRemoteInventoryCache message, MessageContext ctx) {
            var player = ctx.getServerHandler().playerEntity;
            MinecraftServer server = player.mcServer;

            if (ctx.side == Side.SERVER) {
                Set<UniqueItem> itemTypes = new HashSet<>();
                ImmutableMultiset.Builder<UniqueItem> builder = ImmutableMultiset.builder();
                IInventory remoteInventory = GadgetUtils
                    .getRemoteInventory(message.loc.getRight(), message.loc.getLeft(), player.worldObj, player);

                if (remoteInventory != null) {
                    for (int i = 0; i < remoteInventory.getSizeInventory(); i++) {
                        ItemStack stack = remoteInventory.getStackInSlot(i);
                        if (stack != null) {
                            Item item = stack.getItem();
                            int meta = stack.getItemDamage();
                            UniqueItem uniqueItem = new UniqueItem(item, meta);
                            if (!itemTypes.contains(uniqueItem)) {
                                itemTypes.add(uniqueItem);
                                builder.addCopies(
                                    uniqueItem,
                                    InventoryManipulation.countInContainer(remoteInventory, item, meta));
                            }
                        }
                    }
                }

                PacketHandler.INSTANCE
                    .sendTo(new PacketSetRemoteInventoryCache(builder.build(), message.isCopyPaste), player);
                return null;
            }

            if (message.isCopyPaste) {
                EventTooltip.setCache(message.cache);
            } else {
                // TODO(johnrowl) re-enable ToolRenders.
                // ToolRenders.setInventoryCache(message.cache);
            }

            return null;
        }
    }
}
