package com.direwolf20.buildinggadgets.common.tools;

public class ConstructionBlockEntityMetadata {

    private final boolean bright;
    private final boolean neighborBrightness;

    public boolean getNeighborBrightness() {
        return neighborBrightness;
    }

    public boolean getBright() {
        return bright;
    }

    public ConstructionBlockEntityMetadata(boolean bright, boolean neighborBrightness) {
        this.bright = bright;
        this.neighborBrightness = neighborBrightness;
    }
}
