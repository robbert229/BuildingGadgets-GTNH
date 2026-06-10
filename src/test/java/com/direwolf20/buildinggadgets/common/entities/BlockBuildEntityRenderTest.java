package com.direwolf20.buildinggadgets.common.entities;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for the block-grow/shrink animation centering math in
 * {@link BlockBuildEntityRender}.
 *
 * <p>{@code renderBlockAsItem} renders from local (0,0,0) to (1,1,1). After
 * {@code glScalef(scale)} the block occupies [0, scale]. The translation
 * {@code T = entityPos + computeTranslationOffset(scale)} places the origin so
 * that the rendered range is [T, T+scale], whose centre is always
 * {@code entityPos + 0.5} — the centre of the block voxel.
 */
class BlockBuildEntityRenderTest {

    private static final float DELTA = 1e-6f;

    // -----------------------------------------------------------------------
    // computeTranslationOffset
    // -----------------------------------------------------------------------

    @Test
    void offsetAtScaleOneIsZero() {
        assertEquals(0f, BlockBuildEntityRender.computeTranslationOffset(1f), DELTA);
    }

    @Test
    void offsetAtScaleZeroIsHalf() {
        assertEquals(0.5f, BlockBuildEntityRender.computeTranslationOffset(0f), DELTA);
    }

    @Test
    void offsetAtScaleHalf() {
        assertEquals(0.25f, BlockBuildEntityRender.computeTranslationOffset(0.5f), DELTA);
    }

    // -----------------------------------------------------------------------
    // Centre-of-rendered-block = entityPos + 0.5  for any scale in [0, 1]
    // -----------------------------------------------------------------------

    /**
     * For any animation scale the rendered block must be centred exactly at the
     * middle of its 1x1x1 voxel.  The entity is positioned at the minimum corner
     * of the voxel (integer block coordinates), so the visual centre must be at
     * {@code entityPos + 0.5}.
     */
    @ParameterizedTest
    @ValueSource(floats = { 0f, 0.1f, 0.25f, 0.5f, 0.75f, 0.9f, 0.99f, 1f })
    void renderedBlockCentreAlwaysAtVoxelCentre(float scale) {
        double entityPos = 10.0; // arbitrary integer block coordinate

        float offset = BlockBuildEntityRender.computeTranslationOffset(scale);
        double translation = entityPos + offset;

        // After glScalef(scale), the block spans [translation, translation + scale].
        double renderedCentre = translation + scale / 2.0;

        assertEquals(entityPos + 0.5, renderedCentre, DELTA,
            "Rendered centre must equal entityPos + 0.5 at scale=" + scale);
    }

    /**
     * The old (buggy) code used {@code entityPos + offset + 0.5} which placed the
     * centre at {@code entityPos + 1.0} regardless of scale -- always the +X/+Y/+Z
     * corner of the voxel.  This test explicitly documents that regression.
     */
    @ParameterizedTest
    @ValueSource(floats = { 0f, 0.25f, 0.5f, 0.75f, 0.99f })
    void buggyOffsetPlusHalfIsNotCentred(float scale) {
        double entityPos = 10.0;

        float offset = BlockBuildEntityRender.computeTranslationOffset(scale);
        // Simulate the old buggy translation:
        double buggyTranslation = entityPos + offset + 0.5;
        double buggyRenderedCentre = buggyTranslation + scale / 2.0;

        // The buggy centre is always entityPos + 1.0, not entityPos + 0.5.
        assertEquals(entityPos + 1.0, buggyRenderedCentre, DELTA,
            "Buggy formula should give centre at entityPos+1 (the corner) at scale=" + scale);
    }
}
