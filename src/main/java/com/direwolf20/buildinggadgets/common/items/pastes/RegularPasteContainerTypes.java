package com.direwolf20.buildinggadgets.common.items.pastes;

import java.util.function.IntSupplier;

import com.direwolf20.buildinggadgets.BuildingGadgetsConfig.PasteConfig;

public enum RegularPasteContainerTypes {

    /**
     * Iron paste container
     */
    T1("", () -> PasteConfig.t1Capacity),
    /**
     * Gold paste container
     */
    T2("t2", () -> PasteConfig.t2Capacity),
    /**
     * Diamond paste container
     */
    T3("t3", () -> PasteConfig.t3Capacity);

    public final String itemSuffix;
    public final IntSupplier capacitySupplier;

    RegularPasteContainerTypes(String itemSuffix, IntSupplier capacitySupplier) {
        this.itemSuffix = itemSuffix;
        this.capacitySupplier = capacitySupplier;
    }

}
