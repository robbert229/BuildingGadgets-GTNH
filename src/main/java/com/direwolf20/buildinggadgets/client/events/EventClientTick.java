package com.direwolf20.buildinggadgets.client.events;

import com.direwolf20.buildinggadgets.common.items.ITemplate;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetCopyPaste;
import com.direwolf20.buildinggadgets.common.network.PacketHandler;
import com.direwolf20.buildinggadgets.common.network.PacketRequestBlockMap;
import com.direwolf20.buildinggadgets.common.tools.PasteToolBufferBuilder;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.relauncher.Side;

public class EventClientTick {
    private static int counter = 0;
    private static boolean joinedWorld;

    public static void init(){
        FMLCommonHandler.instance().bus().register(new EventClientTick());
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onClientTick(@SuppressWarnings("unused") TickEvent.ClientTickEvent event) {
        counter++;
        if (counter > 600 || !joinedWorld) {
            if (!joinedWorld && counter > 200)
                joinedWorld = true;

            counter = 0;

            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player == null) return;

            for (int i = 0; i < 36; ++i) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack == null || !(stack.getItem() instanceof ITemplate template)) {
                    continue;
                }

                String UUID = template.getUUID(stack);
                if (UUID != null && PasteToolBufferBuilder.isUpdateNeeded(UUID, stack)) {
                    //System.out.println("BlockMap Update Needed for UUID: " + UUID + " in slot " + i);
                    PacketHandler.INSTANCE.sendToServer(new PacketRequestBlockMap(template.getUUID(stack), !(template instanceof GadgetCopyPaste)));
                    joinedWorld = true;
                }
            }
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onJoinWorld(@SuppressWarnings("unused") ClientConnectedToServerEvent event) {
        joinedWorld = false;
    }
}
