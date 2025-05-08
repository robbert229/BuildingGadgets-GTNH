package com.direwolf20.buildinggadgets.client.gui;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.api.widget.IWidget;
import com.cleanroommc.modularui.drawable.ItemDrawable;
import com.cleanroommc.modularui.drawable.Rectangle;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.utils.Color;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ListWidget;
import com.cleanroommc.modularui.widgets.layout.Flow;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.direwolf20.buildinggadgets.common.items.ITemplate;
import com.direwolf20.buildinggadgets.common.tools.InventoryManipulation;

public class MaterialListGUI {

    /**
     * <ol>
     * <li>Item name (localized)
     * <li>Item count
     * </ol>
     */
    public static final String PATTERN_SIMPLE = "%s: %d";

    /**
     * <ol>
     * <li>Item name (localized)
     * <li>Item count
     * <li>Item registry name
     * <li>Formatted stack count, e.g. 5x64+2
     * </ol>
     */
    public static final String PATTERN_DETAILED = "%s: %d (%s, %s)";
    private static final int BUTTON_HEIGHT = 20;

    private static String stringifySimple(List<ItemStack> materials) {
        return materials.stream()
            .map(item -> String.format(PATTERN_SIMPLE, item.getDisplayName(), item.stackSize))
            .collect(Collectors.joining("\n"));
    }

    private static String stringifyDetailed(List<ItemStack> materials) {
        return materials.stream()
            .map(
                item -> String.format(
                    PATTERN_DETAILED,
                    item.getDisplayName(),
                    item.stackSize,
                    item.getItem()
                        .getUnlocalizedName(),
                    InventoryManipulation.formatItemCount(item.getMaxStackSize(), item.stackSize)))
            .collect(Collectors.joining("\n"));
    }

    private static String stringify(List<ItemStack> materials, boolean detailed) {
        if (detailed) return stringifyDetailed(materials);
        return stringifySimple(materials);
    }

    public static ModularScreen createGUI(@Nonnull ItemStack itemStack) {
        ITemplate item = (ITemplate) itemStack.getItem();

        assert item != null;

        var materials = getMaterialsList(itemStack, item);

        ModularPanel panel = ModularPanel.defaultPanel(GuiUtils.getPanelName("material_list"))
            .heightRel(0.8f)
            .widthRel(0.6f);

        panel.child(ButtonWidget.panelCloseButton())
            .child(
                IKey.lang("gui.buildinggadgets.materialList.title")
                    .asWidget()
                    .align(Alignment.TopCenter)
                    .top(7));

        panel.child(
            new ListWidget<>().paddingLeft(7)
                .paddingRight(7)
                .left(7)
                .right(12)
                .top(BUTTON_HEIGHT)
                .children(() -> {
                    return materials.stream()
                        .map(material -> {
                            var current = 0;
                            var needed = material.stackSize;
                            var color = current >= needed ? Color.rgb(0, 255, 0) : Color.rgb(255, 0, 0);

                            return (IWidget) Flow.row()
                                .child(
                                    new ItemDrawable(material).asWidget()
                                        .height(20)
                                        .width(20))
                                .child(
                                    IKey.str(material.getDisplayName())
                                        .color(Color.rgb(255, 255, 255))
                                        .asWidget()
                                        .height(20))
                                .child(
                                    IKey.str(String.format("%d/%d", current, needed))
                                        .color(color)
                                        .asWidget()
                                        .height(20)
                                        .align(Alignment.CenterRight))
                                .coverChildrenHeight();
                        })
                        .iterator();
                })
                .background(new Rectangle().setColor(Color.rgb(43, 43, 43)))
                .bottom(BUTTON_HEIGHT + 7));

        panel.child(
            new Row()
                .child(
                    new ButtonWidget<>().overlay(IKey.lang("gui.buildinggadgets.materialList.button.sortingMode.name"))
                        .marginRight(7)
                        .expanded()
                        .debugName("sort")
                        .disabled()
                        .addTooltipElement(IKey.str("Currently not implemented`")))
                .child(
                    new ButtonWidget<>().overlay(IKey.lang("gui.buildinggadgets.materialList.button.copyList"))
                        .expanded()
                        .debugName("copy")
                        .onMousePressed(mouseButton -> {
                            boolean detailed = GuiScreen.isCtrlKeyDown();
                            GuiScreen.setClipboardString(stringify(materials, detailed));

                            String type;
                            if (detailed) {
                                type = I18n
                                    .format("gui.buildinggadgets.materialList.message.copiedMaterialList.detailed");
                            } else {
                                type = I18n
                                    .format("gui.buildinggadgets.materialList.message.copiedMaterialList.simple");
                            }

                            Minecraft.getMinecraft().thePlayer.addChatMessage(
                                new ChatComponentTranslation(
                                    "gui.buildinggadgets.materialList.message.copiedMaterialList",
                                    type));

                            return true;
                        }))
                .height(BUTTON_HEIGHT)
                .coverChildrenHeight()
                .debugName("button_row")
                .bottom(7)
                .left(7)
                .right(7));

        return new ModularScreen(panel);
    }

    private static List<ItemStack> getMaterialsList(ItemStack itemStack, ITemplate item) {
        return item.getItemCountMap(itemStack)
            .entrySet()
            .stream()
            .map(e -> new ItemStack(e.getElement().item, e.getCount(), e.getElement().meta))
            .collect(Collectors.toList());
    }
}
