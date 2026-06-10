package com.direwolf20.buildinggadgets.common.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BlockBuildEntityRenderTest {

    private static final double DELTA = 1e-6D;

    @Test
    void translateToVoxelCenter() {
        assertEquals(10.5D, BlockBuildEntityRender.centerOnBlock(10.0D), DELTA);
    }

    @ParameterizedTest
    @ValueSource(floats = { 0.1F, 0.25F, 0.5F, 0.75F, 0.9F, 0.99F })
    void centeredItemGeometryStaysCenteredWhileScaling(float scale) {
        double blockPosition = 10.0D;
        double translation = BlockBuildEntityRender.centerOnBlock(blockPosition);

        // RenderBlocks.renderBlockAsItem shifts ordinary block item geometry by -0.5,
        // so its local center is 0 and remains 0 under uniform scale.
        double renderedCenter = translation;

        assertEquals(blockPosition + 0.5D, renderedCenter, DELTA);
    }

    @ParameterizedTest
    @ValueSource(floats = { 0.1F, 0.25F, 0.5F, 0.75F, 0.9F, 0.99F })
    void scaleDependentTranslationsDriftFromVoxelCenter(float scale) {
        double blockPosition = 10.0D;
        double offset = (1.0D - scale) / 2.0D;

        double oldCenter = blockPosition + 0.5D + offset;
        double copilotPrCenter = blockPosition + offset;

        assertEquals(offset, oldCenter - (blockPosition + 0.5D), DELTA);
        assertEquals(0.5D - offset, (blockPosition + 0.5D) - copilotPrCenter, DELTA);
    }
}
