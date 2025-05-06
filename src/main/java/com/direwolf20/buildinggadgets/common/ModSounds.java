package com.direwolf20.buildinggadgets.common;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.client.proxy.ClientProxy;

import net.minecraft.util.ResourceLocation;

public enum ModSounds {
    BEEP("beep");

    private final ResourceLocation sound;

    private ModSounds(String name) {
        sound = new ResourceLocation(BuildingGadgets.MODID, name);
    }

    public ResourceLocation getSound() {
        return sound;
    }

    public void playSound() {
        playSound(1.0F);
    }

    public void playSound(float pitch) {
        ClientProxy.playSound(sound, pitch);
    }
}