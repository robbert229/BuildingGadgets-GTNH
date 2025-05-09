package com.direwolf20.buildinggadgets.common.tools;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record ConstructionBlockEntityMetadata(boolean bright, boolean neighborBrightness) {

}
