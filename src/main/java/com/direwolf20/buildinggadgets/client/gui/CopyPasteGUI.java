/**
 * Parts of this class were adapted from code written by TTerrag for the Chisel mod:
 * https://github.com/Chisel-Team/Chisel
 * Chisel is Open Source and distributed under GNU GPL v2
 */

package com.direwolf20.buildinggadgets.client.gui;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.CustomModularScreen;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.screen.viewport.ModularGuiContext;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetDestruction;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.client.util.MouseUtil;
import com.direwolf20.buildinggadgets.common.config.SyncedConfig;
import com.direwolf20.buildinggadgets.common.items.ModItems;
import com.direwolf20.buildinggadgets.common.network.PacketCopyCoords;
import com.direwolf20.buildinggadgets.common.network.PacketHandler;
import com.mojang.realmsclient.gui.ChatFormatting;

public class CopyPasteGUI extends GadgetGUI {
    public CopyPasteGUI(ItemStack tool) {
        this(tool, false);
    }

    public CopyPasteGUI(ItemStack tool, boolean temporarilyEnabled) {
        super(tool, temporarilyEnabled);
    }

    @Override
    public ModularPanel buildUI(ModularGuiContext context) {
        ModularPanel panel = ModularPanel.defaultPanel(GuiUtils.getPanelName("copy_paste"));
        panel.child(IKey.str("Copy Paste").asWidget()
                .top(7).left(7));

        return panel;
    }
}
