package com.direwolf20.buildinggadgets.common.tools;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import com.cleanroommc.modularui.utils.GlStateManager;
import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.client.RemoteInventoryCache;
import com.direwolf20.buildinggadgets.common.blocks.ModBlocks;
import com.direwolf20.buildinggadgets.common.items.FakeBuilderWorld;
import com.direwolf20.buildinggadgets.common.items.ModItems;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetCopyPaste;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetDestruction;
import com.direwolf20.buildinggadgets.util.VectorTools;
import com.direwolf20.buildinggadgets.util.datatypes.BlockState;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Multiset;

public class ToolRenders {

    private static final FakeBuilderWorld fakeWorld = new FakeBuilderWorld();

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final RemoteInventoryCache cacheInventory = new RemoteInventoryCache(false);
    private static final Cache<Triple<UniqueItemStack, ChunkCoordinates, Integer>, Integer> cacheDestructionOverlay = CacheBuilder
        .newBuilder()
        .maximumSize(1)
        .expireAfterWrite(1, TimeUnit.SECONDS)
        .removalListener(removal -> GLAllocation.deleteDisplayLists((int) removal.getValue()))
        .build();

    // We use these as highlighters
    // private static final BlockState stainedGlassYellow = Blocks.stained_glass.getDefaultState().withProperty(COLOR,
    // EnumDyeColor.YELLOW);
    // private static final BlockState stainedGlassRed = Blocks.STAINED_GLASS.getDefaultState().withProperty(COLOR,
    // EnumDyeColor.RED);
    // private static final BlockState stainedGlassWhite = Blocks.STAINED_GLASS.getDefaultState().withProperty(COLOR,
    // EnumDyeColor.WHITE);

    public static void setInventoryCache(Multiset<UniqueItem> cache) {
        ToolRenders.cacheInventory.setCache(cache);
    }

    public static void updateInventoryCache() {
        cacheInventory.forceUpdate();
    }

    // public static void renderBuilderOverlay(RenderWorldLastEvent evt, EntityPlayer player, ItemStack heldItem) {
    //
    // // Calculate the players current position, which is needed later
    // Vec3 playerPos = ToolRenders.Utils.getPlayerTranslate(player, evt.getPartialTicks());
    //
    // // Render if we have a remote inventory selected
    // renderLinkedInventoryOutline(heldItem, playerPos, player);
    //
    // MovingObjectPosition lookingAt = VectorTools.getLookingAt(player, heldItem);
    // List<ChunkCoordinates> coordinates = getAnchor(heldItem);
    //
    // if (lookingAt == null && coordinates.size() == 0)
    // return;
    //
    // IBlockState startBlock = ToolRenders.Utils.getStartBlock(lookingAt, player);
    // if (startBlock == ModBlocks.effectBlock.getDefaultState())
    // return;
    // Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    //
    // IBlockState renderBlockState = getToolBlock(heldItem);
    //
    // //Don't render anything if there is no block selected (Air)
    // if (renderBlockState == Blocks.AIR.getDefaultState())
    // return;
    //
    // //Build a list of coordinates based on the tool mode and range
    // if (coordinates.size() == 0 && lookingAt != null)
    // coordinates = BuildingModes.collectPlacementPos(player.world, player, lookingAt.getBlockPos(), lookingAt.sideHit,
    // heldItem, lookingAt.getBlockPos());
    //
    // // Figure out how many of the block we're rendering are in the player inventory.
    // ItemStack itemStack = ToolRenders.Utils.getSilkDropIfPresent(player.world, renderBlockState, player);
    //
    // // Check if we have the blocks required
    // long hasBlocks = InventoryManipulation.countItem(itemStack, player, cacheInventory);
    // hasBlocks += InventoryManipulation.countPaste(player);
    //
    // int hasEnergy = SyncedConfig.energyMax == 0 ? Integer.MAX_VALUE : ToolRenders.Utils.getStackEnergy(heldItem,
    // player);
    //
    // // Prepare the fake world -- using a fake world lets us render things properly, like fences connecting.
    // Set<ChunkCoordinates> coords = new HashSet<>(coordinates);
    // fakeWorld.setWorldAndState(player.world, renderBlockState, coords);
    //
    // GlStateManager.pushMatrix();
    // ToolRenders.Utils.stateManagerPrepareBlend();
    //
    // // Render all the raw blocks
    // coordinates.forEach(coordinate -> {
    // GlStateManager.pushMatrix();
    // ToolRenders.Utils.stateManagerPrepare(playerPos, coordinate, null);
    // GL14.glBlendColor(1F, 1F, 1F, 0.55f); //Set the alpha of the blocks we are rendering
    //
    // IBlockState state = Blocks.AIR.getDefaultState();
    // if (fakeWorld.getWorldType() != WorldType.DEBUG_ALL_BLOCK_STATES)
    // state = renderBlockState.getActualState(fakeWorld, coordinate);
    //
    // mc.getBlockRendererDispatcher().renderBlockBrightness(state, 1f);//Render the defined block
    // GlStateManager.popMatrix();
    // });
    //
    // // Render if the block can be built or not
    // for (ChunkCoordinates coordinate : coordinates) {
    // GlStateManager.pushMatrix();
    // ToolRenders.Utils.stateManagerPrepare(playerPos, coordinate, 0.01f);
    // GlStateManager.scale(1.006f, 1.006f, 1.006f);
    // GL14.glBlendColor(1F, 1F, 1F, 0.35f);
    //
    // hasBlocks--;
    // if (heldItem.hasCapability(CapabilityEnergy.ENERGY, null))
    // hasEnergy -= ModItems.gadgetBuilding.getEnergyCost(heldItem);
    // else
    // hasEnergy -= ModItems.gadgetBuilding.getDamageCost(heldItem);
    //
    // if (hasBlocks < 0 || hasEnergy < 0)
    // mc.getBlockRendererDispatcher().renderBlockBrightness(stainedGlassRed, 1f);
    //
    // // Move the render position back to where it was
    // GlStateManager.popMatrix();
    // }
    //
    // //Set blending back to the default mode
    // GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    // ForgeHooksClient.setRenderLayer(MinecraftForgeClient.getRenderLayer());
    // //Disable blend
    // GlStateManager.disableBlend();
    // //Pop from the original push in this method
    // GlStateManager.popMatrix();
    // }

    // public static void renderExchangerOverlay(RenderWorldLastEvent evt, EntityPlayer player, ItemStack heldItem) {
    // // Calculate the players current position, which is needed later
    // Vec3 playerPos = ToolRenders.Utils.getPlayerTranslate(player, evt.getPartialTicks());
    //
    // BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
    // renderLinkedInventoryOutline(heldItem, playerPos, player);
    //
    // MovingObjectPosition lookingAt = VectorTools.getLookingAt(player, heldItem);
    // IBlockState state = Blocks.AIR.getDefaultState();
    // List<ChunkCoordinates> coordinates = getAnchor(heldItem);
    //
    // if (lookingAt == null && coordinates.size() == 0)
    // return;
    //
    // IBlockState startBlock = ToolRenders.Utils.getStartBlock(lookingAt, player);
    // if (startBlock == ModBlocks.effectBlock.getDefaultState())
    // return;
    //
    // IBlockState renderBlockState = getToolBlock(heldItem);
    // Minecraft mc = Minecraft.getMinecraft();
    // mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    // if (renderBlockState == Blocks.AIR.getDefaultState()) {//Don't render anything if there is no block selected
    // (Air)
    // return;
    // }
    // if (coordinates.size() == 0 && lookingAt != null) { //Build a list of coordinates based on the tool mode and
    // range
    // coordinates = ExchangingModes.collectPlacementPos(player.world, player, lookingAt.getBlockPos(),
    // lookingAt.sideHit, heldItem, lookingAt.getBlockPos());
    // }
    //
    // // Figure out how many of the block we're rendering we have in the inventory of the player.
    // ItemStack itemStack = ToolRenders.Utils.getSilkDropIfPresent(player.world, renderBlockState, player);
    //
    // long hasBlocks = InventoryManipulation.countItem(itemStack, player, cacheInventory);
    // hasBlocks = hasBlocks + InventoryManipulation.countPaste(player);
    // int hasEnergy = SyncedConfig.energyMax == 0 ? Integer.MAX_VALUE : ToolRenders.Utils.getStackEnergy(heldItem,
    // player);
    //
    // // Prepare the fake world -- using a fake world lets us render things properly, like fences connecting.
    // Set<ChunkCoordinates> coords = new HashSet<>(coordinates);
    // fakeWorld.setWorldAndState(player.world, renderBlockState, coords);
    //
    // GlStateManager.pushMatrix();
    // ToolRenders.Utils.stateManagerPrepareBlend();
    //
    // for (ChunkCoordinates coordinate : coordinates) {
    // GlStateManager.pushMatrix();
    // ToolRenders.Utils.stateManagerPrepare(playerPos, coordinate, 0.001f);
    // GL14.glBlendColor(1F, 1F, 1F, 0.55f); //Set the alpha of the blocks we are rendering
    //
    // // Get the block state in the fake world
    // if (fakeWorld.getWorldType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
    // state = renderBlockState.getActualState(fakeWorld, coordinate);
    // }
    //
    // if (renderBlockState.getRenderType() != EnumBlockRenderType.INVISIBLE) {
    // try {
    // dispatcher.renderBlockBrightness(state, 1f);//Render the defined block
    // } catch(NullPointerException ex) {
    // // This is to stop crashes with blocks that have not been implemented
    // // correctly by their mod authors.
    // BuildingGadgets.logger.error(ToolRenders.class.getSimpleName() + ": Error within overlay rendering -> " + ex);
    // }
    //
    // GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F); //Rotate it because i'm not sure why but we need to
    // }
    //
    // GL14.glBlendColor(1F, 1F, 1F, 0.1f); //Set the alpha of the blocks we are rendering
    // dispatcher.renderBlockBrightness(stainedGlassWhite, 1f);//Render the defined block - White glass to show non-full
    // block renders (Example: Torch)
    // GlStateManager.popMatrix();
    //
    // GlStateManager.pushMatrix();
    // ToolRenders.Utils.stateManagerPrepare(playerPos, coordinate, 0.002f);
    //
    // GlStateManager.scale(1.02f, 1.02f, 1.02f); //Slightly Larger block to avoid z-fighting.
    // GL14.glBlendColor(1F, 1F, 1F, 0.55f); //Set the alpha of the blocks we are rendering
    // hasBlocks--;
    //
    // if (heldItem.hasCapability(CapabilityEnergy.ENERGY, null))
    // hasEnergy -= ModItems.gadgetExchanger.getEnergyCost(heldItem);
    // else
    // hasEnergy -= ModItems.gadgetExchanger.getDamageCost(heldItem);
    //
    // if (hasBlocks < 0 || hasEnergy < 0)
    // dispatcher.renderBlockBrightness(stainedGlassRed, 1f);
    //
    // // Move the render position back to where it was
    // GlStateManager.popMatrix();
    // }
    //
    // GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    // ForgeHooksClient.setRenderLayer(MinecraftForgeClient.getRenderLayer());
    //
    // GlStateManager.disableBlend();
    // GlStateManager.popMatrix();
    // }

    /**
     * shouldSideBeRendered isn't really an accurate name for this function.
     * It's purpose is to check to see if a block is "exposed" to air. This is
     * used as a heuristic to see if we can skip some rendering logic.
     */
    private static boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        int dx = x, dy = y, dz = z;
        switch (side) {
            case 0:
                dy--;
                break;
            case 1:
                dy++;
                break;
            case 2:
                dz--;
                break;
            case 3:
                dz++;
                break;
            case 4:
                dx--;
                break;
            case 5:
                dx++;
                break;
        }
        return !world.getBlock(dx, dy, dz)
            .isOpaqueCube();
    }

    public static void renderDestructionOverlay(RenderWorldLastEvent evt, EntityPlayer player, ItemStack heldItem) {
        MovingObjectPosition lookingAt = VectorTools.getLookingAt(player, heldItem);
        if (lookingAt == null && GadgetDestruction.getAnchor(heldItem) == null) {
            return;
        }

        World world = player.worldObj;

        ChunkCoordinates startBlockPos = (GadgetDestruction.getAnchor(heldItem) == null)
            ? VectorTools.getPosFromMovingObjectPosition(lookingAt)
            : GadgetDestruction.getAnchor(heldItem);
        var startBlock = BlockState.getBlockState(world, startBlockPos);

        EnumFacing facing = (GadgetDestruction.getAnchorSide(heldItem) == null) ? EnumFacing.getFront(lookingAt.sideHit)
            : GadgetDestruction.getAnchorSide(heldItem);
        if (startBlock.getBlock() == ModBlocks.effectBlock) {
            return;
        }

        Set<ChunkCoordinates> coordinates = GadgetDestruction.getArea(world, startBlockPos, facing, player, heldItem);

        GL11.glPushMatrix();
        double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * evt.partialTicks;
        double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * evt.partialTicks;
        double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * evt.partialTicks;
        GL11.glTranslated(-doubleX, -doubleY, -doubleZ);

        try {
            GL11.glCallList(
                cacheDestructionOverlay
                    .get(new ImmutableTriple<>(new UniqueItemStack(heldItem), startBlockPos, facing.ordinal()), () -> {
                        int displayList = GLAllocation.generateDisplayLists(1);
                        GL11.glNewList(displayList, GL11.GL_COMPILE);

                        renderDestructionOverlay(coordinates, world);

                        GL11.glEndList();
                        return displayList;
                    }));
        } catch (ExecutionException e) {
            BuildingGadgets.LOG.error("Error encountered while rendering destruction gadget overlay", e);
        }

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    private static void renderDestructionOverlay(Set<ChunkCoordinates> coordinates, World world) {
        Minecraft.getMinecraft()
            .getTextureManager()
            .bindTexture(TextureMap.locationBlocksTexture);

        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glLineWidth(2.0F);
        GL11.glColor3f(1.0F, 0.0F, 0.0F); // Red

        Tessellator tess = Tessellator.instance;

        for (var coordinate : coordinates) {
            maybeRenderDestructionOverlayBlock(world, coordinate, tess);
        }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
    }

    /**
     * maybeRenderDestructionOverlayBlock will potentially render the overlay block. If the block isn't exposed to air
     * then it instead ignored.
     */
    private static void maybeRenderDestructionOverlayBlock(World world, ChunkCoordinates coordinate, Tessellator tess) {
        // invisible doesn't actually mean that the block is invisible or not. It just means that at
        // least one of the faces of the block is exposed to air. This is just used as a heuristic
        // to help cut down rendering on invisible blocks.
        boolean invisible = true;
        for (EnumFacing side : EnumFacing.values()) {
            if (shouldSideBeRendered(world, coordinate.posX, coordinate.posY, coordinate.posZ, side.ordinal())) {
                invisible = false;
                break;
            }
        }

        if (invisible) return;

        GL11.glPushMatrix();
        GL11.glTranslated(coordinate.posX, coordinate.posY, coordinate.posZ);
        GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F); // Rotate it because it's needed
        GL11.glTranslatef(-0.005f, -0.005f, 0.005f);
        GL11.glScalef(1.01f, 1.01f, 1.01f); // Slightly larger block to avoid z-fighting

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        renderBoxSolid(tess, 0, 0, -1, 1, 1, 0, 1, 0, 0, 0.5f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glPopMatrix();
    }

    public static void renderPasteOverlay(RenderWorldLastEvent evt, EntityPlayer player, ItemStack stack) {
        // Calculate the players current position, which is needed later
        Vec3 playerPos = ToolRenders.Utils.getPlayerTranslate(player, evt.partialTicks);

        Tessellator tessellator = Tessellator.instance;
        renderLinkedInventoryOutline(evt, stack, player, tessellator);

        if (ModItems.gadgetCopyPaste.getStartPos(stack) == null || ModItems.gadgetCopyPaste.getEndPos(stack) == null) {
            return;
        }

        //
        // mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        String UUID = ModItems.gadgetCopyPaste.getUUID(stack);
        World world = player.worldObj;
        if (GadgetCopyPaste.getToolMode(stack) == GadgetCopyPaste.ToolMode.Paste) {
            // First check if we have an anchor, if not check if we're looking at a block, if not, exit
            ChunkCoordinates startPos = GadgetCopyPaste.getAnchor(stack);
            if (startPos == null) {
                startPos = VectorTools.getPosLookingAt(player, stack);
                if (startPos == null) {
                    return;
                }

                startPos = VectorTools.Up(startPos, GadgetCopyPaste.getY(stack));
                startPos = VectorTools.East(startPos, GadgetCopyPaste.getX(stack));
                startPos = VectorTools.South(startPos, GadgetCopyPaste.getZ(stack));
            } else {
                startPos = VectorTools.Up(startPos, GadgetCopyPaste.getY(stack));
                startPos = VectorTools.East(startPos, GadgetCopyPaste.getX(stack));
                startPos = VectorTools.South(startPos, GadgetCopyPaste.getZ(stack));
            }

            // We store our buffers in PasteToolBufferBuilder (A client only class) -- retrieve the buffer from this
            // locally
            // cache'd map
            ToolDireBuffer toolDireBuffer = PasteToolBufferBuilder.getBufferFromMap(UUID);
            if (toolDireBuffer == null) {
                return;
            }
            // Also get the blockMapList from the local cache - If either the buffer or the blockmap list are empty,
            // exit.
            List<BlockMap> blockMapList = GadgetCopyPaste.getBlockMapList(PasteToolBufferBuilder.getTagFromUUID(UUID));
            if (toolDireBuffer.getVertexCount() == 0 || blockMapList.isEmpty()) {
                return;
            }

            // Don't draw on top of blocks being built by our tools.
            BlockState startBlock = BlockState.getBlockState(world, startPos);
            if (startBlock == null || startBlock.getBlock()
                .equals(ModBlocks.effectBlock)) {
                return;
            }

            // Save the current position that is being rendered
            GlStateManager.pushMatrix();

            // Enable Blending (So we can have transparent effect)
            GlStateManager.enableBlend();

            // This blend function allows you to use a constant alpha, which is defined later
            GlStateManager.blendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);

            GlStateManager.pushMatrix();// Push matrix again just because
            GlStateManager.translate(
                startPos.posX - playerPos.xCoord,
                startPos.posY - playerPos.yCoord,
                startPos.posZ - playerPos.zCoord);// Now move the render position to the coordinates we want to render
                                                  // at
            GL14.glBlendColor(1F, 1F, 1F, 0.55f); // Set the alpha of the blocks we are rendering
            //
            GlStateManager.translate(0.0005f, 0.0005f, -0.0005f);
            GlStateManager.scale(0.999f, 0.999f, 0.999f);// Slightly Larger block to avoid z-fighting.

            // TODO(johnrowl) enable this again
            // PasteToolBufferBuilder.draw(player, playerPos.xCoord, playerPos.yCoord, playerPos.zCoord, startPos,
            // UUID); // Draw
            // the cached buffer in the world.

            GlStateManager.popMatrix();

            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();

        } else {
            ChunkCoordinates startPos = ModItems.gadgetCopyPaste.getStartPos(stack);
            ChunkCoordinates endPos = ModItems.gadgetCopyPaste.getEndPos(stack);
            ChunkCoordinates blankPos = new ChunkCoordinates(0, 0, 0);
            if (startPos == null || endPos == null || startPos.equals(blankPos) || endPos.equals(blankPos)) {
                return;
            }

            List<BlockMap> blockMapList = GadgetCopyPaste.getBlockMapList(PasteToolBufferBuilder.getTagFromUUID(UUID));
            // if (blockMapList.isEmpty()) return;
            // TODO(johnrowl) we probably want to add this back for performance reasons.

            // We want to draw from the starting position to the (ending position)+1
            int x = Math.min(startPos.posX, endPos.posX);
            int y = Math.min(startPos.posY, endPos.posY);
            int z = Math.min(startPos.posZ, endPos.posZ);
            int dx = Math.max(startPos.posX, endPos.posX) + 1;
            int dy = Math.max(startPos.posY, endPos.posY) + 1;
            int dz = Math.max(startPos.posZ, endPos.posZ) + 1;

            GlStateManager.pushMatrix();
            GlStateManager.translate(-playerPos.xCoord, -playerPos.yCoord, -playerPos.zCoord);
            // The render starts at the player, so we subtract the player coordinates and move the render to 0,0,0

            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);

            renderBox(tessellator, AxisAlignedBB.getBoundingBox(x, y, z, dx, dy, dz), 255, 223, 127);
            // Draw the box around the blocks we've copied.

            GL11.glLineWidth(1.0F);
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);

            GlStateManager.popMatrix();
        }
    }

    private static void renderLinkedInventoryOutline(RenderWorldLastEvent evt, ItemStack item, EntityPlayer player,
        Tessellator tess) {
        Integer dim = GadgetUtils.getDIMFromNBT(item, "boundTE");
        ChunkCoordinates coordinate = GadgetUtils.getPOSFromNBT(item, "boundTE");
        Vec3 playerPos = ToolRenders.Utils.getPlayerTranslate(player, evt.partialTicks);

        if (dim == null) {
            return;
        }

        if (player.dimension != dim) {
            return;
        }

        if (coordinate == null) {
            return;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(-playerPos.xCoord, -playerPos.yCoord, -playerPos.zCoord);
        // The render starts at the player, so we subtract the player coordinates and move the render to 0,0,0

        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO);

        renderBoxSolid(
            tess,
            AxisAlignedBB.getBoundingBox(
                coordinate.posX - 0.001,
                coordinate.posY - 0.001,
                coordinate.posZ - 0.001,
                coordinate.posX + 1.001,
                coordinate.posY + 1.001,
                coordinate.posZ + 1.001),
            1f,
            0.9f,
            .5f,
            0.2f);
        // Draw the box around the blocks we've copied.

        GL11.glLineWidth(1.0F);
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);

        GlStateManager.popMatrix();
    }

    /**
     * renderBox renders a box that completely covers the selected axis aligned bounding box.
     */
    private static void renderBox(Tessellator tess, AxisAlignedBB bb, int R, int G, int B) {
        // Set the line width
        GL11.glLineWidth(2.0F);

        tess.startDrawing(GL11.GL_LINE_STRIP);
        tess.setColorRGBA(255, 255, 255, 255);

        // Bottom face
        tess.addVertex(bb.minX, bb.minY, bb.minZ);
        tess.addVertex(bb.maxX, bb.minY, bb.minZ);
        tess.addVertex(bb.maxX, bb.minY, bb.maxZ);
        tess.addVertex(bb.minX, bb.minY, bb.maxZ);
        tess.addVertex(bb.minX, bb.minY, bb.minZ);

        tess.draw();

        tess.startDrawing(GL11.GL_LINE_STRIP);
        tess.setColorRGBA(255, 255, 255, 255);

        // Top face
        tess.addVertex(bb.minX, bb.maxY, bb.minZ);
        tess.addVertex(bb.maxX, bb.maxY, bb.minZ);
        tess.addVertex(bb.maxX, bb.maxY, bb.maxZ);
        tess.addVertex(bb.minX, bb.maxY, bb.maxZ);
        tess.addVertex(bb.minX, bb.maxY, bb.minZ);

        tess.draw();

        tess.startDrawing(GL11.GL_LINES);
        tess.setColorRGBA(255, 255, 255, 255);
        // Vertical edges

        tess.addVertex(bb.maxX, bb.minY, bb.minZ);
        tess.addVertex(bb.maxX, bb.maxY, bb.minZ);

        tess.addVertex(bb.maxX, bb.minY, bb.maxZ);
        tess.addVertex(bb.maxX, bb.maxY, bb.maxZ);

        tess.addVertex(bb.minX, bb.minY, bb.maxZ);
        tess.addVertex(bb.minX, bb.maxY, bb.maxZ);

        tess.setColorRGBA(0, 255, 0, 255);
        tess.addVertex(bb.minX, bb.minY, bb.minZ);
        tess.addVertex(bb.minX, bb.maxY, bb.minZ);

        tess.draw();

        // Reset the line width back to default
        GL11.glLineWidth(1.0F);
    }

    public static void renderBoundingBox(Tessellator tessellator, double minX, double minY, double minZ, double maxX,
        double maxY, double maxZ, float red, float green, float blue, float alpha) {
        // Set the color with transparency for all the vertices
        tessellator.setColorRGBA_F(red, green, blue, alpha);

        // Bottom face (down)
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.addVertex(minX, minY, maxZ);

        // Top face (up)
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(maxX, maxY, minZ);

        // North face
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(maxX, minY, minZ);

        // South face
        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(minX, maxY, maxZ);

        // East face
        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(maxX, minY, maxZ);

        // West face
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.addVertex(minX, maxY, minZ);
    }

    private static void renderBoxSolid(Tessellator tessellator, AxisAlignedBB box, float red, float green, float blue,
        float alpha) {
        renderBoxSolid(
            tessellator,
            box.minX,
            box.minY,
            box.minZ,
            box.maxX,
            box.maxY,
            box.maxZ,
            red,
            green,
            blue,
            alpha);
    }

    private static void renderBoxSolid(Tessellator tessellator, double startX, double startY, double startZ,
        double endX, double endY, double endZ, float red, float green, float blue, float alpha) {
        tessellator.startDrawingQuads(); // In 1.7.10, mode 7 corresponds to quads

        // Set color once for all vertices
        tessellator.setColorRGBA_F(red, green, blue, alpha);

        // down
        tessellator.addVertex(startX, startY, startZ);
        tessellator.addVertex(endX, startY, startZ);
        tessellator.addVertex(endX, startY, endZ);
        tessellator.addVertex(startX, startY, endZ);

        // up
        tessellator.addVertex(startX, endY, startZ);
        tessellator.addVertex(startX, endY, endZ);
        tessellator.addVertex(endX, endY, endZ);
        tessellator.addVertex(endX, endY, startZ);

        // east
        tessellator.addVertex(startX, startY, startZ);
        tessellator.addVertex(startX, endY, startZ);
        tessellator.addVertex(endX, endY, startZ);
        tessellator.addVertex(endX, startY, startZ);

        // west
        tessellator.addVertex(startX, startY, endZ);
        tessellator.addVertex(endX, startY, endZ);
        tessellator.addVertex(endX, endY, endZ);
        tessellator.addVertex(startX, endY, endZ);

        // south
        tessellator.addVertex(endX, startY, startZ);
        tessellator.addVertex(endX, endY, startZ);
        tessellator.addVertex(endX, endY, endZ);
        tessellator.addVertex(endX, startY, endZ);

        // north
        tessellator.addVertex(startX, startY, startZ);
        tessellator.addVertex(startX, startY, endZ);
        tessellator.addVertex(startX, endY, endZ);
        tessellator.addVertex(startX, endY, startZ);

        // Finalize the drawing
        tessellator.draw();
    }

    private static class Utils {

        // private static IBlockState getStartBlock(MovingObjectPosition lookingAt, EntityPlayer player) {
        // IBlockState startBlock = Blocks.AIR.getDefaultState();
        // if (lookingAt != null)
        // startBlock = player.worldObj.getBlockState(VectorTools.getPosFromMovingObjectPosition(lookingAt));
        //
        // return startBlock;
        // }
        //
        // private static int getStackEnergy(ItemStack stack, EntityPlayer player) {
        // if (player.capabilities.isCreativeMode || (!stack.hasCapability(CapabilityEnergy.ENERGY, null) &&
        // !stack.isItemStackDamageable()))
        // return Integer.MAX_VALUE;
        //
        // if (stack.hasCapability(CapabilityEnergy.ENERGY, null))
        // return CapabilityProviderEnergy.getCap(stack).getEnergyStored();
        //
        // return stack.getMaxDamage() - stack.getItemDamage();
        // }
        //
        // /**
        // * Returns a Vec3i of the players position based on partial tick.
        // * Used for Render translation.
        // */
        private static Vec3 getPlayerTranslate(EntityPlayer player, float partialTick) {
            return Vec3.createVectorHelper(
                player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick,
                player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick,
                player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick);
        }
        //
        // /**
        // * Attempts to get the Silk Touch Drop item but if it fails it'll return the original
        // * non-silk touch ItemStack.
        // */
        // private static ItemStack getSilkDropIfPresent(World world, IBlockState state, EntityPlayer player) {
        // ItemStack itemStack = ItemStack.EMPTY;
        // if (state.getBlock().canSilkHarvest(world, new ChunkCoordinates(0, 0, 0), state, player))
        // itemStack = InventoryManipulation.getSilkTouchDrop(state);
        //
        // if (itemStack.isEmpty()) {
        // try {
        // itemStack = state.getBlock().getPickBlock(state, null, world, new ChunkCoordinates(0, 0, 0), player);
        // } catch (Exception ignored) {
        // // This may introduce issues. I hope it doesn't
        // itemStack = InventoryManipulation.getSilkTouchDrop(state);
        // }
        // }
        //
        // return itemStack;
        // }
        //
        // private static void stateManagerPrepareBlend() {
        // GlStateManager.enableBlend();
        // GlStateManager.blendFunc(GL11.GL_CONSTANT_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
        // }
        //
        // /**
        // * Prepares our render using base properties
        // */
        // private static void stateManagerPrepare(Vec3 playerPos, ChunkCoordinates blockPos, Float shift) {
        // GlStateManager.translate(blockPos.posX - playerPos.xCoord, blockPos.posY - playerPos.yCoord, blockPos.posZ -
        // playerPos.zCoord);//Now move the render position to the coordinates we want to render at
        // // Rotate it because i'm not sure why but we need to
        // GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
        // GlStateManager.scale(1f, 1f, 1f);
        //
        // // Slightly Larger block to avoid z-fighting.
        // if (shift != null) {
        // GlStateManager.translate(-shift, -shift, shift);
        // GlStateManager.scale(1.005f, 1.005f, 1.005f);
        // }
        // }
    }
}
