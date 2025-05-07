package com.direwolf20.buildinggadgets.client.proxy;

// import com.direwolf20.buildinggadgets.client.KeyBindings;
// import com.direwolf20.buildinggadgets.common.blocks.ModBlocks;
// import com.direwolf20.buildinggadgets.common.blocks.Models.BakedModelLoader;
// import com.direwolf20.buildinggadgets.common.blocks.templatemanager.TemplateManagerContainer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import cofh.lib.util.position.BlockPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;

import com.direwolf20.buildinggadgets.client.KeyBindings;
import com.direwolf20.buildinggadgets.client.events.EventClientTick;
import com.direwolf20.buildinggadgets.client.events.EventKeyInput;
import com.direwolf20.buildinggadgets.common.entities.ModEntities;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetDestruction;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetGeneric;
import com.direwolf20.buildinggadgets.common.proxy.CommonProxy;
import com.direwolf20.buildinggadgets.common.tools.ToolRenders;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        ModEntities.initModels();
        // ModelLoaderRegistry.registerLoader(new BakedModelLoader());
        super.preInit(e);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        KeyBindings.init();
        EventKeyInput.init();
        EventClientTick.init();
        // EventTooltip.init();

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

        if (heldItem.getItem() instanceof GadgetDestruction) {
            ToolRenders.renderDestructionOverlay(evt, player, heldItem);
        }

        // if (heldItem.getItem() instanceof GadgetBuilding) {
        // ToolRenders.renderBuilderOverlay(evt, player, heldItem);
        // } else if (heldItem.getItem() instanceof GadgetExchanger) {
        // ToolRenders.renderExchangerOverlay(evt, player, heldItem);
        // } else if (heldItem.getItem() instanceof GadgetCopyPaste) {
        // ToolRenders.renderPasteOverlay(evt, player, heldItem);
        // } else if (heldItem.getItem() instanceof GadgetDestruction) {
        // ToolRenders.renderDestructionOverlay(evt, player, heldItem);
        // }

        {
            double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * evt.partialTicks;
            double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * evt.partialTicks;
            double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * evt.partialTicks;

            GL11.glPushMatrix();
            GL11.glTranslated(-dx, -dy, -dz);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glLineWidth(2.0F);
            GL11.glColor3f(1.0F, 0.0F, 0.0F); // Red

            Tessellator tess = Tessellator.instance;

    var blocksToHighlight = getSurroundingBlocksUnderPlayer(player);
            for (var pos : blocksToHighlight) {
                var box = AxisAlignedBB.getBoundingBox(pos.posX, pos.posY, pos.posZ,
                        pos.posX + 1, pos.posY + 1, pos.posZ + 1);
                //renderGlobal.drawOutlinedBoundingBox(box, 1.0F, 0.0F, 0.0F, 1.0F);
                drawOutlinedBoundingBox(tess, box);
            }

            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glPopMatrix();
        }
    }

    public List<ChunkCoordinates> getSurroundingBlocksUnderPlayer(EntityPlayer player) {
        List<ChunkCoordinates> positions = new ArrayList<>();

        int px = MathHelper.floor_double(player.posX);
        int py = MathHelper.floor_double(player.posY - 1); // Block directly beneath
        int pz = MathHelper.floor_double(player.posZ);

        // 3x3 grid centered on player (on the block *below* their feet)
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                positions.add(new ChunkCoordinates(px + dx, py, pz + dz));
            }
        }

        return positions;
    }

    public static void drawOutlinedBoundingBox(Tessellator tess, AxisAlignedBB bb) {
        tess.startDrawing(GL11.GL_LINE_STRIP);

        // Bottom face
        tess.addVertex(bb.minX, bb.minY, bb.minZ);
        tess.addVertex(bb.maxX, bb.minY, bb.minZ);
        tess.addVertex(bb.maxX, bb.minY, bb.maxZ);
        tess.addVertex(bb.minX, bb.minY, bb.maxZ);
        tess.addVertex(bb.minX, bb.minY, bb.minZ);

        tess.draw();

        tess.startDrawing(GL11.GL_LINE_STRIP);

        // Top face
        tess.addVertex(bb.minX, bb.maxY, bb.minZ);
        tess.addVertex(bb.maxX, bb.maxY, bb.minZ);
        tess.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        tess.addVertex(bb.minX, bb.maxY, bb.maxZ);
        tess.addVertex(bb.minX, bb.maxY, bb.minZ);

        tess.draw();

        tess.startDrawing(GL11.GL_LINES);

        // Vertical edges
        tess.addVertex(bb.minX, bb.minY, bb.minZ);
        tess.addVertex(bb.minX, bb.maxY, bb.minZ);

        tess.addVertex(bb.maxX, bb.minY, bb.minZ);
        tess.addVertex(bb.maxX, bb.maxY, bb.minZ);

        tess.addVertex(bb.maxX, bb.minY, bb.maxZ);
        tess.addVertex(bb.maxX, bb.maxY, bb.maxZ);

        tess.addVertex(bb.minX, bb.minY, bb.maxZ);
        tess.addVertex(bb.minX, bb.maxY, bb.maxZ);

        tess.draw();
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
