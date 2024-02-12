package com.direwolf20.buildinggadgets.common.items;

import com.direwolf20.buildinggadgets.BuildingGadgets;
import com.direwolf20.buildinggadgets.common.items.gadgets.GadgetBuilding;

import cpw.mods.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(BuildingGadgets.MODID)
public class ModItems {
    @GameRegistry.ObjectHolder("buildingtool")
    public static GadgetBuilding gadgetBuilding;
    //
    // @GameRegistry.ObjectHolder("exchangertool")
    // public static GadgetExchanger gadgetExchanger;
    //
    // @GameRegistry.ObjectHolder("constructionpaste")
    // public static ConstructionPaste constructionPaste;
    //
    // @GameRegistry.ObjectHolder("construction_chunk_dense")
    // public static ConstructionChunkDense constructionChunkDense;
    //
    // @GameRegistry.ObjectHolder("constructionpastecontainer")
    // public static ConstructionPasteContainer constructionPasteContainer;
    //
    // @GameRegistry.ObjectHolder("constructionpastecontainert2")
    // public static ConstructionPasteContainer constructionPasteContainert2;
    //
    // @GameRegistry.ObjectHolder("constructionpastecontainert3")
    // public static ConstructionPasteContainer constructionPasteContainert3;
    //
    // @GameRegistry.ObjectHolder("constructionpastecontainercreative")
    // public static ConstructionPasteContainerCreative constructionPasteContainerCreative;
    //
    // @GameRegistry.ObjectHolder("copypastetool")
    // public static GadgetCopyPaste gadgetCopyPaste;
    //
    // @GameRegistry.ObjectHolder("template")
    // public static Template template;
    //
    // @GameRegistry.ObjectHolder("destructiontool")
    // public static GadgetDestruction gadgetDestruction;

    public static void init() {
        gadgetBuilding = new GadgetBuilding();
        GameRegistry.registerItem(gadgetBuilding, gadgetBuilding.getUnlocalizedName());
    }
}
