package com.direwolf20.buildinggadgets.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;

public class GuiButtonSound extends GuiButtonSelect {

    private ResourceLocation soundSelect, soundDeselect;
    private float pitchSelect, pitchDeselect;
    private boolean silent;

    private static final ResourceLocation DEFAULT_CLICK_SOUND = new ResourceLocation("random.click");

    public GuiButtonSound(int buttonId, int x, int y, int width, int height, String text, String helpTextKey) {
        super(buttonId, x, y, width, height, text, helpTextKey);
        pitchSelect = pitchDeselect = 1;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public void setSounds(ResourceLocation soundSelect, ResourceLocation soundDeselect) {
        setSounds(soundSelect, soundDeselect, 1, 1);
    }

    public void setSounds(ResourceLocation soundSelect, ResourceLocation soundDeselect, float pitchSelect,
        float pitchDeselect) {
        this.soundSelect = soundSelect;
        this.soundDeselect = soundDeselect;
        this.pitchSelect = pitchSelect;
        this.pitchDeselect = pitchDeselect;
        silent = false;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.playPressSound(mc.getSoundHandler());
            return true;
        }

        return false;
    }

    public void playPressSound(SoundHandler soundHandler) {
        if (silent) {
            return;
        }

        // Use a default sound if no soundSelect or soundDeselect is provided
        ResourceLocation sound = soundSelect == null ? DEFAULT_CLICK_SOUND : (selected ? soundDeselect : soundSelect);
        soundHandler.playSound(PositionedSoundRecord.func_147674_a(sound, selected ? pitchDeselect : pitchSelect));
    }
}
