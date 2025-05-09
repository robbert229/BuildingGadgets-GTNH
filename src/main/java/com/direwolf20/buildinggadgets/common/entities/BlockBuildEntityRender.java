package com.direwolf20.buildinggadgets.common.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.direwolf20.buildinggadgets.common.blocks.ModBlocks;
import com.direwolf20.buildinggadgets.common.tools.ToolRenders;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;

public class BlockBuildEntityRender extends Render {

    public BlockBuildEntityRender(RenderManager renderManager) {
        this.shadowSize = 0F;
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
        BlockBuildEntity blockEntity = (BlockBuildEntity) entity;
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();

        int toolMode = blockEntity.getToolMode();
        mc.getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);
        int teCounter = blockEntity.getTicksExisted();
        int maxLife = blockEntity.maxLife;
        if (teCounter > maxLife) {
            teCounter = maxLife;
        }

        float scale = (float) (teCounter) / (float) maxLife;
        if (scale >= 1.0f) {
            scale = 0.99f;
        }

        if (toolMode == 2 || toolMode == 3) {
            scale = (float) (maxLife - teCounter) / maxLife;
        }

        float trans = (1 - scale) / 2;

        GL11.glTranslated(x + trans+0.5, y + trans+0.5, z + trans+0.5);
        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(scale, scale, scale);

        var renderBlockState = blockEntity.getSetBlock();
        if (blockEntity.getUsingConstructionPaste() && toolMode == 1) {
            renderBlockState = new BlockState(ModBlocks.constructionBlock, 0);
        }

        if (renderBlockState == null) {
            renderBlockState = new BlockState(Blocks.cobblestone, 0);
        }

        // Convert the entity's position to integer coordinates
        int blockX = (int) blockEntity.posX;
        int blockY = (int) blockEntity.posY;
        int blockZ = (int) blockEntity.posZ;
        int metadata = renderBlockState.getBlock()
            .getDamageValue(mc.theWorld, blockX, blockY, blockZ);

        RenderBlocks renderBlocks = new RenderBlocks();
        renderBlocks.renderBlockAsItem(renderBlockState.getBlock(), metadata, 1.0f);

        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(false);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        double minX = x;
        double minY = y;
        double minZ = z;
        double maxX = x + 1;
        double maxY = y + 1;
        double maxZ = z + 1;

        float red = 0f;
        float green = 1f;
        float blue = 1f;

        if (toolMode == 2 || toolMode == 3) {
            red = 1f;
            green = 0.25f;
            blue = 0.25f;
        }

        float alpha = (1f - (scale));
        if (alpha < 0.051f) {
            alpha = 0.051f;
        }

        if (alpha > 0.33f) {
            alpha = 0.33f;
        }

        ToolRenders.renderBoundingBox(
                tessellator,
                minX,
                minY,
                minZ,
                maxX,
                maxY,
                maxZ,
                red,
                green,
                blue,
                alpha
        );

        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null; // No texture for this entity
    }
}
