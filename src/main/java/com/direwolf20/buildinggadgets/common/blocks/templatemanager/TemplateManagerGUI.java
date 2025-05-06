/**
 * Parts of this class were adapted from code written by TTerrag for the Chisel mod: https://github.com/Chisel-Team/Chisel
 * Chisel is Open Source and distributed under GNU GPL v2
 */

package com.direwolf20.buildinggadgets.common.blocks.templatemanager;

import com.direwolf20.buildinggadgets.client.gui.AreaHelpText;
import com.direwolf20.buildinggadgets.client.gui.GuiButtonHelp;
import com.direwolf20.buildinggadgets.client.gui.GuiButtonHelpText;
import com.direwolf20.buildinggadgets.client.gui.IHoverHelpText;
import com.direwolf20.buildinggadgets.client.gui.GuiUtils;
import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.network.PacketHandler;
import com.direwolf20.buildinggadgets.common.network.PacketTemplateManagerLoad;
import com.direwolf20.buildinggadgets.common.network.PacketTemplateManagerPaste;
import com.direwolf20.buildinggadgets.common.network.PacketTemplateManagerSave;
import com.direwolf20.buildinggadgets.common.tools.GadgetUtils;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TemplateManagerGUI extends GuiContainer {
    public static final int HELP_TEXT_BACKGROUNG_COLOR = 1694460416;

    private boolean panelClicked;
    private int clickButton;
    private int clickX, clickY;
    private float initRotX, initRotY, initZoom, initPanX, initPanY;
    private float prevRotX, prevRotY;// prevPanX, prevPanY;
    private float momentumX, momentumY;
    private float momentumDampening = 0.98f;
    private float rotX = 0, rotY = 0, zoom = 1;
    private float panX = 0, panY = 0;

    private Rectangle panel = new Rectangle(8, 18, 62, 62);
    private GuiTextField nameField;
    private GuiButton buttonSave, buttonLoad, buttonCopy, buttonPaste;

    private GuiButtonHelp buttonHelp;
    private List<IHoverHelpText> helpTextProviders = new ArrayList<>();

    private TemplateManagerTileEntity te;
    private TemplateManagerContainer container;

    private static final ResourceLocation background = new ResourceLocation(BuildingGadgets.MODID, "textures/gui/testcontainer.png");

    public TemplateManagerGUI(TemplateManagerTileEntity tileEntity, TemplateManagerContainer container) {
        super(container);
        this.te = tileEntity;
        this.container = container;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (buttonHelp.isSelected()) {

            GL11.glColor4f(1, 1, 1, 1);

            for (IHoverHelpText helpTextProvider : helpTextProviders)
                helpTextProvider.drawRect(this, HELP_TEXT_BACKGROUNG_COLOR);

            for (IHoverHelpText helpTextProvider : helpTextProviders) {
                if (helpTextProvider.isHovered(mouseX, mouseY))
                    drawHoveringText(
                            Collections.singletonList(helpTextProvider.getHoverHelpText()),
                            mouseX,
                            mouseY,
                            this.fontRendererObj
                    );
            }
        } else {
            // TODO(johnrowl) verify that this is correct.
//            this.drawHoveringText(
//                    Collections.singletonList(buttonHelp.getHoverHelpText()),
//                    mouseX,
//                    mouseY,
//                    this.fontRendererObj
//            );
        }

        if (buttonHelp.isHovered(mouseX, mouseY)) {
            drawHoveringText(
                    Collections.singletonList(buttonHelp.getHoverText()),
                    mouseX,
                    mouseY,
                    this.fontRendererObj
            );
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        helpTextProviders.clear();

        this.buttonList.add(buttonHelp = new GuiButtonHelp(100, this.guiLeft + this.xSize - 16, this.guiTop + 4));
        //The parameters of GuiButton are(id, x, y, width, height, text);
        this.buttonList.add(buttonSave = createAndAddButton(0, 79, 17, 30, 20, "Save"));
        this.buttonList.add(buttonLoad = createAndAddButton(1, 137, 17, 30, 20, "Load"));
        this.buttonList.add(buttonCopy = createAndAddButton(2, 79, 61, 30, 20, "Copy"));
        this.buttonList.add(buttonPaste = createAndAddButton(3, 135, 61, 34, 20, "Paste"));
        this.nameField = new GuiTextField(this.fontRendererObj, this.guiLeft + 8, this.guiTop + 6, 149, this.fontRendererObj.FONT_HEIGHT);
        this.nameField.setMaxStringLength(50);
        this.nameField.setVisible(true);

        helpTextProviders.add(new AreaHelpText(nameField, "field.template_name"));
        helpTextProviders.add(new AreaHelpText(inventorySlots.getSlot(0), guiLeft, guiTop, "slot.gadget"));
        helpTextProviders.add(new AreaHelpText(inventorySlots.getSlot(1), guiLeft, guiTop, "slot.template"));
        helpTextProviders.add(new AreaHelpText(guiLeft + 112, guiTop + 41, 22, 15, "arrow.data_flow"));
        helpTextProviders.add(new AreaHelpText(panel, guiLeft, guiTop + 10, "preview"));
        //NOTE: the id always has to be different or else it might get called twice or never!
    }

    private GuiButton createAndAddButton(int id, int x, int y, int witdth, int height, String text) {
        GuiButtonHelpText button = new GuiButtonHelpText(id, this.guiLeft + x, this.guiTop + y, witdth, height, text, text.toLowerCase());
        helpTextProviders.add(button);
        return button;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GL11.glColor4f(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if (!GuiUtils.isHovered(buttonCopy,mouseX, mouseY) && !GuiUtils.isHovered(buttonPaste, mouseX, mouseY)) {
            drawTexturedModalRectReverseX(
                    guiLeft + 112,
                    guiTop + 41,
                    176,
                    0,
                    22,
                    15,
                    GuiUtils.isHovered(buttonLoad, mouseX, mouseY)
            );
        }

        this.nameField.drawTextBox();
        //drawStructure();
    }


    public void drawTexturedModalRectReverseX(int x, int y, int textureX, int textureY, int width, int height, boolean reverse) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        if (reverse) {
            tessellator.addVertexWithUV(x + 0, y + height, zLevel, (textureX + width) * GuiUtils.TEXTURE_CONVERSION_FACTOR, textureY * GuiUtils.TEXTURE_CONVERSION_FACTOR);
            tessellator.addVertexWithUV(x + width, y + height, zLevel, textureX * GuiUtils.TEXTURE_CONVERSION_FACTOR, textureY * GuiUtils.TEXTURE_CONVERSION_FACTOR);
            tessellator.addVertexWithUV(x + width, y + 0, zLevel, textureX * GuiUtils.TEXTURE_CONVERSION_FACTOR, (textureY + height) * GuiUtils.TEXTURE_CONVERSION_FACTOR);
            tessellator.addVertexWithUV(x + 0, y + 0, zLevel, (textureX + width) * GuiUtils.TEXTURE_CONVERSION_FACTOR, (textureY + height) * GuiUtils.TEXTURE_CONVERSION_FACTOR);
        } else {
            tessellator.addVertexWithUV(x + 0, y + height, zLevel, textureX * GuiUtils.TEXTURE_CONVERSION_FACTOR, (textureY + height) * GuiUtils.TEXTURE_CONVERSION_FACTOR);
            tessellator.addVertexWithUV(x + width, y + height, zLevel, (textureX + width) * GuiUtils.TEXTURE_CONVERSION_FACTOR, (textureY + height) * GuiUtils.TEXTURE_CONVERSION_FACTOR);
            tessellator.addVertexWithUV(x + width, y + 0, zLevel, (textureX + width) * GuiUtils.TEXTURE_CONVERSION_FACTOR, textureY * GuiUtils.TEXTURE_CONVERSION_FACTOR);
            tessellator.addVertexWithUV(x + 0, y + 0, zLevel, textureX * GuiUtils.TEXTURE_CONVERSION_FACTOR, textureY * GuiUtils.TEXTURE_CONVERSION_FACTOR);
        }
        tessellator.draw();
    }

//    private void drawStructure() {
//        return;
//
//        int scale = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight).getScaleFactor();
//        drawRect(guiLeft + panel.getX() - 1, guiTop + panel.getY() - 1, guiLeft + panel.getX() + panel.getWidth() + 1, guiTop + panel.getY() + panel.getHeight() + 1, 0xFF8A8A8A);
//        ItemStack itemstack = this.container.getSlot(0).getStack();
//
//        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//
//        if (itemstack != null) {
//            String UUID = ModItems.gadgetCopyPaste.getUUID(itemstack);
//            ToolDireBuffer bufferBuilder = PasteToolBufferBuilder.getBufferFromMap(UUID);
//            if (bufferBuilder != null) {
//                ChunkCoordinates startPos = ModItems.gadgetCopyPaste.getStartPos(itemstack);
//                ChunkCoordinates endPos = ModItems.gadgetCopyPaste.getEndPos(itemstack);
//                if (startPos == null || endPos == null) return;
//                double lengthX = Math.abs(startPos.posX - endPos.posX);
//                double lengthY = Math.abs(startPos.posY - endPos.posY);
//                double lengthZ = Math.abs(startPos.posZ - endPos.posZ);
//
//                final double maxW = 6 * 16;
//                final double maxH = 11 * 16;
//
//                double overW = Math.max(lengthX * 16 - maxW, lengthZ * 16 - maxW);
//                double overH = lengthY * 16 - maxH;
//
//                double sc = 1;
//                double zoomScale = 1;
//
//                if (overW > 0 && overW >= overH) {
//                    sc = maxW / (overW + maxW);
//                    zoomScale = overW / 40;
//                } else if (overH > 0 && overH >= overW) {
//                    sc = maxH / (overH + maxH);
//                    zoomScale = overH / 40;
//                }
//
//                GL11.glPushMatrix();
//
//                GL11.glMatrixMode(GL11.GL_PROJECTION);
//                GL11.glPushMatrix();
//                GL11.glLoadIdentity();
//
//                Project.gluPerspective(60, (float) panel.getWidth() / panel.getHeight(), 0.01F, 4000);
//
//                GL11.glMatrixMode(GL11.GL_MODELVIEW);
//                GL11.glViewport((guiLeft + panel.getX()) * scale, mc.displayHeight - (guiTop + panel.getY() + panel.getHeight()) * scale, panel.getWidth() * scale, panel.getHeight() * scale);
//                GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
//
//                //double sc = 300 + 8 * 0.0125 * (Math.sqrt(zoom + 99) - 9);
//                sc = (293 * sc) + zoom / zoomScale;
//                GL11.glScaled(sc, sc, sc);
//
//                int moveX = startPos.posX - endPos.posX;
//
//                if (startPos.posX >= endPos.posX) {
//                    moveX--;
//                }
//
//                GL11.glTranslated(moveX / 1.75, -Math.abs(startPos.posY - endPos.posY) / 1.75, 0);
//                GL11.glTranslated(panX, panY, 0);
//
//                GL11.glTranslated((
//                        (double) (startPos.posX - endPos.posX) / 2) * -1,
//                        ((double) (startPos.posY - endPos.posY) / 2) * -1,
//                        ((double) (startPos.posZ - endPos.posZ) / 2) * -1);
//                GL11.glRotated(rotX, 1, 0, 0);
//                GL11.glRotated(rotY, 0, 1, 0);
//                GL11.glTranslated(
//                        ((double) (startPos.posX - endPos.posX) / 2),
//                        ((double) (startPos.posY - endPos.posY) / 2),
//                        ((double) (startPos.posZ - endPos.posZ) / 2));
//
//                mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
//
//
//                if (bufferBuilder.getVertexCount() > 0) {
//
//                    VertexFormat vertexformat = bufferBuilder.getVertexFormat();
//                    int i = vertexformat.getNextOffset();
//                    ByteBuffer bytebuffer = bufferBuilder.getByteBuffer();
//                    List<VertexFormatElement> list = vertexformat.getElements();
//
//                    for (int j = 0; j < list.size(); ++j) {
//                        VertexFormatElement vertexformatelement = list.get(j);
//                        bytebuffer.position(vertexformat.getOffset(j));
//
//                        // moved to VertexFormatElement.preDraw
//                        vertexformatelement.getUsage().preDraw(vertexformat, j, i, bytebuffer);
//                    }
//
//                    //GL11.glDrawArrays("", 0, bufferBuilder.getVertexCount());
//                    //GlStateManager.glDrawArrays(bufferBuilder.getDrawMode(), 0, bufferBuilder.getVertexCount());
//                    int i1 = 0;
//
//                    for (int j1 = list.size(); i1 < j1; ++i1) {
//                        VertexFormatElement vertexformatelement1 = list.get(i1);
//                        vertexformatelement1.getUsage().postDraw(vertexformat, i1, i, bytebuffer);
//                    }
//                }
//
//                GL11.glPopMatrix();
//                GL11.glMatrixMode(GL11.GL_PROJECTION);
//                GL11.glPopMatrix();
//                GL11.glMatrixMode(GL11.GL_MODELVIEW);
//                GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
//            }
//        } else {
//            rotX = 0;
//            rotY = 0;
//            zoom = 1;
//            momentumX = 0;
//            momentumY = 0;
//            panX = 0;
//            panY = 0;
//        }
//    }

    @Override
    protected void actionPerformed(GuiButton b) {
        if (b.id == buttonHelp.id) {
            buttonHelp.toggleSelected();
        } else if (b.id == 0) {
            PacketHandler.INSTANCE.sendToServer(
                    new PacketTemplateManagerSave(
                            new ChunkCoordinates(te.xCoord, te.yCoord, te.zCoord),
                            nameField.getText()
                    )
            );
        } else if (b.id == 1) {
            PacketHandler.INSTANCE.sendToServer(
                    new PacketTemplateManagerLoad(new ChunkCoordinates(te.xCoord, te.yCoord, te.zCoord))
            );
        } else if (b.id == 2) {
            TemplateManagerCommands.copyTemplate(container);
        } else if (b.id == 3) {
            String CBString = getClipboardString();

            if (GadgetUtils.mightBeLink(CBString)) {
                Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(
                                ChatFormatting.RED + new ChatComponentTranslation("message.gadget.pastefailed.linkcopied").getUnformattedTextForChat()
                        )
                );
                return;
            }
            try {
                //Anything larger than below is likely to overflow the max packet size, crashing your client.
                ByteArrayOutputStream pasteStream = GadgetUtils.getPasteStream(
                        (NBTTagCompound) JsonToNBT.func_150315_a(CBString),
                        nameField.getText()
                );

                if (pasteStream != null) {
                    PacketHandler.INSTANCE.sendToServer(
                            new PacketTemplateManagerPaste(
                                    pasteStream,
                                    new ChunkCoordinates(te.xCoord, te.yCoord, te.zCoord),
                                    nameField.getText()
                            )
                    );

                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                            new ChatComponentText(
                                    ChatFormatting.AQUA + new ChatComponentTranslation("message.gadget.pastesuccess")
                                            .getUnformattedTextForChat()
                            )
                    );
                } else {
                    Minecraft.getMinecraft().thePlayer.addChatMessage(
                            new ChatComponentText(
                                    ChatFormatting.RED + new ChatComponentTranslation("message.gadget.pastetoobig")
                                            .getUnformattedTextForChat()
                            )
                    );
                }
            } catch (Throwable t) {
                BuildingGadgets.LOG.error(t);

                Minecraft.getMinecraft().thePlayer.addChatMessage(
                        new ChatComponentText(
                                ChatFormatting.RED + new ChatComponentTranslation("message.gadget.pastefailed").getUnformattedTextForChat()
                        )
                );
            }
        }
    }


    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (this.nameField.textboxKeyTyped(typedChar, keyCode)) {
//
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.nameField.isFocused()) {
            nameField.setFocused(true);
        } else {
            nameField.setFocused(false);
            if (panel.contains(mouseX - guiLeft, mouseY - guiTop)) {
                clickButton = mouseButton;
                panelClicked = true;
                clickX = Mouse.getX();
                clickY = Mouse.getY();
            }
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int mouseButton) {
        super.mouseMovedOrUp(mouseX, mouseY, mouseButton);

        // Check if the mouse button has been released
        if (mouseButton != -1) {
            // Simulate the mouseReleased method
            mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        panelClicked = false;
        initRotX = rotX;
        initRotY = rotY;
        initPanX = panX;
        initPanY = panY;
        initZoom = zoom;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        boolean doMomentum = false;
        if (panelClicked) {
            if (clickButton == 0) {
                prevRotX = rotX;
                prevRotY = rotY;
                rotX = initRotX - (Mouse.getY() - clickY);
                rotY = initRotY + (Mouse.getX() - clickX);
                momentumX = rotX - prevRotX;
                momentumY = rotY - prevRotY;
                doMomentum = false;
            } else if (clickButton == 1) {
                panX = initPanX + (float) (Mouse.getX() - clickX) / 8;
                panY = initPanY + (float) (Mouse.getY() - clickY) / 8;
            }
        }

        if (doMomentum) {
            rotX += momentumX;
            rotY += momentumY;
            momentumX *= momentumDampening;
            momentumY *= momentumDampening;
        }

        if (!nameField.isFocused() && nameField.getText().isEmpty())
            fontRendererObj.drawString("template name", nameField.xPosition - guiLeft + 4, nameField.yPosition - guiTop, -10197916);

        if (GuiUtils.isHovered(buttonSave, mouseX, mouseY) || GuiUtils.isHovered(buttonLoad,mouseX, mouseY) || GuiUtils.isHovered(buttonPaste, mouseX, mouseY))
            drawSlotOverlay(
                    GuiUtils.isHovered(buttonLoad, mouseX, mouseY)
                            ? container.getSlot(0)
                            : container.getSlot(1)
            );
    }

    private void drawSlotOverlay(Slot slot) {
        GL11.glTranslatef(0, 0, 1000); // Use GL11 for 1.7.10 equivalent of GlStateManager.translate
        drawRect(slot.xDisplayPosition, slot.yDisplayPosition, slot.xDisplayPosition + 16, slot.yDisplayPosition + 16, -1660903937);
        GL11.glTranslatef(0, 0, -1000); // Translate back after drawing
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();

        zoom = initZoom + (float) Mouse.getEventDWheel() / 2;
        if (zoom < -200) {
            zoom = -200;
        }

        if (zoom > 1000) {
            zoom = 1000;
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if (!panelClicked) {
            initRotX = rotX;
            initRotY = rotY;
            initZoom = zoom;
            initPanX = panX;
            initPanY = panY;
        }
    }
}