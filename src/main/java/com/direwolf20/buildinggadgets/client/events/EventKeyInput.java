package com.direwolf20.buildinggadgets.client.events;

import com.direwolf20.buildinggadgets.client.KeyBindings;
//import com.direwolf20.buildinggadgets.client.gui.ModeRadialMenu;
//import com.direwolf20.buildinggadgets.client.gui.materiallist.MaterialListGUI;
import com.direwolf20.buildinggadgets.common.items.ITemplate;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
//import com.direwolf20.buildinggadgets.common.network.PacketAnchor;
//import com.direwolf20.buildinggadgets.common.network.PacketChangeRange;
import com.direwolf20.buildinggadgets.common.network.PacketHandler;
import com.direwolf20.buildinggadgets.common.network.PacketRotateMirror;
import com.direwolf20.buildinggadgets.common.network.PacketToggleConnectedArea;
//import com.direwolf20.buildinggadgets.common.network.PacketToggleFuzzy;
//import com.direwolf20.buildinggadgets.common.network.PacketUndo;
import com.direwolf20.buildinggadgets.common.tools.InventoryManipulation;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
//import net.minecraftforge.client.settings.KeyModifier;
//import cpw.mods.fml.common.Mod.EventBusSubscriber;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;

public class EventKeyInput {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onKeyInput(@SuppressWarnings("unused") InputEvent.KeyInputEvent event) {
        handleEventInput();
    }

    @SubscribeEvent
    public static void onMouseInput(@SuppressWarnings("unused") InputEvent.MouseInputEvent event) {
        handleEventInput();
    }

    private static void handleEventInput() {
        //boolean extracted = (KeyBindings.menuSettings.getKeyModifier() == KeyModifier.NONE && KeyModifier.getActiveModifier() == KeyModifier.NONE) ||
        //        KeyBindings.menuSettings.getKeyModifier() != KeyModifier.NONE;
        boolean extracted = false;
        if (KeyBindings.menuSettings.isPressed() && extracted) {
            //PacketHandler.INSTANCE.sendToServer(new PacketToggleMode());
            Minecraft mc = Minecraft.getMinecraft();
            ItemStack tool = GadgetGeneric.getGadget(mc.thePlayer);
            if (tool != null && tool.getItem() != null) {
//                mc.displayGuiScreen(new ModeRadialMenu(tool));
            }
        } else if (KeyBindings.range.isPressed()) {
//            PacketHandler.INSTANCE.sendToServer(new PacketChangeRange());
        } else if (KeyBindings.rotateMirror.isPressed()) {
            PacketHandler.INSTANCE.sendToServer(new PacketRotateMirror());
        } else if (KeyBindings.undo.isPressed()) {
//            PacketHandler.INSTANCE.sendToServer(new PacketUndo());
//      }  else if (KeyBindings.anchor.isPressed()) {
//            PacketHandler.INSTANCE.sendToServer(new PacketAnchor());
//      }  else if (KeyBindings.fuzzy.isPressed()) {
//            PacketHandler.INSTANCE.sendToServer(new PacketToggleFuzzy());
//      }  else if (KeyBindings.connectedArea.isPressed()) {
            PacketHandler.INSTANCE.sendToServer(new PacketToggleConnectedArea());
        } else if (KeyBindings.materialList.isPressed()) {
            ItemStack held = InventoryManipulation.getStackInEitherHand(Minecraft.getMinecraft().thePlayer, ITemplate.class);
            if( held != null && held.getItem() != null ) {
//                Minecraft.getMinecraft().displayGuiScreen(new MaterialListGUI(held));
            }
        }
    }
}
