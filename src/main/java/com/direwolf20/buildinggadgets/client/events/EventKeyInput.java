package com.direwolf20.buildinggadgets.client.events;

import com.cleanroommc.modularui.factory.ClientGUI;
import com.direwolf20.buildinggadgets.client.gui.MaterialListGUI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import com.direwolf20.buildinggadgets.client.KeyBindings;
import com.direwolf20.buildinggadgets.common.items.ITemplate;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import com.direwolf20.buildinggadgets.common.network.*;
import com.direwolf20.buildinggadgets.common.tools.InventoryManipulation;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;

@SideOnly(Side.CLIENT)
public class EventKeyInput {

    public static void init() {
        // Register this class to listen to events
        FMLCommonHandler.instance()
            .bus()
            .register(new EventKeyInput());
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        handleEventInput();
    }

    private void handleEventInput() {
        if (KeyBindings.temporarilyEnableMenu.isPressed() && checkNoModifier(KeyBindings.temporarilyEnableMenu)) {
            Minecraft mc = Minecraft.getMinecraft();

            ItemStack tool = GadgetGeneric.getGadget(mc.thePlayer);
            if (tool != null && tool.getItem() instanceof GadgetGeneric gadget) {
                gadget.openShortcutMenu(tool, true);
            }
        }

        if (KeyBindings.range.isPressed()) {
            PacketHandler.INSTANCE.sendToServer(new PacketChangeRange());
        } else if (KeyBindings.rotateMirror.isPressed()) {
            PacketHandler.INSTANCE.sendToServer(new PacketRotateMirror());
        } else if (KeyBindings.undo.isPressed()) {
            PacketHandler.INSTANCE.sendToServer(new PacketUndo());
        } else if (KeyBindings.anchor.isPressed()) {
            PacketHandler.INSTANCE.sendToServer(new PacketAnchor());
        } else if (KeyBindings.fuzzy.isPressed()) {
            PacketHandler.INSTANCE.sendToServer(new PacketToggleFuzzy());
        } else if (KeyBindings.connectedArea.isPressed()) {
            PacketHandler.INSTANCE.sendToServer(new PacketToggleConnectedArea());
        } else if (KeyBindings.materialList.isPressed()) {
            ItemStack held = InventoryManipulation
                .getStackInEitherHand(Minecraft.getMinecraft().thePlayer, ITemplate.class);

            if (held != null) {
                var gui = MaterialListGUI.createGUI(held);
                ClientGUI.open(gui);
            }
        }
    }

    private boolean checkNoModifier(KeyBinding keyBinding) {
        // This checks if no modifier keys are pressed (like shift, control, etc.)
        return !Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)
            && !Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
            && !Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)
            && !Keyboard.isKeyDown(Keyboard.KEY_LMENU)
            && !Keyboard.isKeyDown(Keyboard.KEY_RMENU);
    }
}
