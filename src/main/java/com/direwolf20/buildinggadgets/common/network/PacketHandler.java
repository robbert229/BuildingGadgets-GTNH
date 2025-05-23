package com.direwolf20.buildinggadgets.common.network;

import com.direwolf20.buildinggadgets.BuildingGadgets;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE
        .newSimpleChannel(BuildingGadgets.MODID);
    private static int packetId = 0;

    public static enum Side {
        CLIENT,
        SERVER,
        BOTH;
    }

    public static void registerMessages() {

        // Server side
        registerMessage(PacketToggleMode.Handler.class, PacketToggleMode.class, Side.SERVER);
        registerMessage(PacketChangeRange.Handler.class, PacketChangeRange.class, Side.SERVER);
        registerMessage(PacketRotateMirror.Handler.class, PacketRotateMirror.class, Side.SERVER);

        registerMessage(PacketToggleFuzzy.Handler.class, PacketToggleFuzzy.class, Side.SERVER);
        registerMessage(PacketToggleBlockPlacement.Handler.class, PacketToggleBlockPlacement.class, Side.SERVER);
        registerMessage(PacketRequestBlockMap.Handler.class, PacketRequestBlockMap.class, Side.SERVER);
        registerMessage(PacketTemplateManagerSave.Handler.class, PacketTemplateManagerSave.class, Side.SERVER);
        registerMessage(PacketTemplateManagerLoad.Handler.class, PacketTemplateManagerLoad.class, Side.SERVER);
        registerMessage(PacketTemplateManagerPaste.Handler.class, PacketTemplateManagerPaste.class, Side.SERVER);
        registerMessage(PacketCopyCoords.Handler.class, PacketCopyCoords.class, Side.SERVER);
        registerMessage(PacketPasteGUI.Handler.class, PacketPasteGUI.class, Side.SERVER);

        // ReImplemented
        registerMessage(PacketDestructionGUI.Handler.class, PacketDestructionGUI.class, Side.SERVER);

        // TODO for destruction gadget
        registerMessage(PacketUndo.Handler.class, PacketUndo.class, Side.SERVER);
        registerMessage(PacketAnchor.Handler.class, PacketAnchor.class, Side.SERVER);
        registerMessage(PacketToggleConnectedArea.Handler.class, PacketToggleConnectedArea.class, Side.SERVER);
        registerMessage(PacketToggleRayTraceFluid.Handler.class, PacketToggleRayTraceFluid.class, Side.SERVER);

        // Client side
        registerMessage(PacketBlockMap.Handler.class, PacketBlockMap.class, Side.CLIENT);

        // Both sides
        registerMessage(PacketSetRemoteInventoryCache.Handler.class, PacketSetRemoteInventoryCache.class, Side.BOTH);
    }

    private static void registerMessage(Class handler, Class packet, Side side) {
        if (side != Side.CLIENT) {
            registerMessage(handler, packet, cpw.mods.fml.relauncher.Side.SERVER);
        }

        if (side != Side.SERVER) {
            registerMessage(handler, packet, cpw.mods.fml.relauncher.Side.CLIENT);
        }
    }

    private static void registerMessage(Class handler, Class packet, cpw.mods.fml.relauncher.Side side) {
        INSTANCE.registerMessage(handler, packet, packetId++, side);
    }
}
