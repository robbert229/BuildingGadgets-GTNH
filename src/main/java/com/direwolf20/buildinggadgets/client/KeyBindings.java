package com.direwolf20.buildinggadgets.client;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindings {

    public static KeyBinding menuSettings;
    public static KeyBinding range;
    public static KeyBinding rotateMirror;
    public static KeyBinding undo;
    public static KeyBinding anchor;
    public static KeyBinding fuzzy;
    public static KeyBinding connectedArea;
    public static KeyBinding materialList;

    public static void init() {
        menuSettings = createBinding("settings_menu", Keyboard.KEY_G);
        range = createBinding("range", Keyboard.KEY_R);
        undo = createBinding("undo", Keyboard.KEY_U);
        anchor = createBinding("anchor", Keyboard.KEY_H);
        fuzzy = createBinding("fuzzy", Keyboard.KEY_NONE);
        connectedArea = createBinding("connected_area", Keyboard.KEY_NONE);
        rotateMirror = createBinding("rotate_mirror", Keyboard.KEY_NONE);
        materialList = createBinding("material_list", Keyboard.KEY_M);
    }

    private static KeyBinding createBinding(String name, int key) {
        // In 1.7.10, simply create the KeyBinding without conflict contexts
        KeyBinding keyBinding = new KeyBinding("key." + name, key, "key.categories.buildingGadgets");
        ClientRegistry.registerKeyBinding(keyBinding);
        return keyBinding;
    }
}
