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

        float trans = computeTranslationOffset(scale);

        GL11.glTranslated(x + trans, y + trans, z + trans);
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
        int metadata = renderBlockState.block()
            .getDamageValue(mc.theWorld, blockX, blockY, blockZ);

        RenderBlocks renderBlocks = new RenderBlocks();
        renderBlocks.renderBlockAsItem(renderBlockState.block(), metadata, 1.0f);

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

        ToolRenders.renderBoundingBox(tessellator, minX, minY, minZ, maxX, maxY, maxZ, red, green, blue, alpha);

        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDepthMask(true);

        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    /**
     * Computes the translation offset needed to keep a scaled block centred within its
     * unit-cube voxel.
     *
     * <p>{@code renderBlockAsItem} draws a block from local (0,0,0) to (1,1,1). After
     * {@code glScalef(scale)}, the block occupies [0, scale]. To centre it in [0, 1] the
     * origin must be shifted by {@code (1 - scale) / 2}, so the rendered range becomes
     * [{@code offset}, {@code offset + scale}] with its midpoint always at {@code 0.5}.
     *
     * @param scale the current animation scale, in [0, 1]
     * @return the per-axis translation offset to apply before {@code glScalef}
     */
    static float computeTranslationOffset(float scale) {
        return (1f - scale) / 2f;
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return null; // No texture for this entity
    }
}
