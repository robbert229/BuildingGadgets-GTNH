package com.direwolf20.buildinggadgets;

// import com.direwolf20.buildinggadgets.client.KeyBindings;
// import com.direwolf20.buildinggadgets.common.blocks.ModBlocks;
// import com.direwolf20.buildinggadgets.common.blocks.Models.BakedModelLoader;
// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerContainer;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import com.direwolf20.buildinggadgets.client.KeyBindings;
import com.direwolf20.buildinggadgets.client.events.EventClientTick;
import com.direwolf20.buildinggadgets.client.events.EventKeyInput;
import com.direwolf20.buildinggadgets.common.entities.ModEntities;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        ModEntities.initModels();
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        KeyBindings.init();
        EventKeyInput.init();
        EventClientTick.init();

        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance()
            .bus()
            .register(this);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent evt) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.thePlayer;
        ItemStack heldItem = GadgetGeneric.getGadget(player);
        if (heldItem == null || heldItem.getItem() == null) return;

        if (heldItem.getItem() instanceof GadgetGeneric gadget) {
            gadget.renderOverlay(evt, player, heldItem);
        }
    }

    public static void playSound(ResourceLocation sound, float pitch) {
        Minecraft.getMinecraft()
            .getSoundHandler()
            .playSound(PositionedSoundRecord.func_147674_a(sound, pitch));
    }

    public static Color getColor(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}
