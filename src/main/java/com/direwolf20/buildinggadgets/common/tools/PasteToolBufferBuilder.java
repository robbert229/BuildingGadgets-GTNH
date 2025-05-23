package com.direwolf20.buildinggadgets.common.tools;

import static com.direwolf20.buildinggadgets.util.ref.NBTKeys.TEMPLATE_COPY_COUNT;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

import org.lwjgl.opengl.GL11;

import com.direwolf20.buildinggadgets.common.items.ModItems;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetCopyPaste;

public class PasteToolBufferBuilder {

    private static Map<String, NBTTagCompound> tagMap = new HashMap<String, NBTTagCompound>();
    private static Map<String, ToolDireBuffer> bufferMap = new HashMap<String, ToolDireBuffer>();

    private static int getCopyCounter(String UUID) {
        if (tagMap.containsKey(UUID)) {
            return tagMap.get(UUID)
                .getInteger(TEMPLATE_COPY_COUNT);
        }
        return -1;
    }

    public static void clearMaps() {
        tagMap = new HashMap<String, NBTTagCompound>();
        bufferMap = new HashMap<String, ToolDireBuffer>();
    }

    public static void addToMap(String UUID, NBTTagCompound tag) {
        tagMap.put(UUID, tag);
    }

    @Nullable
    public static NBTTagCompound getTagFromUUID(String UUID) {
        if (tagMap.containsKey(UUID)) {
            return tagMap.get(UUID);
        }
        return null;
    }

    @Nullable
    public static ToolDireBuffer getBufferFromMap(String UUID) {
        if (bufferMap.containsKey(UUID)) {
            return bufferMap.get(UUID);
        }
        return null;
    }

    public static void addMapToBuffer(String UUID) {
        long time = System.nanoTime();
        List<BlockMap> blockMapList = GadgetCopyPaste.getBlockMapList(tagMap.get(UUID));
        // BlockRendererDispatcher dispatcher = Minecraft.getMinecraft()
        // .getBlockRendererDispatcher();
        ToolDireBuffer bufferBuilder = new ToolDireBuffer(blockMapList);
        // bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        // for (BlockMap blockMap : blockMapList) {
        // IBlockState renderBlockState = blockMap.state;
        // if (!(renderBlockState.equals(Blocks.AIR.getDefaultState()))) {
        // IBakedModel model = dispatcher.getModelForState(renderBlockState);
        // dispatcher.getBlockModelRenderer()
        // .renderModelFlat(
        // Minecraft.getMinecraft().world,
        // model,
        // renderBlockState,
        // new ChunkCoordinates(blockMap.xOffset, blockMap.yOffset, blockMap.zOffset),
        // bufferBuilder,
        // false,
        // 0L);
        // }
        // }
        // bufferBuilder.finishDrawing();
        bufferMap.put(UUID, bufferBuilder);

        System.out.printf(
            "Created %d Vertexes for %d blocks in %.2f ms%n",
            /* bufferBuilder.getVertexCount() */0,
            blockMapList.size(),
            (System.nanoTime() - time) * 1e-6);
    }

    public static void draw(EntityPlayer player, ChunkCoordinates startPos, String UUID) {
        // long time = System.nanoTime();
        ToolDireBuffer bufferBuilder = bufferMap.get(UUID);
        var tess = Tessellator.instance;

        for (var block : bufferBuilder.blockMapList) {
            var coordinate = new ChunkCoordinates(block.xOffset, block.yOffset, block.zOffset);
            GL11.glPushMatrix();
            GL11.glTranslated(coordinate.posX, coordinate.posY, coordinate.posZ);
            GL11.glScalef(1.01f, 1.01f, 1.01f);

            GL11.glDisable(GL11.GL_LIGHTING);

            ToolRenders.renderBoxTextured(tess, block.state, 0, 0, 0, 1, 1, 1);

            GL11.glEnable(GL11.GL_LIGHTING);

            GL11.glPopMatrix();
        }

        // bufferBuilder.sortVertexData((float) (x - startPos.getX()), (float) ((y + player.getEyeHeight()) -
        // startPos.getY()), (float) (z - startPos.getZ()));
        // //System.out.printf("Sorted %d Vertexes in %.2f ms%n", bufferBuilder.getVertexCount(), (System.nanoTime() -
        // time)
        // *1e-6);
        // if (bufferBuilder.getVertexCount() > 0) {
        // VertexFormat vertexformat = bufferBuilder.getVertexFormat();
        // int i = vertexformat.getNextOffset();
        // ByteBuffer bytebuffer = bufferBuilder.getByteBuffer();
        // List<VertexFormatElement> list = vertexformat.getElements();
        //
        // for (int j = 0; j < list.size(); ++j) {
        // VertexFormatElement vertexformatelement = list.get(j);
        // // VertexFormatElement.EnumUsage vertexformatelement$enumusage = vertexformatelement.getUsage();
        // // int k = vertexformatelement.getType().getGlConstant();
        // // int l = vertexformatelement.getIndex();
        // bytebuffer.position(vertexformat.getOffset(j));
        //
        // // moved to VertexFormatElement.preDraw
        // vertexformatelement.getUsage().preDraw(vertexformat, j, i, bytebuffer);
        // }
        //
        // GlStateManager.glDrawArrays(bufferBuilder.getDrawMode(), 0, bufferBuilder.getVertexCount());
        // int i1 = 0;
        //
        // for (int j1 = list.size(); i1 < j1; ++i1) {
        // VertexFormatElement vertexformatelement1 = list.get(i1);
        // // VertexFormatElement.EnumUsage vertexformatelement$enumusage1 = vertexformatelement1.getUsage();
        // // int k1 = vertexformatelement1.getIndex();
        //
        // // moved to VertexFormatElement.postDraw
        // vertexformatelement1.getUsage().postDraw(vertexformat, i1, i, bytebuffer);
        // }
        // }
    }

    //
    public static boolean isUpdateNeeded(String UUID, ItemStack stack) {
        return ((ModItems.gadgetCopyPaste.getCopyCounter(stack) != getCopyCounter(UUID)
            || PasteToolBufferBuilder.getTagFromUUID(UUID) == null));
    }
}
