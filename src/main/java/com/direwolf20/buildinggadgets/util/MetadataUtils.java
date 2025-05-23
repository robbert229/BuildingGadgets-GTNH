package com.direwolf20.buildinggadgets.util;

import com.direwolf20.buildinggadgets.common.tools.ConstructionBlockEntityMetadata;

public class MetadataUtils {

    public static ConstructionBlockEntityMetadata getConstructionBlockEntityMetadataFromDamage(int damage) {
        boolean bright = (damage & 0b0001) != 0;
        boolean neighborBrightness = (damage & 0b010) != 0;
        return new ConstructionBlockEntityMetadata(bright, neighborBrightness);
    }

    public static int getDamageFromConstructionBlockEntityMetadata(ConstructionBlockEntityMetadata metadata) {
        int bright = (metadata.bright() ? 1 : 0);
        int neighborBrightness = (metadata.neighborBrightness() ? 1 : 0) << 1;
        return neighborBrightness + bright;
    }
}
